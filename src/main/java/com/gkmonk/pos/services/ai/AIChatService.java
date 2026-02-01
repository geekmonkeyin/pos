package com.gkmonk.pos.services.ai;

public interface AIChatService {

    AiResult reply(String message, String threadId);
    record AiResult(String reply, String threadId) {}
}
