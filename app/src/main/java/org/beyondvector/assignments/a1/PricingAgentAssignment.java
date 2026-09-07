package org.beyondvector.assignments.a1;

import org.beyondvector.assignments.common.ModelHelper;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public class PricingAgentAssignment {

    static ChatModel chatModel;
    private static enum PromptStyle {
        BASIC,
        COT,
    }

    interface PricingAgentBasic {
        @UserMessage("You are a pricing agent for a retail company." +
                "Your task is to recommend an optimal price for a product considering margin and competition." +
                "\\n\\nInputs: {{cost}}, {{current_price}},{{competition_price}}, {{margin}}, {{flexibility}}\\n" +
                "\\nRecommended Price: ")
        String recommendPrice(@V("cost") String cost, @V("current_price") String currentPrice, @V("competition_price") String competitionPrice, @V("margin") String margin, @V("flexibility") String flexibility);
    }

    interface PricingAgentCOT {
        @SystemMessage("You are a pricing agent for a retail company." +
                "Your task is to recommend an optimal price for a product considering margin and competition." +
                "Think step-by-step:" +
                "0. Cost is the total cost of producing the product." + 
                "1. If current_price is less than competition_price, consider increasing the price to match or slightly undercut the competition." +
                "2. If current_price is higher than competition_price, consider lowering the price while maintaining the margin." +
                "3. If changing the current_price, keep in mind the flexibility factor, which indicates how much the price can be adjusted.")
        @UserMessage("Inputs: {{cost}}, {{current_price}},{{competition_price}}, {{margin}}, {{flexibility}}\\n" +
                "\\nRecommended Price: " +
                "Provide a quick price recommendation with brief reasoning.")
        String recommendPrice(@V("cost") String cost, @V("current_price") String currentPrice, @V("competition_price") String competitionPrice, @V("margin") String margin, @V("flexibility") String flexibility);
    }

    public static void init() {
        chatModel = ModelHelper.getChatModel(ModelHelper.ModelType.GOOGLE_AI_GEMINI);
    }

    public static void recommendPrice(String cost, String currentPrice, String competitionPrice, String margin, String flexibility, PromptStyle style) {
        String recommendedPrice = null;
        switch (style) {
            case PromptStyle.BASIC:
                recommendedPrice = 
                AiServices.create(PricingAgentBasic.class, chatModel).recommendPrice(cost, currentPrice, competitionPrice, margin, flexibility);
                break;
            case PromptStyle.COT:
                recommendedPrice = 
                AiServices.create(PricingAgentCOT.class, chatModel).recommendPrice(cost, currentPrice, competitionPrice, margin, flexibility);
                break;
           default:
                break;
        }
        System.out.println("================== Style: " + style + " ==================");
        System.out.println("Recommended Price: " + recommendedPrice);
    }

    public static void main(String[] args) {
        init();
        recommendPrice("400", "599", "579", "20%", "medium", PromptStyle.BASIC);
        recommendPrice("500", "599", "580", "20%", "high", PromptStyle.COT);
    }
}
