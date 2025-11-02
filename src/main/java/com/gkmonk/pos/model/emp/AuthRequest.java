package com.gkmonk.pos.model.emp;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

public record AuthRequest(@JsonProperty("password") @NotBlank String password) {}

