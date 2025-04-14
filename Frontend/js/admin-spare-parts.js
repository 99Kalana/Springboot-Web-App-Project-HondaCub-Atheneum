$(document).ready(function() {
    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function() {
        $(".sidebar").toggleClass("collapsed");
    });

    // Populate Category Filter and Edit Modal Category Dropdown
    function populateCategoryFilter() {
        $.ajax({
            url: "http://localhost:8080/api/v1/admincategories/getAll",
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data && Array.isArray(response.data)) {
                    const filterCategory = $("#filterCategory");
                    const editCategory = $("#editCategory");

                    filterCategory.empty().append('<option value="all">All Categories</option>');
                    editCategory.empty();

                    response.data.forEach(category => {
                        filterCategory.append(`<option value="${category.categoryName}">${category.categoryName}</option>`);
                        editCategory.append(`<option value="${category.categoryId}">${category.categoryName}</option>`); // add category id to value.
                    });
                } else {
                    console.warn("No categories found or invalid response format.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching categories:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // Populate Table
    function populateTable(parts) {
        const tableBody = $('#sparePartTable');
        tableBody.empty();
        if (parts && parts.length > 0) {
            parts.forEach(part => {
                let imagesHtml = '';
                if (part.images && part.images.length > 0) {
                    let imagePromises = part.images.map(image => {
                        return new Promise((resolve, reject) => {
                            const imageUrl = "/images/" + image.imageUrl; // Here is the change.
                            const img = new Image();
                            img.onload = () => resolve(`<img src="${imageUrl}" alt="Image" class="img-thumbnail" style="width: 50px; margin-right: 5px;">`);
                            img.onerror = () => {
                                console.error("Failed to load image:", imageUrl);
                                resolve('Image Load Failed');
                            };
                            img.src = imageUrl;
                        });
                    });
                    Promise.all(imagePromises).then(imageTags => {
                        imagesHtml = imageTags.join("");
                        const row = `
                        <tr>
                            <td>${part.partId}</td>
                            <td>${part.partName}</td>
                            <td>${part.description}</td>
                            <td>${part.price}</td>
                            <td>${part.stock}</td>
                            <td>${part.categoryName}</td>
                            <td>${part.sellerId}</td>
                            <td>${imagesHtml}</td>
                            <td>
                                <button class="btn btn-primary btn-sm view-btn" data-id="${part.partId}" data-bs-toggle="modal" data-bs-target="#viewSparePartModal"><i class="bi bi-eye"></i> View</button>
                            </td>
                        </tr>`;
                        tableBody.append(row);
                    });
                } else {
                    const row = `...`;
                    tableBody.append(row);
                }
            });
        } else {
            tableBody.append('<tr><td colspan="9" class="text-center">No spare parts found.</td></tr>');
        }
    }

    // Load Spare Parts from Backend
    function loadSpareParts() {
        $.ajax({
            url: "http://localhost:8080/api/v1/adminspareparts/getAll",
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateTable(response.data);
                } else {
                    console.error("Failed to load spare parts:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching spare parts:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }



    // View spare part details in modal
    $(document).on("click", ".view-btn", function() {
        const partId = $(this).data("id");
        $.ajax({
            url: `http://localhost:8080/api/v1/adminspareparts/get/${partId}`,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    const part = response.data;
                    let imagesHtml = 'No Images Provided'; // Default message
                    if (part.images && part.images.length > 0) {
                        imagesHtml = part.images.map(image => {
                            const imageUrl = "/images/" + image.imageUrl; // Adding the prefix
                            return `<img src="${imageUrl}" alt="Image" class="img-fluid" style="max-width: 150px; margin-right: 10px;">`;
                        }).join("");
                    }
                    const detailsHtml = `
                    <p><strong>Part ID:</strong> ${part.partId}</p>
                    <p><strong>Part Name:</strong> ${part.partName}</p>
                    <p><strong>Description:</strong> ${part.description}</p>
                    <p><strong>Price:</strong> ${part.price}</p>
                    <p><strong>Stock:</strong> ${part.stock}</p>
                    <p><strong>Category:</strong> ${part.categoryName}</p>
                    <p><strong>Seller ID:</strong> ${part.sellerId}</p>
                    <p><strong>Images:</strong> ${imagesHtml}</p>
                `;
                    $('#sparePartDetails').html(detailsHtml);
                } else {
                    alert("Failed to load spare part details.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching spare part details:", error);
                console.log("Response:", xhr.responseText);
                alert("Error loading spare part details.");
            }
        });
    });




    // Filter spare parts based on category
    $('#filterCategory').change(function() {
        const category = $(this).val();
        let url = "http://localhost:8080/api/v1/adminspareparts/getAll";
        if (category !== "all") {
            url = `http://localhost:8080/api/v1/adminspareparts/filter?category=${category}`;
        }
        $.ajax({
            url: url,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateTable(response.data);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error filtering spare parts:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    });

    // Search spare parts by name
    $('#searchPart').keyup(function() {
        const query = $(this).val();
        $.ajax({
            url: `http://localhost:8080/api/v1/adminspareparts/search?query=${query}`,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateTable(response.data);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error searching spare parts:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    });

    // Initial Load
    loadSpareParts();
    populateCategoryFilter();
});

// New JavaScript function to trigger spare parts report download
function downloadSparePartsReport() {
    window.location.href = "http://localhost:8080/api/v1/adminspareparts/report/pdf";
}