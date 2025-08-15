package com.gkmonk.pos.controller.ai;

import com.gkmonk.pos.model.ai.ChatMessageLog;
import com.gkmonk.pos.services.ai.ChatGPTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatGPTService chatGPTService;


    @PostMapping
    public ResponseEntity<ChatMessageLog> chat(@RequestBody Map<String, String> request) {
        String userInput = request.get("message");
        String sessionId = request.get("sessionId");
        ChatMessageLog reply = chatGPTService.getResponse(userInput,sessionId);
        return ResponseEntity.ok(reply);
    }
}
