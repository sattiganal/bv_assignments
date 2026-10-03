package org.beyondvector.assignments.a2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.beyondvector.assignments.common.DBHelper;
import org.beyondvector.assignments.common.DocumentHelper;
import org.beyondvector.assignments.common.ModelHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;

public class ChunkAndStore {

    private static final Logger log = LoggerFactory.getLogger(ChunkAndStore.class);
    private static EmbeddingModel embeddingModel = ModelHelper.getEmbeddingModel(ModelHelper.ModelType.HUGGINGFACE_EMBEDDING);
    private static EmbeddingStore<TextSegment> embeddingStore = DBHelper.getEmbeddingStore(DBHelper.DBType.IN_MEMORY);

    private static void chunkMultiple(Document document) {
        int[] chunkSizes = {200};
        Map<Integer, List<TextSegment>> chunkSizeToSegments = new HashMap<>();
        for(int chunkSize : chunkSizes) {
            DocumentSplitter documentSplitter = DocumentSplitters.recursive(chunkSize, 10);
            chunkSizeToSegments.put(chunkSize, documentSplitter.split(document));    
         }
        chunkSizeToSegments.forEach((chunkSize, segments) -> {
            log.info("Chunk size: {}, Number of segments: {}", chunkSize, segments.size());
            segments.forEach(segment -> {
                if(segment.text().contains("Free Plan")) 
                    log.info("Segment Hit: {}, Segment text: {}", segments.indexOf(segment), segment.text().trim());
            });
        });
    }

    private static void chunkSemantically(Document document) {
        List<TextSegment> semanticallyChunks = DocumentHelper.doSemanticChunking(document, 500, 50);
        embeddingStore.addAll(embeddingModel.embedAll(semanticallyChunks).content(), semanticallyChunks);
    }

    private static void embeddPricingDocument(Document document) {
        
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(200, 10);
        documentSplitter.split(document).forEach(segment -> {
            //log.info(" ###### Segment is {} : {}", segment.text().length(),segment.text().trim());
            embeddingStore.add(embeddingModel.embed(segment).content(), segment);
        });
    }
   
    public static void main(String[] args) {

        Document pricingDocument = new DocumentHelper().loadDocumentFromDisk("/pricing_guide.md");
        // chunkMultiple(pricingDocument);

        //embeddPricingDocument(pricingDocument);

        chunkSemantically(pricingDocument);
        
        Embedding queryEmbedding = embeddingModel.embed("Is the CRM free?").content();
        EmbeddingSearchRequest embeddingSearchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3)
                .build();
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.search(embeddingSearchRequest).matches();
        EmbeddingMatch<TextSegment> embeddingMatch = matches.get(0);

        System.out.println(embeddingMatch.score()); // 0.8144288493114709
        System.out.println(embeddingMatch.embedded().text()); // I like football.
    }

     private static void log(Document document) {
        log.info("{}: {} ...", document.metadata().getString("file_name"), document.text().trim().substring(0, 50));
    }
}
