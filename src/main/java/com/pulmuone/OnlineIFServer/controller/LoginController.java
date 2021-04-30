package com.pulmuone.OnlineIFServer.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulmuone.OnlineIFServer.common.IFException;
import com.pulmuone.OnlineIFServer.common.ResponseStatus;
import com.pulmuone.OnlineIFServer.config.auth.PrincipalDetails;
import com.pulmuone.OnlineIFServer.dto.IFUser;
import com.pulmuone.OnlineIFServer.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class LoginController {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping(value = "/doLogout")
	public Map<String, Object> doLogout(HttpSession session) {

		Map<String, Object> params = new HashMap<>();

		try {
			params.put("responseCode", "000");
			params.put("responseMessage", "로그아웃 되었습니다.");

		} catch (Exception e) {
			params.put("responseCode", "007");
			params.put("responseMessage", "로그아웃 실패!");
		}

		return params;
	}

	@GetMapping(value = "/getSession")
	public Map<String, Object> getSession(Authentication authentication) throws Exception {

		Map<String, Object> params = new HashMap<>();

		PrincipalDetails principalDetails = null;

		try {
			// 주체의 정보(Principal)를 가져온다
			principalDetails = (PrincipalDetails) authentication.getPrincipal();

			params.put("responseCode", "000");
			params.put("responseMessage", "세션 불러오기 성공");
			params.put("data", principalDetails);

		} catch (Exception e) {
			params.put("responseCode", "999");
			params.put("responseMessage", "토큰 인증 실패");
		}

		return params;
	}

	// yarn start 시에 필요합니다.
	@GetMapping(value = "/getProxySession")
	public Map<String, Object> getProxySession(HttpServletRequest request) throws Exception {

		Map<String, Object> params = new HashMap<>();

		params.put("responseCode", "000");
		params.put("responseMessage", "세션 불러오기 성공");

		return params;
	}
	
	//postMan 으로 회원 등록 합니다.
	@PostMapping("join")
	public String join(@RequestBody IFUser user) throws IFException {
		try {
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			user.setRole("USER");
			userRepository.save(user);

			return "회원가입완료";
		} catch (Exception e) {
			throw new IFException(ResponseStatus.FAIL, e.getMessage().replaceAll("\n", "").replaceAll("\"", "'"));
		}
	}
}
