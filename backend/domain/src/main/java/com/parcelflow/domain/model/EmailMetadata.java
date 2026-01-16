package com.parcelflow.domain.model;

import java.time.LocalDateTime;

public record EmailMetadata(
    String id,
    String subject,
    String snippet,
    LocalDateTime receivedAt
) {}
