package com.pulmuone.OnlineIFServer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pulmuone.OnlineIFServer.dto.IFUser;

public interface UserRepository extends JpaRepository<IFUser, Long>{
	IFUser findByUsername(String username);
}