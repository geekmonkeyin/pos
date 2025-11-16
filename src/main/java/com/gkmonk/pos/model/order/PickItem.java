package com.gkmonk.pos.model.order;

import lombok.Data;

@Data
public class PickItem {

        private String id;          // internal id (UUID or SKU)
        private String variationId; // VAR/SKU shown to pickers
        private String name;
        private int qty;
        private String imageUrl;
        private Priority priority;  // HIGH / MEDIUM / LOW
        private boolean picked;
        private String notes;
        private String storage;
        private String shopifyQuantity;
        private String ourQuantity;

        public enum Priority { HIGH, MEDIUM, LOW }

        public PickItem() {}
        public PickItem(String id, String variationId, String name, int qty, String imageUrl, Priority priority, boolean picked, String notes,String storage) {
            this.id = id; this.variationId = variationId; this.name = name; this.qty = qty;
            this.imageUrl = imageUrl; this.priority = priority; this.picked = picked; this.notes = notes;
            this.storage = storage;
        }

    }
