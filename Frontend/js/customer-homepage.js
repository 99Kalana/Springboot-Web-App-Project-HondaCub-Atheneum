$(document).ready(function () {

    function loadFeaturedProducts() {
        const token = localStorage.getItem('authToken'); // Get token from local storage

        if (!token) {
            console.error('No JWT token found.');
            return; // Handle missing token appropriately
        }

        $.ajax({
            url: 'http://localhost:8080/api/v1/customer/home/featured-products',
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            },
            success: function (response) {
                if (response && response.data) {
                    displayProducts(response.data);
                } else {
                    console.error('Failed to load featured products:', response);
                }
            },
            error: function (error) {
                console.error('Error loading featured products:', error);
            }
        });
    }

    function displayProducts(products) {
        const carouselInner = $('#carousel-product-list');
        carouselInner.empty(); // Clear existing content
        const baseUrl = 'http://localhost:8080/images/'; // Add base URL

        let carouselItem = $('<div class="carousel-item active"></div>'); // Create initial active item
        let cardRow = $('<div class="d-flex justify-content-around"></div>'); // Create card row

        products.forEach((product, index) => {
            let imagesHtml = '';
            if (product.images && product.images.length > 0) {
                imagesHtml = `<img src="${baseUrl}${product.images[0].imageUrl}" class="card-img-top" alt="${product.partName}">`;
            }

            const cardHtml = `
                    <div class="card shadow-sm rounded product-card">
                        ${imagesHtml}
                        <div class="card-body">
                            <h5 class="card-title">${product.partName}</h5>
                            <p class="card-text">Price: $${product.price}</p>
                            <button class="btn btn-primary add-to-cart" data-partid="${product.partId}">Add to Cart</button>
                        </div>
                    </div>
                `;

            cardRow.append(cardHtml); // Add card to the row

            if ((index + 1) % 4 === 0 || index === products.length - 1) { // 4 cards or last card
                carouselItem.append(cardRow);
                carouselInner.append(carouselItem);

                carouselItem = $('<div class="carousel-item"></div>'); // Create new item
                cardRow = $('<div class="d-flex justify-content-around"></div>'); // Create new row
            }
        });

        // Event listener for "Add to Cart" buttons
        $('.add-to-cart').click(function () {
            const productId = $(this).data('partid');
            addToCart(productId);
        });
    }

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

    updateCartCount();

    loadFeaturedProducts();
});