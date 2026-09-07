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

    interface ReviewHelperAgent {
        @UserMessage("Summarize the following customer review into a single sentence:\\n\\nReview: {{review}}")
        String summarize(@V("review") String review);

        @UserMessage ("Classify the sentiment of the following review summary as positive, negative, or neutral:\\n\\nSummary: {{summary}}")
        String analyzeSentiment(@V("summary") String summary);

        @UserMessage ("Suggest an appropriate action based on the sentiment:\\n\\nSentiment: {{sentiment}}")
        String suggestAction(@V("sentiment") String sentiment);
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
            System.out.println("Text: " + text);
            System.out.println(chain.generate(text) );
        }
    }

    public static void runReviewHelper(String review) {
        ReviewHelperAgent agent = AiServices.create(ReviewHelperAgent.class, chatModel);
        
        String summary = agent.summarize(review);
        String sentiment = agent.analyzeSentiment(summary);
        String action = agent.suggestAction(sentiment);

        System.out.println("Review: " + review);
        System.out.println("Summary: " + summary);
        System.out.println("Sentiment: " + sentiment);
        System.out.println("Suggested Action: " + action);
    }

    public static void main(String[] args) {
        init();
        //promptTemplateTest();
        runReviewHelper("The product arrived late and was damaged, but the customer service was helpful in resolving the issue.");
    }

}

