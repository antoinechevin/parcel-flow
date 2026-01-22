package com.parcelflow.domain.model;

import java.time.ZonedDateTime;

public record InboundEmail(
    String id,
    String subject,
    String body,
    String sender,
    ZonedDateTime receivedAt
) {
}
