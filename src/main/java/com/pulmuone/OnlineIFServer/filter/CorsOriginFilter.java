package com.pulmuone.OnlineIFServer.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsOriginFilter {
	
   @Bean
   public CorsFilter corsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      config.addAllowedOrigin("*"); 			// e.g. https://rsifdev.pulmuone.com
      config.addAllowedHeader("*");
      config.addAllowedMethod("*");
      config.addExposedHeader("Authorization");	//2021-03-03 sim  CORS response header 이슈
      //가 있어(ACCESS-ORIGIN-EXPOSED-HEADERS) exposedHeaders 관련 설정 추가

      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);
   }

}