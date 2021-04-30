package com.pulmuone.OnlineIFServer.service;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.pulmuone.OnlineIFServer.dto.UserInfo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Service("jwtService")
@Slf4j
public class JwtServiceImpl implements JwtService {
	
	private static final String SALT = "Shim";
	
	//jwt 토큰 생성. claim() 안에 user정보 객체를 넣어서 payload로 응답한다.
	@Override
	public String create(String key, UserInfo data) {
		return Jwts.builder()
				.setHeaderParam("type", "JWT")
				.claim(key, data)
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
	
	//세션 스토리지로부터 얻은 토큰 값이 유효한 정보인지 확인하는 로직
	@Override
	public boolean isUsable(String token) throws Exception{
		try{
			Jws<Claims> claims = Jwts.parser()
					  .setSigningKey(this.generateKey())
					  .parseClaimsJws(token);
			return true;
			
		}catch (Exception e) {
			return true;
		}
	}	
	
	@Override
	public Map<String, Object> get(String key) throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		String jwt = request.getHeader("Authorization");
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parser()
						 .setSigningKey(SALT.getBytes("UTF-8"))
						 .parseClaimsJws(jwt);
		} catch (Exception e) {
			throw new Exception();
		}
		@SuppressWarnings("unchecked")
		Map<String, Object> value = (LinkedHashMap<String, Object>)claims.getBody().get("member");
		return value;
	}
}
