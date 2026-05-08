package ru.mentee.power.crm.leadservice.adapter.in.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import ru.mentee.power.crm.leadservice.adapter.out.persistence.repository.LeadJpaRepository;

@SpringBootTest
@Testcontainers
@AutoConfigureWebMvc
@Rollback
class LeadControllerTest {

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
  }

  @Autowired private WebApplicationContext webApplicationContext;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private LeadJpaRepository leadJpaRepository;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    leadJpaRepository.deleteAll();
  }

  @Test
  void createLead_withValidRequest_shouldReturn201() throws Exception {
    LeadCreateRequest request = new LeadCreateRequest();
    request.setTitle("Test Lead");
    request.setSource("WEBSITE");
    request.setPersonId(UUID.randomUUID());

    mockMvc
        .perform(
            post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("Test Lead"))
        .andExpect(jsonPath("$.status").value("NEW"))
        .andExpect(jsonPath("$.personId").exists());
  }

  @Test
  void createLead_withoutTitle_shouldReturn400() throws Exception {
    LeadCreateRequest request = new LeadCreateRequest();
    request.setSource("WEB_FORM");
    request.setPersonId(UUID.randomUUID());

    mockMvc
        .perform(
            post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createLead_withEmptyTitle_shouldReturn400() throws Exception {
    LeadCreateRequest request = new LeadCreateRequest();
    request.setTitle("");
    request.setSource("WEB_FORM");
    request.setPersonId(UUID.randomUUID());

    mockMvc
        .perform(
            post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createLead_withoutPersonId_shouldReturn400() throws Exception {
    LeadCreateRequest request = new LeadCreateRequest();
    request.setTitle("Test Lead");
    request.setSource("WEBSITE");

    mockMvc
        .perform(
            post("/api/v1/leads")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createLead_withInvalidSource_shouldReturn400() throws Exception {
    String invalidJson =
        String.format(
            """
        {
          "title": "Test Lead",
          "source": "INVALID_SOURCE",
          "personId": "%s"
        }
        """,
            UUID.randomUUID());

    mockMvc
        .perform(post("/api/v1/leads").contentType(MediaType.APPLICATION_JSON).content(invalidJson))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getLeadById_withExistingId_shouldReturn200() throws Exception {
    LeadCreateRequest createRequest = new LeadCreateRequest();
    createRequest.setTitle("Test Lead");
    createRequest.setSource("WEBSITE");
    createRequest.setPersonId(UUID.randomUUID());

    String response =
        mockMvc
            .perform(
                post("/api/v1/leads")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    LeadResponse createdLead = objectMapper.readValue(response, LeadResponse.class);
    UUID leadId = createdLead.getId();

    mockMvc
        .perform(get("/api/v1/leads/" + leadId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(leadId.toString()))
        .andExpect(jsonPath("$.title").value("Test Lead"));
  }

  @Test
  void getLeadById_withNonExistingId_shouldReturn404() throws Exception {
    mockMvc.perform(get("/api/v1/leads/" + UUID.randomUUID())).andExpect(status().isNotFound());
  }
}
