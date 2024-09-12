package com.springnewshub.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.springnewshub.model.User;
import com.springnewshub.repository.UserRepository;
import com.springnewshub.repository.VoteRepository;

@RestController
public class UserController {

	@Autowired
	UserRepository repository;
	
	@Autowired
	VoteRepository voteRepository;
	
	//Route to get all users
	@GetMapping("/api/users")
	public ResponseEntity<List<User>> getAllUsers() {
	    try {
	    	//Fetches all users
	        List<User> userList = repository.findAll();
	        //Iterates over the users
	        userList.forEach(user -> 
	        	//Iterates over the posts
	            user.getPosts().forEach(post ->
	            	//Gets the vote count for each post
	                post.setVoteCount(voteRepository.countVotesByPostId(post.getId()))
	            )
	        );
	        return ResponseEntity.ok(userList);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
	    }
	}

	
	//Route to get a single user
	@GetMapping("/api/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Integer id) {
	  try {
	  //Fetches an user by its id as an Optional<User>
	  Optional<User> optionalUser = repository.findById(id);
	  
	  //Checks if the user exists
	  if(optionalUser.isEmpty()) {
		  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	  }
	  
	  //Gets the actual user from the optionalUser
	  User returnUser = optionalUser.get();
	  	//Iterates over the user posts
	  	returnUser.getPosts().forEach(post ->
	  		//Gets the votes for each post
	  		post.setVoteCount(voteRepository.countVotesByPostId(post.getId()))
	  			);
	  return ResponseEntity.ok(returnUser);
	  } catch(Exception e) {
		  return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	  }
	}

	
	//Route to create an user
	@PostMapping("/api/users")
	public ResponseEntity<User> addUser(@RequestBody User user) {
		try {
			//Encrypts the password
			user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
			
			//Saves the user to the repository
			repository.save(user);
			
			return ResponseEntity.status(HttpStatus.CREATED).body(user);
		}catch(DataIntegrityViolationException e) {
			//Handles unique constrain violations
			return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}		
	}
	
	//Route to update an user
	@PutMapping("/api/users/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
	    try {
	        // Fetch the existing user from the repository an Optional<User>
	        Optional<User> optionalUser = repository.findById(id);

	        // Check if the user exists
	        if (optionalUser.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }

	        // Update the existing user with new values
	        User existingUser = optionalUser.get();
	        
	        // Update fields only if they are not null in the request body
	        if (user.getUsername() != null) {
	            existingUser.setUsername(user.getUsername());
	        }
	        if (user.getEmail() != null) {
	            existingUser.setEmail(user.getEmail());
	        }
	        if (user.getPassword() != null) {
	            existingUser.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
	        }

	        // Save the updated user
	        User updatedUser = repository.save(existingUser);

	        return ResponseEntity.ok(updatedUser);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

	
	
	//Route to delete an user
	@DeleteMapping("/api/users/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Integer id) {
		try {
			// Checks if the user exists
	        if (!repository.existsById(id)) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	        }
	        
	        //Deletes the user
			repository.deleteById(id);
			return ResponseEntity.noContent().build();
		}catch(Exception e){
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user");
		}
	}

}
