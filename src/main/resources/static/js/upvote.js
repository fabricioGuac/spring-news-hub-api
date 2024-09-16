const upvoteClickHandler = async(e) => {
  // Prevents the default form submission behavior
  e.preventDefault();

  // Retrieves the id from the URL by splitting the string and getting the last segment
  const id = window.location.toString().split('/')[window.location.toString().split('/').length - 1];

  // Makes the API call to add an upvote for the post
  const response = await fetch('/posts/upvote', {
    method: 'PUT',
    body: JSON.stringify({
        postId: id
    }),
    headers: {
      'Content-Type': 'application/json'
    }
  });
  
  // If the response is ok refreshes the page to reflect the changes
  if (response.ok) {
    document.location.reload();
  } else {
    // If the response indicates an error, shows the status in an alert
    alert(response.statusText);
  }
}

// Adds the event listener to the upvote button to trigger the upvoteClickHandler
document.querySelector('.upvote-btn').addEventListener('click', upvoteClickHandler);