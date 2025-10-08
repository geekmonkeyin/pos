package com.gkmonk.pos.model.returns;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
class ImagePayload {
    /** e.g. GMK-2025-000123_2025-10-06_19-05_EMP-102_img01.jpg */
    @NotBlank
    private String name;

    /** data URL (“data:image/jpeg;base64,...”) or raw base64 — decide in controller */
    @NotBlank
    private String data;

    public ImagePayload() {}
    public ImagePayload(String name, String data) { this.name = name; this.data = data; }

}
