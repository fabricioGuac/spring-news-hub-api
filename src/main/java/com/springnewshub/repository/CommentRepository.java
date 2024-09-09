package com.springnewshub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springnewshub.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer>{

	// Custom query method interpreted by Spring Data JPA to retrieve all comments by a specific post ID
	List<Comment> findAllCommentsByPostId(int postId) throws Exception;// Replace the parameter type?
	
}
