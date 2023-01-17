package com.prgms.allen.dining.domain.reservation.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class ReservationDetail {

	@Column(name = "visit_date", nullable = false)
	private LocalDate visitDate;

	@Column(name = "visit_time", nullable = false)
	private LocalTime visitTime;

	@Column(name = "visitor_count", nullable = false)
	private int visitorCount;

	@Lob
	@Column(name = "customer_memo")
	private String customerMemo;

	protected ReservationDetail() {
	}

	public ReservationDetail(LocalDate visitDate, LocalTime visitTime, int visitorCount, String customerMemo) {
		this.visitDate = visitDate;
		this.visitTime = visitTime;
		this.visitorCount = visitorCount;
		this.customerMemo = customerMemo;
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

	public LocalDateTime getVisitDateTime() {
		return LocalDateTime.of(visitDate, visitTime);
	}
}