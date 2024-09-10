package com.springnewshub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springnewshub.model.Post;

@Repository
public interface PostRepository extends JpaRepository <Post, Integer>{

	// Custom query method interpreted by Spring Data JPA to retrieve all posts by a specific user ID
	List<Post> findAllPostByUserId(Integer id) throws Exception;
	
}
