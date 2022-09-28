package com.mmserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmserver.domain.SignupDto;
import com.mmserver.domain.model.User;
import com.mmserver.exception.DuplicationEmailException;
import com.mmserver.exception.DuplicationUserNameException;
import com.mmserver.repository.UserRepository;
import com.mmserver.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Transactional
public class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원가입 성공")
    public void givenSignupDto_whenSignup_thenSuccess() throws Exception {
        SignupDto dto = SignupDto.builder()
                .email("email@gmail.com")
                .password("password")
                .userName("userName")
                .build();

        this.mockMvc.perform(
                post("/signup")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("user/signup/success",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("사용자 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("사용자 비밀번호"),
                                fieldWithPath("userName").type(JsonFieldType.STRING)
                                        .description("사용자 이름")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("식별 값"),
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("사용자 이메일"),
                                fieldWithPath("userName").type(JsonFieldType.STRING)
                                        .description("사용자 이름"),
                                fieldWithPath("role").type(JsonFieldType.STRING)
                                        .description("사용자 역할"),
                                fieldWithPath("point").type(JsonFieldType.NUMBER)
                                        .description("사용가능 포인트"),
                                fieldWithPath("theme").type(JsonFieldType.NUMBER)
                                        .description("설정 태마 값")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    public void givenSignupDto_whenSignup_thenFailDuplecationEmail() throws Exception {
        User user = User.builder()
                .email("email@gmail.com")
                .password("password")
                .userName("userName")
                .build();

        userRepository.save(user);

        SignupDto dto = SignupDto.builder()
                .email("email@gmail.com")
                .password("password")
                .userName("userName")
                .build();

        doThrow(new DuplicationEmailException()).when(authService).signup(dto, null);

        this.mockMvc.perform(
                        post("/signup")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andDo(document("users/duplicateEmail/failure",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("사용자 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("사용자 비밀번호"),
                                fieldWithPath("userName").type(JsonFieldType.STRING)
                                        .description("사용자 이름")
                        )
                ));
    }

    @Test
    @DisplayName("회원가입 실패 - 사용자 이름 중복")
    public void givenSignupDto_whenSignup_thenFailDuplecationUserName() throws Exception {
        User user = User.builder()
                .email("email@gmail.com")
                .password("password")
                .userName("userName")
                .build();

        userRepository.save(user);

        SignupDto dto = SignupDto.builder()
                .email("email@gmail.com")
                .password("password")
                .userName("userName")
                .build();

        doThrow(new DuplicationUserNameException()).when(authService).signup(any(), any());

        this.mockMvc.perform(
                        post("/signup")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andDo(document("users/duplicateEmail/failure",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING)
                                        .description("사용자 이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("사용자 비밀번호"),
                                fieldWithPath("userName").type(JsonFieldType.STRING)
                                        .description("사용자 이름")
                        )
                ));
    }
}
