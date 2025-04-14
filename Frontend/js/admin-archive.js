$(document).ready(function() {

    const modelModal = $("#modelModal");
    const modelForm = $("#modelForm");
    const modelNameInput = $("#model-name");
    const modelYearInput = $("#model-year");
    const engineCapacityInput = $("#engine-capacity");
    const topSpeedInput = $("#top-speed");
    const fuelConsumptionInput = $("#fuel-consumption");
    const productionYearsInput = $("#production-years");
    const descriptionInput = $("#description");

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


    let models = []; // Store models from the backend

    // Function to populate table with models data
    function populateTable() {
        $("#model-table tbody").empty();
        models.forEach((model, index) => {
            let imagesHtml = '';
            if (model.modelImageIds && model.modelImageIds.length > 0) {
                let imagePromises = model.modelImageIds.map(imageId => {
                    return new Promise((resolve, reject) => {
                        $.ajax({
                            url: `http://localhost:8080/api/v1/adminarchive/image/${imageId}`,
                            method: "GET",
                            success: function(response) {
                                if (response.status === 200 && response.data) {
                                    resolve(`<img src="http://localhost:8080/images/${response.data.imageUrl}" alt="${model.modelName}" class="img-thumbnail" style="width: 50px; margin-right: 5px;">`); // Modified URL
                                } else {
                                    resolve('');
                                }
                            },
                            error: function() {
                                resolve('');
                            }
                        });
                    });
                });

                // Wait for all image fetches to complete
                Promise.all(imagePromises).then(imageTags => {
                    imagesHtml = imageTags.join("");
                    const rowHtml = `
                        <tr>
                            <td>${model.modelName}</td>
                            <td>${model.modelYear}</td>
                            <td>${model.engineCapacity}</td>
                            <td>${model.topSpeed}</td>
                            <td>${model.fuelConsumption}</td>
                            <td>${model.productionYears}</td>
                            <td>${model.description}</td>
                            <td>${imagesHtml}</td>
                            <td>
                                <button class="btn btn-warning btn-sm edit-btn" data-id="${model.modelId}"><i class="bi bi-pencil"></i> Edit</button>
                                <button class="btn btn-danger btn-sm delete-btn" data-id="${model.modelId}"><i class="bi bi-trash"></i> Delete</button>
                            </td>
                        </tr>
                    `;
                    $("#model-table tbody").append(rowHtml);
                });
            } else {
                const rowHtml = `
                        <tr>
                            <td>${model.modelName}</td>
                            <td>${model.modelYear}</td>
                            <td>${model.engineCapacity}</td>
                            <td>${model.topSpeed}</td>
                            <td>${model.fuelConsumption}</td>
                            <td>${model.productionYears}</td>
                            <td>${model.description}</td>
                            <td></td>
                            <td>
                                <button class="btn btn-warning btn-sm edit-btn" data-id="${model.modelId}"><i class="bi bi-pencil"></i> Edit</button>
                                <button class="btn btn-danger btn-sm delete-btn" data-id="${model.modelId}"><i class="bi bi-trash"></i> Delete</button>
                            </td>
                        </tr>
                    `;
                $("#model-table tbody").append(rowHtml);
            }
        });
    }


    // Load Models from Backend (with optional search query)
    function loadModels(searchQuery) {
        let url = "http://localhost:8080/api/v1/adminarchive/getAll";
        if (searchQuery) {
            url = `http://localhost:8080/api/v1/adminarchive/search?modelName=${searchQuery}`;
        }

        $.ajax({
            url: url,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    models = response.data;
                    populateTable();
                } else {
                    console.error("Failed to load models:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching models:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // Search button event listener
    $("#search-model").on('input', function() {
        const searchQuery = $(this).val();
        loadModels(searchQuery); // Repopulate table with search results
    });



    // Show Add/Edit model modal (using event delegation)
    $(document).on("click", "#add-model-btn, .edit-btn", function() {
        const modelId = $(this).data("id");
        if (modelId !== undefined) {
            // Edit mode: populate fields with model data
            $.ajax({
                url: `http://localhost:8080/api/v1/adminarchive/get/${modelId}`,
                method: "GET",
                dataType: "json",
                success: function(response) {
                    if (response.status === 200 && response.data) {
                        const model = response.data;
                        $("#model-name").val(model.modelName);
                        $("#model-year").val(model.modelYear);
                        $("#engine-capacity").val(model.engineCapacity);
                        $("#top-speed").val(model.topSpeed);
                        $("#fuel-consumption").val(model.fuelConsumption);
                        $("#production-years").val(model.productionYears);
                        $("#description").val(model.description);
                        $("#save-model-btn").data("id", model.modelId).text("Save Changes");
                    } else {
                        console.error("Failed to load model:", response);
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Error fetching model:", error);
                    console.log("Response:", xhr.responseText);
                }
            });
        } else {
            // Add mode: clear fields
            $("#model-name").val("");
            $("#model-year").val("");
            $("#engine-capacity").val("");
            $("#top-speed").val("");
            $("#fuel-consumption").val("");
            $("#production-years").val("");
            $("#description").val("");
            $("#save-model-btn").removeData("id").text("Save Model");
        }
        $("#modelModal").modal("show");
    });

    // Save or Update model
    $("#save-model-btn").click(function() {

        let isValid = true;

        // Validate Model Name
        if (modelNameInput.val().trim() === "") {
            displayError(modelNameInput, "Model name is required.");
            isValid = false;
        } else if (modelNameInput.val().trim().length < 2 || modelNameInput.val().trim().length > 100) {
            displayError(modelNameInput, "Model name must be between 2 and 100 characters.");
            isValid = false;
        } else {
            clearError(modelNameInput);
        }

        // Validate Model Year
        const yearValue = parseInt(modelYearInput.val());
        if (isNaN(yearValue)) {
            displayError(modelYearInput, "Model year is required.");
            isValid = false;
        } else if (yearValue < 1958 || yearValue > 2050) {
            displayError(modelYearInput, "Model year must be between 1958 and 2050.");
            isValid = false;
        } else {
            clearError(modelYearInput);
        }

        // Validate Engine Capacity
        if (engineCapacityInput.val().trim().length > 20) {
            displayError(engineCapacityInput, "Engine capacity cannot exceed 20 characters.");
            isValid = false;
        } else {
            clearError(engineCapacityInput);
        }

        // Validate Top Speed
        if (topSpeedInput.val().trim().length > 20) {
            displayError(topSpeedInput, "Top speed cannot exceed 20 characters.");
            isValid = false;
        } else {
            clearError(topSpeedInput);
        }

        // Validate Fuel Consumption
        if (fuelConsumptionInput.val().trim().length > 20) {
            displayError(fuelConsumptionInput, "Fuel consumption cannot exceed 20 characters.");
            isValid = false;
        } else {
            clearError(fuelConsumptionInput);
        }

        // Validate Production Years
        if (productionYearsInput.val().trim().length > 100) {
            displayError(productionYearsInput, "Production years cannot exceed 100 characters.");
            isValid = false;
        } else {
            clearError(productionYearsInput);
        }

        // Validate Description
        if (descriptionInput.val().trim().length > 500) {
            displayError(descriptionInput, "Description cannot exceed 500 characters.");
            isValid = false;
        } else {
            clearError(descriptionInput);
        }

        if (!isValid) {
            return; // Stop the function if validation fails
        }


        const modelId = $(this).data("id");
        const formData = new FormData();
        formData.append("modelArchive", new Blob([JSON.stringify({
            modelId: modelId,
            modelName: $("#model-name").val(),
            modelYear: $("#model-year").val(),
            engineCapacity: $("#engine-capacity").val(),
            topSpeed: $("#top-speed").val(),
            fuelConsumption: $("#fuel-consumption").val(),
            productionYears: $("#production-years").val(),
            description: $("#description").val()
        })], {
            type: "application/json"
        }));

        const files = $("#modelImage")[0].files;
        for (let i = 0; i < files.length; i++) {
            formData.append("images", files[i]);
        }

        const method = modelId !== undefined ? "PUT" : "POST";
        const url = modelId !== undefined ? "http://localhost:8080/api/v1/adminarchive/update" : "http://localhost:8080/api/v1/adminarchive/save";

        $.ajax({
            url: url,
            method: method,
            data: formData,
            contentType: false,
            processData: false,
            success: function(response) {
                if (response.status === 200 || response.status === 201) {
                    $("#modelModal").modal("hide");
                    loadModels();
                } else {
                    alert("Failed to save/update model.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error saving/updating model:", error);
                console.log("Response:", xhr.responseText);
                alert("Error saving/updating model.");
            }
        });
    });

    // Delete model
    $(document).on("click", ".delete-btn", function() {
        const modelId = $(this).data("id");
        if (confirm("Are you sure you want to delete this model?")) {
            $.ajax({
                url: `http://localhost:8080/api/v1/adminarchive/delete/${modelId}`,
                method: "DELETE",
                success: function(response) {
                    if (response.status === 200) {
                        loadModels();
                    } else {
                        alert("Failed to delete model.");
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Error deleting model:", error);
                    console.log("Response:", xhr.responseText);
                    alert("Error deleting model.");
                }
            });
        }
    });

    // Initial Load
    loadModels();
});