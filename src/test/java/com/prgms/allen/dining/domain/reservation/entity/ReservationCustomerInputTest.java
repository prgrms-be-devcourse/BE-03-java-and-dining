package com.prgms.allen.dining.domain.reservation.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ReservationDetailTest {

	private static final int VALID_VISITOR_COUNT = 2;
	private static final String VALID_MEMO = "맛있게 해주세요~";

	@Nested
	@DisplayName("고객은 예약의 상세 정보의 방문 날짜&시간 입력 시,")
	class visitDateTime {

		@ParameterizedTest
		@ValueSource(longs = {0L, 30L})
		@DisplayName("예약일 당일 다음 단위시간 ~ 30일 사이의 날짜로 방문 날짜를 잡으면 예약에 성공한다.")
		void success_by_valid_date_time_format(long validDay) {
			// given
			LocalDateTime visitAt = LocalDateTime.now()
				.plusDays(validDay)
				.plusHours(1L)
				.truncatedTo(ChronoUnit.HOURS);

			// when
			ReservationDetail reservationDetail = new ReservationDetail(visitAt, VALID_VISITOR_COUNT, VALID_MEMO);

			// then
			LocalDate actualVisitDate = visitAt.toLocalDate();
			LocalTime actualVisitTime = visitAt.toLocalTime();
			Assertions.assertThat(reservationDetail.getVisitDate())
				.isEqualTo(actualVisitDate);
			Assertions.assertThat(reservationDetail.getVisitTime())
				.isEqualTo(actualVisitTime);
			Assertions.assertThat(reservationDetail.getVisitorCount())
				.isEqualTo(VALID_VISITOR_COUNT);
			Assertions.assertThat(reservationDetail.getCustomerMemo())
				.isEqualTo(VALID_MEMO);
		}

		@ParameterizedTest
		@ValueSource(longs = {0L, 31L})
		@DisplayName("예약일보다 일찍, 혹은 30일을 넘겨서 날짜를 잡으면 예약에 실패한다.")
		void fail_by_invalid_day(long invalidDay) {
			// given
			LocalDateTime visitAt = LocalDateTime.now()
				.plusDays(invalidDay)
				.truncatedTo(ChronoUnit.HOURS);

			// when & then
			assertThrows(IllegalStateException.class, () ->
				new ReservationDetail(visitAt, VALID_VISITOR_COUNT, VALID_MEMO)
			);
		}

		@ParameterizedTest
		@CsvSource(value = {"30,30", "0,30", "30,0"})
		@DisplayName("예약 시간이 시간 단위가 아니면 예약에 실패한다.")
		void fail_by_visit_time_format(int minuteFormat, int secondFormat) {
			// given
			LocalDateTime visitAt = LocalDateTime.now()
				.withMinute(minuteFormat)
				.withSecond(secondFormat);

			// when & then
			assertThrows(IllegalStateException.class, () ->
				new ReservationDetail(visitAt, VALID_VISITOR_COUNT, VALID_MEMO)
			);
		}
	}
}