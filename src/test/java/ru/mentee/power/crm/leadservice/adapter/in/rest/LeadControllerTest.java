package ru.mentee.power.crm.leadservice.adapter.in.rest;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadCreateRequest;
import ru.mentee.power.crm.leadservice.adapter.in.rest.dto.LeadResponse;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.repository.LeadJpaRepository;

@SpringBootTest
@Testcontainers
@AutoConfigureWebMvc
@Rollback
class LeadControllerTest {

  private WireMockServer wireMockServer;

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"))
          .withDatabaseName("leadservice")
          .withUsername("test")
          .withPassword("test");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("app.contact-service.url", () -> "http://localhost:8083");
  }

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private LeadJpaRepository leadJpaRepository;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {

    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    leadJpaRepository.deleteAll();

    wireMockServer = new WireMockServer(8083);
    wireMockServer.start();

    wireMockServer.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/api/v1/persons"))
            .withQueryParam("email", WireMock.equalTo("existing@example.com"))
            .willReturn(
                WireMock.okJson(
                        "{\"content\":[{"
                            + "\"id\":\"123e4567-e89b-12d3-a456-426614174000\","
                            + "\"fullName\":\"Existing Person\","
                            + "\"email\":\"existing@example.com\","
                            + "\"phone\":null,"
                            + "\"createdAt\":\"2026-05-19T18:00:00Z\","
                            + "\"updatedAt\":\"2026-05-19T18:00:00Z\""
                            + "}],"
                            + "\"page\":0,\"size\":1,\"totalElements\":1,\"totalPages\":1}")
                    .withHeader("Content-Type", "application/json")));

    wireMockServer.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/api/v1/persons"))
            .withQueryParam("email", WireMock.equalTo("notfound@example.com"))
            .willReturn(
                WireMock.okJson(
                        "{\"content\":[],\"page\":0,\"size\":0,\"totalElements\":0,\"totalPages\":0}")
                    .withHeader("Content-Type", "application/json")));

    wireMockServer.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/api/v1/persons"))
            .withQueryParam("email", WireMock.equalTo("only-email@example.com"))
            .willReturn(
                WireMock.okJson(
                        "{\"content\":[],\"page\":0,\"size\":0,\"totalElements\":0,\"totalPages\":0}")
                    .withHeader("Content-Type", "application/json")));

    wireMockServer.stubFor(
        WireMock.get(WireMock.urlPathEqualTo("/api/v1/persons"))
            .withQueryParam("email", WireMock.equalTo("new@example.com"))
            .willReturn(
                WireMock.okJson(
                        "{\"content\":[],\"page\":0,\"size\":0,\"totalElements\":0,\"totalPages\":0}")
                    .withHeader("Content-Type", "application/json")));

    wireMockServer.stubFor(
        WireMock.post(WireMock.urlPathEqualTo("/api/v1/persons"))
            .withRequestBody(containing("only-email@example.com"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\": \"123e4567-e89b-12d3-a456-426614174002\", "
                            + "\"fullName\": null, "
                            + "\"email\": \"only-email@example.com\", "
                            + "\"phone\": null, "
                            + "\"createdAt\": \"2026-05-19T18:00:00Z\", "
                            + "\"updatedAt\": \"2026-05-19T18:00:00Z\"}")));

    wireMockServer.stubFor(
        WireMock.post(WireMock.urlPathEqualTo("/api/v1/persons"))
            .withRequestBody(containing("new@example.com"))
            .willReturn(
                aResponse()
                    .withStatus(201)
                    .withHeader("Content-Type", "application/json")
                    .withBody(
                        "{\"id\": \"123e4567-e89b-12d3-a456-426614174001\", "
                            + "\"fullName\": \"New Person\", "
                            + "\"email\": \"new@example.com\", "
                            + "\"phone\": null, "
                            + "\"createdAt\": \"2026-05-19T18:00:00Z\", "
                            + "\"updatedAt\": \"2026-05-19T18:00:00Z\"}")));
  }

  @AfterEach
  void tearDown() {
    if (wireMockServer != null) {
      wireMockServer.stop();
    }
  }

  @Test
  void createLeadWithValidRequestShouldReturn201() throws Exception {
    LeadCreateRequest request = new LeadCreateRequest();
    request.setTitle("Test Lead");
    request.setSource("WEBSITE");
    request.setPersonId(UUID.randomUUID());

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("Test Lead"))
        .andExpect(jsonPath("$.status").value("NEW"))
        .andExpect(jsonPath("$.personId").exists());
  }

  @Test
  void createLeadWithoutTitleShouldReturn400() throws Exception {
    LeadCreateRequest request = new LeadCreateRequest();
    request.setSource("WEB_FORM");
    request.setPersonId(UUID.randomUUID());

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createLeadWithoutPersonIdShouldReturn400() throws Exception {
    LeadCreateRequest request = new LeadCreateRequest();
    request.setTitle("Test Lead");
    request.setSource("WEBSITE");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getLeadByIdWithExistingIdShouldReturn200() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Test Lead");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/leads/" + leadId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(leadId.toString()))
        .andExpect(jsonPath("$.title").value("Test Lead"));
  }

  @Test
  void getLeadByIdWithNonExistingIdShouldReturn404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/leads/" + UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateLeadWithValidRequestShouldReturn200() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Original Title");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    String updateJson = "{ \"title\": \"Updated Title\", \"description\": \"New Description\" }";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/v1/leads/" + leadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"))
        .andExpect(jsonPath("$.description").value("New Description"));
  }

  @Test
  void updateLeadWithEmptyTitleShouldReturn400() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Original Title");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    String updateJson = "{ \"title\": \"\" }";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/v1/leads/" + leadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateLeadWithNonExistingIdShouldReturn404() throws Exception {
    String updateJson = "{ \"title\": \"Updated Title\" }";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/v1/leads/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteLeadWithExistingIdShouldReturn204() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("To Delete");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/v1/leads/" + leadId))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/leads/" + leadId))
        .andExpect(status().isNotFound());
  }

  @Test
  void deleteLeadWithNonExistingIdShouldReturn404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/api/v1/leads/" + UUID.randomUUID()))
        .andExpect(status().isNotFound());
  }

  @Test
  void changeStatusWithInvalidTransitionShouldReturn409() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Test Lead");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("NEW"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    String statusJson = "{ \"status\": \"QUALIFIED\" }";

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/v1/leads/" + leadId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusJson))
        .andExpect(status().isConflict());
  }

  @Test
  void changeStatusWithNonExistingIdShouldReturn404() throws Exception {
    String statusJson = "{ \"status\": \"CONTACTED\" }";

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/v1/leads/" + UUID.randomUUID() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusJson))
        .andExpect(status().isNotFound());
  }

  @Test
  void changeStatusWithValidTransitionFromNewToContactedShouldReturn200() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Test Lead");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    String statusJson = "{ \"status\": \"CONTACTED\" }";

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/v1/leads/" + leadId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(statusJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("CONTACTED"));
  }

  @Test
  void changeStatusFromContactedToQualifiedShouldReturn409() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Test Lead");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/v1/leads/" + leadId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\": \"CONTACTED\" }"))
        .andExpect(status().isOk());

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/v1/leads/" + leadId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\": \"QUALIFIED\" }"))
        .andExpect(status().isConflict());
  }

  @Test
  void changeStatusWithInvalidStatusValueShouldReturn400() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Test Lead");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    String invalidStatusJson = "{ \"status\": \"INVALID_STATUS\" }";

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/v1/leads/" + leadId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidStatusJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void updateLeadWithNullDescriptionShouldReturn200() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Original Title");
    createRequest.setDescription("Original Description");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    String updateJson = "{ \"title\": \"Updated Title\" }";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/v1/leads/" + leadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Title"))
        .andExpect(jsonPath("$.description").value("Original Description"));
  }

  @Test
  void updateLeadWithNullTitleShouldReturn400() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Original");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    String updateJson = "{}";

    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/api/v1/leads/" + leadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void changeStatusWithMalformedJsonShouldReturn400() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Test");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    String malformedJson = "{ \"status\": \"INVALID_STATUS\" }";

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/v1/leads/" + leadId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void changeStatusFromNewToDisqualifiedShouldReturn200() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Test");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    mockMvc
        .perform(
            MockMvcRequestBuilders.patch("/api/v1/leads/" + leadId + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"status\": \"DISQUALIFIED\" }"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DISQUALIFIED"));
  }

  @Test
  void listLeadsWithDefaultPaginationShouldReturnPage() throws Exception {
    UUID personId = UUID.randomUUID();
    for (int i = 0; i < 25; i++) {
      createTestLead("Lead " + i, "WEBSITE", personId);
    }

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/leads"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(20))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(20))
        .andExpect(jsonPath("$.totalElements").value(25))
        .andExpect(jsonPath("$.totalPages").value(2));
  }

  @Test
  void listLeadsWithCustomPageAndSizeShouldReturnCorrectPage() throws Exception {
    UUID personId = UUID.randomUUID();
    for (int i = 0; i < 15; i++) {
      createTestLead("Lead " + i, "WEBSITE", personId);
    }

    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/leads").param("page", "1").param("size", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(5))
        .andExpect(jsonPath("$.page").value(1))
        .andExpect(jsonPath("$.size").value(5))
        .andExpect(jsonPath("$.totalElements").value(15))
        .andExpect(jsonPath("$.totalPages").value(3));
  }

  @Test
  void listLeadsWithPageOutOfRangeShouldReturnEmptyPage() throws Exception {
    UUID personId = UUID.randomUUID();
    createTestLead("Test Lead", "WEBSITE", personId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get("/api/v1/leads").param("page", "10").param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.page").value(10))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));
  }

  private void createTestLead(String title, String source, UUID personId) throws Exception {
    LeadCreateRequest request = new LeadCreateRequest();
    request.setTitle(title);
    request.setSource(source);
    request.setPersonId(personId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
  }

  @Test
  void listLeadsWithEmptyDatabaseShouldReturnEmptyPage() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/api/v1/leads"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(0))
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.totalElements").value(0))
        .andExpect(jsonPath("$.totalPages").value(0));
  }

  @Test
  void createLeadWithPersonIdShouldNotCallDeDuplication() throws Exception {
    UUID existingPersonId = UUID.randomUUID();

    LeadCreateRequest request = new LeadCreateRequest();
    request.setTitle("Test Lead with PersonId");
    request.setSource("WEBSITE");
    request.setPersonId(existingPersonId);
    request.setEmail("should-be-ignored@example.com");
    request.setFullName("Should Be Ignored");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("Test Lead with PersonId"))
        .andExpect(jsonPath("$.status").value("NEW"))
        .andExpect(jsonPath("$.personId").value(existingPersonId.toString()));
  }

  @Test
  void createLeadWithoutPersonIdAndEmailShouldReturn400() throws Exception {
    LeadCreateRequest request = new LeadCreateRequest();
    request.setTitle("Test Lead");
    request.setSource("WEBSITE");

    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
