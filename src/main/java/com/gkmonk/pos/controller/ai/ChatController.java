package com.gkmonk.pos.controller.ai;

import com.gkmonk.pos.model.ai.ChatDtos;
import com.gkmonk.pos.model.ai.ChatMessageLog;
import com.gkmonk.pos.model.ai.ChatReply;
import com.gkmonk.pos.model.ai.ChatRequest;
import com.gkmonk.pos.services.ai.ChatGPTService;
import com.gkmonk.pos.services.ai.ChatService;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatGPTService chatGPTService;
    @Autowired
    private ChatService chatService;


    @GetMapping("/options")
    public ChatDtos.OptionsResponse options(@RequestParam @NotBlank String sessionId){
        return chatService.getRootOptions(sessionId);
    }

    @PostMapping
    public ResponseEntity<ChatMessageLog> chat(@RequestBody Map<String, String> request) {
        String userInput = request.get("message");
        String sessionId = request.get("sessionId");
        ChatMessageLog reply = chatGPTService.getResponse(userInput,sessionId);
        return ResponseEntity.ok(reply);
    }

    // POST /api/chat/message
    @PostMapping("/message")
    public ChatReply message(@RequestBody ChatRequest req) {
        // If req.choiceId present, route based on predefined intents
        // else free text -> your NLP/LLM/keyword logic
        String reply;
        List<ChatReply.Option> next = List.of();
        if (req.getChoiceId() != null) {
            switch (req.getChoiceId()) {
                case "opt_shipping" -> reply = "Enter source/destination pin codes and weight to see rates.";
                case "refund_policy" -> reply = "Our refund policy allows returns within 7 days...";
                case "opt_orders" -> {
                    reply = "Please share your order number and I’ll check it.";
                    next = List.of(new ChatReply.Option("Provide Order Number", "opt_provide_order"));
                }
                default -> reply = "I can help with shipping, refunds, or order status.";
            }
        } else {
            reply = "Thanks! I’m processing your message: \"" + req.getMessage() + "\"";
        }
        return new ChatReply(reply, next);
    }

}
