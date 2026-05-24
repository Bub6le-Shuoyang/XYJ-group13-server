package com.xyj.xyjserver;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class ApiUnitTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String authToken = "";

    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // 尝试获取Token
        String loginJson = "{\"account\":\"admin@example.com\",\"password\":\"MyPass123!\",\"role\":\"ADMIN\"}";
        try {
            MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(loginJson))
                    .andReturn();
            
            if (result.getResponse().getStatus() == 200) {
                JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
                if (root.has("data") && root.get("data").has("token")) {
                    authToken = root.get("data").get("token").asText();
                }
            }
        } catch (Exception e) {
            // Ignored
        }
    }

    @Test
    public void testAuthLogin() throws Exception {
        String loginJson = "{\"account\":\"admin@example.com\",\"password\":\"MyPass123!\",\"role\":\"ADMIN\"}";
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testSwaggerDoc() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetHelpCenter() throws Exception {
        mockMvc.perform(get("/api/v1/user/help-center")
                .header("Authorization", "Bearer dummy_token"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCustomerService() throws Exception {
        mockMvc.perform(get("/api/v1/user/customer-service")
                .header("Authorization", "Bearer dummy_token"))
                .andExpect(status().isOk());
    }
}
