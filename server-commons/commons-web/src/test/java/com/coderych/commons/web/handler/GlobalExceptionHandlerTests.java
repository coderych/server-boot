package com.coderych.commons.web.handler;

import com.coderych.commons.core.enums.ResultCode;
import com.coderych.commons.core.exception.BizException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTests {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldHandleBizException() throws Exception {
        mockMvc.perform(get("/biz-exception"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ResultCode.USER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.USER_NOT_FOUND.getMessage()));
    }

    @Test
    void shouldHandleBizExceptionWithCustomMessage() throws Exception {
        mockMvc.perform(get("/biz-exception-custom"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ResultCode.BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value("自定义业务错误"));
    }

    @Test
    void shouldHandleMethodArgumentNotValidException() throws Exception {
        mockMvc.perform(post("/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ResultCode.BAD_REQUEST.getCode()));
    }

    @Test
    void shouldHandleHttpMessageNotReadableException() throws Exception {
        mockMvc.perform(post("/read-body")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ResultCode.BAD_REQUEST.getCode()))
                .andExpect(jsonPath("$.message").value("请求体格式错误"));
    }

    @Test
    void shouldHandleGenericException() throws Exception {
        mockMvc.perform(get("/generic-exception"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(ResultCode.ERROR.getCode()))
                .andExpect(jsonPath("$.message").value(ResultCode.ERROR.getMessage()));
    }

    @RestController
    static class TestController {

        @GetMapping("/biz-exception")
        void throwBizException() {
            throw new BizException(ResultCode.USER_NOT_FOUND);
        }

        @GetMapping("/biz-exception-custom")
        void throwBizExceptionWithCustomMessage() {
            throw new BizException(ResultCode.BAD_REQUEST, "自定义业务错误");
        }

        @PostMapping("/validation")
        void validate(@Valid @RequestBody TestDto dto) {
        }

        @PostMapping("/read-body")
        void readBody(@RequestBody TestDto dto) {
        }

        @GetMapping("/generic-exception")
        void throwGenericException() {
            throw new RuntimeException("unexpected error");
        }
    }

    static class TestDto {

        @NotBlank(message = "名称不能为空")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
