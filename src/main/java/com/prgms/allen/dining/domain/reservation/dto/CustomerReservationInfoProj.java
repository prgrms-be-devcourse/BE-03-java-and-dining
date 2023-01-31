package com.prgms.allen.dining.domain.reservation.dto;

import java.util.Objects;
import java.util.Optional;

public class CustomerReservationInfoProj {

	private String name;
	private String phone;
	private long visitedCount;
	private long noShowCount;
	private String lastVisitedDateTime;

	public CustomerReservationInfoProj() {
	}

	public CustomerReservationInfoProj(
		String name,
		String phone,
		long visitedCount,
		long noShowCount,
		String lastVisitedDateTime
	) {
		this.name = name;
		this.phone = phone;
		this.visitedCount = visitedCount;
		this.noShowCount = noShowCount;
		this.lastVisitedDateTime = lastVisitedDateTime;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public long getVisitedCount() {
		return visitedCount;
	}

	public long getNoShowCount() {
		return noShowCount;
	}

	public Optional<String> getLastVisitedDateTime() {
		return Optional.ofNullable(lastVisitedDateTime);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CustomerReservationInfoProj that = (CustomerReservationInfoProj)o;
		return visitedCount == that.visitedCount && noShowCount == that.noShowCount && Objects.equals(name,
			that.name) && Objects.equals(phone, that.phone) && Objects.equals(lastVisitedDateTime,
			that.lastVisitedDateTime);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, phone, visitedCount, noShowCount, lastVisitedDateTime);
	}

	@Override
	public String toString() {
		return "CustomerReservationInfoProj{" +
			"name='" + name + '\'' +
			", phone='" + phone + '\'' +
			", visitedCount=" + visitedCount +
			", noShowCount=" + noShowCount +
			", lastVisitedDateTime='" + lastVisitedDateTime + '\'' +
			'}';
	}
}
