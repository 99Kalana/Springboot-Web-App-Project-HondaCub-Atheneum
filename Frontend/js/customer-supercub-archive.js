$(document).ready(function () {
    const authToken = localStorage.getItem('authToken');
    const baseUrl = 'http://localhost:8080/api/v1/customer/archive';

    function loadModels() {
        const year = $('#yearSelect').val();
        const modelName = $('#searchInput').val();
        const vinNumber = $('#vinSearch').val();

        console.log("Year:", year);
        console.log("Model Name:", modelName);
        console.log("VIN Number:", vinNumber);

        let url = `${baseUrl}/models/search?`;
        if (year) url += `year=${year}&`;
        if (modelName) url += `modelName=${modelName}&`;
        if (vinNumber) url += `vinNumber=${vinNumber}&`;

        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    $('#model-list').empty();
                    response.data.forEach(model => {

                        let imageUrl = 'images/default-model.jpg';

                        if (model.modelImages && model.modelImages.length > 0) {
                            imageUrl = model.modelImages[0].imageUrl; // Use the full imageUrl from the API
                        }

                        const modelCard = `
                            <div class="col-md-4">
                                <div class="card" data-model-id="${model.modelId}">
                                    <img src="${imageUrl}" class="card-img-top" alt="${model.modelName}">
                                    <div class="card-body">
                                        <h5 class="card-title">${model.modelName}</h5>
                                        <p class="model-year">Year: ${model.modelYear || 'N/A'}</p>
                                        <p class="model-info">${model.description || 'No description available'}</p>
                                        <button class="btn btn-primary view-details" data-model-id="${model.modelId}">View Details</button>
                                    </div>
                                </div>
                            </div>
                        `;
                        $('#model-list').append(modelCard);
                    });
                } else {
                    console.error("Failed to load models:", response);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading models:", error);
            }
        });
    }

    // Populate Year Dropdown
    function populateYearDropdown() {
        $.ajax({
            url: `${baseUrl}/models`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const years = [...new Set(response.data.map(model => model.modelYear))].sort();
                    years.forEach(year => {
                        $('#yearSelect').append(`<option value="${year}">${year}</option>`);
                    });
                } else {
                    console.error("Failed to load models for years:", response);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading models for years:", error);
            }
        }); // Corrected closing parenthesis here!
    }

    $('#applyFiltersBtn').click(function () {
        loadModels();
    });

    // Event listeners for filters
    $('#yearSelect').change(function () {
        loadModels();
    });

    $('#searchInput').on('input', function () {
        loadModels();
    });

    $('#vinSearch').on('input', function () {
        loadModels();
    });

    $('#applyFiltersBtn').click(function () {
        loadModels();
    });

    // Click event for "View Details" button
    $(document).on('click', '.view-details', function () {
        const modelId = $(this).data('model-id');

        $.ajax({
            url: `${baseUrl}/models/${modelId}`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const model = response.data;
                    $('#model-name').text(model.modelName || 'N/A');
                    $('#model-year').text(model.modelYear || 'N/A');
                    $('#engine-capacity').text(model.engineCapacity || 'N/A');
                    $('#top-speed').text(model.topSpeed || 'N/A');
                    $('#fuel-consumption').text(model.fuelConsumption || 'N/A');
                    $('#production-years').text(model.productionYears || 'N/A');
                    $('#description').text(model.description || 'No description available');

                    $('#carouselImages').empty();
                    if (model.modelImages && model.modelImages.length > 0) {
                        model.modelImages.forEach((image, index) => {
                            const activeClass = index === 0 ? 'active' : '';
                            const imageUrl = image.imageUrl; // Use the full imageUrl from the API

                            const carouselItem = `
                                <div class="carousel-item ${activeClass}">
                                    <img src="${imageUrl}" class="d-block w-100" alt="Image ${index + 1}">
                                </div>
                            `;
                            $('#carouselImages').append(carouselItem);
                        });
                    } else {
                        $('#carouselImages').html('<p>No images available.</p>');
                    }
                    $('#modelDetailsModal').modal('show'); // Show the modal
                } else {
                    console.error("Failed to load model details:", response);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading model details:", error);
            }
        });
    });

    populateYearDropdown();
    loadModels();

    // Loading overlay functionality
    setTimeout(function() {
        $('#loadingOverlay').fadeOut(); // Hide the loading overlay after 2 seconds
    }, 2000);

});