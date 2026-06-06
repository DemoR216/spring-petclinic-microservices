package org.springframework.samples.petclinic.genai;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Placeholder REST controller for the GenAI chat endpoint.
 * Returns canned responses until Spring AI integration is available (requires Spring Boot 3+).
 *
 * @author Oded Shopen
 */
@RestController
public class PetclinicChatClient {

    private static final Logger LOG = LoggerFactory.getLogger(PetclinicChatClient.class);

    @PostMapping("/chatclient")
    public String chatclient(@RequestBody String query) {
        LOG.info("Received chat query: {}", query);
        return "This is a placeholder response from the GenAI service. "
            + "Spring AI integration requires Spring Boot 3+. "
            + "Your query was: " + query;
    }

    @PostMapping("/api/genai/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String query = request.getOrDefault("message", "");
        LOG.info("Received chat API query: {}", query);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "This is a placeholder response from the GenAI service. "
            + "Spring AI integration requires Spring Boot 3+.");
        response.put("query", query);
        response.put("status", "ok");
        return ResponseEntity.ok(response);
    }
}
