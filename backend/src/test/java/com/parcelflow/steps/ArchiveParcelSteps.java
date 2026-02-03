package com.parcelflow.steps;

import com.parcelflow.application.usecases.ArchiveParcelUseCase;
import com.parcelflow.domain.model.BarcodeType;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelId;
import com.parcelflow.domain.model.ParcelStatus;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ArchiveParcelSteps {

    @Autowired
    private ArchiveParcelUseCase archiveParcelUseCase;

    @Autowired
    private ParcelRepositoryPort parcelRepository;

    @Given("un colis avec le numéro de suivi {string} existe")
    public void un_colis_avec_le_numero_de_suivi_existe(String trackingNumber) {
        Parcel parcel = new Parcel(
                ParcelId.random(),
                trackingNumber,
                "TEST_CARRIER",
                LocalDate.now().plusDays(10),
                ParcelStatus.AVAILABLE,
                null,
                null,
                null,
                BarcodeType.NONE
        );
        parcelRepository.save(parcel);
    }

    @When("j'archive le colis {string}")
    public void j_archive_le_colis(String trackingNumber) {
        archiveParcelUseCase.archive(trackingNumber);
    }

    @Then("le colis {string} doit avoir le statut {string} en base de données")
    public void le_colis_doit_avoir_le_statut_en_base_de_donnees(String trackingNumber, String status) {
        Optional<Parcel> parcel = parcelRepository.findByTrackingNumber(trackingNumber);
        assertTrue(parcel.isPresent());
        assertEquals(ParcelStatus.valueOf(status), parcel.get().status());
    }
}
