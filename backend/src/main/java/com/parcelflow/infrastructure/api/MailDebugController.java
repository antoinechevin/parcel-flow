package com.parcelflow.infrastructure.api;

import com.parcelflow.domain.model.MailFetchResult;
import com.parcelflow.domain.ports.MailSourcePort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/debug/mails")
public class MailDebugController {

    private final MailSourcePort mailSourcePort;

    public MailDebugController(MailSourcePort mailSourcePort) {
        this.mailSourcePort = mailSourcePort;
    }

    @GetMapping("/fetch")
    public MailFetchResult fetch(@RequestParam(required = false) String since) {
        ZonedDateTime sinceDate = (since != null) ? ZonedDateTime.parse(since) : ZonedDateTime.now().minusDays(1);
        return mailSourcePort.fetchEmails(sinceDate, null);
    }
}
