package com.springnewshub.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.ui.Model;

import com.springnewshub.model.Comment;
import com.springnewshub.model.Post;
import com.springnewshub.model.User;
import com.springnewshub.model.Vote;
import com.springnewshub.repository.CommentRepository;
import com.springnewshub.repository.PostRepository;
import com.springnewshub.repository.UserRepository;
import com.springnewshub.repository.VoteRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class SpringnewsController {

	@Autowired
	  PostRepository postRepository;
	  @Autowired
	  VoteRepository voteRepository;
	  @Autowired
	  UserRepository userRepository;
	  @Autowired
	  CommentRepository commentRepository;
	  
	  //Route to login
	 @PostMapping("/users/login")
	 public String login(@ModelAttribute User user, Model model, HttpServletRequest request) {
		 
		 
		 //Ensures the email and password are populated
		 if(user.getPassword() == null || user.getPassword().isEmpty() || user.getEmail() == null || user.getEmail().isEmpty()) {
			 model.addAttribute("notice", "Email address and password must be populated in order to login!");
			 return "login";
		 }
		 
		 
		 try {
			 //Fetches user from the database
			 User sessionUser = userRepository.findUserByEmail(user.getEmail());
			 
			 
			 //Ensures the user exists
			 if(sessionUser == null) {
				 model.addAttribute("notice","Email address not reconigzed!");
				 return "login";
			 }
			 
			 //Validates the password
			 boolean isPasswordValid = BCrypt.checkpw(user.getPassword(), sessionUser.getPassword());
			 if(!isPasswordValid) {
				 model.addAttribute("notice", "Password is not valid");
				 return "login";
			 }
			 
			 
			 
			 //Sets user as logged in and add to the session
			 sessionUser.setLoggedIn(true);
			 request.getSession().setAttribute("SESSION_USER", sessionUser);
			 
			// Redirects to the dashboard after successful signup
	         return "redirect:/dashboard";
			 
		 } catch (Exception e) {
			 // Logs the error
	          e.printStackTrace();
	          model.addAttribute("notice", "An unexpected error occurred. Please try again.");
	          return "login";
		 }
		 
	 }
	 
	 //Route to signup
	 @PostMapping("/users")
	 public String signup(@ModelAttribute User user, Model model, HttpServletRequest request) {

	     //Ensures username, password, and email are populated
	     if (user.getUsername() == null || user.getUsername().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty() || user.getEmail() == null || user.getEmail().isEmpty()) {
	         model.addAttribute("notice", "In order to sign up, username, email address, and password must be populated!");
	         return "login";
	     }

	     try {
	         //Encrypts and saves the user's password
	         user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
	         userRepository.save(user);

	         // Fetches the user after saving to ensure it's properly stored
	         User sessionUser = userRepository.findUserByEmail(user.getEmail());

	         // Checks if sessionUser is null (if for some reason it wasn't saved or retrieved correctly)
	         if (sessionUser == null) {
	             model.addAttribute("notice", "User is not recognized!");
	             return "login";
	         }

	         // Sets the user as logged in and store them in the session
	         sessionUser.setLoggedIn(true);
	         request.getSession().setAttribute("SESSION_USER", sessionUser);
	         
	         
	         // Redirects to the dashboard after successful signup
	         return "redirect:/dashboard";

	     } catch (DataIntegrityViolationException e) {
	         // Handles case where the email already exists in the database
	         model.addAttribute("notice", "Email address is not available! Please choose a different unique email address.");
	         return "login";

	     } catch (Exception e) {
	         // General exception handling to catch unexpected errors
	         model.addAttribute("notice", "An error occurred during the signup process. Please try again later.");
	         e.printStackTrace(); 
	         return "login";
	     }
	 }
	 
	 
	 
	// Route to add a post
	 @PostMapping("/posts")
	 public String addPostDashboardPage(@ModelAttribute Post post, HttpServletRequest request) {
	     
	     // Ensures the user is logged in
	     if (request.getSession(false) == null) {
	    	// Redirects to login if no active session
	         return "redirect:/login"; 
	     }

	     // Checks if the title and URL are populated
	     if (post.getTitle() == null || post.getTitle().trim().isEmpty() || post.getPostUrl() == null || post.getPostUrl().isEmpty()) {
	         return "redirect:/dashboardEmptyTitleAndLink"; // Redirects to an error page
	     }

	     // Retrieves the current user from the session
	     User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");

	     try {
	         // Sets the post's userId to the session user's id
	         post.setUserId(sessionUser.getId());
	         // Saves the post to the repository
	         postRepository.save(post);
	     } catch (Exception e) {
	         // Logs the exception and redirects to the error page
	         e.printStackTrace();
	         return "redirect:/error";
	     }
	     
	     // Redirects back to the dashboard
	     return "redirect:/dashboard"; 
	 }

	 
	 //Route to update the title of a post
	 @PostMapping("/post/{id}")
	 public String updatePostDashboardPage(@PathVariable int id, @ModelAttribute Post post, Model model, HttpServletRequest request) {
		 
		 //Ensures the user is logged in
		 if(request.getSession(false) == null) {
			 // Redirects to login if no active session
			 return "redirect:/login";
		 }
		// Retrieves the current user from the session
		User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
			 
	    //Fetches the post to be updates
		Post tempPost = postRepository.findById(id).orElse(null);
			 
		//Ensures the post exists
		if(tempPost == null) {
			model.addAttribute("notice", "Post not found");
			return "redirect:/dashboard";
		}
			 
		//Ensures the post belongs to the user
		if(tempPost.getUserId() != sessionUser.getId()) {
			model.addAttribute("notice", "You are not authorized to edit this post!");
			return "redirect:/dashboard";
			 }
			 
		//Updates the title
		tempPost.setTitle(post.getTitle());
			 
		//Saves the updated post
		postRepository.save(tempPost);
			
		return "redirect:/dashboard";
	 }
	 
	 //Route to add a comment
	 @PostMapping("/comments")
	 public String createCommentCommentsPage(@ModelAttribute Comment comment, HttpServletRequest request) {
		 //Ensures the user is logged in
		 if(request.getSession(false) == null) {
			 // Redirects to login if no active session
			 return "redirect:/login";
		 }
		 
		 //Ensures the comment is not empty
		 if(comment.getCommentText() == null || comment.getCommentText().trim().isEmpty()) {
			 return "redirect:/singlePostEmptyComment/" + comment.getPostId();
		 }
		 
		// Retrieves the current user from the session
		User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
		
		//Sets the comment userIs to the sessionUser id
		comment.setUserId(sessionUser.getId());
		
		//Saves the comment to the repository
		commentRepository.save(comment);
		
		return "redirect:/post/" + comment.getPostId();	 
	}
	 
	//Route to edit comments
	@PostMapping("/comments/edit")
	public String createCommentEditPage(@ModelAttribute Comment comment, HttpServletRequest request) {
		 //Ensures the user is logged in
		 if(request.getSession(false) == null) {
			 // Redirects to login if no active session
			 return "redirect:/login";
		 }
		 
		//Ensures the comment is not empty
		 if(comment.getCommentText() == null || comment.getCommentText().trim().isEmpty()) {
			 return "redirect:/editPostEmptyComment/" + comment.getPostId();
		 }
		 
		// Retrieves the current user from the session
		User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
			
		//Sets the comment userIs to the sessionUser id
		comment.setUserId(sessionUser.getId());
		 
		//Saves the comment to the repository
		commentRepository.save(comment);
		
		return "redirect:/dashboard/edit/" + comment.getPostId();
	}
	
	//Route to upvote
	@PutMapping("/posts/upvote")
	public String addVoteCommentsPage(@RequestBody Vote vote, HttpServletRequest request) {
	    // Checks if the user is logged in
	    if (request.getSession(false) != null) {
	        // Gets the current user from the session
	        User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
	        vote.setUserId(sessionUser.getId());
	        
	        // Saves the vote
	        voteRepository.save(vote);

	        // Fetches the post and update the vote count
	        Post updatedPost = postRepository.findById(vote.getPostId()).orElse(null);
	        if (updatedPost != null) {
	            updatedPost.setVoteCount(voteRepository.countVotesByPostId(vote.getPostId()));
	            postRepository.save(updatedPost);
	        }

	        // Redirects to the post page after upvoting
	        return "redirect:/post/" + vote.getPostId();
	    } else {
	        // If no session, redirect to login page
	        return "redirect:/login";
	    }
	}

}
	 







