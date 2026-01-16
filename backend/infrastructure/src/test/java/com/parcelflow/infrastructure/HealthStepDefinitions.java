package com.parcelflow.infrastructure;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class HealthStepDefinitions {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> lastResponse;
    private String domainPomContent;

    @Given("the Spring Boot application context is loaded")
    public void the_spring_boot_application_context_is_loaded() {
        assertThat(restTemplate).isNotNull();
    }

    @When("I request the status from the {string} endpoint")
    public void i_request_the_status_from_the_endpoint(String endpoint) {
        String path = "/api/" + endpoint.replace("health-check", "health");
        lastResponse = restTemplate.getForEntity(path, String.class);
    }

    @Then("I should receive a 200 OK response")
    public void i_should_receive_a_200_ok_response() {
        assertThat(lastResponse.getStatusCode().value()).isEqualTo(200);
    }

    @Then("the response body should contain {string}")
    public void the_response_body_should_contain(String expectedContent) {
        assertThat(lastResponse.getBody()).contains(expectedContent);
    }

    @Given("the project structure")
    public void the_project_structure() {
        // Assumes running from project root or infrastructure module
    }

    @When("I analyze the dependencies of the {string} module")
    public void i_analyze_the_dependencies_of_the_module(String moduleName) throws IOException {
        // Robustly finding the domain pom by locating project root first
        java.nio.file.Path currentPath = Paths.get("").toAbsolutePath();
        java.nio.file.Path rootPath = currentPath;
        
        // Walk up until we find .git or mvnw, indicating project root
        while (rootPath != null && !Files.exists(rootPath.resolve(".git")) && !Files.exists(rootPath.resolve("mvnw"))) {
            rootPath = rootPath.getParent();
        }

        if (rootPath == null) {
            // Fallback if root not found (unlikely in valid project)
            rootPath = Paths.get("../.."); 
        }

        // Handle case where root might be the 'backend' folder itself or the monorepo root
        java.nio.file.Path domainPomPath = rootPath.resolve("backend/domain/pom.xml");
        if (!Files.exists(domainPomPath)) {
             // If we are already in backend root
             domainPomPath = rootPath.resolve("domain/pom.xml");
        }
        
        assertThat(domainPomPath).exists();
        domainPomContent = Files.readString(domainPomPath);
    }

    @Then("it should only depend on standard Java libraries")
    public void it_should_only_depend_on_standard_java_libraries() {
        assertThat(domainPomContent).doesNotContain("org.springframework");
        assertThat(domainPomContent).doesNotContain("jakarta.persistence");
        assertThat(domainPomContent).doesNotContain("spring-boot-starter");
    }
    
    @Then("it should NOT depend on {string}")
    public void it_should_not_depend_on(String dependency) {
        assertThat(domainPomContent).doesNotContain(dependency);
    }
}