package com.pulmuone.OnlineIFServer.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
   private String username;
   private String password;
}
