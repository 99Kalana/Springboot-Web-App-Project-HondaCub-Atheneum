// Sample data for Honda Super Cub models
let models = [
    {
        model_id: 1,
        model_name: "Honda Super Cub 50",
        model_year: 1958,
        engine_capacity: "49cc",
        top_speed: "50 km/h",
        fuel_consumption: "2.5 L/100 km",
        production_years: "1958-1965",
        description: "The original model that became the most sold motorbike in history.",
        images: [
            "images/3.jpg",
            "images/4.jpg"
        ]
    },
    {
        model_id: 2,
        model_name: "Honda Super Cub 90",
        model_year: 1967,
        engine_capacity: "89cc",
        top_speed: "65 km/h",
        fuel_consumption: "3.0 L/100 km",
        production_years: "1967-1976",
        description: "An upgraded version with more power and better features.",
        images: [
            "images/1.jpg",
            "images/2.jpg"
        ]
    }
];

$(document).ready(function() {

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
    });


    // Function to populate table with models data
    function populateTable() {
        $("#model-table tbody").empty();
        models.forEach((model, index) => {
            const imagesHtml = model.images.map(image => `<img src="${image}" alt="${model.model_name}" class="img-thumbnail" style="width: 50px; margin-right: 5px;">`).join("");
            const rowHtml = `
                    <tr>
                        <td>${model.model_name}</td>
                        <td>${model.model_year}</td>
                        <td>${model.engine_capacity}</td>
                        <td>${model.top_speed}</td>
                        <td>${model.fuel_consumption}</td>
                        <td>${model.production_years}</td>
                        <td>${model.description}</td>
                        <td>${imagesHtml}</td>
                        <td>
                            <button class="btn btn-warning btn-sm edit-btn" data-id="${index}"><i class="bi bi-pencil"></i> Edit</button>
                            <button class="btn btn-danger btn-sm delete-btn" data-id="${index}"><i class="bi bi-trash"></i> Delete</button>
                        </td>
                    </tr>
                `;
            $("#model-table tbody").append(rowHtml);
        });
    }

    // Call to populate the table on page load
    populateTable();

    // Show Add/Edit model modal
    $("#add-model-btn, .edit-btn").click(function() {
        const modelId = $(this).data("id");
        if (modelId !== undefined) {
            const model = models[modelId];
            $("#model-name").val(model.model_name);
            $("#model-year").val(model.model_year);
            $("#engine-capacity").val(model.engine_capacity);
            $("#top-speed").val(model.top_speed);
            $("#fuel-consumption").val(model.fuel_consumption);
            $("#production-years").val(model.production_years);
            $("#description").val(model.description);
            $("#images").val(model.images.join(","));
            $("#save-model-btn").data("id", modelId).text("Save Changes");
        } else {
            $("#save-model-btn").removeData("id").text("Save Model");
        }
        $("#modelModal").modal("show");
    });

    // Save or Update model
    $("#save-model-btn").click(function() {
        const modelId = $(this).data("id");
        const modelData = {
            model_name: $("#model-name").val(),
            model_year: $("#model-year").val(),
            engine_capacity: $("#engine-capacity").val(),
            top_speed: $("#top-speed").val(),
            fuel_consumption: $("#fuel-consumption").val(),
            production_years: $("#production-years").val(),
            description: $("#description").val(),
            images: $("#images").val().split(",")
        };

        if (modelId !== undefined) {
            // Edit model
            models[modelId] = modelData;
        } else {
            // Add new model
            models.push(modelData);
        }

        $("#modelModal").modal("hide");
        populateTable();
    });

    // Delete model
    $(document).on("click", ".delete-btn", function() {
        const modelId = $(this).data("id");
        models.splice(modelId, 1);
        populateTable();
    });
});