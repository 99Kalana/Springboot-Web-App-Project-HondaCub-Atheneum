$(document).ready(function () {

    const newDiscussionForm = $("#newDiscussionForm");
    const discussionTitleInput = $("#title");
    const discussionContentInput = $("#content");

    // Function to display validation errors
    function displayError(inputElement, message) {
        inputElement.addClass("is-invalid");
        inputElement.next(".invalid-feedback").text(message).show();
    }

    // Function to clear validation errors
    function clearError(inputElement) {
        inputElement.removeClass("is-invalid");
        inputElement.next(".invalid-feedback").hide();
    }

    $('#newDiscussionModal').on('shown.bs.modal', function () {
        discussionTitleInput.on('focus', function() { clearError(discussionTitleInput); });
        discussionContentInput.on('focus', function() { clearError(discussionContentInput); });
    });

    $('#newDiscussionModal').on('hidden.bs.modal', function () {
        discussionTitleInput.off('focus');
        discussionContentInput.off('focus');
    });

    const authToken = localStorage.getItem('authToken'); // Retrieve auth token from localStorage
    let currentForumId; // Global variable to store the current forum ID

    // Function to fetch and display forums
    function loadForums() {
        $.ajax({
            url: 'http://localhost:8080/api/v1/customer/forums',
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + authToken // Send auth token in header
            },
            success: function (response) {
                $('#forum-list').empty(); // Clear existing forum list
                if (response && response.data) {
                    response.data.forEach(function (forum) {
                        const forumItem = `
                        <div class="col-md-4">
                            <div class="card">
                                <div class="card-body">
                                    <h5 class="card-title">${forum.title}</h5>
                                    <p class="card-text">${forum.content.substring(0, 100)}...</p>
                                    <button class="btn btn-primary join-discussion" data-id="${forum.forumId}" data-title="${forum.title}" data-content="${forum.content}">Join Discussion</button>
                                </div>
                            </div>
                        </div>
                    `;
                        $('#forum-list').append(forumItem);
                    });
                }
            },
            error: function (xhr, status, error) {
                console.error('Error fetching forums:', xhr, status, error);
                alert('Failed to load forums. Please try again.');
            }
        });
    }

    // Load forums on page load
    loadForums();

    // Handle Join Discussion button click
    $(document).on('click', '.join-discussion', function () {
        const title = $(this).data('title');
        const content = $(this).data('content');
        currentForumId = $(this).data('id'); // Store the forum ID in the global variable

        // Update modal content
        $('#discussionContent').text(content);

        // Open the modal
        $('#joinDiscussionModal').modal('show');
    });

    // Handle the form submission to add a new discussion
    $('#newDiscussionForm').submit(function (event) {
        event.preventDefault();

        let isValid = true;

        // Validate Title
        if (discussionTitleInput.val().trim() === "") {
            displayError(discussionTitleInput, "Title cannot be empty.");
            isValid = false;
        } else {
            clearError(discussionTitleInput);
        }

        // Validate Content
        if (discussionContentInput.val().trim() === "") {
            displayError(discussionContentInput, "Content cannot be empty.");
            isValid = false;
        } else {
            clearError(discussionContentInput);
        }

        if (!isValid) {
            return; // Stop the form submission if validation fails
        }


        const newTitle = $('#title').val();
        const newContent = $('#content').val();

        $.ajax({
            url: 'http://localhost:8080/api/v1/customer/forums',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                title: newTitle,
                content: newContent
            }),
            headers: {
                'Authorization': 'Bearer ' + authToken // Send auth token in header
            },
            success: function (response) {
                alert('Forum created successfully!');
                $('#newDiscussionModal').modal('hide');
                loadForums(); // Reload forums
            },
            error: function (xhr, status, error) {
                console.error('Error creating forum:', xhr, status, error);
                alert('Failed to create forum. Please try again.');
            }
        });
    });

    // Handle the form submission to join a discussion
    $('#joinDiscussionForm').submit(function (event) {
        event.preventDefault();
        const responseContent = $('#response').val();
        const forumId = currentForumId; // Use the stored forum ID

        $.ajax({
            url: `http://localhost:8080/api/v1/customer/forums/${forumId}/responses`,
            type: 'POST',
            contentType: 'text/plain', // Send as plain text
            data: responseContent,
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('authToken') // Include the auth token
            },
            success: function (response) {
                alert('Your response has been submitted: ' + responseContent);
                $('#joinDiscussionModal').modal('hide');
                $('#response').val(''); // Clear the response textarea
                loadForums(); // Optionally reload forums to see the updated content
            },
            error: function (xhr, status, error) {
                console.error('Error submitting response:', xhr, status, error);
                alert('Failed to submit response. Please try again.');
            }
        });
    });
});