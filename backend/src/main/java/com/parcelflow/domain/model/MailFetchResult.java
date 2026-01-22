package com.parcelflow.domain.model;

import java.time.ZonedDateTime;
import java.util.List;

public record MailFetchResult(
    List<InboundEmail> emails,
    ZonedDateTime newWatermark
) {
}
