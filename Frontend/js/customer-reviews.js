$(document).ready(function () {
    const authToken = localStorage.getItem('authToken');
    let currentPartId = null;

    // Function to display product details
    function loadProductDetails(partId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/reviews/${partId}/details`,
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + authToken
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const product = response.data;
                    let images = product.images;

                    if (images && images.length > 0) {
                        $('#modalImages').show();
                        let carouselItems = '';
                        images.forEach((image, index) => {
                            carouselItems += `
                                <div class="carousel-item ${index === 0 ? 'active' : ''}">
                                    <img src="${image.imageUrl}" class="d-block w-100" alt="Product Image" style="max-height: 300px; object-fit: contain;">
                                </div>
                            `;
                        });
                        $('#carouselImages').html(carouselItems);

                    } else {
                        $('#modalImages').hide();
                    }

                    const productHtml = `
                        <div class="product">
                            <h2>${product.partName}</h2>
                            <p><strong>Description:</strong> ${product.description}</p>
                            <p><strong>Price:</strong> $${product.price}</p>
                            <p><strong>Stock:</strong> ${product.stock} available</p>
                        </div>
                    `;
                    $('#product-details').append(productHtml);
                } else {
                    $('#product-details').html('<p>Product details not found.</p>');
                }
            },
            error: function () {
                $('#product-details').html('<p>Failed to load product details.</p>');
            }
        });
    }

    // Fetch existing reviews for the product
    function loadReviews(partId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/reviews/${partId}/reviews`,
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + authToken
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    let reviewsHtml = '';
                    response.data.forEach(review => {
                        reviewsHtml += `
                            <div class="review-item">
                                <h5>User ${review.userId}</h5>
                                <div class="rating">${'★'.repeat(review.rating)}${'☆'.repeat(5 - review.rating)}</div>
                                <p>${review.comment}</p>
                                <small>Reviewed on: ${review.reviewDate}</small>
                            </div>
                        `;
                    });
                    $('#reviews-list').html(reviewsHtml);
                } else {
                    $('#reviews-list').html('<p>No reviews found.</p>');
                }
            },
            error: function () {
                $('#reviews-list').html('<p>Failed to load reviews.</p>');
            }
        });
    }

    // Submit new review
    $('#submit-review').click(function () {
        const rating = $('#rating').val();
        const comment = $('#comment').val();

        if (rating && comment && currentPartId) {
            $.ajax({
                url: 'http://localhost:8080/api/v1/customer/reviews',
                type: 'POST',
                headers: {
                    'Authorization': 'Bearer ' + authToken,
                    'Content-Type': 'application/json'
                },
                data: JSON.stringify({
                    sparePartId: currentPartId,
                    rating: parseFloat(rating),
                    comment: comment
                }),
                success: function (response) {
                    if (response.status === 201) {
                        alert('Review submitted successfully!');
                        loadReviews(currentPartId);
                        $('#comment').val(''); // Clear the comment input
                    } else {
                        alert('Failed to submit review.');
                    }
                },
                error: function () {
                    alert('Failed to submit review.');
                }
            });
        } else {
            alert('Please fill in all fields and search for a product.');
        }
    });

    // Search parts by name
    $('#part-name').on('keypress', function (event) {
        if (event.which === 13) { // 13 is the Enter key code
            const partName = $(this).val().trim();
            if (partName) {
                $.ajax({
                    url: `http://localhost:8080/api/v1/customer/reviews/search?partName=${partName}`,
                    type: 'GET',
                    headers: {
                        'Authorization': 'Bearer ' + authToken
                    },
                    success: function (response) {
                        if (response.status === 200 && response.data && response.data.length > 0) {
                            const firstPart = response.data[0];
                            currentPartId = firstPart.partId;
                            loadProductDetails(currentPartId);
                            loadReviews(currentPartId);
                        } else {
                            $('#product-details').html('<p>No products found.</p>');
                            $('#reviews-list').html('');
                        }
                    },
                    error: function () {
                        $('#product-details').html('<p>Failed to search products.</p>');
                        $('#reviews-list').html('');
                    }
                });
            } else {
                alert('Please enter a part name.');
            }
        }
    });
});