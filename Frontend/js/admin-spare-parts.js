$(document).ready(function () {
    const spareParts = [
        {id: 'P001', name: 'Engine Oil', description: 'High quality engine oil', price: '20.00', stock: '150', category: 'Engine', sellerId: 'S001', imageUrl: 'path_to_image1.jpg'},
        {id: 'P002', name: 'Brake Pads', description: 'Durable brake pads for Honda Super Cub', price: '15.00', stock: '80', category: 'Brake', sellerId: 'S002', imageUrl: 'path_to_image2.jpg'},
        {id: 'P003', name: 'Suspension Spring', description: 'Heavy-duty suspension springs', price: '25.00', stock: '60', category: 'Suspension', sellerId: 'S003', imageUrl: 'path_to_image3.jpg'}
    ];


    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
    });

    // Populate Table
    function populateTable(parts) {
        const tableBody = $('#sparePartTable');
        tableBody.empty();
        parts.forEach(part => {
            const row = `<tr>
                            <td>${part.id}</td>
                            <td>${part.name}</td>
                            <td>${part.description}</td>
                            <td>${part.price}</td>
                            <td>${part.stock}</td>
                            <td>${part.category}</td>
                            <td>${part.sellerId}</td>
                            <td><img src="${part.imageUrl}" alt="Image" width="50"></td>
                            <td>
                                <button class="btn btn-primary btn-sm" onclick="editPart('${part.id}')">Edit</button>
                                <button class="btn btn-danger btn-sm">Delete</button>
                            </td>
                          </tr>`;
            tableBody.append(row);
        });
    }

    // Edit spare part
    window.editPart = function (partId) {
        const part = spareParts.find(p => p.id === partId);
        if (part) {
            $('#editPartName').val(part.name);
            $('#editDescription').val(part.description);
            $('#editPrice').val(part.price);
            $('#editStock').val(part.stock);
            $('#editCategory').val(part.category);
            $('#editSellerId').val(part.sellerId);
            $('#editImage').val(part.imageUrl);
            $('#editPartModal').modal('show');
        }
    };

    // Filter spare parts based on category
    $('#filterCategory').change(function() {
        const category = $(this).val();
        const filteredParts = category === 'all' ? spareParts : spareParts.filter(part => part.category.toLowerCase() === category.toLowerCase());
        populateTable(filteredParts);
    });

    // Search spare parts by name
    $('#searchPart').keyup(function() {
        const query = $(this).val().toLowerCase();
        const filteredParts = spareParts.filter(part => part.name.toLowerCase().includes(query));
        populateTable(filteredParts);
    });

    // Populate the table on initial load
    populateTable(spareParts);
});
