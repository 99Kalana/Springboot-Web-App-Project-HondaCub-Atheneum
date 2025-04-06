// admin-forum-blogs.js

$(document).ready(function() {
    // Sample forum and blog data (can be replaced with real data from an API)
    const forums = [
        { forumId: 'F001', title: 'Forum 1', content: 'Content 1', status: 'Active', createdAt: '2025-03-18', userId: 'U001' },
        { forumId: 'F002', title: 'Forum 2', content: 'Content 2', status: 'Closed', createdAt: '2025-02-20', userId: 'U002' },
        // Add more forums here
    ];

    const blogs = [
        { blogId: 'B001', title: 'Blog 1', content: 'Blog Content 1', status: 'Published', createdAt: '2025-03-18', userId: 'U001' },
        { blogId: 'B002', title: 'Blog 2', content: 'Blog Content 2', status: 'Draft', createdAt: '2025-02-15', userId: 'U002' },
        // Add more blogs here
    ];

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
    });

    // Function to render the forums table
    function renderForumsTable(filteredForums) {
        const forumRows = filteredForums.map(forum => {
            return `
                <tr>
                    <td>${forum.forumId}</td>
                    <td>${forum.title}</td>
                    <td>${forum.content}</td>
                    <td>${forum.status}</td>
                    <td>${forum.createdAt}</td>
                    <td>${forum.userId}</td>
                    <td>
                        <button class="btn btn-warning editForumBtn" data-forum-id="${forum.forumId}">Edit</button>
                    </td>
                </tr>
            `;
        }).join('');
        $('#forumsTable').html(forumRows);
    }

    // Function to render the blogs table
    function renderBlogsTable(filteredBlogs) {
        const blogRows = filteredBlogs.map(blog => {
            return `
                <tr>
                    <td>${blog.blogId}</td>
                    <td>${blog.title}</td>
                    <td>${blog.content}</td>
                    <td>${blog.status}</td>
                    <td>${blog.createdAt}</td>
                    <td>${blog.userId}</td>
                    <td>
                        <button class="btn btn-warning editBlogBtn" data-blog-id="${blog.blogId}">Edit</button>
                    </td>
                </tr>
            `;
        }).join('');
        $('#blogsTable').html(blogRows);
    }

    // Function to filter and display forums based on search and status
    function filterAndRenderForums() {
        const searchText = $('#searchForum').val().toLowerCase();
        const selectedStatus = $('#filterForumStatus').val();
        const filteredForums = forums.filter(forum => {
            const matchesSearch = forum.title.toLowerCase().includes(searchText) || forum.content.toLowerCase().includes(searchText);
            const matchesStatus = selectedStatus === 'all' || forum.status === selectedStatus;
            return matchesSearch && matchesStatus;
        });
        renderForumsTable(filteredForums);
    }

    // Function to filter and display blogs based on search and status
    function filterAndRenderBlogs() {
        const searchText = $('#searchBlog').val().toLowerCase();
        const selectedStatus = $('#filterBlogStatus').val();
        const filteredBlogs = blogs.filter(blog => {
            const matchesSearch = blog.title.toLowerCase().includes(searchText) || blog.content.toLowerCase().includes(searchText);
            const matchesStatus = selectedStatus === 'all' || blog.status === selectedStatus;
            return matchesSearch && matchesStatus;
        });
        renderBlogsTable(filteredBlogs);
    }

    // Initial render of the forums and blogs
    renderForumsTable(forums);
    renderBlogsTable(blogs);

    // Event listeners
    $('#searchForum').on('input', filterAndRenderForums);
    $('#filterForumStatus').on('change', filterAndRenderForums);

    $('#searchBlog').on('input', filterAndRenderBlogs);
    $('#filterBlogStatus').on('change', filterAndRenderBlogs);

    // Open the 'Create Forum' modal when the button is clicked
    $('#createForumBtn').on('click', function() {
        // Clear form inputs in case there was any previous input
        $('#createForumTitle').val('');
        $('#createForumContent').val('');
        $('#createForumStatus').val('Active'); // Default status
        $('#createForumModal').modal('show'); // Show the modal
    });

    // Open the 'Create Blog' modal when the button is clicked
    $('#createBlogBtn').on('click', function() {
        // Clear form inputs in case there was any previous input
        $('#createBlogTitle').val('');
        $('#createBlogContent').val('');
        $('#createBlogStatus').val('Draft'); // Default status
        $('#createBlogImage').val(''); // Clear any previously added image
        $('#createBlogModal').modal('show'); // Show the modal
    });

    // Handle 'Create Forum' form submission
    $('#createForumForm').on('submit', function(event) {
        event.preventDefault();
        const newForum = {
            forumId: 'F00' + (forums.length + 1),
            title: $('#createForumTitle').val(),
            content: $('#createForumContent').val(),
            status: $('#createForumStatus').val(),
            createdAt: new Date().toISOString().split('T')[0], // Current date
            userId: 'U001', // Assuming userId is 'U001', this can be dynamic
        };
        forums.push(newForum); // Add the new forum to the array
        filterAndRenderForums(); // Re-render the forums table after creation
        $('#createForumModal').modal('hide'); // Hide the modal after submission
    });

    // Handle 'Create Blog' form submission
    $('#createBlogForm').on('submit', function(event) {
        event.preventDefault();
        const newBlog = {
            blogId: 'B00' + (blogs.length + 1),
            title: $('#createBlogTitle').val(),
            content: $('#createBlogContent').val(),
            status: $('#createBlogStatus').val(),
            createdAt: new Date().toISOString().split('T')[0], // Current date
            userId: 'U001', // Assuming userId is 'U001', this can be dynamic
            image: $('#createBlogImage').val(),
        };
        blogs.push(newBlog); // Add the new blog to the array
        filterAndRenderBlogs(); // Re-render the blogs table after creation
        $('#createBlogModal').modal('hide'); // Hide the modal after submission
    });

    // Open the 'Edit Forum' modal and populate it with selected forum data
    $(document).on('click', '.editForumBtn', function() {
        const forumId = $(this).data('forum-id');
        const forumToEdit = forums.find(forum => forum.forumId === forumId);
        if (forumToEdit) {
            $('#editForumTitle').val(forumToEdit.title);
            $('#editForumContent').val(forumToEdit.content);
            $('#editForumStatus').val(forumToEdit.status);
            $('#editForumModal').modal('show');
        }
    });

    // Open the 'Edit Blog' modal and populate it with selected blog data
    $(document).on('click', '.editBlogBtn', function() {
        const blogId = $(this).data('blog-id');
        const blogToEdit = blogs.find(blog => blog.blogId === blogId);
        if (blogToEdit) {
            $('#editBlogTitle').val(blogToEdit.title);
            $('#editBlogContent').val(blogToEdit.content);
            $('#editBlogStatus').val(blogToEdit.status);
            $('#editBlogImage').val(blogToEdit.image || '');  // Add image handling if necessary
            $('#editBlogModal').modal('show');
        }
    });

    // Handle 'Edit Forum' form submission
    $('#editForumForm').on('submit', function(event) {
        event.preventDefault();
        const forumId = $('#editForumTitle').data('forum-id');
        const updatedForum = {
            forumId: forumId,
            title: $('#editForumTitle').val(),
            content: $('#editForumContent').val(),
            status: $('#editForumStatus').val(),
        };
        // Update the forum in your database or array (here, we just update the forums array)
        const forumIndex = forums.findIndex(forum => forum.forumId === forumId);
        if (forumIndex !== -1) {
            forums[forumIndex] = updatedForum;
            filterAndRenderForums(); // Re-render forums table after update
            $('#editForumModal').modal('hide');
        }
    });

    // Handle 'Edit Blog' form submission
    $('#editBlogForm').on('submit', function(event) {
        event.preventDefault();
        const blogId = $('#editBlogTitle').data('blog-id');
        const updatedBlog = {
            blogId: blogId,
            title: $('#editBlogTitle').val(),
            content: $('#editBlogContent').val(),
            status: $('#editBlogStatus').val(),
            image: $('#editBlogImage').val(),
        };
        // Update the blog in your database or array (here, we just update the blogs array)
        const blogIndex = blogs.findIndex(blog => blog.blogId === blogId);
        if (blogIndex !== -1) {
            blogs[blogIndex] = updatedBlog;
            filterAndRenderBlogs(); // Re-render blogs table after update
            $('#editBlogModal').modal('hide');
        }
    });
});
