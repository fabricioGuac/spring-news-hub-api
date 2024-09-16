const deleteFormHandler = async (e) => {
	// Prevents the default form submission behavior
	e.preventDefault();
	
	// Retrieves the id from the URL by splitting the string and getting the last segment
	const id = window.location.toString().split('/')[window.location.toString('/').length - 1];
	
	// Makes the API call to delete the post
	const response = await fetch(`/api/[posts/${id}`, {
		method: 'DELETE'
	});
	
	// If the reponse is ok directs the user to te dashboard
	if(response.ok){
		document.location.replace('/dashboard/');
	} else {
		// If the response indicates an error, shows the status in an alert
		alert(response.statusText);
	}
	
	// Adds the event listener to the delete buttom to trigger the deleteFormHandler
	document.querySelector('.delete-post-btn').addEventListener('click', deleteFormHandler);
}