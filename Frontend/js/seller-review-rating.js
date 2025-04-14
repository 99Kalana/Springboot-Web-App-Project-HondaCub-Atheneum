$(document).ready(function () {
    const baseUrl = 'http://localhost:8080/api/v1'; // Define the base URL
    let searchTimeout; // Debounce timer for live search

    // Function to load reviews from the backend
    function loadReviews(partId = '') {
        let url = `${baseUrl}/seller/reviews`;
        if (partId) {
            url += `?partId=${partId}`;
        }

        $.ajax({
            url: url,
            type: 'GET',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    let rows = '';
                    response.data.forEach(review => {
                        rows += `
                            <tr>
                                <td>${review.reviewId}</td>
                                <td>${review.userId}</td>
                                <td>${review.sparePartId}</td>
                                <td>${review.rating}</td>
                                <td>${review.comment}</td>
                                <td>${review.reviewDate}</td>
                                <td>
                                    <button class="btn btn-primary reply-btn" data-review-id="${review.reviewId}">
                                        Reply
                                    </button>
                                </td>
                            </tr>
                        `;
                    });
                    $('#reviewsTable tbody').html(rows);
                } else {
                    console.error('Failed to load reviews:', response);
                    alert('Failed to load reviews.');
                }
            },
            error: function (error) {
                console.error('Error fetching reviews:', error);
                alert('An error occurred while fetching reviews.');
            }
        });
    }

    // Event listener for the reply button
    $(document).on('click', '.reply-btn', function () {
        const reviewId = $(this).data('review-id');
        $('#replyModalLabel').text(`Reply to Review #${reviewId}`);
        $('#replyForm').off('submit').on('submit', function (e) {
            e.preventDefault();
            const replyText = $('#replyComment').val();
            $.ajax({
                url: `${baseUrl}/seller/reviews/${reviewId}/reply?replyComment=${encodeURIComponent(replyText)}`,
                type: 'PUT',
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
                success: function (response) {
                    if (response.status === 200) {
                        alert('Reply submitted successfully!');
                        $('#replyModal').modal('hide');
                        loadReviews($('#partIdSearch').val()); // Reload with current search
                    } else {
                        alert('Failed to submit reply.');
                    }
                },
                error: function (error) {
                    console.error('Error submitting reply:', error);
                    alert('An error occurred while submitting the reply.');
                }
            });
        });
        $('#replyModal').modal('show');
    });

    // Event listener for Part ID search input
    $('#partIdSearch').on('input', function () {
        const partId = $(this).val();
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(function () {
            loadReviews(partId);
        }, 300);

        if (partId.trim() === '') {
            loadReviews(); // Load all if search is empty
        }
    });

    // Event listener for Part ID search input (Enter key)
    $('#partIdSearch').keypress(function (event) {
        if (event.which === 13) { // Enter key pressed
            clearTimeout(searchTimeout);
            loadReviews($(this).val());
        }
    });

    // Load all reviews on page load
    loadReviews();

    // Sidebar Toggle Functionality
    $('#toggleBtn').click(function () {
        $('#sidebar').toggleClass('collapsed');
        $('.content').toggleClass('collapsed');
    });
});

// New JavaScript function to trigger seller review report download
function downloadSellerReviewReport() {
    const authToken = localStorage.getItem('authToken');
    if (authToken) {
        fetch(`http://localhost:8080/api/v1/seller/reviews/report/download`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + authToken
            }
        })
            .then(response => response.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'seller_reviews_report.pdf';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
            })
            .catch(error => {
                console.error('Error downloading review report:', error);
                alert('Failed to download review report.');
            });
    } else {
        alert('Authentication token not found. Please log in again.');
    }
}