package com.pulmuone.OnlineIFServer.common;

public enum ResponseStatus {
	OK("000", "성공"),
	FAIL("999", "실패"),
	DUP("100", "키값 중복 오류"),
	IF_ID("101", "인터페이스ID 오류"),
	AUTHKEY("102", "인증키 오류"),
	NOT_FOUND("103", "데이터 없음"),
	MISSING("104", "필수항목 누락 오류"),
	JSON("105", "JSON 형식 오류"),
	ACCESS("106", "데이터 접근 제한 오류"),
	SEARCH("107", "검색조건 형식 오류"),
	UPD_COND("108", "업데이트 조건 형식 오류"),
	UNKNOWN("109", "미등록API"), 
	NOT_DATE("110", "날짜 조건 형식 오류"),
	NOT_UPD("111", "업데이트 제한 컬럼");
	
	private final String value;
	private final String phrase;
	
	ResponseStatus(String value, String phrase) {
		this.value = value;
		this.phrase = phrase;
	}
	
	public String value() {
		return this.value;
	}
	
	public String phrase() {
		return this.phrase;
	}
	
	public static ResponseStatus getStatusByValue(String value) {
		for(ResponseStatus status : ResponseStatus.values())
			if(value.equals(status.value()))
				return status;
		
		return null;
	}
}
