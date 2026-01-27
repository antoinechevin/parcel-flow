package com.parcelflow.steps;

import com.parcelflow.application.usecases.ExtractParcelUseCase;
import com.parcelflow.domain.model.Parcel;
import com.parcelflow.domain.model.ParcelMetadata;
import com.parcelflow.domain.ports.ParcelExtractionPort;
import com.parcelflow.domain.ports.ParcelRepositoryPort;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ParcelExtractionSteps {

    @Autowired
    private ExtractParcelUseCase useCase;

    @Autowired
    private ParcelRepositoryPort repository;

    @Autowired
    private ParcelExtractionPort extractionPort;

    private String currentEmailContent;

    @Given("un email avec le contenu {string}")
    public void unEmailAvecLeContenu(String content) {
        this.currentEmailContent = content;
    }

    @When("le système traite cet email pour l'extraction")
    public void leSystemeTraiteCetEmailPourLExtraction() {
        // On configure le mock pour simuler l'extraction réussie
        // Dans un vrai test ATDD, on pourrait aussi tester l'intégration avec Gemini via WireMock,
        // mais ici on se concentre sur l'orchestration du Use Case.
        
        // On infère des métadonnées basées sur le contenu pour le mock
        ParcelMetadata metadata = null;
        if (currentEmailContent.contains("UPS")) {
            metadata = new ParcelMetadata(
                "1Z12345", "UPS", LocalDate.parse("2026-02-15"), "Relais Colis"
            );
        }
        
        when(extractionPort.extract(currentEmailContent)).thenReturn(Optional.ofNullable(metadata));
        
        useCase.execute(currentEmailContent);
    }

    @Then("un nouveau colis devrait être créé avec les informations suivantes:")
    public void unNouveauColisDevraitEtreCreeAvecLesInformationsSuivantes(List<Map<String, String>> dataTable) {
        Map<String, String> expected = dataTable.getFirst();
        
        List<Parcel> parcels = repository.findAll();
        Parcel parcel = parcels.stream()
            .filter(p -> p.trackingNumber().equals(expected.get("trackingNumber")))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Parcel not found"));
        
        assertEquals(expected.get("carrier"), parcel.carrier());
        assertEquals(LocalDate.parse(expected.get("deadline")), parcel.deadline());
    }
}
