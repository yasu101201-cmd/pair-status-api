package com.example.pairstatusapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PairFlowTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void pair_flow_should_work() throws Exception {

        // 1) ユーザーA作成
        String resA = mockMvc.perform(post("/users"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonA = objectMapper.readTree(resA);
        String tokenA = jsonA.get("token").asText();

        // 2) ユーザーB作成
        String resB = mockMvc.perform(post("/users"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode jsonB = objectMapper.readTree(resB);
        String tokenB = jsonB.get("token").asText();

        // 3) Aがcreate
        String createRes = mockMvc.perform(
                        post("/pairs/create")
                                .header("Authorization", "Bearer " + tokenA)
                )
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode createJson = objectMapper.readTree(createRes);
        String joinCode = createJson.get("joinCode").asText();

        // 4) A status → WAITING
        mockMvc.perform(
                        get("/pairs/status")
                                .header("Authorization", "Bearer " + tokenA)
                )
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("WAITING"));

        // 5) B join
        mockMvc.perform(
                        post("/pairs/join")
                                .param("joinCode", joinCode)
                                .header("Authorization", "Bearer " + tokenB)
                )
                .andExpect(status().isOk());

        // 6) A/B status → PAIRED
        mockMvc.perform(
                        get("/pairs/status")
                                .header("Authorization", "Bearer " + tokenA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("PAIRED"));

        mockMvc.perform(
                        get("/pairs/status")
                                .header("Authorization", "Bearer " + tokenB)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("PAIRED"));

        // 7) B leave
        mockMvc.perform(
                        post("/pairs/leave")
                                .header("Authorization", "Bearer " + tokenB)
                )
                .andExpect(status().isOk());

        // 8) A status → WAITING に戻る
        mockMvc.perform(
                        get("/pairs/status")
                                .header("Authorization", "Bearer " + tokenA)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("WAITING"));
    }
}