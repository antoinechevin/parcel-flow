package com.parcelflow.steps;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class TestClock extends Clock {
    private Clock delegate;

    public TestClock() {
        this.delegate = Clock.systemDefaultZone();
    }

    public void setFixedDate(String dateIso) {
        this.delegate = Clock.fixed(Instant.parse(dateIso + "T10:00:00Z"), ZoneId.of("UTC"));
    }

    @Override
    public ZoneId getZone() {
        return delegate.getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return delegate.withZone(zone);
    }

    @Override
    public Instant instant() {
        return delegate.instant();
    }
}
