package com.salihguneyin.papertradehub;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PaperTradeHubApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void dashboardEndpointReturnsSeededMetrics() throws Exception {
        mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary.length()").value(4))
                .andExpect(jsonPath("$.recentTrades.length()").value(3));
    }

    @Test
    void createAssetAddsANewRecord() throws Exception {
        String payload = """
                {
                  "symbol": "AVAX",
                  "name": "Avalanche",
                  "assetClass": "Crypto",
                  "currentPrice": 38.40,
                  "dailyChangePercent": 1.15,
                  "thesis": "Watching for compression breakout near weekly range high.",
                  "active": true
                }
                """;

        String response = mockMvc.perform(post("/api/assets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.symbol").value("AVAX"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(response).contains("Avalanche");
    }
}
