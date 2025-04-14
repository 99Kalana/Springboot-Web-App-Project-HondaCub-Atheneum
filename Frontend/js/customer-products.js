$(document).ready(function () {
    const authToken = localStorage.getItem('authToken');

    function loadProducts() {
        const priceFilter = $('#priceRange').val();
        const categoryFilter = $('#categorySelect').val();
        const sellerFilter = $('#sellerSelect').val();
        const searchFilter = $('#searchInput').val().toLowerCase();

        $('#priceLabel').text('$' + priceFilter);
        $('#product-list').empty();

        let url = `http://localhost:8080/api/v1/customer/products/filter?price=${priceFilter}`;
        if (categoryFilter) url += `&category=${categoryFilter}`;
        if (sellerFilter) url += `&sellerId=${sellerFilter}`;
        if (searchFilter) url += `&query=${searchFilter}`;

        if (searchFilter) {
            url = `http://localhost:8080/api/v1/customer/products/search?query=${searchFilter}`;
        }

        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const products = response.data;
                    products.forEach(function (product) {
                        const imageUrl = product.images && product.images.length > 0 ? `/images/${product.images[0].imageUrl.split('/').pop()}` : 'images/default-image.jpg';

                        const productCard = `
                            <div class="col-md-4">
                                <div class="card" data-product-id="${product.partId}">
                                    <img src="${imageUrl}" class="card-img-top" alt="${product.partName}">
                                    <div class="card-body">
                                        <h5 class="card-title">${product.partName}</h5>
                                        <p class="card-text">$${product.price}</p>
                                        <p class="card-text">${product.categoryName}</p>
                                        <button class="btn btn-primary add-to-cart" data-product-id="${product.partId}">Add to Cart</button>
                                        <button class="btn btn-info view-details" data-product-id="${product.partId}">View Details</button>
                                    </div>
                                </div>
                            </div>
                        `;
                        $('#product-list').append(productCard);
                    });
                } else {
                    console.error("Failed to load products:", response);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading products:", error);
            }
        });
    }

    function loadFiltersData() {
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/products/categories`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const categories = response.data;
                    categories.forEach(category => {
                        $('#categorySelect').append(`<option value="${category}">${category}</option>`);
                    });
                } else {
                    console.error("Failed to load categories:", response);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading categories:", error);
            }
        });

        $.ajax({
            url: `http://localhost:8080/api/v1/customer/products/sellers`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const sellers = response.data;
                    sellers.forEach(seller => {
                        $('#sellerSelect').append(`<option value="${seller.userId}">${seller.fullName}</option>`);
                    });
                } else {
                    console.error("Failed to load sellers:", response);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading sellers:", error);
            }
        });
    }

    $(document).on('click', '.view-details', function () {
        const productId = $(this).data('product-id');

        $.ajax({
            url: `http://localhost:8080/api/v1/customer/products/${productId}`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const product = response.data;

                    $('#productName').text(product.partName);
                    $('#productPrice').text(`$${product.price}`);
                    $('#productDescription').text(product.description);
                    $('#productCategory').text(product.categoryName);

                    $('#carouselImages').empty();
                    product.images.forEach((image, index) => {
                        const isActive = index === 0 ? 'active' : '';
                        const imageUrl = `/images/${image.imageUrl.split('/').pop()}`;

                        const imageSlide = `
                            <div class="carousel-item ${isActive}">
                                <img src="${imageUrl}" class="d-block w-100" alt="Image ${index + 1}">
                            </div>
                        `;
                        $('#carouselImages').append(imageSlide);
                    });

                    $('#addToCart').data('product-id', productId);
                    $('#productModal').modal('show');

                    // Load and display reviews
                    loadAndDisplayReviews(productId);

                } else {
                    console.error("Failed to load product details:", response);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading product details:", error);
            }
        });
    });

    function loadAndDisplayReviews(productId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/reviews/${productId}/reviews`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    $('#reviewsList').empty();
                    response.data.forEach(review => {
                        const reviewItem = `
                        <div class="review-item">
                            <h5>User ${review.userId}</h5>
                            <div class="rating" style="color: gold;">${'★'.repeat(review.rating)}${'☆'.repeat(5 - review.rating)}</div>
                            <p>${review.comment}</p>
                            <small>Reviewed on: ${review.reviewDate}</small>
                        </div>
                    `;
                        $('#reviewsList').append(reviewItem);
                    });
                    $('#productReviews').find('h5:first').css('text-align', 'center'); // Selects the first h5 inside productReviews
                } else {
                    $('#reviewsList').html('<p>No reviews found.</p>');
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading reviews:", error);
                $('#reviewsList').html('<p>Failed to load reviews.</p>');
            }
        });
    }

    $(document).on('click', '.add-to-cart', function () {
        const productId = $(this).data('product-id');
        addToCart(productId);
    });

    $(document).on('click', '#addToCart', function () {
        const productId = $(this).data('product-id');
        addToCart(productId);
    });


    function addToCart(productId) {
        const authToken = localStorage.getItem('authToken');

        function getUserIdFromToken(token) {
            try {
                const payload = JSON.parse(atob(token.split('.')[1]));
                return payload.userId;
            } catch (e) {
                console.error("Error extracting userId from token:", e);
                return null;
            }
        }

        const userId = getUserIdFromToken(authToken);

        if (userId === null) {
            alert('User ID not found. Please log in.');
            return;
        }

        const addedAt = new Date().toISOString();

        $.ajax({
            url: `http://localhost:8080/api/v1/customer/products/cart/add`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                sparePartId: productId,
                quantity: 1,
                userId: userId,
                addedAt: addedAt,
            }),
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 201) {
                    alert('Product added to cart.');

                    // Update cart count immediately after adding to cart
                    updateCartCount();

                } else {
                    alert('Failed to add product to cart.');
                }
            },
            error: function (xhr, status, error) {
                console.error("Error adding product to cart:", error);
                alert('An error occurred while adding to cart.');
            }
        });
    }

    // Function to update the cart count display
    function updateCartCount() {
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/cart/count`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`
            },
            success: function (response) {
                if (response) {
                    const cartCount = response; // Directly use the response as the count
                    $('#cart-count').text(cartCount);
                    if (cartCount > 0) {
                        $('#cart-count').show();
                    } else {
                        $('#cart-count').hide();
                    }
                } else {
                    console.error("Failed to get cart count:", response);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error getting cart count:", error);
            }
        });
    }


    $('.btn-primary').click(function () {
        loadProducts();
    });

    $('#priceRange').on('input', function () {
        loadProducts();
    });

    $('#categorySelect').change(function () {
        loadProducts();
    });

    $('#sellerSelect').change(function () {
        loadProducts();
    });

    $('#searchInput').keyup(function () {
        loadProducts();
    });

    updateCartCount();
    loadProducts();
    loadFiltersData();
});