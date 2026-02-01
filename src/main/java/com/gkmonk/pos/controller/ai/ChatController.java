package com.gkmonk.pos.controller.ai;

import com.gkmonk.pos.model.ai.ChatDtos;
import com.gkmonk.pos.model.ai.ChatRequest;
import com.gkmonk.pos.services.ai.ChatGPTService;
import com.gkmonk.pos.services.ai.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatGPTService chatGPTService;
    @Autowired
    private ChatService aiChatService;

}
