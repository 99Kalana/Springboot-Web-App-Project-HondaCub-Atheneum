$(document).ready(function () {
    // Simulate fetching reviews from the database (using static data for now)
    const reviews = [
        { review_id: 1, user_id: 101, part_id: 1, rating: 4.5, comment: 'Great product, loved it!', review_date: '2025-03-01 10:30:00' },
        { review_id: 2, user_id: 102, part_id: 2, rating: 3.0, comment: 'Decent quality, but could be better.', review_date: '2025-03-02 12:00:00' },
        { review_id: 3, user_id: 103, part_id: 3, rating: 5.0, comment: 'Amazing performance! Highly recommended.', review_date: '2025-03-03 14:30:00' }
    ];

    // Render the reviews dynamically in the table
    function loadReviews() {
        let rows = '';
        reviews.forEach(review => {
            rows += `
                <tr>
                    <td>${review.review_id}</td>
                    <td>${review.user_id}</td>
                    <td>${review.part_id}</td>
                    <td>${review.rating}</td>
                    <td>${review.comment}</td>
                    <td>${review.review_date}</td>
                    <td>
                        <button class="btn btn-primary reply-btn" data-review-id="${review.review_id}" data-user-id="${review.user_id}" data-part-id="${review.part_id}">
                            Reply
                        </button>
                    </td>
                </tr>
            `;
        });
        $('#reviewsTable tbody').html(rows);
    }

    // Event listener for the reply button
    $(document).on('click', '.reply-btn', function () {
        const reviewId = $(this).data('review-id');
        const userId = $(this).data('user-id');
        const partId = $(this).data('part-id');

        // Pre-fill the modal with information for the review being replied to (for now, just the reviewId)
        $('#replyModalLabel').text(`Reply to Review #${reviewId}`);
        $('#replyForm').off('submit').on('submit', function (e) {
            e.preventDefault();

            const replyText = $('#replyComment').val();

            // Here, you would make an AJAX call to save the reply in the database
            console.log(`Replying to review #${reviewId} from user #${userId} about part #${partId}`);
            console.log('Reply:', replyText);

            // Simulate saving the reply and closing the modal
            alert('Reply submitted successfully!');
            $('#replyModal').modal('hide');
        });

        // Open the reply modal
        $('#replyModal').modal('show');
    });

    // Load reviews when the page is ready
    loadReviews();

    // Sidebar Toggle Functionality
    $('#toggleBtn').click(function () {
        $('#sidebar').toggleClass('collapsed');
        $('.content').toggleClass('collapsed');
    });
});
