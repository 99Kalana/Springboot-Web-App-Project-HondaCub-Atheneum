$(document).ready(function() {
    // Sample data for models
    const models = [
        { model_id: 1, model_name: "Honda Super Cub 50" },
        { model_id: 2, model_name: "Honda Super Cub 90" }
    ];

    // Sample data for parts
    const parts = [
        { part_id: 1, part_name: "Engine Block" },
        { part_id: 2, part_name: "Exhaust Pipe" }
    ];

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
    });

    // Populate the model dropdown
    models.forEach(model => {
        $('#model_id').append(`<option value="${model.model_id}">${model.model_name}</option>`);
        $('#vin_number_link').append(`<option value="${model.model_id}">${model.model_name}</option>`);
    });

    // Populate the parts dropdown
    parts.forEach(part => {
        $('#part_id').append(`<option value="${part.part_id}">${part.part_name}</option>`);
    });

    // Form submission for adding VIN
    $('#vin-form').submit(function(e) {
        e.preventDefault();
        const vin_number = $('#vin_number').val();
        const model_id = $('#model_id').val();
        const compatibility_notes = $('#compatibility_notes').val();

        // Add VIN to the VIN table
        const model_name = models.find(model => model.model_id === parseInt(model_id)).model_name;
        $('#vin-table tbody').append(`
            <tr>
                <td>${vin_number}</td>
                <td>${model_name}</td>
                <td>${compatibility_notes}</td>
                <td><button class="btn btn-danger btn-sm delete-vin">Delete</button></td>
            </tr>
        `);

        // Reset form and close modal
        $('#vin_number').val('');
        $('#model_id').val('');
        $('#compatibility_notes').val('');
        $('#addVinModal').modal('hide');
    });

    // Form submission for linking VIN to part
    $('#link-vin-part-form').submit(function(e) {
        e.preventDefault();
        const vin_number = $('#vin_number_link').val();
        const part_id = $('#part_id').val();

        // Find part name based on part_id
        const part_name = parts.find(part => part.part_id === parseInt(part_id)).part_name;

        // Add part to linked parts table
        $('#linked-parts-table tbody').append(`
            <tr>
                <td>${part_id}</td>
                <td>${part_name}</td>
                <td>${vin_number}</td>
                <td>${models.find(model => model.model_id === parseInt(vin_number)).model_name}</td>
                <td><button class="btn btn-danger btn-sm delete-part">Remove</button></td>
            </tr>
        `);

        // Reset form and close modal
        $('#vin_number_link').val('');
        $('#part_id').val('');
        $('#linkVinPartModal').modal('hide');
    });

    // Delete functionality for VIN rows
    $('#vin-table').on('click', '.delete-vin', function() {
        $(this).closest('tr').remove();
    });

    // Delete functionality for linked parts rows
    $('#linked-parts-table').on('click', '.delete-part', function() {
        $(this).closest('tr').remove();
    });
});