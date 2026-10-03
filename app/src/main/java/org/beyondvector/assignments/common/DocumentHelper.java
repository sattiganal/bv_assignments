package org.beyondvector.assignments.common;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

public class DocumentHelper {

    private static EmbeddingModel embeddingModel = ModelHelper.getEmbeddingModel(ModelHelper.ModelType.HUGGINGFACE_EMBEDDING);


     public List<TextSegment> doFixedLengthChunking(Document document, int chunkSize) {
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(chunkSize,10);
        return documentSplitter.split(document);
    }

    public static List<TextSegment> doSemanticChunking(Document document, int chunkSize, int overlap) {
        List<TextSegment> sentences = new DocumentBySentenceSplitter(chunkSize, overlap).split(document);
        List<Embedding> sentenceEmbeddings = embeddingModel.embedAll(sentences).content();
        
        List<TextSegment> semanticallyChunks = new ArrayList<TextSegment>();
        StringBuilder currentChunk = new StringBuilder(sentences.get(0).text());

        for(int i=1; i<sentences.size(); i++) {
            Embedding currentEmbedding = sentenceEmbeddings.get(i);
            Embedding previousEmbedding = sentenceEmbeddings.get(i-1);
            double similarity = CosineSimilarity.between(currentEmbedding, previousEmbedding);
            if(similarity > 0.8) {
                currentChunk.append(" ").append(sentences.get(i).text());
            } else {
                semanticallyChunks.add(TextSegment.from(currentChunk.toString()));
                currentChunk = new StringBuilder(sentences.get(i).text());
            }
        }
        return semanticallyChunks;
    }

    public Document loadDocumentFromDisk(String fileName) {
        Path documentPath = toPath(fileName);
        Document document = loadDocument(documentPath, new ApacheTikaDocumentParser());
        return document;
    }

    public static Path toPath(String fileName) {
        try {
            URL fileUrl = DocumentHelper.class.getResource(fileName);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
