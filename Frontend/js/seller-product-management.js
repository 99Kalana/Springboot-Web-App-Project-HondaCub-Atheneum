$(document).ready(function() {

    const productForm = $("#productForm");
    const partNameInput = $("#partName");
    const categorySelect = $("#category");
    const priceInput = $("#price");
    const stockInput = $("#stock");
    const descriptionInput = $("#description");

    const editProductForm = $("#editProductForm");
    const editPartNameInput = $("#editPartName");
    const editCategorySelect = $("#editCategory");
    const editPriceInput = $("#editPrice");
    const editStockInput = $("#editStock");
    const editDescriptionInput = $("#editDescription");

    // Function to display validation errors
    function displayError(inputElement, message) {
        inputElement.addClass("is-invalid");
        inputElement.next(".invalid-feedback").text(message).show();
    }

    // Function to clear validation errors
    function clearError(inputElement) {
        inputElement.removeClass("is-invalid");
        inputElement.next(".invalid-feedback").hide();
    }

    let categoriesCache = []; // Cache categories

    // Sidebar toggle
    $('#toggleBtn').click(function() {
        $('#sidebar').toggleClass('collapsed');
    });

    // Get sellerId from URL
    const urlParams = new URLSearchParams(window.location.search);
    const sellerId = urlParams.get('sellerId');

    // Function to display loading indicator
    function showLoading(element) {
        $(element).html('<div class="spinner-border text-primary" role="status"><span class="visually-hidden">Loading...</span></div>');
    }

    // Function to display error message
    function showError(message) {
        alert(message);
    }

    function fetchProducts(searchQuery = '', categoryId = '') {
        showLoading('#tables tbody');
        let url = 'http://localhost:8080/api/v1/seller/spareparts';
        const params = [];
        if (searchQuery) {
            params.push(`search=${searchQuery}`);
        }
        if (categoryId) {
            params.push(`category=${categoryId}`);
        }
        if (params.length > 0) {
            url += '?' + params.join('&');
        }

        $.ajax({
            url: url,
            type: 'GET',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function(response) {
                if (response.data && Array.isArray(response.data)) {
                    $('#tables tbody').empty();
                    response.data.forEach(product => {
                        let imagesHtml = '';
                        if (product.images && product.images.length > 0) {
                            product.images.forEach(image => {
                                if (image.imageUrl) {
                                    imagesHtml += `<img src="http://localhost:8080/images/${image.imageUrl}" alt="Product Image" class="img-thumbnail" style="width: 50px; margin-right: 5px;">`;
                                } else {
                                    console.error("imageUrl is undefined for image:", image);
                                }
                            });
                        }
                        const productRow = `
                                <tr>
                                    <td>${product.partId}</td>
                                    <td>${product.partName}</td>
                                    <td>${product.categoryName}</td>
                                    <td>${product.price}</td>
                                    <td>${product.stock}</td>
                                    <td>${imagesHtml}</td>
                                    <td>
                                        <button class="btn btn-warning btn-sm editBtn" data-part-id="${product.partId}">Edit</button>
                                        <button class="btn btn-danger btn-sm deleteBtn" data-part-id="${product.partId}">Delete</button>
                                    </td>
                                </tr>
                            `;
                        $('#tables tbody').append(productRow);
                    });
                } else {
                    $('#tables tbody').html('<tr><td colspan="7">No products found.</td></tr>');
                }
            },
            error: function(error) {
                console.error('Error fetching products:', error);
                showError('Failed to fetch products.');
                $('#tables tbody').empty();
            }
        });
    }

    // Function to fetch categories
    function fetchCategories() {
        if (categoriesCache.length > 0) {
            populateCategoryDropdowns(categoriesCache);
            return;
        }
        $.ajax({
            url: 'http://localhost:8080/api/v1/admincategories/getAll', // Full URL
            type: 'GET',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function(response) {
                categoriesCache = response.data;
                populateCategoryDropdowns(categoriesCache);
            },
            error: function(error) {
                console.error('Error fetching categories:', error);
                showError('Failed to fetch categories.');
            }
        });
    }

    // Function to populate category dropdowns
    function populateCategoryDropdowns(categories) {
        categories.forEach(category => {
            $('#category, #editCategory, #categoryFilter').append(`<option value="${category.categoryId}">${category.categoryName}</option>`);
        });
    }

    function getUserIdFromToken() {
        const token = localStorage.getItem('authToken');
        if (!token) {
            return null;
        }

        try {
            const base64Url = token.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));

            const payload = JSON.parse(jsonPayload);
            return payload.userId; // Assuming 'userId' is a claim in your token
        } catch (error) {
            console.error("Error decoding token:", error);
            return null;
        }
    }

    // Function to add product
    function addProduct(formData) {
        console.log("Adding product with data:", formData);

        const sparePartDTO = {
            partName: $('#partName').val(),
            categoryId: $('#category').val(),
            price: $('#price').val(),
            stock: $('#stock').val(),
            description: $('#description').val()
        };

        formData.append('sparePart', JSON.stringify(sparePartDTO));

        const userId = getUserIdFromToken();
        if (!userId) {
            showError("User ID is invalid. Please log in again.");
            return;
        }

        formData.append('sellerId', userId);

        const imageFiles = $('#images')[0].files; // Get the files

        for (let i = 0; i < imageFiles.length; i++) {
            formData.append('images', imageFiles[i]); // Append each file
        }

        $.ajax({
            url: 'http://localhost:8080/api/v1/seller/spareparts/save',
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function(response) {
                if (response.status === 201) {
                    $('#productModal').modal('hide');
                    $('#productForm')[0].reset();
                    fetchProducts();
                } else {
                    showError(response.message || 'Failed to add product.');
                }
            },
            error: function(error) {
                console.error('Error adding product:', error);
                showError('Failed to add product.');
            }
        });
    }

    // Function to edit product
    function editProduct(partId, formData) {
        console.log("Editing product:", partId, formData);

        // Create sparePartDTO from form data
        const sparePartDTO = {
            partName: $('#editPartName').val(),
            categoryId: $('#editCategory').val(),
            price: $('#editPrice').val(),
            stock: $('#editStock').val(),
            description: $('#editDescription').val()
        };

        formData.append('sparePart', JSON.stringify(sparePartDTO));

        const userId = getUserIdFromToken();
        if (!userId) {
            showError("User ID is invalid. Please log in again.");
            return;
        }

        // sellerId is now a query parameter, do not add to form data.
        // formData.append('sellerId', userId);

        const imageFiles = $('#editImages')[0].files; // Get the files

        for (let i = 0; i < imageFiles.length; i++) {
            formData.append('images', imageFiles[i]); // Append each file
        }

        $.ajax({
            url: `http://localhost:8080/api/v1/seller/spareparts/update/${partId}?sellerId=${userId}`, // Append sellerId as query parameter
            type: 'PUT',
            data: formData,
            contentType: false,
            processData: false,
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function(response) {
                if (response.status === 200) {
                    $('#editProductModal').modal('hide');
                    fetchProducts();
                } else {
                    showError(response.message || 'Failed to update product.');
                }
            },
            error: function(error) {
                console.error('Error editing product:', error);
                showError('Failed to update product.');
            }
        });
    }




    // Function to delete product
    function deleteProduct(partId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/seller/spareparts/${partId}`,
            type: 'DELETE',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function(response) {
                if (response.status === 200) { // Check for 200 OK
                    fetchProducts();
                } else {
                    showError(response.message || 'Failed to delete product.');
                }
            },
            error: function(error) {
                console.error('Error deleting product:', error);
                showError('Failed to delete product.');
            }
        });
    }

    // Event listeners
    $('#productForm').submit(function(e) {

        let isValid = true;

        // Validate Part Name
        if (partNameInput.val().trim() === "") {
            displayError(partNameInput, "Part name is required.");
            isValid = false;
        } else if (partNameInput.val().trim().length < 2 || partNameInput.val().trim().length > 255) {
            displayError(partNameInput, "Part name must be between 2 and 255 characters.");
            isValid = false;
        } else {
            clearError(partNameInput);
        }

        // Validate Category
        if (categorySelect.val() === "") {
            displayError(categorySelect, "Please select a category.");
            isValid = false;
        } else {
            clearError(categorySelect);
        }

        // Validate Price
        const priceValue = parseFloat(priceInput.val());
        if (isNaN(priceValue) || priceValue <= 0) {
            displayError(priceInput, "Price is required and must be greater than 0.");
            isValid = false;
        } else {
            clearError(priceInput);
        }

        // Validate Stock
        const stockValue = parseInt(stockInput.val());
        if (isNaN(stockValue) || stockValue < 0) {
            displayError(stockInput, "Stock is required and cannot be negative.");
            isValid = false;
        } else {
            clearError(stockInput);
        }

        // Validate Description
        if (descriptionInput.val().trim().length > 1000) {
            displayError(descriptionInput, "Description cannot exceed 1000 characters.");
            isValid = false;
        } else {
            clearError(descriptionInput);
        }

        if (!isValid) {
            e.preventDefault(); // Prevent form submission if validation fails
            return;
        }


        e.preventDefault();
        const formData = new FormData(this);
        addProduct(formData);
    });

    $('#editProductForm').submit(function(e) {

        let isValid = true;

        // Validate Edit Part Name
        if (editPartNameInput.val().trim() === "") {
            displayError(editPartNameInput, "Part name is required.");
            isValid = false;
        } else if (editPartNameInput.val().trim().length < 2 || editPartNameInput.val().trim().length > 255) {
            displayError(editPartNameInput, "Part name must be between 2 and 255 characters.");
            isValid = false;
        } else {
            clearError(editPartNameInput);
        }

        // Validate Edit Category
        if (editCategorySelect.val() === "") {
            displayError(editCategorySelect, "Please select a category.");
            isValid = false;
        } else {
            clearError(editCategorySelect);
        }

        // Validate Edit Price
        const priceValue = parseFloat(editPriceInput.val());
        if (isNaN(priceValue) || priceValue <= 0) {
            displayError(editPriceInput, "Price is required and must be greater than 0.");
            isValid = false;
        } else {
            clearError(editPriceInput);
        }

        // Validate Edit Stock
        const stockValue = parseInt(editStockInput.val());
        if (isNaN(stockValue) || stockValue < 0) {
            displayError(editStockInput, "Stock is required and cannot be negative.");
            isValid = false;
        } else {
            clearError(editStockInput);
        }

        // Validate Edit Description
        if (editDescriptionInput.val().trim().length > 1000) {
            displayError(editDescriptionInput, "Description cannot exceed 1000 characters.");
            isValid = false;
        } else {
            clearError(editDescriptionInput);
        }

        if (!isValid) {
            e.preventDefault(); // Prevent form submission if validation fails
            return;
        }


        e.preventDefault();
        const partId = $('#editProductForm').data('partId');
        const formData = new FormData(this);
        editProduct(partId, formData);
    });

    $(document).on('click', '.editBtn', function() {
        const partId = $(this).data('part-id');
        $.ajax({
            url: `http://localhost:8080/api/v1/seller/spareparts/${partId}`,
            type: 'GET',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function(response) {
                const product = response.data;
                $('#editPartName').val(product.partName);
                $('#editCategory').val(product.categoryId);
                $('#editPrice').val(product.price);
                $('#editStock').val(product.stock);
                $('#editDescription').val(product.description);
                $('#editProductModal').modal('show');
                $('#editProductForm').data('partId', partId);
            },
            error: function(error) {
                console.error('Error fetching product:', error);
                showError('Failed to fetch product for editing.');
            }
        });
    });

    $(document).on('click', '.deleteBtn', function() {
        const partId = $(this).data('part-id');
        if (confirm('Are you sure you want to delete this product?')) {
            deleteProduct(partId);
        }
    });

    // Search and Filter Event Listeners
    $('#searchBtn').click(function() {
        const searchQuery = $('#searchPart').val();
        const category = $('#categoryFilter').val();
        fetchProducts(searchQuery, category);
    });

    $('#searchPart').on('input', function() {
        const searchQuery = $(this).val();
        if (searchQuery.trim() === '') {
            fetchProducts(); // Fetch all products if search field is empty
        }
    });

    $('#searchPart').keypress(function(event) {
        if (event.which === 13) { // Enter key pressed
            $('#searchBtn').click();
        }
    });

    $('#categoryFilter').change(function() {
        const searchQuery = $('#searchPart').val();
        const category = $(this).val();
        fetchProducts(searchQuery, category);
    });

    // Initialize
    fetchProducts();
    fetchCategories();
});

// New JavaScript function to trigger seller products report download
function downloadSellerProductsReport() {
    const authToken = localStorage.getItem('authToken');
    if (authToken) {
        fetch('http://localhost:8080/api/v1/seller/spareparts/report/download', {
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
                a.download = 'seller_products_report.pdf';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
            })
            .catch(error => {
                console.error('Error downloading products report:', error);
                alert('Failed to download products report.');
            });
    } else {
        alert('Authentication token not found. Please log in again.');
    }
}