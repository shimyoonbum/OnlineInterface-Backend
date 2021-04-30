package com.pulmuone.OnlineIFServer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
	
	public String id = "N";
	public String name;
	public String system_id;
	public String role;
	public String use_yn;
	public String password;
}
