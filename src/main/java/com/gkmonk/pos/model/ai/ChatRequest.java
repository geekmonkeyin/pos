package com.gkmonk.pos.model.ai;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChatRequest {
    private String threadId;
    private String message;   // when user types free-text
}

