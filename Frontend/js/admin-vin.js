$(document).ready(function() {

    const vinForm = $("#vin-form");
    const vinNumberInput = $("#vin_number");
    const modelIdSelect = $("#model_id");
    const compatibilityNotesInput = $("#compatibility_notes");

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

    let models = [];
    let parts = [];
    let vinHistories = [];

    function populateModelsDropdown() {
        $.get("http://localhost:8080/api/v1/adminarchive/getAll", function(response) {
            if (response.status === 200 && response.data) {
                models = response.data;
                models.forEach(model => {
                    $('#model_id').append(`<option value="${model.modelId}">${model.modelName}</option>`);
                });
            }
        });
    }

    function populatePartsDropdown() {
        $.get("http://localhost:8080/api/v1/adminspareparts/getAll", function(response) {
            if (response.status === 200 && response.data) {
                parts = response.data;
                parts.forEach(part => {
                    $('#part_id').append(`<option value="${part.partId}">${part.partName}</option>`);
                });
            }
        });
    }

    function loadVinHistories() {
        $.get("http://localhost:8080/api/v1/adminvin/getAll", function(response) {
            if (response.status === 200 && response.data) {
                vinHistories = response.data;
                populateVinTable();
                populateVinNumberLinkDropdown();
            }
        });
    }

    function populateVinTable() {
        $('#vin-table tbody').empty();
        vinHistories.forEach(vin => {
            const modelName = models.find(model => model.modelId === vin.modelId)?.modelName || 'Unknown';
            $('#vin-table tbody').append(`
                <tr>
                    <td>${vin.vinNumber}</td>
                    <td>${modelName}</td>
                    <td>${vin.compatibilityNotes}</td>
                    <td><button class="btn btn-danger btn-sm delete-vin" data-id="${vin.vinId}">Delete</button></td>
                </tr>
            `);
        });
    }

    function loadLinkedParts(vinId) {
        $.get(`http://localhost:8080/api/v1/adminvin/linkedParts/${vinId}`, function(response) {
            if (response.status === 200 && response.data) {
                linkedParts = response.data;
                populateLinkedPartsTable();
            }
        });
    }

    function populateLinkedPartsTable() {
        $('#linked-parts-table tbody').empty();
        linkedParts.forEach(partLink => {
            const partName = parts.find(part => part.partId === partLink.partId)?.partName || 'Unknown';
            const modelName = models.find(model => model.modelId === vinHistories.find(vin => vin.vinId === partLink.vinId)?.modelId)?.modelName || 'Unknown';
            $('#linked-parts-table tbody').append(`
                    <tr>
                        <td>${partLink.partId}</td>
                        <td>${partName}</td>
                        <td>${partLink.vinId}</td>
                        <td>${modelName}</td>
                        <td><button class="btn btn-danger btn-sm delete-part" data-vin-id="${partLink.vinId}" data-part-id="${partLink.partId}">Remove</button></td>
                    </tr>
                `);
        });
    }

    function populateVinNumberLinkDropdown() {
        $('#vin_number_link').empty();
        $('#vin_number_link').append(`<option value="" disabled selected>Select a VIN Number</option>`);
        vinHistories.forEach(vin => {
            $('#vin_number_link').append(`<option value="${vin.vinId}">${vin.vinNumber}</option>`);
            console.log("Adding vinId to dropdown:", vin.vinId);
        });
    }

    $('#vin-form').submit(function(e) {
        e.preventDefault();

        let isValid = true;

        // Validate VIN Number
        const vinNumber = vinNumberInput.val().trim();
        const vinRegex = /^(C(50|65|70|90|100|125))[A-HJ-NPR-Z0-9]{0,7}$/;
        if (vinNumber === "") {
            displayError(vinNumberInput, "VIN number is required.");
            isValid = false;
        } else if (!vinRegex.test(vinNumber) || vinNumber.length < 3 || vinNumber.length > 10) {
            displayError(vinNumberInput, "VIN number must start with C50, C65, C70, C90, C100, or C125 and be between 3 and 10 characters.");
            isValid = false;
        } else {
            clearError(vinNumberInput);
        }

        // Validate Model Selection
        if (modelIdSelect.val() === "") {
            displayError(modelIdSelect, "Please select a model.");
            isValid = false;
        } else {
            clearError(modelIdSelect);
        }

        // Validate Compatibility Notes
        if (compatibilityNotesInput.val().trim().length > 500) {
            displayError(compatibilityNotesInput, "Compatibility notes cannot exceed 500 characters.");
            isValid = false;
        } else {
            clearError(compatibilityNotesInput);
        }

        if (!isValid) {
            e.preventDefault(); // Prevent form submission if validation fails
            return;
        }



        const vinData = {
            vinNumber: $('#vin_number').val(),
            modelId: parseInt($('#model_id').val()),
            compatibilityNotes: $('#compatibility_notes').val()
        };
        $.ajax({
            url: "http://localhost:8080/api/v1/adminvin/save",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(vinData),
            success: function() {
                loadVinHistories();
                $('#addVinModal').modal('hide');
            }
        });
    });

    $('#link-vin-part-form').submit(function(e) {
        e.preventDefault();
        const linkData = {
            vinId: parseInt($('#vin_number_link').val()),
            partId: parseInt($('#part_id').val())
        };
        $.ajax({
            url: "http://localhost:8080/api/v1/adminvin/linkPart",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(linkData),
            success: function() {
                loadVinHistories();
                $('#linkVinPartModal').modal('hide');
            }
        });
    });

    $('#vin-table').on('click', '.delete-vin', function() {
        const vinId = $(this).data('id');
        $.ajax({
            url: `http://localhost:8080/api/v1/adminvin/delete/${vinId}`,
            type: "DELETE",
            success: function() {
                loadVinHistories();
            }
        });
    });


    populateModelsDropdown();
    populatePartsDropdown();
    loadVinHistories();
});