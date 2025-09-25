package com.gkmonk.pos.model.ai;

import lombok.Data;
import java.util.List;

@Data
public class ChatReply {
    private String message;
    private List<Option> options;

    public ChatReply(String message, List<Option> options) {
        this.message = message;
        this.options = options;
    }

    public ChatReply() {}

    // nested DTO for options
    public static class Option {
        private String id;
        private String label;

        public Option() {}
        public Option(String id, String label) {
            this.id = id;
            this.label = label;
        }

        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }
        public void setLabel(String label) {
            this.label = label;
        }
    }
}
