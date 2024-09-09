package com.springnewshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springnewshub.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	// Custom query method interpreted by Spring Data JPA to find a User by email
	User findUserByEmail(String email) throws Exception;
	
}
