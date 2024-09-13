package com.springnewshub.controller;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springnewshub.model.Post;
import com.springnewshub.model.User;
import com.springnewshub.model.Vote;
import com.springnewshub.repository.PostRepository;
import com.springnewshub.repository.UserRepository;
import com.springnewshub.repository.VoteRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class PostController {

	@Autowired
	PostRepository repository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	VoteRepository voteRepository;
	
	//Route to get all posts
	@GetMapping("/api/posts")
	public ResponseEntity<List<Post>> getAllPosts() {
		try {
			//Fetches all posts
			List<Post> postList = repository.findAll();
			//Iterates over the posts
			postList.forEach(post -> 
            	//Gets the vote count for each post
				post.setVoteCount(voteRepository.countVotesByPostId(post.getId()))
            );
			
			return ResponseEntity.ok(postList);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	//Route to get a single post
	@GetMapping("api/posts/{id}")
	public ResponseEntity<Post> getPostById(@PathVariable Integer id) {
		try {
			//Fetches a post as an Optional<Post>
			Optional<Post> optionalPost = repository.findById(id);
			
			//Checks if the post exists
			if(optionalPost.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			
			//Gets the actual post from the optional post
			Post returnPost = optionalPost.get();
			
			//Gets the vote count for the post
			returnPost.setVoteCount(voteRepository.countVotesByPostId(id));
			
			return ResponseEntity.ok(returnPost);
			
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	//Route to create a new post
	@PostMapping("api/posts")
	public ResponseEntity<Post> addPost(@RequestBody Post post) {
		try {
			//Saves the new post to the repository
			Post newPost = repository.save(post);
			return ResponseEntity.status(HttpStatus.CREATED).body(newPost);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	//Route to update a post
	@PutMapping("/api/posts/{id}")
	public ResponseEntity<Post> updatePost(@PathVariable Integer id, @RequestBody Post post) {
	    try {
	        // Fetch the existing post from the repository as an Optional<Post>
	        Optional<Post> optionalPost = repository.findById(id);

	        // Check if the post exists
	        if (optionalPost.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }

	        // Get the existing post
	        Post existingPost = optionalPost.get();

	        // Update fields only if they are not null in the request body
	        if (post.getTitle() != null) {
	            existingPost.setTitle(post.getTitle());
	        }
	        if (post.getPostUrl() != null) {
	            existingPost.setPostUrl(post.getPostUrl());
	        }
	        if (post.getUserId() != null) {
	            existingPost.setUserId(post.getUserId());
	        }
	        // Update the updated_at field
	        existingPost.setUpdatesAt(new Date());

	        // Save the updated post
	        Post updatedPost = repository.save(existingPost);

	        return ResponseEntity.ok(updatedPost);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

	//Route to vote a post
	@PutMapping("/api/posts/upvote")
	public ResponseEntity<String> addVote(@RequestBody Vote vote, HttpServletRequest request) {
	    try {
	        // Checks if the user is logged in
	        if (request.getSession(false) != null) {
	            User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

	            // Checks if the sessionUser is present
	            if (sessionUser == null) {
	                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
	            }

	            // Sets the user ID for the vote and save it
	            vote.setUserId(sessionUser.getId());
	            voteRepository.save(vote);

	            // Fetches the updated post and update the vote count
	            Post updatedPost = repository.findById(vote.getPostId()).orElse(null);
	            if (updatedPost == null) {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
	            }

	            updatedPost.setVoteCount(voteRepository.countVotesByPostId(vote.getPostId()));
	            repository.save(updatedPost);

	            return ResponseEntity.ok("Vote added successfully");
	        } else {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User session not found");
	        }
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error adding vote");
	    }
	}

	
	//Route to delete a post
	@DeleteMapping("/api/posts/{id}")
	public ResponseEntity<String> deletePost(@PathVariable Integer id) {
		try {
			// Checks if the post exists
	        if (!repository.existsById(id)) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
	        }
	        
	        //Deletes the post
			repository.deleteById(id);
			return ResponseEntity.noContent().build();
		}catch(Exception e){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting post");
		}
	}
}
