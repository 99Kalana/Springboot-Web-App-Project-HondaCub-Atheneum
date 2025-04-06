$(document).ready(function () {
    // Sample model data (replace with actual database data)
    const models = [
        {
            model_id: 1,
            model_name: "Honda Super Cub 50",
            model_year: 1958,
            engine_capacity: "49cc",
            top_speed: "60 km/h",
            fuel_consumption: "3.5 L/100km",
            production_years: "1958-1965",
            description: "The Honda Super Cub 50 is a classic motorcycle from Honda, known for its reliability and simplicity.",
            image_urls: ["images/10.jpg", "images/11.jpg", "images/12.jpg"] // Replace with actual image URLs from the database
        },
        {
            model_id: 2,
            model_name: "Honda Super Cub 70",
            model_year: 1965,
            engine_capacity: "72cc",
            top_speed: "70 km/h",
            fuel_consumption: "3.2 L/100km",
            production_years: "1965-1970",
            description: "The Honda Super Cub 70 offers enhanced performance with a larger engine and improved top speed.",
            image_urls: ["images/13.jpg", "images/14.jpg", "images/15.jpg"] // Replace with actual image URLs from the database
        }
    ];

    // Function to display models in the UI
    function loadModels() {
        $('#model-list').empty(); // Clear the model list

        models.forEach(function (model) {
            const modelCard = `
                <div class="col-md-4">
                    <div class="card">
                        <img src="${model.image_urls[0]}" class="card-img-top" alt="${model.model_name}">
                        <div class="card-body">
                            <h5 class="card-title">${model.model_name}</h5>
                            <p class="model-year">Year: ${model.model_year}</p>
                            <p class="model-info">${model.description}</p>
                            <a href="#" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modelDetailsModal" data-model-id="${model.model_id}">View Details</a>
                        </div>
                    </div>
                </div>
            `;
            $('#model-list').append(modelCard);
        });
    }

    // Event listener to populate modal with selected model details
    $('#modelDetailsModal').on('show.bs.modal', function (event) {
        const button = $(event.relatedTarget); // Button that triggered the modal
        const modelId = button.data('model-id'); // Extract model ID from data attribute

        // Find the model object based on modelId
        const selectedModel = models.find(model => model.model_id === modelId);

        // Populate modal with model data
        $('#model-name').text(selectedModel.model_name);
        $('#model-year').text(selectedModel.model_year);
        $('#engine-capacity').text(selectedModel.engine_capacity);
        $('#top-speed').text(selectedModel.top_speed);
        $('#fuel-consumption').text(selectedModel.fuel_consumption);
        $('#production-years').text(selectedModel.production_years);
        $('#description').text(selectedModel.description);

        // Populate carousel images dynamically
        const carouselInner = $('#carouselImages');
        carouselInner.empty(); // Clear previous images

        selectedModel.image_urls.forEach((imageUrl, index) => {
            const activeClass = index === 0 ? 'active' : ''; // Make the first image active
            const carouselItem = `
                <div class="carousel-item ${activeClass}">
                    <img src="${imageUrl}" class="d-block w-100" alt="Image ${index + 1}">
                </div>
            `;
            carouselInner.append(carouselItem);
        });
    });

    // Call loadModels on page load
    loadModels();
});
