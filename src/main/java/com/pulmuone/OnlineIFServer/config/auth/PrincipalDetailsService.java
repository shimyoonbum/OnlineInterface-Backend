package com.pulmuone.OnlineIFServer.config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pulmuone.OnlineIFServer.dto.IFUser;
import com.pulmuone.OnlineIFServer.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService{

   private final UserRepository userRepository;
   
   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      System.out.println("PrincipalDetailsService : 진입");
      IFUser user = userRepository.findByUsername(username);

      // session.setAttribute("loginUser", user);
      System.out.println("PrincipalDetailsService : 수행완료");
      return new PrincipalDetails(user);
   }
}