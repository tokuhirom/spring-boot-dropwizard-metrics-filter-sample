package me.geso.spring.boot.actuate.richmetrics;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringBootStatsApplication.class)
@WebIntegrationTest({"server.port=0", "management.port=0"})
public class SpringBootStatsApplicationTests {
    private MockMvc mockMvc;
    @Value("${local.server.port}")
    int serverPort;
    @Value("${local.management.port}")
    int mgmtPort;


    RestTemplate template = new TestRestTemplate();


    @Test
    public void contextLoads() throws Exception {
        ResponseEntity<String> forEntity = template.getForEntity("http://127.0.0.1:" + serverPort + "/hello/1", String.class);
        assertThat(forEntity.getBody())
                .isEqualTo("Hello 1");

        ResponseEntity<SSHash> entity = template.getForEntity("http://127.0.0.1:" + mgmtPort + "/metrics", SSHash.class);
        assertThat(entity.getStatusCode().is2xxSuccessful())
                .isTrue();
        System.out.println(entity.getBody());
    }

    public static class SSHash extends HashMap<String, String> {
    }

}
