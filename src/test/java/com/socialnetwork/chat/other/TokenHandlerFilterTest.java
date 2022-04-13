package com.socialnetwork.chat.other;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.socialnetwork.chat.TestUtils;
import com.socialnetwork.chat.config.security.TokenHandlerFilter;
import com.socialnetwork.chat.config.security.UserSecurity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@RunWith(MockitoJUnitRunner.class)
class TokenHandlerFilterTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Mock
    private RestTemplate restTemplate;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String jwtToken;


    @BeforeEach
    void setUp() {
        jwtToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIn0.ALK5_VD2qNA7os_yUdhOSaARty2NkqYhx0Q5zWelBwU";
        var tokenHandlerFilter = new TokenHandlerFilter(resolver, restTemplate);
        ReflectionTestUtils.setField(tokenHandlerFilter, "url", TestUtils.BASE_URL);
        ReflectionTestUtils.setField(tokenHandlerFilter, "allowedNotAuthEndpoints", new String[]{"/not-auth-endpoint"});
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
            .addFilter(tokenHandlerFilter)
            .build();
    }

    @Test
    void testJwtTokenHandlerFilter_successAuthorizeEndpoint() throws Exception {
        var dto = new TestDto();
        dto.setText("some text");
        var userId = "38bcd488-2d2b-4f50-976b-cae650f6a3f0";

        when(restTemplate.exchange(TestUtils.BASE_URL + TestUtils.USER_ID_BY_TOKEN_ENDPOINT, HttpMethod.GET, getTokenEntity(jwtToken), String.class))
            .thenReturn(TestUtils.getResponseEntityText(userId));

        mockMvc.perform(get("/test_method")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text").value("some text"))
            .andExpect(jsonPath("$.userId").value(userId))
            .andExpect(jsonPath("$.*", hasSize(2)));

        verify(restTemplate).exchange(TestUtils.BASE_URL + TestUtils.USER_ID_BY_TOKEN_ENDPOINT, HttpMethod.GET, getTokenEntity(jwtToken), String.class);
    }

    @Test
    void testJwtTokenHandlerFilter_successNotAuthorizeEndpoint() throws Exception {
        var dto = new TestDto();
        dto.setText("some text");

        when(restTemplate.exchange(any(), any(), any(), eq(String.class)))
            .thenReturn(null);

        mockMvc.perform(get("/not-auth-endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text").value("some text"))
            .andExpect(jsonPath("$.userId", nullValue()))
            .andExpect(jsonPath("$.*", hasSize(2)));

        verify(restTemplate, never()).exchange(any(), any(), any(), eq(String.class));
    }

    @Test
    void testJwtTokenHandlerFilter_ifTokenIsNotExist() throws Exception {
        var dto = new TestDto();
        dto.setText("some text");

        when(restTemplate.exchange(any(), any(), any(), eq(String.class)))
            .thenReturn(null);

        mockMvc.perform(get("/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isUnauthorized());

        verify(restTemplate, never()).exchange(any(), any(), any(), eq(String.class));
    }

    @Test
    void testJwtTokenHandlerFilter_ifUnexpectedException() throws Exception {
        var dto = new TestDto();
        dto.setText("some text");

        when(restTemplate.exchange(TestUtils.BASE_URL + TestUtils.USER_ID_BY_TOKEN_ENDPOINT, HttpMethod.GET, getTokenEntity(jwtToken), String.class))
            .thenThrow(new RestClientException("server error"));

        mockMvc.perform(get("/test")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isForbidden());

        verify(restTemplate).exchange(TestUtils.BASE_URL + TestUtils.USER_ID_BY_TOKEN_ENDPOINT, HttpMethod.GET, getTokenEntity(jwtToken), String.class);
    }


    HttpEntity<String> getTokenEntity(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtToken);
        return new HttpEntity<>(null, headers);
    }

    @RestController
    public static class TestController {

        @GetMapping("/test_method")
        public TestDto testMethod(
            @RequestBody TestDto testDto
            ) {
            var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            testDto.setUserId(((UserSecurity) principal).getUserId());
            return testDto;
        }

        @GetMapping("/not-auth-endpoint")
        public TestDto testMethod2(
            @RequestBody TestDto testDto
        ) {
            return testDto;
        }
    }

    @Data
    @NoArgsConstructor
    public static class TestDto {
        private String text;
        private String userId;
    }
}
