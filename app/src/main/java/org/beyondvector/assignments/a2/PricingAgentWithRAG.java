package org.beyondvector.assignments.a2;

import org.beyondvector.assignments.common.DocumentHelper;
import org.beyondvector.assignments.common.HydeQueryTransformer;
import org.beyondvector.assignments.common.DBHelper;
import org.beyondvector.assignments.common.ModelHelper;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;

public class PricingAgentWithRAG {

    interface PricingAssistant {
    @SystemMessage("""
        You are a pricing assistant for DataFlow Platform.
        Answer using ONLY the provided context.
        Be precise with prices and plan names.
        If the context doesn't cover the question, say 'I don't have that information'.
        """)

        String answer(@UserMessage String question);
    }

    public static void main(String[] args) {
        
        Document pricingDocument = new DocumentHelper().loadDocumentFromDisk("/pricing_guide.md");

        EmbeddingModel embeddingModel = ModelHelper.getEmbeddingModel(ModelHelper.ModelType.HUGGINGFACE_EMBEDDING);
        EmbeddingStore<TextSegment> embeddingStore = DBHelper.getEmbeddingStore(DBHelper.DBType.IN_MEMORY);
        ChatModel chatModel = ModelHelper.getChatModel(ModelHelper.ModelType.OPENAI);

        DocumentHelper.doSemanticChunking(pricingDocument, 1000, 50).forEach(segment -> {
            embeddingStore.add(embeddingModel.embed(segment).content(), segment);
        });

        ContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(4)
                .build();

        PricingAssistant assistant = AiServices.builder(PricingAssistant.class)
                .chatModel(chatModel)
                .contentRetriever(retriever)
                .build();
        
        String[] questions = {
            "What is the price of the Free Plan?",
            "What is included in the Analytics Pro plan?",
            "What is the cost of the Enterprise plan?",
            "affordable option for a small startup with basic needs"
        };

        for (String question : questions) {
            System.out.println("Question: " + question);
            System.out.println("Answer: " + assistant.answer(question));
            System.out.println("--------------------------------------------------");
        }

        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(new HydeQueryTransformer(chatModel))
                .contentRetriever(retriever) 
                .build();
        
         PricingAssistant assistantWithHyDE = AiServices.builder(PricingAssistant.class)
                .chatModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .build();
        
        System.out.println("----------------- Answers with HyDE --------------------------");
        System.out.println(assistantWithHyDE.answer(questions[3]));
    }
}
