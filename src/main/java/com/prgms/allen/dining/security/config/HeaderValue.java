package com.prgms.allen.dining.security.config;

public enum HeaderValue {
	CONTENT_TYPE("application/json"),
	AUTHORIZATION("Authorization");

	private final String value;

	HeaderValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
