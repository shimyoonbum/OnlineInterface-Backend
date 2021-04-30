package com.pulmuone.OnlineIFServer.dto;

import lombok.Data;

@Data
public class MailInfo {
	private String date;
	private String name;
	private String posStart;
	private String posEnd;
	private String posTime;
	private int posCount;
	private String ErpStart;
	private String ErpEnd;
	private String ErpTime;
	private int ErpCount;
	private String message;	
}
