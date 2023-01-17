package com.prgms.allen.dining.domain.member.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class MemberTest {

	public static final String VALID_NICKNAME = "홍길동123";
	public static final String VALID_NAME = "홍길동";
	public static final String VALID_PHONE = "01012341234";
	public static final String VALID_PASSWORD = "gildong123@";

	@Nested
	@DisplayName("고객은 닉네임 입력시,")
	class nickname {
		@ParameterizedTest
		@CsvSource({"ab", "abcdefghijklmnopdfsdf"})
		@DisplayName("3 ~ 20글자 범위를 벗어나면 생성에 실패한다")
		public void fail_by_length(String invalidNickname) {
			// when & then
			assertThrows(IllegalStateException.class, () ->
				new Member(invalidNickname, VALID_NAME, VALID_PHONE, VALID_PASSWORD, MemberType.CUSTOMER)
			);
		}

		@Test
		@DisplayName("알파벳 대소문자, 한글, 숫자 외의 문자 입력 시 생성에 실패한다.")
		public void fail_by_special_character() {
			// given
			String invalidNickname = "abc@";

			// when & then
			assertThrows(IllegalStateException.class, () ->
				new Member(invalidNickname, VALID_NAME, VALID_PHONE, VALID_PASSWORD, MemberType.CUSTOMER)
			);
		}
	}

	@Nested
	@DisplayName("고객은 이름 입력시,")
	class name {
		@ParameterizedTest
		@CsvSource({"가", "가나다라마바"})
		@DisplayName("2 ~ 5글자 범위를 벗어나면 생성에 실패한다")
		public void fail_by_length(String invalid) {

			// when & then
			assertThrows(IllegalStateException.class, () ->
				new Member(VALID_NICKNAME, invalid, VALID_PHONE, VALID_PASSWORD, MemberType.CUSTOMER)
			);
		}

		@ParameterizedTest
		@CsvSource({"abcdefg", "!@#$%", "abcdefgh!", "김환12", "김환!", "김환a"})
		@DisplayName("정규식에 어긋나면 생성에 실패한다")
		public void fail_by_regex(String invalid) {

			//when & then
			assertThrows(IllegalStateException.class, () ->
				new Member(VALID_NICKNAME, invalid, VALID_PHONE, VALID_PASSWORD, MemberType.CUSTOMER)
			);
		}
	}

	@Nested
	@DisplayName("고객은 비밀번호 입력시,")
	class password {
		@ParameterizedTest
		@CsvSource({"abcde1!", "a1234567890123456789!"})
		@DisplayName("8 ~ 20글자 범위를 벗어나면 생성에 실패한다")
		public void fail_by_length(String invalid) {

			// when & then
			assertThrows(IllegalStateException.class, () ->
				new Member(VALID_NICKNAME, VALID_NAME, VALID_PHONE, invalid, MemberType.CUSTOMER)
			);
		}

		@ParameterizedTest
		@CsvSource({"abcdefgh", "12345678", "!@#$%^&*", "abcd1234", "1234!@#$", "abcd!@#$"})
		@DisplayName("정규식에 어긋나면 생성에 실패한다")
		public void fail_by_regex(String invalid) {

			//when & then
			assertThrows(IllegalStateException.class, () ->
				new Member(VALID_NICKNAME, VALID_NAME, VALID_PHONE, invalid, MemberType.CUSTOMER)
			);
		}
	}

	@Nested
	@DisplayName("고객은 핸드폰 번호 입력시,")
	class phone {
		@ParameterizedTest
		@CsvSource({"abcdefgh", "!@#$%^&*", "abcd1234", "1234!@#$", "abcd!@#$"})
		@DisplayName("정규식에 어긋나면 생성에 실패한다")
		public void fail_by_regex(String invalid) {

			//when & then
			assertThrows(IllegalStateException.class, () ->
				new Member(VALID_NICKNAME, VALID_NAME, invalid, VALID_PASSWORD, MemberType.CUSTOMER)
			);
		}
	}
}