package io.gardenerframework.camellia.authentication.server.test.cases;

import io.gardenerframework.camellia.authentication.server.main.UsernamePasswordAuthenticationService;
import io.gardenerframework.camellia.authentication.server.main.schema.UserAuthenticationRequestToken;
import io.gardenerframework.camellia.authentication.server.main.user.schema.User;
import io.gardenerframework.camellia.authentication.server.test.UsernamePasswordAuthenticationServiceTestApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.UUID;

@SpringBootTest(classes = UsernamePasswordAuthenticationServiceTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({UsernamePasswordAuthenticationServiceTest.UsernamePasswordAuthenticationServiceTestEndpoint.class})
public class UsernamePasswordAuthenticationServiceTest {

    @LocalServerPort
    private int port;

    @Test
    public void smokeTest() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getForObject("http://localhost:{port}?username={username}&password={password}", void.class, port, UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    @RestController
    @RequestMapping("")
    public static class UsernamePasswordAuthenticationServiceTestEndpoint {
        @Autowired
        private UsernamePasswordAuthenticationService service;

        @GetMapping
        public void smokeTest(HttpServletRequest request) {
            UserAuthenticationRequestToken convert = service.convert(request, null, new HashMap<>());
            service.authenticate(convert, null, User.builder()
                    .id(UUID.randomUUID().toString())
                    .credential(convert.getCredentials())
                    .principal(convert.getPrincipal())
                    .name(UUID.randomUUID().toString())
                    .build(), new HashMap<>());
        }
    }
}
