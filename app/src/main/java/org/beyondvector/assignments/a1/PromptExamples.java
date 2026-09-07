package org.beyondvector.assignments.a1;

import java.util.List;

import org.beyondvector.assignments.common.ModelHelper;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public class PromptExamples {

    static ChatModel chatModel;

    interface PromptChain {
        @UserMessage("Classify the sentiment of the following customer product review\r\n" + //
                        "as positive, negative, or neutral.\r\n" + //
                        "\r\n" + //
                        "Here are a few examples:\r\n" + //
                        "\r\n" + //
                        "Review: The shoes fit perfectly and the quality is amazing.\r\n" + //
                        "Sentiment: positive\r\n" + //
                        "\r\n" + //
                        "Review: The delivery was late and the product was damaged.\r\n" + //
                        "Sentiment: negative\r\n" + //
                        "\r\n" + //
                        "Review: The packaging was okay, nothing special.\r\n" + //
                        "Sentiment: neutral\r\n" + //
                        "\r\n" + //
                        "Review: {{text}}\r\n" + //
                        "Sentiment:\r\n" + //
                        "Answer concisely in 1 or two words\r\n" + //
                        "\r\n" + //
                        "Make sure the output format is clearly folowed as Sentiment: <positive/negative/neutral>")
        String generate(@V("text") String text);
    }

    public static void init() {
        // Initialize your chat agent here
        chatModel = ModelHelper.getChatModel(ModelHelper.ModelType.GOOGLE_AI_GEMINI);
    }

    public static void promptTemplateTest() {
        
        PromptChain chain = AiServices.create(PromptChain.class, chatModel);
        List<String> texts = List.of(
            "The headphones have excellent sound quality, totally worth the price!",
            "I'm not happy, the phone case broke after two days of use.",
            "The employee was struggling initially but then picked up and was fine "
        );
        for (String text : texts) {
            //System.out.println(chatModel.chat(chain.generate(text)));
            System.out.println("Text: " + text);
            System.out.println(chain.generate(text) );
        }
    }

    public static void main(String[] args) {
        init();
        promptTemplateTest();
    }

}

