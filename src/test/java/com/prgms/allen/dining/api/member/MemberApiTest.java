package com.prgms.allen.dining.api.member;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prgms.allen.dining.domain.member.MemberRepository;
import com.prgms.allen.dining.domain.member.dto.MemberSignupReq;
import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;
import com.prgms.allen.dining.generator.DummyGenerator;
import com.prgms.allen.dining.security.config.MemberLoginReq;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class MemberApiTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Test
	@DisplayName("회원은 회원가입 할 수 있다.")
	void member_join() throws Exception {
		// given
		MemberSignupReq signupReq = new MemberSignupReq(
			"hongildong123",
			"홍길동",
			"01033334444",
			"password123!",
			MemberType.CUSTOMER
		);

		// when & then
		mockMvc.perform(post("/api/members/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupReq)))
			.andExpect(status().isCreated())
			.andDo(print())
			.andDo(document("member-join",
				requestFields(
					fieldWithPath("nickname").description("닉네임"),
					fieldWithPath("name").description("이름"),
					fieldWithPath("phone").description("전화번호"),
					fieldWithPath("password").description("비밀번호"),
					fieldWithPath("memberType").description("회원 유형")
				))
			);
	}

	@Test
	@DisplayName("회원은 로그인을 할 수 있다.")
	void member_login() throws Exception {
		// given
		Member customer = memberRepository.save(DummyGenerator.CUSTOMER);
		MemberLoginReq loginReq = new MemberLoginReq(
			customer.getNickname(),
			customer.getPassword()
		);

		// when & then
		mockMvc.perform(post("/api/members/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginReq)))
			.andExpect(status().isOk())
			.andDo(print())
			.andDo(document("member-login",
				requestFields(
					fieldWithPath("nickname").description("닉네임"),
					fieldWithPath("password").description("비밀번호")
				),
				responseFields(
					fieldWithPath("token").description("JWT 토큰")
				))
			);
	}
}