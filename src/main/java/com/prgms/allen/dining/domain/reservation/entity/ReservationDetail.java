package com.prgms.allen.dining.domain.reservation.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

import org.springframework.util.Assert;

import com.prgms.allen.dining.domain.common.entity.BaseEntity;

@Embeddable
public class ReservationDetail extends BaseEntity {

	private static final int MIN_VISITOR_COUNT = 2;
	private static final int MAX_VISITOR_COUNT = 8;
	private static final long DAYS_TO_ADD = 31L;
	private static final int MINUTE_FORMAT = 0;
	private static final int SECOND_FORMAT = 0;
	private static final int MAX_MEMO_LENGTH = 300;

	@Column(name = "visit_date", nullable = false)
	private LocalDate visitDate;

	@Column(name = "visit_time", nullable = false)
	private LocalTime visitTime;

	@Column(name = "visitor_count", nullable = false)
	private int visitorCount;

	@Lob
	@Column(name = "memo", length = 300)
	private String memo;

	protected ReservationDetail() {
	}

	public ReservationDetail(LocalDateTime visitAt, int visitorCount, String memo) {
		validate(visitAt, visitorCount, memo);

		this.visitDate = visitAt.toLocalDate();
		this.visitTime = visitAt.toLocalTime()
			.truncatedTo(ChronoUnit.SECONDS);
		this.visitorCount = visitorCount;
		this.memo = memo;
	}

	private void validate(LocalDateTime visitAt, int visitorCount, String memo) {
		validateVisitBoundary(visitAt);
		validateHour(visitAt.toLocalTime());
		validateVisitorCount(visitorCount);
		validateMemo(memo);
	}

	private void validateVisitBoundary(LocalDateTime visitAt) {
		Assert.notNull(visitAt, "Field visitAt must not be null");

		LocalDateTime now = LocalDateTime.now();
		Assert.state(
			visitAt.isAfter(now.truncatedTo(ChronoUnit.HOURS)),
			"Field visitTime must be after the next hour based on the current date time."
		);

		LocalDate visitDate = visitAt.toLocalDate();
		LocalDate nowDate = now.toLocalDate();
		Assert.state(
			visitDate.isBefore(nowDate.plusDays(DAYS_TO_ADD)),
			"Field visitDate must be within 30 days."
		);
	}

	private void validateHour(LocalTime visitTime) {
		Assert.notNull(visitTime, "Field visitTime must not be null");
		Assert.state(
			visitTime.getMinute() == MINUTE_FORMAT && visitTime.getSecond() == SECOND_FORMAT,
			"Field visitTime's minute and second must be zero."
		);
	}

	private void validateVisitorCount(int visitorCount) {
		Assert.state(
			visitorCount >= MIN_VISITOR_COUNT && visitorCount <= MAX_VISITOR_COUNT,
			"Field visitorCount must be between 2 and 8"
		);
	}

	private void validateMemo(String memo) {
		Assert.notNull(memo, "Memo must not be null");
		Assert.state(memo.length() <= MAX_MEMO_LENGTH, "Memo length must be under 300.");
	}

	public LocalDate getVisitDate() {
		return visitDate;
	}

	public LocalTime getVisitTime() {
		return visitTime;
	}

	public int getVisitorCount() {
		return visitorCount;
	}

	public String getMemo() {
		return memo;
	}

	public boolean checkVisitingToday() {
		return visitDate.equals(LocalDate.now());
	}

	@Override
	public String toString() {
		return "ReservationDetail{" +
			"visitDate=" + visitDate +
			", visitTime=" + visitTime +
			", visitorCount=" + visitorCount +
			", memo='" + memo + '\'' +
			'}';
	}
}
