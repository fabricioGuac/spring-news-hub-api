package com.springnewshub.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springnewshub.model.Vote;

public interface VoteRepository extends JpaRepository<Vote, Integer>{

	// Custom query method defined by Spring Data JPA to count the number of votes for a specific post
	@Query("SELECT count(*) FROM Vote v where v.postId = :id")
	int countVotesByPostId(@Param("id") Integer id);
	
	// Custom query method interpreted by Spring Data JPA to find a vote by post id and user id 
	 Vote findByPostIdAndUserId(Integer postId, Integer userId);
}
