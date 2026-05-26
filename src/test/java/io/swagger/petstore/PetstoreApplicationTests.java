package io.swagger.petstore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "server.servlet.context-path=/")
class PetstoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void contextLoads() {}

    @Test
    void addAndGetPetRoundtrip() throws Exception {
        String body = """
            {
              "id": 500,
              "name": "fido",
              "photoUrls": ["http://example.com/fido.png"],
              "status": "available"
            }""";

        MvcResult created = mockMvc.perform(post("/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(500))
                .andReturn();

        JsonNode node = mapper.readTree(created.getResponse().getContentAsString());
        long id = node.get("id").asLong();

        mockMvc.perform(get("/pet/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("fido"))
                .andExpect(jsonPath("$.status").value("available"));
    }

    @Test
    void findByStatusReturnsArray() throws Exception {
        mockMvc.perform(get("/pet/findByStatus").param("status", "available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void inventoryReturnsMap() throws Exception {
        mockMvc.perform(get("/store/inventory"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").exists());
    }

    @Test
    void missingPetReturns404() throws Exception {
        mockMvc.perform(get("/pet/{id}", 99999999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePetReturns200ThenGone() throws Exception {
        mockMvc.perform(post("/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"id":600,"name":"tempPet","photoUrls":["http://x.com"],"status":"pending"}
                            """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/pet/600"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/pet/600"))
                .andExpect(status().isNotFound());
    }

    @Test
    void placeAndGetAndDeleteOrder() throws Exception {
        MvcResult result = mockMvc.perform(post("/store/order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"id":0,"petId":1,"quantity":1,"status":"placed","complete":false}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("placed"))
                .andReturn();

        long orderId = mapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        mockMvc.perform(get("/store/order/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId));

        mockMvc.perform(delete("/store/order/{id}", orderId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/store/order/{id}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePetReflectsChanges() throws Exception {
        mockMvc.perform(post("/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"id":700,"name":"OldName","photoUrls":["http://x.com"],"status":"available"}
                            """))
                .andExpect(status().isOk());

        mockMvc.perform(put("/pet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"id":700,"name":"NewName","photoUrls":["http://x.com"],"status":"sold"}
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.status").value("sold"));
    }
}