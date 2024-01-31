package no.delalt.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.delalt.back.AbstractContainerBaseTest;
import no.delalt.back.Authentication;
import no.delalt.back.DelApplication;
import no.delalt.back.UserDAOBuilder;
import no.delalt.back.model.dao.UserDAO;
import no.delalt.back.model.dto.input.CommunityCreationDTO;
import no.delalt.back.service.CommunityService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(
  webEnvironment = SpringBootTest.WebEnvironment.MOCK,
  classes = DelApplication.class
)
public class CommunityControllerTest extends AbstractContainerBaseTest {
  @Autowired
  private WebApplicationContext context;

  private MockMvc mockMvc;

  @BeforeEach
  void setupMockMvc() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Autowired
  private DataSource dataSource;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private Authentication authentication;

  @MockBean
  private CommunityService communityService;

  UserDAO user;
  String userToken;

  @DynamicPropertySource
  static void databaseProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgisContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgisContainer::getUsername);
    registry.add("spring.datasource.password", postgisContainer::getPassword);
  }

  @BeforeAll
  void setup() throws SQLException {
    try (Connection conn = dataSource.getConnection()) {
      ScriptUtils.executeSqlScript(
        conn,
        new ClassPathResource("communityData.sql")
      );
    }
  }

  @BeforeEach
  void login() {
    user =
      new UserDAOBuilder()
        .withUserID("1")
        .withEmail("test14@email.com")
        .withFirstName("test")
        .withLastName("testesen")
        .withImage("ok")
        .withHash(
          "Ge7Y9frKWdgKcAysHdYCIoOOsAcn9We3f2+C74xlc6kWQZn2scBE8sEf4iZezwsmG/KdeeEuspZD9Q4Ojt27Hg=="
        )
        .build();
    userToken = authentication.authenticate(user).authToken();
  }

  @Test
  public void testAddCommunity() throws Exception {
    CommunityCreationDTO communityDTO = new CommunityCreationDTO(
      "Test community",
      "Test description",
      (short) 0,
      "Test location",
      null
    );

    mockMvc
      .perform(
        post("/community/")
          .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer " + userToken)
          .content(objectMapper.writeValueAsString(communityDTO))
      )
      .andExpect(status().isOk());
  }
}
