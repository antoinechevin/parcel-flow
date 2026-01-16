package com.parcelflow.domain.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParcelTest {

    @Test
    void should_create_parcel_with_valid_data() {
        ParcelId id = new ParcelId("TRK-123456");
        Parcel parcel = Parcel.create(id, "Mon Mac");

        assertThat(parcel.getId()).isEqualTo(id);
        assertThat(parcel.getLabel()).isEqualTo("Mon Mac");
        assertThat(parcel.getStatus()).isEqualTo(ParcelStatus.CREATED);
    }

    @Test
    void should_fail_creation_without_id() {
        assertThatThrownBy(() -> Parcel.create(null, "Label"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID required");
    }

    @Test
    void should_fail_creation_without_label() {
        ParcelId id = new ParcelId("TRK-123");
        assertThatThrownBy(() -> Parcel.create(id, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Label required");
        
        assertThatThrownBy(() -> Parcel.create(id, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Label required");
    }
}
