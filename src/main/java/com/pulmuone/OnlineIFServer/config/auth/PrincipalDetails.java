package com.pulmuone.OnlineIFServer.config.auth;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.pulmuone.OnlineIFServer.dto.IFUser;

import lombok.Data;

@Data
public class PrincipalDetails implements UserDetails{

   private IFUser user;

    public PrincipalDetails(IFUser user){
        this.user = user;
    }

    public IFUser getUser() {
      return user;
   }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    
   @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        user.getRoleList().forEach(r -> {
           authorities.add(()->{ return r;});
        });
        return authorities;
    }
}