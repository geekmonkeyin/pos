package com.gkmonk.pos.model.ai;

// model/ChatOption.java

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document("chat_options")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatOption {
    @Id private String id;
    @Indexed(unique = true) private String optionId;   // e.g., "opt_orders"
    private String label;                              // shown to user
    private String replyMessage;                       // bot reply
    private List<String> nextOptionIds;                // children
    private Map<String,Object> meta;                   // optional conditions
    private boolean root;                              // top-level?
}
