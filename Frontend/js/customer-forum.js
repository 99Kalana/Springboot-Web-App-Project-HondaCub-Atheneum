$(document).ready(function () {
    // Sample data for forum posts
    const forums = [
        { forum_id: 1, user_id: 1, title: "Honda Cub Maintenance Tips", content: "Discuss your tips for maintaining your Honda Cub...", status: "Active", created_at: "2025-03-19" },
        { forum_id: 2, user_id: 2, title: "Best Spare Parts for Honda Cub", content: "Let's talk about the best spare parts available for your Honda Cub.", status: "Active", created_at: "2025-03-18" },
        { forum_id: 3, user_id: 3, title: "Honda Cub Modifications", content: "Share your experience with modifying Honda Cub.", status: "Active", created_at: "2025-03-17" }
    ];

    // Generate the forum posts
    forums.forEach(function(forum) {
        const forumItem = `
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${forum.title}</h5>
                        <p class="card-text">${forum.content.substring(0, 100)}...</p>
                        <button class="btn btn-primary join-discussion" data-id="${forum.forum_id}" data-title="${forum.title}" data-content="${forum.content}">Join Discussion</button>
                    </div>
                </div>
            </div>
        `;
        $('#forum-list').append(forumItem);
    });

    // Handle Join Discussion button click
    $(document).on('click', '.join-discussion', function () {
        const forumId = $(this).data('id');
        const title = $(this).data('title');
        const content = $(this).data('content');

        // Update modal content
        $('#discussionContent').text(content);
        $('#joinButton').attr('href', `/forum/${forumId}`);

        // Open the modal
        $('#joinDiscussionModal').modal('show');
    });

    // Handle the form submission to add a new discussion
    $('#newDiscussionForm').submit(function(event) {
        event.preventDefault();
        const newTitle = $('#title').val();
        const newContent = $('#content').val();

        // Create new forum post (just a mock for now)
        const newForum = {
            forum_id: forums.length + 1,
            user_id: 1, // Replace with actual user ID from session or context
            title: newTitle,
            content: newContent,
            status: "Active",
            created_at: new Date().toISOString().split('T')[0]
        };

        forums.push(newForum); // Add the new forum post to the array

        // Append the new forum post to the page
        const forumItem = `
            <div class="col-md-4">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${newForum.title}</h5>
                        <p class="card-text">${newForum.content.substring(0, 100)}...</p>
                        <button class="btn btn-primary join-discussion" data-id="${newForum.forum_id}" data-title="${newForum.title}" data-content="${newForum.content}">Join Discussion</button>
                    </div>
                </div>
            </div>
        `;
        $('#forum-list').append(forumItem);

        // Close the modal
        $('#newDiscussionModal').modal('hide');
    });
});
