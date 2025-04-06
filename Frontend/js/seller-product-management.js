$(document).ready(function() {
    // Sidebar toggle
    $('#toggleBtn').click(function() {
        $('#sidebar').toggleClass('collapsed');
    });

    // Sample data for products
    const sampleProducts = [
        {
            partId: 'P001',
            partName: 'Engine Oil',
            category: 'Engine',
            price: '30.00',
            stock: '50',
            image: 'https://via.placeholder.com/50'
        },
        {
            partId: 'P002',
            partName: 'Brake Pads',
            category: 'Brakes',
            price: '20.00',
            stock: '100',
            image: 'https://via.placeholder.com/50'
        },
        {
            partId: 'P003',
            partName: 'Clutch Kit',
            category: 'Transmission',
            price: '120.00',
            stock: '25',
            image: 'https://via.placeholder.com/50'
        }
    ];

    // Add product rows for sample data
    sampleProducts.forEach(function(product) {
        const productRow = `
            <tr>
                <td>${product.partId}</td>
                <td>${product.partName}</td>
                <td>${product.category}</td>
                <td>${product.price}</td>
                <td>${product.stock}</td>
                <td><img src="${product.image}" alt="Product Image" style="width: 50px; height: auto;"></td>
                <td>
                    <button class="btn btn-warning btn-sm editBtn" data-part-id="${product.partId}">Edit</button>
                    <button class="btn btn-danger btn-sm deleteBtn" data-part-id="${product.partId}">Delete</button>
                </td>
            </tr>
        `;
        $('#tables tbody').append(productRow);
    });

    // Add Product Form Submit
    $('#productForm').submit(function(e) {
        e.preventDefault();

        const partName = $('#partName').val();
        const category = $('#category').val();
        const price = $('#price').val();
        const stock = $('#stock').val();
        const description = $('#description').val();
        const image = $('#image').val();

        if (partName && category && price && stock && description && image) {
            const partId = `P${Math.floor(Math.random() * 1000) + 1}`; // Generate random Part ID
            const productRow = `
                <tr>
                    <td>${partId}</td>
                    <td>${partName}</td>
                    <td>${category}</td>
                    <td>${price}</td>
                    <td>${stock}</td>
                    <td><img src="${image}" alt="Product Image" style="width: 50px; height: auto;"></td>
                    <td>
                        <button class="btn btn-warning btn-sm editBtn" data-part-id="${partId}">Edit</button>
                        <button class="btn btn-danger btn-sm deleteBtn" data-part-id="${partId}">Delete</button>
                    </td>
                </tr>
            `;
            $('#tables tbody').append(productRow);
            $('#productModal').modal('hide'); // Close modal after adding
            $('#productForm')[0].reset(); // Reset form
        } else {
            alert('Please fill in all fields.');
        }
    });

    // Edit Product
    $(document).on('click', '.editBtn', function() {
        const partId = $(this).data('part-id');
        // Fetch the row data based on partId (here, just using static data for simplicity)
        const row = $(this).closest('tr');
        const partName = row.find('td').eq(1).text();
        const category = row.find('td').eq(2).text();
        const price = row.find('td').eq(3).text();
        const stock = row.find('td').eq(4).text();
        const image = row.find('td').eq(5).find('img').attr('src');

        $('#editPartName').val(partName);
        $('#editCategory').val(category);
        $('#editPrice').val(price);
        $('#editStock').val(stock);
        $('#editImage').val(image);

        $('#editProductModal').modal('show');
    });

    // Edit Product Form Submit
    $('#editProductForm').submit(function(e) {
        e.preventDefault();

        const partName = $('#editPartName').val();
        const category = $('#editCategory').val();
        const price = $('#editPrice').val();
        const stock = $('#editStock').val();
        const image = $('#editImage').val();

        if (partName && category && price && stock && image) {
            const row = $('#tables tbody tr').filter(function() {
                return $(this).find('td').eq(1).text() === partName; // Find the matching row by part name
            });

            row.find('td').eq(1).text(partName);
            row.find('td').eq(2).text(category);
            row.find('td').eq(3).text(price);
            row.find('td').eq(4).text(stock);
            row.find('td').eq(5).find('img').attr('src', image);

            $('#editProductModal').modal('hide');
            $('#editProductForm')[0].reset();
        } else {
            alert('Please fill in all fields.');
        }
    });

    // Delete Product
    $(document).on('click', '.deleteBtn', function() {
        const partId = $(this).data('part-id');
        if (confirm('Are you sure you want to delete this product?')) {
            $(this).closest('tr').remove();
        }
    });

    // Populate Categories for both Add and Edit Modals (Static categories for example)
    const categories = ['Engine', 'Transmission', 'Suspension', 'Brakes', 'Electrical'];
    categories.forEach(function(category) {
        $('#category, #editCategory').append(`<option value="${category}">${category}</option>`);
    });
});
