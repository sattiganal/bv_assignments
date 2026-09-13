package org.beyondvector.assignments.common;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ModelHelper {

    public enum ModelType {
        OPENAI,
        GOOGLE_AI_GEMINI,
        HUGGINGFACE_EMBEDDING,
    }

    public static EmbeddingModel getEmbeddingModel(ModelType modelType) {
        switch (modelType) {
            case HUGGINGFACE_EMBEDDING:
                return getHuggingFaceEmbeddingModel();
            default:
                throw new IllegalArgumentException("Unsupported model type: " + modelType);
        }
    }

    public static EmbeddingModel getHuggingFaceEmbeddingModel() {
        // return OpenAiEmbeddingModel.builder()
        //         .apiKey(System.getenv("HF_API_KEY"))
        //         .baseUrl("https://router.huggingface.co/v1")
        //         .modelName("BAAI/bge-base-en-v1.5")
        //         .build();
        return new AllMiniLmL6V2EmbeddingModel();
    }

    public static ChatModel getChatModel(ModelType modelType) {
        switch (modelType) {
            case GOOGLE_AI_GEMINI:
                return getGoogleAiGeminiChatModel();
            case OPENAI:
                return getOpenAiChatModel();
            default:
                throw new IllegalArgumentException("Unsupported model type: " + modelType);
        }
    }

    private static ChatModel getGoogleAiGeminiChatModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .modelName("gemini-3.5-flash-lite").build();
    }

    private static ChatModel getOpenAiChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(System.getenv("GROQ_API_KEY"))
                .modelName("openai/gpt-oss-120b").build();
    }

}
