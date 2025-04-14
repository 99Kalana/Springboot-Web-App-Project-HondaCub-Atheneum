$(document).ready(function() {

    const categoryForm = $("#categoryForm");
    const categoryNameInput = $("#categoryName");
    const categoryDescriptionInput = $("#categoryDescription");

    const editCategoryForm = $("#editCategoryForm");
    const editCategoryNameInput = $("#editCategoryName");
    const editCategoryDescriptionInput = $("#editCategoryDescription");

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

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
    });


    // Load Categories from Backend
    function loadCategories() {
        $.ajax({
            url: "http://localhost:8080/api/v1/admincategories/getAll",
            method: "GET",
            dataType: "json",
            success: function (response) {
                console.log("Response from server:", response); // Log the full response

                if (response.status === 200 && response.data && Array.isArray(response.data)) {
                    const categoryTable = $("#categoryTable");
                    categoryTable.empty(); // Clear the table body

                    response.data.forEach(category => {
                        const row = `
                            <tr>
                                <td>${category.categoryId}</td>
                                <td>${category.categoryName}</td>
                                <td>${category.description}</td>
                                <td>
                                    <button class="btn btn-warning btn-sm edit-btn" data-id="${category.categoryId}" data-bs-toggle="modal" data-bs-target="#editCategoryModal"><i class="bi bi-pencil-square"></i> Edit</button>
                                    <button class="btn btn-danger btn-sm delete-btn" data-id="${category.categoryId}"><i class="bi bi-trash"></i> Delete</button>
                                </td>
                            </tr>
                        `;
                        categoryTable.append(row);
                    });
                } else {
                    console.warn("No categories found or invalid response format.");
                    const categoryTable = $("#categoryTable");
                    categoryTable.empty(); // Clear the table body
                    categoryTable.append('<tr><td colspan="4" class="text-center">No categories found.</td></tr>');
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching categories:", error);
                console.log("Response:", xhr.responseText); // Log the response text for more details
            }
        });
    }

    // Add a new category
    $("#categoryForm").on("submit", function(e) {
        e.preventDefault();

        let isValid = true;

        // Validate Category Name
        if (categoryNameInput.val().trim() === "") {
            displayError(categoryNameInput, "Category name is required.");
            isValid = false;
        } else if (categoryNameInput.val().trim().length < 2 || categoryNameInput.val().trim().length > 100) {
            displayError(categoryNameInput, "Category name must be between 2 and 100 characters.");
            isValid = false;
        } else {
            clearError(categoryNameInput);
        }

        // Validate Category Description
        if (categoryDescriptionInput.val().trim().length > 500) {
            displayError(categoryDescriptionInput, "Description cannot exceed 500 characters.");
            isValid = false;
        } else {
            clearError(categoryDescriptionInput);
        }

        if (!isValid) {
            e.preventDefault(); // Prevent form submission if validation fails
            return;
        }

        const categoryName = $("#categoryName").val().trim();
        const categoryDescription = $("#categoryDescription").val().trim();

        if (categoryName === "" || categoryDescription === "") {
            alert("Both category name and description are required!");
            return;
        }

        const categoryData = {
            categoryName: categoryName,
            description: categoryDescription
        };

        $.ajax({
            url: "http://localhost:8080/api/v1/admincategories/save",
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(categoryData),
            success: function (response) {
                if (response.status === 201) {
                    loadCategories(); // Reload categories after adding
                    $("#categoryName").val("");
                    $("#categoryDescription").val("");
                } else {
                    alert("Failed to add category!");
                }
            },
            error: function () {
                alert("Error adding category.");
            }
        });
    });

    // Edit category
    $(document).on("click", ".edit-btn", function() {
        const categoryId = $(this).data("id");
        console.log("Category ID: ", categoryId);

        // Fetch category details based on the category ID
        $.ajax({
            url: `http://localhost:8080/api/v1/admincategories/get/${categoryId}`,
            method: "GET",
            dataType: "json",
            success: function(response) {
                console.log("Response from server:", response);

                if (response.status === 200 && response.data) {
                    console.log("Category Data:", response.data);

                    // Populate modal fields with the category data
                    const category = response.data;
                    $("#editCategoryId").val(category.categoryId);
                    $("#editCategoryName").val(category.categoryName);
                    $("#editCategoryDescription").val(category.description);
                } else {
                    console.warn("No category data found.");
                    alert("Failed to load category data.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching category data:", error);
                console.log("Response:", xhr.responseText);  // Log the response text for more details
                alert("Error fetching category data.");
            }
        });
    });



    // Handle the save changes functionality
    $("#editCategoryForm").on("submit", function(e) {
        e.preventDefault();

        let isValid = true;

        // Validate Edit Category Name
        if (editCategoryNameInput.val().trim() === "") {
            displayError(editCategoryNameInput, "Category name is required.");
            isValid = false;
        } else if (editCategoryNameInput.val().trim().length < 2 || editCategoryNameInput.val().trim().length > 100) {
            displayError(editCategoryNameInput, "Category name must be between 2 and 100 characters.");
            isValid = false;
        } else {
            clearError(editCategoryNameInput);
        }

        // Validate Edit Category Description
        if (editCategoryDescriptionInput.val().trim().length > 500) {
            displayError(editCategoryDescriptionInput, "Description cannot exceed 500 characters.");
            isValid = false;
        } else {
            clearError(editCategoryDescriptionInput);
        }

        if (!isValid) {
            e.preventDefault(); // Prevent form submission if validation fails
            return;
        }

        const categoryId = $("#editCategoryId").val();
        const categoryName = $("#editCategoryName").val().trim();
        const categoryDescription = $("#editCategoryDescription").val().trim();

        if (categoryName === "" || categoryDescription === "") {
            alert("Both category name and description are required!");
            return;
        }

        const categoryData = {
            categoryId: categoryId,
            categoryName: categoryName,
            description: categoryDescription
        };

        // Send the updated category data to the backend
        $.ajax({
            url: "http://localhost:8080/api/v1/admincategories/update",
            method: "PUT",
            contentType: "application/json",
            data: JSON.stringify(categoryData),
            success: function(response) {
                if (response.status === 200) {
                    loadCategories(); // Reload the categories after updating
                    $('#editCategoryModal').modal('hide'); // Close the modal
                } else {
                    alert("Failed to update category.");
                }
            },
            error: function() {
                alert("Error updating category.");
            }
        });
    });


    // Delete category
    $(document).on("click", ".delete-btn", function() {
        const categoryId = parseInt($(this).data("id"));

        if (confirm("Are you sure you want to delete this category?")) {
            $.ajax({
                url: `http://localhost:8080/api/v1/admincategories/delete/${categoryId}`,
                method: "DELETE",
                success: function (response) {
                    if (response.status === 200) {
                        loadCategories(); // Reload categories after deletion
                    } else {
                        alert("Failed to delete category!");
                    }
                },
                error: function () {
                    alert("Error deleting category.");
                }
            });
        }
    });

    // Initial Load
    loadCategories();
});

// Implement Search Functionality
$("#searchCategory").on("input", function() {
    const searchQuery = $(this).val().toLowerCase();
    const rows = $("#categoryTable tr"); // All table rows

    rows.each(function() {
        const categoryName = $(this).find("td:nth-child(2)").text().toLowerCase(); // Get the category name from the second column
        const categoryDescription = $(this).find("td:nth-child(3)").text().toLowerCase(); // Get the description from the third column

        // Show or hide rows based on the search query
        if (categoryName.includes(searchQuery) || categoryDescription.includes(searchQuery)) {
            $(this).show(); // Show row if it matches the search
        } else {
            $(this).hide(); // Hide row if it doesn't match
        }
    });
});

// New JavaScript function to trigger report download
function downloadCategoryReport() {
    window.location.href = "http://localhost:8080/api/v1/admincategories/report/pdf";
}