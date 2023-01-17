package com.prgms.allen.dining.domain.restaurant.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.prgms.allen.dining.domain.member.entity.Member;
import com.prgms.allen.dining.domain.member.entity.MemberType;

class RestaurantTest {

	public static final String VALID_NAME = "맛있는식당";
	public static final String VALID_PHONE = "01012341234";
	public static final int VALID_CAPACITY = 30;
	public static final String VALID_LOCATION = "서울특별시 어딘구 어딘가로 어디빌딩";
	public static final List<Menu> VALID_MENU_LIST = new ArrayList<>();
	public static final List<ClosingDay> VALID_CLOSING_DAY_LIST = new ArrayList<>();
	public static final LocalTime VALID_OPEN_TIME = LocalTime.of(11, 20);
	public static final LocalTime VALID_LAST_ORDER_TIME = LocalTime.of(20, 20);
	public static final FoodType VALID_FOOD_TYPE = FoodType.KOREAN;
	public static final String VALID_DESCRIPTION = "";
	public static final Member owner = new Member(
		"nickname",
		"홍길동",
		"01022223333",
		"qwer1234!",
		MemberType.OWNER
	);
	public static final Member customer = new Member(
		"nickname",
		"아버지",
		"01011112222",
		"qwer1234!",
		MemberType.CUSTOMER
	);

	@ParameterizedTest
	@CsvSource({"뭐", "30글자 넘어가는 아주 긴 식당이다 너무 길어서 당황스럽지 더 길게 만들어주지 아주 그냥 길게 만들어"})
	@DisplayName("점주는 레스토랑 이름 입력시,1 ~ 30 글자 범위를 벗어나면 생성에 실패한다")
	void fail_by_name(String invalidName) {

		// when & then
		assertThrows(IllegalArgumentException.class, () ->
			new Restaurant(owner,
				VALID_FOOD_TYPE,
				invalidName,
				VALID_CAPACITY,
				VALID_OPEN_TIME,
				VALID_LAST_ORDER_TIME,
				VALID_LOCATION,
				VALID_DESCRIPTION,
				VALID_PHONE,
				VALID_MENU_LIST,
				VALID_CLOSING_DAY_LIST)
		);
	}

	@DisplayName("레스토랑 생성시, 고객의 타입이 구매자이면 생성에 실패한다")
	@Test
	void fail_by_customer_type() {

		// when & then
		assertThrows(IllegalArgumentException.class, () ->
			new Restaurant(customer,
				VALID_FOOD_TYPE,
				VALID_NAME,
				VALID_CAPACITY,
				VALID_OPEN_TIME,
				VALID_LAST_ORDER_TIME,
				VALID_LOCATION,
				VALID_DESCRIPTION,
				VALID_PHONE,
				VALID_MENU_LIST,
				VALID_CLOSING_DAY_LIST)
		);
	}

	@Test
	@DisplayName("레스토랑 생성시, 최대 수용 인원이 2명보다 작을 경우 생성에 실패한다.")
	public void fail_by_capacity_under_2() {

		// when & then
		assertThrows(IllegalArgumentException.class,
			() -> new Restaurant(owner,
				VALID_FOOD_TYPE,
				VALID_NAME,
				1,
				VALID_OPEN_TIME,
				VALID_LAST_ORDER_TIME,
				VALID_LOCATION,
				VALID_DESCRIPTION,
				VALID_PHONE,
				VALID_MENU_LIST,
				VALID_CLOSING_DAY_LIST)
		);
	}

	@ParameterizedTest
	@CsvSource({"11112222", "0102222"})
	@DisplayName("레스토랑 생성시, 전화번호가 정규식에 맞지 않으면 생성에 실패한다.")
	void fail_by_regex(String invalidPhone) {

		// when & then
		assertThrows(IllegalArgumentException.class, () ->
			new Restaurant(owner,
				VALID_FOOD_TYPE,
				VALID_NAME,
				VALID_CAPACITY,
				VALID_OPEN_TIME,
				VALID_LAST_ORDER_TIME,
				VALID_LOCATION,
				VALID_DESCRIPTION,
				invalidPhone,
				VALID_MENU_LIST,
				VALID_CLOSING_DAY_LIST)
		);
	}

}