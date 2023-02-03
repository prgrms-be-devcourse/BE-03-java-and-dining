package com.prgms.allen.dining.domain.member.entity;

public enum MemberType {

	CUSTOMER("ROLE_CUSTOMER"),
	OWNER("ROLE_OWNER");

	private final String value;

	MemberType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
