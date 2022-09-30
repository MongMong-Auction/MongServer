package com.mmserver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmserver.domain.LoginDto;
import com.mmserver.domain.SignupDto;
import com.mmserver.domain.UserInfoDto;
import com.mmserver.exception.DuplicationEmailException;
import com.mmserver.exception.DuplicationUserNameException;
import com.mmserver.exception.MisMatchPasswordException;
import com.mmserver.exception.NotFoundEmailException;
import com.mmserver.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletResponse;

import static com.mmserver.domain.EnumType.RoleType.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@MockBean(JpaMetamodelMappingContext.class)
public class AuthControllerTest {

    MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(sharedHttpSession())
                .build();
    }

    @Test
    @DisplayName("회원가입 성공")
    public void givenSignupDto_whenSignup_thenSuccess() throws Exception {
        final UserInfoDto userInfoDto = UserInfoDto.builder()
                .id(1L)
                .email("email@gmail.com")
                .userName("userName")
                .role(USER)
                .point(100)
                .theme(0)
                .build();

        final SignupDto dto = SignupDto.builder()
                .email("email@gmail.com")
                .password("password")
                .userName("userName")
                .build();

        when(authService.signup(any(SignupDto.class), any(HttpServletResponse.class))).thenReturn(userInfoDto);

        mockMvc.perform(
                post("/signup")
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document("users/signup/success",
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
    public void givenSignupDto_whenSignup_thenFailDuplecationEmailException() throws Exception {
        SignupDto dto = SignupDto.builder()
                .email("email@gmail.com")
                .password("password")
                .userName("userName1")
                .build();

        when(authService.signup(any(SignupDto.class), any(HttpServletResponse.class))).thenThrow(new DuplicationEmailException());

        mockMvc.perform(
                        post("/signup")
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andDo(document("users/signup/fail/duplicateEmail",
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
    @DisplayName("회원가입 실패 - 닉네임 중복")
    public void givenSignupDto_whenSignup_thenFailDuplecationUserNameException() throws Exception {
        SignupDto dto = SignupDto.builder()
                .email("email1@gmail.com")
                .password("password")
                .userName("userName")
                .build();

        when(authService.signup(any(SignupDto.class), any(HttpServletResponse.class))).thenThrow(new DuplicationUserNameException());

        mockMvc.perform(
                        post("/signup")
                                .characterEncoding("UTF-8")
                                .content(objectMapper.writeValueAsString(dto))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isConflict())
                .andDo(document("users/signup/fail/duplicateUserName",
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
    @DisplayName("이메일 중복 검사 - 사용가능")
    public void given_whenCheckedEmail_thenSuccess() throws Exception {
        final String email = "email@gmail.com";

        when(authService.isCheckedEmail(email)).thenReturn(false);

        mockMvc.perform(
                        get("/check/email")
                                .param("email", email)
                                .characterEncoding("UTF-8")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andDo(document("users/checkEmail/success",
                        requestParameters(
                                parameterWithName("email").description("사용자 이메일")
                        )
                ));
    }

    @Test
    @DisplayName("이메일 중복 검사 - 중복")
    public void given_whenCheckedEmail_thenFail() throws Exception {
        final String email = "email@gmail.com";

        when(authService.isCheckedEmail(email)).thenReturn(true);

        mockMvc.perform(
                        get("/check/email")
                                .param("email", email)
                                .characterEncoding("UTF-8")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("users/checkEmail/fail",
                        requestParameters(
                                parameterWithName("email").description("사용자 이메일")
                        )
                ));
    }

    @Test
    @DisplayName("닉네임 중복 검사 - 사용가능")
    public void given_whenCheckedUserName_thenSuccess() throws Exception {
        final String userName = "userName";

        when(authService.isCheckedUserName(userName)).thenReturn(false);

        mockMvc.perform(
                        get("/check/userName")
                                .param("userName", userName)
                                .characterEncoding("UTF-8")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"))
                .andDo(document("users/checkUserName/success",
                        requestParameters(
                                parameterWithName("userName").description("사용자 이름")
                        )
                ));
    }

    @Test
    @DisplayName("닉네임 중복 검사 - 중복")
    public void given_whenCheckedUserName_thenFail() throws Exception {
        final String userName = "userName";

        when(authService.isCheckedUserName(userName)).thenReturn(true);

        mockMvc.perform(
                        get("/check/userName")
                                .param("userName", userName)
                                .characterEncoding("UTF-8")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"))
                .andDo(document("users/checkUserName/fail",
                        requestParameters(
                                parameterWithName("userName").description("사용자 이름")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 성공")
    public void givenLoginDto_whenLogin_thenSuccess() throws Exception {
        final UserInfoDto userInfoDto = UserInfoDto.builder()
                .id(1L)
                .email("email@gmail.com")
                .userName("userName")
                .role(USER)
                .point(100)
                .theme(0)
                .build();

        when(authService.login(any(LoginDto.class), any(HttpServletResponse.class))).thenReturn(userInfoDto);

        mockMvc.perform(
                        get("/login")
                                .param("email", "email@gamil.com")
                                .param("password", "password")
                                .characterEncoding("UTF-8")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("users/login/success",
                        requestParameters(
                                parameterWithName("email").description("사용자 이메일"),
                                parameterWithName("password").description("사용자 비밀번호")
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
    @DisplayName("로그인 실패 - 잘못된 아이디")
    public void givenLoginDto_whenLogin_thenFailNotFoundEmail() throws Exception {
        when(authService.login(any(LoginDto.class), any(HttpServletResponse.class))).thenThrow(new NotFoundEmailException());

        mockMvc.perform(
                        get("/login")
                                .param("email", "email@gamil.com")
                                .param("password", "password")
                                .characterEncoding("UTF-8")
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("users/login/fail/notFoundEmail",
                        requestParameters(
                                parameterWithName("email").description("사용자 이메일"),
                                parameterWithName("password").description("사용자 비밀번호")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 패스워드")
    public void givenLoginDto_whenLogin_thenFailMisMatchPassword() throws Exception {
        when(authService.login(any(LoginDto.class), any(HttpServletResponse.class))).thenThrow(new MisMatchPasswordException());

        mockMvc.perform(
                        get("/login")
                                .param("email", "email@gamil.com")
                                .param("password", "password")
                                .characterEncoding("UTF-8")
                )
                .andDo(print())
                .andExpect(status().isNotFound())
                .andDo(document("users/login/fail/misMatchPassword",
                        requestParameters(
                                parameterWithName("email").description("사용자 이메일"),
                                parameterWithName("password").description("사용자 비밀번호")
                        )
                ));
    }
}
