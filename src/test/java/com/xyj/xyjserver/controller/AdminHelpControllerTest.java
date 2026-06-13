package com.xyj.xyjserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyj.xyjserver.entity.HelpItem;
import com.xyj.xyjserver.service.AdminHelpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminHelpController.class)
class AdminHelpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AdminHelpService adminHelpService;

    private String adminToken;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        adminToken = com.xyj.xyjserver.common.util.JwtUtil.generateToken(1L, "ADMIN");
    }

    @Test
    void getAll_shouldReturnHelpItems() throws Exception {
        HelpItem item = new HelpItem();
        item.setId(1L);
        item.setTitle("如何取件");
        when(adminHelpService.getAll()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/admin/help")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("如何取件"));
    }

    @Test
    void create_shouldReturnCreatedItem() throws Exception {
        HelpItem created = new HelpItem();
        created.setId(2L);
        created.setTitle("新问题");
        when(adminHelpService.create(any(HelpItem.class))).thenReturn(created);

        HelpItem request = new HelpItem();
        request.setTitle("新问题");
        request.setContent("答案");

        mockMvc.perform(post("/api/v1/admin/help")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2));
    }

    @Test
    void delete_shouldReturnSuccess() throws Exception {
        doNothing().when(adminHelpService).delete(3L);

        mockMvc.perform(delete("/api/v1/admin/help/3")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
