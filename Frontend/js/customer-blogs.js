$(document).ready(function () {

    const createBlogForm = $("#createBlogForm");
    const blogTitleInput = $("#blogTitle");
    const blogContentInput = $("#blogContents");

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

    $('#createBlogModal').on('shown.bs.modal', function () {
        blogTitleInput.on('focus', function() { clearError(blogTitleInput); });
        blogContentInput.on('focus', function() { clearError(blogContentInput); });
    });

    // Ensure to remove the focus event listeners when the modal is hidden
    $('#createBlogModal').on('hidden.bs.modal', function () {
        blogTitleInput.off('focus');
        blogContentInput.off('focus');
    });

    const authToken = localStorage.getItem('authToken');
    const baseUrl = 'http://localhost:8080/api/v1/customer/blogs';

    // Function to load all blogs
    function loadBlogs() {
        $.ajax({
            url: baseUrl,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response && response.status === 200 && response.data) {
                    $('#blog-list').empty();
                    response.data.forEach(function (blog) {
                        const blogItem = `
                            <div class="col-md-4">
                                <div class="card">
                                    <img src="${blog.imageUrl}" class="card-img-top" alt="${blog.title}">
                                    <div class="card-body">
                                        <h5 class="card-title">${blog.title}</h5>
                                        <p class="card-text">${blog.content.substring(0, 100)}...</p>
                                        <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#blogModal"
                                            data-title="${blog.title}" data-content="${blog.content}" data-image="${blog.imageUrl}">
                                            Read More
                                        </button>
                                    </div>
                                </div>
                            </div>
                        `;
                        $('#blog-list').append(blogItem);
                    });
                } else {
                    console.error("Failed to load blogs:", response);
                    alert("Failed to load blogs.");
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading blogs:", error);
                alert("Error loading blogs.");
            }
        });
    }

    // Modal pop-up with blog content
    $('#blogModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var title = button.data('title');
        var content = button.data('content');
        var imageUrl = button.data('image');

        var modal = $(this);
        modal.find('.modal-title').text(title);
        modal.find('#blogContent').text(content);
        modal.find('#blogImage').attr('src', imageUrl);
    });

    // Create blog form submission
    $('#createBlogForm').on('submit', function (e) {
        e.preventDefault();

        let isValid = true;

        // Validate Blog Title
        if (blogTitleInput.val().trim() === "") {
            displayError(blogTitleInput, "Blog title cannot be empty.");
            isValid = false;
        } else {
            clearError(blogTitleInput);
        }

        // Validate Blog Content
        if (blogContentInput.val().trim() === "") {
            displayError(blogContentInput, "Blog content cannot be empty.");
            isValid = false;
        } else {
            clearError(blogContentInput);
        }

        if (!isValid) {
            return; // Stop the form submission if validation fails
        }

        const formData = new FormData();
        formData.append('title', $('#blogTitle').val());
        formData.append('content', $('#blogContents').val());

        const imageFile = $('#blogImages')[0].files[0]; // Get single image
        if (imageFile) {
            formData.append('image', imageFile);
        }

        // Extract user ID from JWT token (You'll need a function to do this)
        const userId = getUserIdFromToken(authToken); // Implement this function

        if (userId) {
            formData.append('userId', userId);

            $.ajax({
                url: baseUrl,
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                headers: {
                    'Authorization': `Bearer ${authToken}`
                },
                success: function (response) {
                    if (response && response.status === 201) {
                        alert('Blog created successfully!');
                        $('#createBlogModal').modal('hide');
                        loadBlogs(); // Reload blogs
                    } else {
                        alert('Failed to create blog. Please try again.');
                    }
                },
                error: function (err) {
                    console.error('Error creating blog:', err);
                    alert('Error creating blog. Please try again.');
                }
            });
        } else {
            alert('User ID not found. Please log in again.');
        }
    });

    // Function to extract user ID from JWT token
    function getUserIdFromToken(token) {
        if (!token) return null;

        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            return payload.userId;
        } catch (error) {
            console.error('Error extracting user ID from token:', error);
            return null;
        }
    }

    loadBlogs(); // Load blogs on page load
});