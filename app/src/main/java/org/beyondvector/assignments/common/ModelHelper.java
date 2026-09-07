package org.beyondvector.assignments.common;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ModelHelper {

    public enum ModelType {
        OPENAI,
        GOOGLE_AI_GEMINI
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
