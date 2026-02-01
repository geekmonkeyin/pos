package com.gkmonk.pos.model.taskmgt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Document("taskmgt_attachment")
public class Attachment {
    @Id
    private String id;
    private String filename;
    private String data;       // base64
    private String uploadedAt; // ISO string (to match python)
    private String uploadedBy; // username
    private String taskId;
    private String contentType;
    private int size;
    private String dataBase64;


}
