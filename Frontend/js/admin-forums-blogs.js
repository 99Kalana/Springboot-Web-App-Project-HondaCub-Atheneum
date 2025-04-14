$(document).ready(function() {

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function() {
        $(".sidebar").toggleClass("collapsed");
    });

    function renderForumsTable(forums) {
        const tableBody = $('#forumsTable');
        tableBody.empty();
        if (forums && forums.length > 0) {
            forums.forEach(forum => {
                const row = `
                <tr>
                    <td>${forum.forumId}</td>
                    <td>${forum.title}</td>
                    <td>${forum.content}</td>
                    <td>${forum.status}</td>
                    <td>${forum.createdAt}</td>
                    <td>${forum.userId}</td>
                    <td>
                        <button class="btn btn-warning editForumBtn" data-forum-id="${forum.forumId}" data-user-id="${forum.userId}" data-bs-toggle="modal" data-bs-target="#editForumModal"><i class="bi bi-pencil-square"></i> Edit</button>
                        <button class="btn btn-danger deleteForumBtn" data-forum-id="${forum.forumId}"><i class="bi bi-trash"></i> Delete</button>
                    </td>
                </tr>
            `;
                tableBody.append(row);
            });
        } else {
            tableBody.append('<tr><td colspan="7" class="text-center">No forums found.</td></tr>');
        }
    }

    function renderBlogsTable(blogs) {
        const tableBody = $('#blogsTable');
        tableBody.empty();
        if (blogs && blogs.length > 0) {
            blogs.forEach(blog => {
                const row = `
                <tr>
                    <td>${blog.blogId}</td>
                    <td>${blog.title}</td>
                    <td>${blog.content}</td>
                    <td>${blog.status}</td>
                    <td>${blog.createdAt}</td>
                    <td>${blog.userId}</td>
                    <td>
                        <button class="btn btn-warning editBlogBtn" data-blog-id="${blog.blogId}" data-user-id="${blog.userId}" data-bs-toggle="modal" data-bs-target="#editBlogModal"><i class="bi bi-pencil-square"></i> Edit</button>
                        <button class="btn btn-danger deleteBlogBtn" data-blog-id="${blog.blogId}"><i class="bi bi-trash"></i> Delete</button>
                    </td>
                </tr>
            `;
                tableBody.append(row);
            });
        } else {
            tableBody.append('<tr><td colspan="7" class="text-center">No blogs found.</td></tr>');
        }
    }

    // Function to load forums from the backend
    function loadForums() {
        $.ajax({
            url: "http://localhost:8080/api/v1/adminforumblogs/forums/getAll",
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    renderForumsTable(response.data);
                } else {
                    console.error("Failed to load forums:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching forums:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // Function to load blogs from the backend
    function loadBlogs() {
        $.ajax({
            url: "http://localhost:8080/api/v1/adminforumblogs/blogs/getAll",
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    renderBlogsTable(response.data);
                } else {
                    console.error("Failed to load blogs:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching blogs:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // Function to filter and display forums based on search and status
    function filterAndRenderForums() {
        const searchText = $('#searchForum').val().toLowerCase();
        const selectedStatus = $('#filterForumStatus').val();
        let url = `http://localhost:8080/api/v1/adminforumblogs/forums/search?status=${selectedStatus}`; // Changed to /search
        if (searchText) {
            url += `&query=${searchText}`;
        }

        $.ajax({
            url: url,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    renderForumsTable(response.data);
                } else {
                    console.error("Failed to filter forums:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error filtering forums:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // Function to filter and display blogs based on search and status
    function filterAndRenderBlogs() {
        const searchText = $('#searchBlog').val().toLowerCase();
        const selectedStatus = $('#filterBlogStatus').val();
        let url = `http://localhost:8080/api/v1/adminforumblogs/blogs/search?status=${selectedStatus}`; // Changed to /search
        if (searchText) {
            url += `&query=${searchText}`;
        }

        $.ajax({
            url: url,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    renderBlogsTable(response.data);
                } else {
                    console.error("Failed to filter blogs:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error filtering blogs:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // Event listeners
    $('#searchForum').on('input', filterAndRenderForums);
    $('#filterForumStatus').on('change', filterAndRenderForums);

    $('#searchBlog').on('input', filterAndRenderBlogs);
    $('#filterBlogStatus').on('change', filterAndRenderBlogs);

    // Open the 'Create Forum' modal when the button is clicked
    $('#createForumBtn').on('click', function() {
        $('#createForumForm')[0].reset();
        $('#createForumModal').modal('show');
    });

    // Open the 'Create Blog' modal when the button is clicked
    $('#createBlogBtn').on('click', function() {
        $('#createBlogForm')[0].reset();
        $('#createBlogModal').modal('show');
    });

    // Handle 'Create Forum' form submission
    $('#createForumForm').on('submit', function(event) {
        event.preventDefault();
        const forumData = {
            title: $('#createForumTitle').val(),
            content: $('#createForumContent').val(),
            status: $('#createForumStatus').val(),
            userId: 1, // Replace with actual userId logic
        };

        $.ajax({
            url: "http://localhost:8080/api/v1/adminforumblogs/forums/create",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(forumData),
            success: function(response) {
                if (response.status === 201) {
                    loadForums();
                    $('#createForumModal').modal('hide');
                } else {
                    alert("Failed to create forum.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error creating forum:", error);
                console.log("Response:", xhr.responseText);
                alert("Error creating forum.");
            }
        });
    });

    // Handle 'Create Blog' form submission
    /*$('#createBlogForm').on('submit', function(event) {
        event.preventDefault();
        const blogData = {
            title: $('#createBlogTitle').val(),
            content: $('#createBlogContent').val(),
            imageUrl: $('#createBlogImage').val(),
            status: $('#createBlogStatus').val(),
            userId: 1, // Replace with actual userId logic
        };

        $.ajax({
            url: "http://localhost:8080/api/v1/adminforumblogs/blogs/create",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(blogData),
            success: function(response) {
                if (response.status === 201) {
                    loadBlogs();
                    $('#createBlogModal').modal('hide');
                } else {
                    alert("Failed to create blog.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error creating blog:", error);
                console.log("Response:", xhr.responseText);
                alert("Error creating blog.");
            }
        });
    });*/
    // Handle 'Create Blog' form submission
    $('#createBlogForm').on('submit', function(event) {
        event.preventDefault();

        const formData = new FormData();
        formData.append('title', $('#createBlogTitle').val());
        formData.append('content', $('#createBlogContent').val());
        formData.append('status', $('#createBlogStatus').val());
        formData.append('userId', 1); // Replace with actual userId logic

        const imageFile = $('#createBlogImage')[0].files[0]; // Get the file from the input
        if (imageFile) {
            formData.append('image', imageFile); // Append the file to the FormData
        }

        $.ajax({
            url: "http://localhost:8080/api/v1/adminforumblogs/blogs/create",
            method: "POST",
            processData: false, // Important for FormData
            contentType: false, // Important for FormData
            data: formData,
            success: function(response) {
                if (response.status === 201) {
                    loadBlogs();
                    $('#createBlogModal').modal('hide');
                } else {
                    alert("Failed to create blog.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error creating blog:", error);
                console.log("Response:", xhr.responseText);
                alert("Error creating blog.");
            }
        });
    });

    // Open the 'Edit Forum' modal and populate it with selected forum data
    $(document).on('click', '.editForumBtn', function() {
        const forumId = $(this).data('forum-id');
        $.ajax({
            url: `http://localhost:8080/api/v1/adminforumblogs/forums/get/${forumId}`,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    $('#editForumTitle').val(response.data.title);
                    $('#editForumContent').val(response.data.content);
                    $('#editForumStatus').val(response.data.status);
                    $('#editForumTitle').data('forum-id', forumId);
                    $('#editForumTitle').data('user-id', response.data.userId); // Store userId
                    $('#editForumModal').modal('show');
                } else {
                    alert("Failed to load forum data.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching forum data:", error);
                console.log("Response:", xhr.responseText);
                alert("Error fetching forum data.");
            }
        });
    });

    // Open the 'Edit Blog' modal and populate it with selected blog data
    $(document).on('click', '.editBlogBtn', function() {
        const blogId = $(this).data('blog-id'); // Get blog ID from the button
        const userId = $(this).data('user-id'); // Get user ID from the button

        console.log("Blog ID:", blogId); // Debugging
        console.log("User  ID:", userId); // Debugging

        $.ajax({
            url: `http://localhost:8080/api/v1/adminforumblogs/blogs/get/${blogId}`,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    $('#editBlogTitle').val(response.data.title);
                    $('#editBlogContent').val(response.data.content);
                    $('#editBlogStatus').val(response.data.status);
                    // Do not set the value of the file input directly
                    // $('#editBlogImage').val(response.data.imageUrl); // Remove this line
                    $('#editBlogTitle').data('blog-id', blogId); // Store blogId
                    $('#editBlogTitle').data('user-id', userId); // Store userId
                    $('#editBlogModal').modal('show');
                } else {
                    alert("Failed to load blog data.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching blog data:", error);
                console.log("Response:", xhr.responseText);
                alert("Error fetching blog data.");
            }
        });
    });


    // Handle 'Edit Forum' form submission
    $('#editForumForm').on('submit', function(event) {
        event.preventDefault();
        const forumId = $('#editForumTitle').data('forum-id');
        const userId = $('#editForumTitle').data('user-id');
        const now = new Date();
        const createdAt = now.toISOString().slice(0, 19).replace('T', ' ');
        const updatedForum = {
            forumId: forumId,
            title: $('#editForumTitle').val(),
            content: $('#editForumContent').val(),
            status: $('#editForumStatus').val(),
            createdAt: createdAt,
            userId: userId, //add the userId.
        };

        $.ajax({
            url: `http://localhost:8080/api/v1/adminforumblogs/forums/update/${forumId}`,
            method: "PUT",
            contentType: "application/json",
            data: JSON.stringify(updatedForum),
            success: function(response) {
                if (response.status === 200) {
                    loadForums();
                    $('#editForumModal').modal('hide');
                } else {
                    alert("Failed to update forum.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error updating forum:", error);
                console.log("Response:", xhr.responseText);
                alert("Error updating forum.");
            }
        });
    });

    // Handle 'Edit Blog' form submission
    $('#editBlogForm').on('submit', function(event) {
        event.preventDefault();
        const blogId = $('#editBlogTitle').data('blog-id'); // Retrieve blogId from data attribute
        const userId = $('#editBlogTitle').data('user-id'); // Retrieve userId from data attribute

        console.log("Submitting Blog ID:", blogId); // Debugging
        console.log("Submitting User ID:", userId); // Debugging

        const formData = new FormData();
        formData.append('title', $('#editBlogTitle').val());
        formData.append('content', $('#editBlogContent').val());
        formData.append('status', $('#editBlogStatus').val());
        formData.append('userId', userId); // Use the userId from the modal

        const imageFile = $('#editBlogImage')[0].files[0]; // Get the file from the input
        if (imageFile) {
            formData.append('image', imageFile); // Append the new file to the FormData
        }

        $.ajax({
            url: `http://localhost:8080/api/v1/adminforumblogs/blogs/update/${blogId}`,
            method: "PUT",
            processData: false, // Important for FormData
            contentType: false, // Important for FormData
            data: formData,
            success: function(response) {
                if (response.status === 200) {
                    loadBlogs();
                    $('#editBlogModal').modal('hide');
                } else {
                    alert("Failed to update blog.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error updating blog:", error);
                console.log("Response:", xhr.responseText);
                alert("Error updating blog.");
            }
        });
    });

    // Delete forum
    $(document).on('click', '.deleteForumBtn', function() {
        const forumId = $(this).data('forum-id');
        if (confirm("Are you sure you want to delete this forum?")) {
            $.ajax({
                url: `http://localhost:8080/api/v1/adminforumblogs/forums/delete/${forumId}`,
                method: "DELETE",
                success: function(response) {
                    if (response.status === 200) {
                        loadForums();
                    } else {
                        alert("Failed to delete forum.");
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Error deleting forum:", error);
                    console.log("Response:", xhr.responseText);
                    alert("Error deleting forum.");
                }
            });
        }
    });

    // Delete blog
    $(document).on('click', '.deleteBlogBtn', function() {
        const blogId = $(this).data('blog-id');
        if (confirm("Are you sure you want to delete this blog?")) {
            $.ajax({
                url: `http://localhost:8080/api/v1/adminforumblogs/blogs/delete/${blogId}`,
                method: "DELETE",
                success: function(response) {
                    if (response.status === 200) {
                        loadBlogs();
                    } else {
                        alert("Failed to delete blog.");
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Error deleting blog:", error);
                    console.log("Response:", xhr.responseText);
                    alert("Error deleting blog.");
                }
            });
        }
    });

    // Initial Load
    loadForums();
    loadBlogs();
});