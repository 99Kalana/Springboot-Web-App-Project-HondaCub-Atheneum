$(document).ready(function () {
    // Sample data for blogs
    const blogs = [
        { blog_id: 1, user_id: 1, title: "Exploring Honda Cub Mods", content: "This blog covers the coolest modifications you can do on your Honda Cub. Honda Cub is the most iconic motorcycle in the world, and mods make it even more exciting. Here are the top mods that you can consider.", image_url: "images/25.jpg", status: "Published", created_at: "2025-03-19" },
        { blog_id: 2, user_id: 2, title: "Honda Cub Spare Parts Review", content: "A detailed review of the top spare parts available for the Honda Cub. From tires to exhaust systems, we discuss the best replacements and upgrades you can choose for your Cub.", image_url: "images/25.jpg", status: "Published", created_at: "2025-03-18" },
        { blog_id: 3, user_id: 3, title: "Honda Cub Maintenance 101", content: "Essential maintenance tips for keeping your Honda Cub running smoothly. Regular maintenance is key to ensuring your Honda Cub stays in top condition for years to come.", image_url: "images/25.jpg", status: "Published", created_at: "2025-03-17" }
    ];

    // Generate the blog posts
    blogs.forEach(function(blog) {
        const blogItem = `
            <div class="col-md-4">
                <div class="card">
                    <img src="${blog.image_url}" class="card-img-top" alt="${blog.title}">
                    <div class="card-body">
                        <h5 class="card-title">${blog.title}</h5>
                        <p class="card-text">${blog.content.substring(0, 100)}...</p>
                        <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#blogModal" 
                            data-title="${blog.title}" data-content="${blog.content}" data-image="${blog.image_url}">
                            Read More
                        </button>
                    </div>
                </div>
            </div>
        `;
        $('#blog-list').append(blogItem);
    });

    // Modal pop-up with blog content
    $('#blogModal').on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget); // Button that triggered the modal
        var title = button.data('title'); // Extract info from data-* attributes
        var content = button.data('content');
        var imageUrl = button.data('image');

        var modal = $(this);
        modal.find('.modal-title').text(title);
        modal.find('#blogContent').text(content);
        modal.find('#blogImage').attr('src', imageUrl);
    });


    $('#createBlogForm').on('submit', function(e) {
        e.preventDefault();

        // Gather form data
        var formData = new FormData();
        formData.append('title', $('#blogTitle').val());
        formData.append('content', $('#blogContent').val());
        formData.append('image', $('#blogImage')[0].files[0]);

        $.ajax({
            url: '/api/create-blog',  // Endpoint to save the blog (Backend API)
            type: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(response) {
                alert('Blog created successfully!');
                location.reload();  // Reload the page to show the new blog
            },
            error: function(err) {
                alert('Error creating blog.');
            }
        });
    });


});
