package com.gkmonk.pos.services.ai;

import com.gkmonk.pos.model.ai.ChatMessageLog;
import com.gkmonk.pos.model.ai.IntentType;
import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.pod.services.PODServiceImpl;
import com.gkmonk.pos.repo.ai.ChatMessageRepository;
import com.gkmonk.pos.utils.StringUtils;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatGPTService {

    private String apiKey;
    @Autowired
    private ChatMessageRepository chatRepo;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private PODServiceImpl podService;
    private OpenAiService openAiService;

    @PostConstruct
    public void init() {
        apiKey = System.getenv().get("openai_api_key");
        openAiService = new OpenAiService(apiKey);


    }

    public String getResponseFromAI(String userMessage) {
        List<ChatMessage> chatMessageList = new ArrayList<>();
        chatMessageList.add(new ChatMessage("user", userMessage));
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("GPT‑3.5‑Turbo")
                .messages(chatMessageList)
                .maxTokens(100)
                .build();

        ChatCompletionResult result = openAiService.createChatCompletion(request);

        return result.getChoices().get(0).getMessage().getContent();
    }

    public ChatMessageLog  getResponse(String userMessage,String sessionId) {
        // Call OpenAI
        ChatMessageLog log = new ChatMessageLog();

        String response =  checkOrderStatus(userMessage,log);
       response = StringUtils.isBlank(response) ? checkProductStatus(userMessage) : response;
        String reply = "Checking status for the given "+response;//getResponseFromAI(userMessage);
        // Save to MongoDB
        log.setUserMessage(userMessage);
        log.setAiReply(reply);
        log.setSessionID(sessionId);
        chatRepo.save(log);
        return log;
    }

    private String checkProductStatus(String userMessage) {
        boolean isProductStatus = userMessage.matches(".*(stock|available|product|price|variant).*");
        if(!isProductStatus){
            System.out.println("Not a product status query.");
            return null;
        } else {
                boolean hasProductName = userMessage.matches(".*\\b(product|item|variant|name)\\b.*");
                boolean hasProductId = userMessage.matches(".*\\b(prod\\d+|\\d{6,})\\b.*");

                if (hasProductName) {
                    System.out.println("Product name shared.");
                    return "Product name detected.";
                } else if (hasProductId) {
                    System.out.println("Product ID shared.");
                    return "Product ID detected.";
                } else {
                    System.out.println("No product information shared.");
                    return "No product information detected.";
                }
        }
    }

    private String checkOrderStatus(String userMessage, ChatMessageLog log) {
        boolean isOrderStatus = userMessage.matches(".*(order|track|shipment|awb|tracking).*");
        if (isOrderStatus) {
            log.setIntentType(IntentType.ORDER_STATUS);
            boolean hasOrderNumber = userMessage.matches(".*\\b(gm\\d{5}|\\d{13})\\b.*");
            if (hasOrderNumber) {
                return getOrderStatus(userMessage);
            } else {
                System.out.println("No order number provided.");
            }
        }
        return null;
    }

    private String getOrderStatus(String userMessage) {
        String orderNumber = extractOrderNumber(userMessage);
        PackedOrder packedOrder =  podService.findByOrderId(orderNumber);
        String awb = packedOrder.getAwb();

        return null;
    }

    private String extractOrderNumber(String userMessage) {
        // Regex pattern for order numbers (e.g., gm34212 or 6795061231931)
        String regex = "\\b(gm\\d{5}|\\d{13})\\b";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(userMessage);

        if (matcher.find()) {
            return matcher.group(); // Return the first match
        }

        return null; // Return null if no match is found
    }


}
