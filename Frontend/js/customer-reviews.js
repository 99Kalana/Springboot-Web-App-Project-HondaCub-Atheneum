$(document).ready(function() {
    const userId = 1; // Assuming the user is logged in and their ID is 1
    const partId = 101; // Assuming we're viewing the product with ID 101

    // Sample product data (mocking a real product API)
    const product = {
        product_id: 101,
        product_name: "Honda Super Cub Part",
        description: "High-quality replacement part for the Honda Super Cub.",
        price: "$49.99",
        stock: 20,
        image: "images/16.jpg" // Sample product image
    };

    // Function to display product details
    function loadProductDetails() {
        const productHtml = `
            <div class="product">
                <h2>${product.product_name}</h2>
                <img src="${product.image}" alt="${product.product_name}" class="img-fluid">
                <p><strong>Description:</strong> ${product.description}</p>
                <p><strong>Price:</strong> ${product.price}</p>
                <p><strong>Stock:</strong> ${product.stock} available</p>
            </div>
        `;
        $('#product-details').html(productHtml);
    }

    // Fetch existing reviews for the product
    function loadReviews() {
        // Mock data for reviews
        const reviews = [
            { user_id: 1, rating: 5, comment: "Great part! It fits perfectly.", review_date: "2025-03-19" },
            { user_id: 2, rating: 4, comment: "Good quality but a bit pricey.", review_date: "2025-03-18" }
        ];

        let reviewsHtml = '';
        reviews.forEach(review => {
            reviewsHtml += `
                <div class="review-item">
                    <h5>User ${review.user_id}</h5>
                    <div class="rating">${'★'.repeat(review.rating)}${'☆'.repeat(5 - review.rating)}</div>
                    <p>${review.comment}</p>
                    <small>Reviewed on: ${review.review_date}</small>
                </div>
            `;
        });
        $('#reviews-list').html(reviewsHtml);
    }

    loadProductDetails(); // Load product details when the page loads
    loadReviews(); // Load reviews when the page loads

    // Submit new review
    $('#submit-review').click(function() {
        const rating = $('#rating').val();
        const comment = $('#comment').val();

        if (rating && comment) {
            // In a real scenario, you would send this data to a backend (e.g., via AJAX)
            alert('Review submitted successfully!');
            loadReviews(); // Reload the reviews after submission
        } else {
            alert('Please fill in all fields.');
        }
    });


    // Placeholder function for searching parts by name
    $('#search-part').on('click', function() {
        var partName = $('#part-name').val().trim();
        if (partName) {
            // Here, you could make an AJAX request to fetch the product details and reviews based on the part name
            console.log("Searching for part: " + partName);
            // Simulate product and reviews loading (you can replace this with real data)
            $('#product-details').html(`<h5>Product Name: ${partName}</h5><p>Product description and details...</p>`);
            $('#reviews-list').html(`<div class="review-item"><h5>Review 1</h5><p>Great product!</p><div class="rating">★★★★★</div></div>`);
        } else {
            alert("Please enter a part name.");
        }
    });
});
