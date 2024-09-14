package com.springnewshub.controller;

import java.util.Collections;
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

import com.springnewshub.model.Comment;
import com.springnewshub.repository.CommentRepository;

public class CommentController {
	@Autowired
	CommentRepository repository;
	
	//Route to get all comments
	@GetMapping("/api/comments")
	public ResponseEntity<List<Comment>> getAllComments() {
		try {
			//Fetches all comments
			return ResponseEntity.ok(repository.findAll());
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
		}
	}
	
	//Route to get a single comment 
	@GetMapping("/api/comments/{id}")
	public ResponseEntity<Comment> getComment(@PathVariable int id) {
		try {
			//Fetches a comment as an Optional<Comment>
	        Optional<Comment> optionalComment = repository.findById(id);
	        
	        //Checks if the comment exists
	        if (optionalComment.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }

	        //Returns the actual comment from the optionalComment
	        return ResponseEntity.ok(optionalComment.get());
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
	//Route to create a new comment
	@PostMapping("/api/comments")
	public ResponseEntity<Comment> addComment(@RequestBody Comment comment) {
		try {
			//Returns the newly created comment
			return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(comment));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	//Route to update a comment
	@PutMapping("/api/comments/{id}")
	public ResponseEntity<Comment> updateComment(@PathVariable int id, @RequestBody Comment comment) {
		try {
			//Fetches the comment as an Optional<Comment>
	        Optional<Comment> optionalComment = repository.findById(id);
	        
	        //Checks if the comment exists
	        if (optionalComment.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }
	        
	        //Gets the existing comment 
	        Comment existingComment = optionalComment.get();
	        
	        // Update the comment text if it's provided
	        if (comment.getCommentText() != null) {
	            existingComment.setCommentText(comment.getCommentText());
	        }

	        //Returns the update comment
	        return ResponseEntity.ok(repository.save(existingComment));
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	//Route to delete a comment
		@DeleteMapping("/api/comments/{id}")
		public ResponseEntity<String> deleteComment(@PathVariable Integer id) {
			try {
				// Checks if the comment exists
		        if (!repository.existsById(id)) {
		            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");
		        }
		        
		        //Deletes the comment
				repository.deleteById(id);
				return ResponseEntity.noContent().build();
			}catch(Exception e){
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting comment");
			}
		}
}
