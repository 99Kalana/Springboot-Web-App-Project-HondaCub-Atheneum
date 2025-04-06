$(document).ready(function () {
    // Sample product data
    const products = [
        { id: 1, name: "Engine Part 1", category: "Engine Parts", seller: "Honda Official", price: 30, description: "A high-quality engine part for your Honda Cub.", image: "images/16.jpg", images: ["images/16.jpg", "images/16.jpg"] },
        { id: 2, name: "Brake Pad A", category: "Brakes", seller: "Local Seller", price: 15, description: "Durable brake pads for safe stopping power.", image: "images/16.jpg", images: ["images/16.jpg", "images/16.jpg"] },
        { id: 3, name: "Electronics Part X", category: "Electronics", seller: "Honda Official", price: 45, description: "Electronics part for engine management.", image: "images/16.jpg", images: ["images/16.jpg", "images/16.jpg"] },
        { id: 4, name: "Engine Part 2", category: "Engine Parts", seller: "Local Seller", price: 40, description: "Another essential engine part for Honda Cub.", image: "images/16.jpg", images: ["images/16.jpg", "images/16.jpg"] },
    ];

    // Function to load products dynamically based on filters
    function loadProducts() {
        const priceFilter = $('#priceRange').val();
        const categoryFilter = $('#categorySelect').val();
        const sellerFilter = $('#sellerSelect').val();
        const searchFilter = $('#searchInput').val().toLowerCase();

        // Update price label dynamically as the slider is adjusted
        $('#priceLabel').text('$' + priceFilter);

        // Clear current product listings
        $('#product-list').empty();

        // Filter products based on the selected filters
        const filteredProducts = products.filter(function (product) {
            const matchesPrice = product.price <= priceFilter;
            const matchesCategory = categoryFilter === "" || product.category === categoryFilter;
            const matchesSeller = sellerFilter === "" || product.seller === sellerFilter;
            const matchesSearch = product.name.toLowerCase().includes(searchFilter);

            return matchesPrice && matchesCategory && matchesSeller && matchesSearch;
        });

        // Display the filtered products
        filteredProducts.forEach(function (product) {
            const productCard = `
                <div class="col-md-4">
                    <div class="card" data-product-id="${product.id}">
                        <img src="${product.image}" class="card-img-top" alt="${product.name}">
                        <div class="card-body">
                            <h5 class="card-title">${product.name}</h5>
                            <p class="card-text">$${product.price}</p>
                            <p class="card-text">${product.category}</p>
                            <button class="btn btn-primary add-to-cart" data-product-id="${product.id}">Add to Cart</button>
                            <button class="btn btn-info view-details" data-product-id="${product.id}">View Details</button>
                        </div>
                    </div>
                </div>
            `;
            $('#product-list').append(productCard);
        });
    }

    // Event listener to open the modal with product images and details
    $(document).on('click', '.view-details', function () {
        const productId = $(this).data('product-id');
        const product = products.find(p => p.id === productId);

        // Set product details in the modal
        $('#productName').text(product.name);
        $('#productPrice').text(`$${product.price}`);
        $('#productDescription').text(product.description);
        $('#productCategory').text(product.category);

        // Add images to the carousel
        $('#carouselImages').empty();
        product.images.forEach((image, index) => {
            const isActive = index === 0 ? 'active' : '';
            const imageSlide = `
                <div class="carousel-item ${isActive}">
                    <img src="${image}" class="d-block w-100" alt="Image ${index + 1}">
                </div>
            `;
            $('#carouselImages').append(imageSlide);
        });

        // Show the modal
        $('#productModal').modal('show');
    });

    // Event listener to add product to the cart
    $(document).on('click', '.add-to-cart', function () {
        const productId = $(this).data('product-id');
        const product = products.find(p => p.id === productId);

        // Add product to cart (you can implement a cart functionality)
        console.log('Product added to cart:', product);
        alert(`${product.name} has been added to the cart.`);
    });

    // Event listener to apply filters
    $('.btn-primary').click(function () {
        loadProducts();
    });

    // Event listener for price range slider
    $('#priceRange').on('input', function () {
        loadProducts();
    });

    // Event listener for category select
    $('#categorySelect').change(function () {
        loadProducts();
    });

    // Event listener for seller select
    $('#sellerSelect').change(function () {
        loadProducts();
    });

    // Event listener for search input
    $('#searchInput').keyup(function () {
        loadProducts();
    });

    // Initialize products on page load
    loadProducts();
});
