package com.prgms.allen.dining.web.security;

public enum HeaderValue {
	CONTENT_TYPE("application/json");

	private final String value;

	HeaderValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
