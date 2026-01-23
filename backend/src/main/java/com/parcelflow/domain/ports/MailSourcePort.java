package com.parcelflow.domain.ports;

import com.parcelflow.domain.model.MailFetchResult;
import java.time.ZonedDateTime;

public interface MailSourcePort {
    MailFetchResult fetchEmails(ZonedDateTime since, String query);
}
