package com.springnewshub.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.springnewshub.model.Comment;
import com.springnewshub.model.Post;
import com.springnewshub.model.User;
import com.springnewshub.repository.CommentRepository;
import com.springnewshub.repository.PostRepository;
import com.springnewshub.repository.UserRepository;
import com.springnewshub.repository.VoteRepository;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomePageController {

	@Autowired
	UserRepository userRepository;
	@Autowired
	PostRepository postRepository;
	@Autowired
	VoteRepository voteRepository;
	@Autowired
	CommentRepository commentRepository;
	
	//Route to login
	@GetMapping("/login")
	public String login(Model model, HttpServletRequest request) {
		//Checks if the user is already logged in
		if(request.getSession(false) != null) {
			//Redirects to the home page if the user is already logged in
			return "redirect:/";
		}
		
		//Creates a new User object and add it to the model for the login page
		model.addAttribute("user", new User());
		//
		return "login";
	}

	//Route to logout
	@GetMapping("/users/logout")
	public String logout(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		 //Checks if the user is currently logged in
		if(session != null) {
			// Invalidates the session to log out the user
			session.invalidate();
		}
		//Redirects to the login page after logging out
		return "redirect:/login";
	}
	
	//Route to homepage
	@GetMapping("/")
	public String homePageSetUp(Model model, HttpServletRequest request) {
		User sessionUser = new User();
		
		//Checks if the user is currently  logged in
		if(request.getSession(false) != null) {
			sessionUser = (User) request.getSession(false).getAttribute("SESSION_USER");
			model.addAttribute("loggedIn", sessionUser.isLoggedIn());
		} else {
			model.addAttribute("loggedIn", false);
		}
		
		// Fetches all posts from the post repository
		List<Post> postList = postRepository.findAll();

		// Extracts all unique user IDs from the list of posts
		Set<Integer> userIds = postList.stream()//Converts the list of posts into a stream
		                                .map(Post::getUserId)  // Extracts user IDs from each post
		                                .collect(Collectors.toSet());  // Collects unique user IDs into a Set

		// Fetches all users whose IDs are in the set of user IDs
		List<User> users = userRepository.findAllById(userIds);

		// Maps user IDs to usernames for quick lookup
		Map<Integer, String> userIdToUsername = users.stream()//Converts the list of users into a stream
		                                             .collect(Collectors.toMap(User::getId, User::getUsername));//Collects the user IDs and corresponding usernames into a Map
																												//where the user ID is the key and the username is the value

		//Iterates over each post in the list
		postList.forEach(post -> {
			//Sets the vote count for the post
		    post.setVoteCount(voteRepository.countVotesByPostId(post.getId()));
		    //Sets the username for the post using the user ID
		    post.setUsername(userIdToUsername.get(post.getUserId()));  
		});

		model.addAttribute("postList", postList);
		
		// "point" and "points" attributes refer to upvotes.
	    model.addAttribute("point", "point");
	    model.addAttribute("points", "points");
	    return "homepage";
	}
	
	
	// Route to dashboard
	@GetMapping("/dashboard")
	public String dashboardPageSetup(Model model, HttpServletRequest request) {
	    // Check if the user is logged in
	    if (request.getSession(false) != null) {
	        try {
	            // Setup the dashboard page with user data and posts
	            setupDashboardPage(model, request);
	            // Return the dashboard view
	            return "dashboard"; 
	        } catch (Exception e) {
	        	// Add error message to the model
	        	model.addAttribute("error", "An error occurred while setting up the dashboard.");
	        	// Redirect to an error page if something goes wrong 
	            return "error"; 
	        }
	    } else {
	        // If no session exists, return to the login page
	        model.addAttribute("user", new User());
	        return "login";
	    }
	}
	
	
	// Route to display the dashboard page with an error message if the title and link are missing
	@GetMapping("/dashboardEmptyTitleAndLink")
	public String dashboardEmptyTitleAndLinkHandler(Model model, HttpServletRequest request) {
		try {
			// Setup the dashboard page with user data and posts
			setupDashboardPage(model, request);
			
			//Add a notice to the model indicating that both the title and link are required for creating a post
			model.addAttribute("notice","To create a post the Title and Link must be populated!");
			
			// Return the dashboard view
            return "dashboard";			
		}catch (Exception e) {
			
			// Add error message to the model
	        model.addAttribute("error", "An error occurred while setting up the dashboard.");
			// Redirect to an error page if something goes wrong 
            return "error";
		}
	}
	
	// Route to display a single post page with a notice if the comment text area is empty
	@GetMapping("/singlePostEmptyComment/{id}")
	public String singlePostEmptyCommentHandler(@PathVariable int id, Model model, HttpServletRequest request) {
	    // Setup the page by fetching post, comments, and user information
	    setupSinglePostPage(id, model, request);
	    
	    // Add a notice to inform the user that they must enter a comment before submitting
	    model.addAttribute("notice", "To add a comment, you must enter the comment in the comment text area!");
	    
	    // Return the single-post view
	    return "single-post";
	}
	
	// Route to display a single post page based on the post ID
	@GetMapping("/post/{id}")
	public String singlePostPageSetup(@PathVariable int id, Model model, HttpServletRequest request) {
	    // Setup the page by fetching the post, comments, and user information
	    setupSinglePostPage(id, model, request);
	    
	    // Return the view for displaying the single post
	    return "single-post";
	}

	
	// Route to handle editing a post when the comment text area is empty
	@GetMapping("/editPostEmptyComment/{id}")
	public String editPostEmptyCommentHandler(@PathVariable int id, Model model, HttpServletRequest request) {
	    
	    // Check if a user session exists (i.e., if the user is logged in)
	    if (request.getSession(false) != null) {
	        
	        // Set up the page with the post details for editing
	        setupEditPostPage(id, model, request);
	        
	        // Add a notice to the model indicating the comment text area was empty
	        model.addAttribute("notice", "To add a comment, you must enter the comment in the comment text area");
	        
	        // Return the view for editing the post
	        return "edit-post";
	    } else {
	        // If the user is not logged in, prepare the login page
	        model.addAttribute("user", new User());
	        
	        // Redirect the user to the login page
	        return "login";
	    }
	}

	
	@GetMapping("/dashboard/edit/{id}")
	public String editPostPageSetup(@PathVariable int id, Model model, HttpServletRequest request) {
		 // Check if a user session exists (i.e., if the user is logged in)
	    if (request.getSession(false) != null) {
	        
	        // Set up the page with the post details for editing
	        setupEditPostPage(id, model, request);

	        // Return the view for editing the post
	        return "edit-post";
	    } else {
	        // If the user is not logged in, prepare the login page
	        model.addAttribute("user", new User());
	        
	        // Redirect the user to the login page
	        return "login";
	    }
	}
	
	// Method to set up the dashboard page by populating the model with necessary data
	public Model setupDashboardPage(Model model, HttpServletRequest request) {
	    try {
	        // Retrieve the current user from the session
	        User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
	        Integer userId = sessionUser.getId();
	        
	        // Fetch only posts related to the current user
	        List<Post> postList = postRepository.findAllPostByUserId(userId);
	        
	        // Extract all unique user IDs from the list of posts
	        Set<Integer> userIds = postList.stream()
	            .map(Post::getUserId)
	            .collect(Collectors.toSet());

	        // Fetch all users whose IDs are in the set of user IDs
	        List<User> users = userRepository.findAllById(userIds);

	        // Map user IDs to usernames for quick lookup
	        Map<Integer, String> userIdToUsername = users.stream()
	            .collect(Collectors.toMap(User::getId, User::getUsername));

	        // Update each post with vote count and username
	        postList.forEach(post -> {
	            // Set the vote count for the post
	            post.setVoteCount(voteRepository.countVotesByPostId(post.getId()));
	            // Set the username for the post using the user ID
	            post.setUsername(userIdToUsername.get(post.getUserId()));
	        });
	        
	        // Add attributes to the model
	        model.addAttribute("user", sessionUser);
	        model.addAttribute("loggedIn", sessionUser.isLoggedIn());
	        model.addAttribute("postList", postList);
	        model.addAttribute("post", new Post());
	        
	    } catch (Exception e) {
	        // Handle and log the exception
	        e.printStackTrace();
	    }
	    
	    return model;
	}

	
	// Method to set up the single post page by populating the model with necessary data
	public Model setupSinglePostPage(int id, Model model, HttpServletRequest request) {
	    // Checks if a the user is currently logged in
	    if (request.getSession(false) != null) {
	    	 // Retrieve the current session user from the session object
	        User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
	        model.addAttribute("sessionUser", sessionUser);
	        model.addAttribute("loggedIn", sessionUser.isLoggedIn());
	    }

	    // Fetches the post from the repository by ID
	    Post post = postRepository.findById(id).orElse(null);
	    
	    if (post != null) {
	        // Sets the vote count for the post
	        post.setVoteCount(voteRepository.countVotesByPostId(post.getId()));

	        // Fetches the user who created the post
	        User postUser = userRepository.findById(post.getUserId()).orElse(null);
	        if (postUser != null) {
	            post.setUsername(postUser.getUsername());
	        }

	        // Fetches comments related to the post
	        List<Comment> commentList = null;
	        try {
	            commentList = commentRepository.findAllCommentsByPostId(post.getId());
	        } catch (Exception e) {
	        	// Logs the exception
	            e.printStackTrace(); 
	            //Sets commentList to an empty list
	            commentList =  new ArrayList<>();
	        }

	        // Adds attributes to the model
	        model.addAttribute("post", post);
	        model.addAttribute("commentList", commentList);
	        model.addAttribute("comment", new Comment());
	    } 

	    return model;
	}

	
	// Method to set up the edit post page by populating the model with necessary data
	public Model setupEditPostPage(int id, Model model, HttpServletRequest request) {
		//Checks if the user is currently logged in
		if(request.getSession(false) != null) {
			 // Retrieves the current session user from the session object
			User sessionUser = (User) request.getSession().getAttribute("SESSION_USER");
			// Fetches the post to be edited from the repository by its id
			Post returnPost = postRepository.findById(id).orElse(null);
			
			//Ensures the post exists
			if(returnPost != null) {
			
				//Fetches the user that created the post
				User tempUser = userRepository.findById(returnPost.getUserId()).orElse(null);
				
				//Ensures the post's author exists
				if (tempUser != null) {  
	                // Sets the post's username to the post creator's username
	                returnPost.setUsername(tempUser.getUsername());
	            }
				
				//Sets the voteCount for the post
				returnPost.setVoteCount(voteRepository.countVotesByPostId(returnPost.getId()));
			
				// Fetches comments related to the post
				List<Comment> commentList = null;
				try {
	                // Fetches comments related to the post and handles potential exceptions
	                commentList = commentRepository.findAllCommentsByPostId(returnPost.getId());
	            } catch (Exception e) {
	                // Logs the exception for debugging purposes
	                e.printStackTrace();
	                //Sets commentList to an empty list
	                commentList = new ArrayList<>();  
	            }

				// Adds attributes to the model
				model.addAttribute("post", returnPost);
				model.addAttribute("loggedIn", sessionUser.isLoggedIn());
				model.addAttribute("commentList", commentList);
				model.addAttribute("comment", new Comment());
			}
		}
		return model;
	}
}