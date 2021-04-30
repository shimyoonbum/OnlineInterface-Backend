package com.pulmuone.OnlineIFServer.util;

import java.io.UnsupportedEncodingException;

import org.springframework.stereotype.Component;

import com.pulmuone.OnlineIFServer.config.auth.PrincipalDetails;
import com.pulmuone.OnlineIFServer.dto.UserInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {

	private static final String SALT = "Shim";

    public String createToken(String key, PrincipalDetails principalDetailis) {
    	return Jwts.builder()
				.setHeaderParam("type", "JWT")
				.claim(key, principalDetailis)
				.signWith(SignatureAlgorithm.HS256, this.generateKey())
				.compact();	
    }
    
    private byte[] generateKey() {
		byte[] key = null;
		try {
			key = SALT.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			if(log.isInfoEnabled()){
				e.printStackTrace();
			}else{
				log.error("Making JWT Key Error ::: {}", e.getMessage());
			}
		}
		
		return key;
	}    
    
//    public Claims getClaims(String token) {
//        try{
//        	return Jwts.parser()
//        			.setSigningKey(this.generateKey())
//                    .parseClaimsJws(token)
//                    .getBody();
//        }catch(Exception e) {
//        	return null;
//        }
//                
//    }
}

