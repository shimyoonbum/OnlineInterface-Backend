package com.pulmuone.OnlineIFServer.util;

import java.security.*;

import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {	
	
	public static String encryptSHA256(String str) {
		String sha = "";
	    
		try {
			
			MessageDigest md = MessageDigest.getInstance("SHA-256");
	    	  
	    	byte[] byteDatas = md.digest(str.getBytes("UTF-8"));
	        StringBuffer sb = new StringBuffer();
	          
	        for(byte byteData : byteDatas) {
	        	String hex = Integer.toHexString(0xff & byteData);
	        	if(hex.length() == 1)
	        		sb.append("0");
	        	sb.append(hex); 
	        }
	          
	        sha = sb.toString();
	          
	    } catch(Exception e) {
	        sha = null;
	    }
		
	    return sha;  
	}
}
