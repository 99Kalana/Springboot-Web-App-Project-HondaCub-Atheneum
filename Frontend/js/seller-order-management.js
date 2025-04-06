$(document).ready(function() {

    // Sidebar toggle
    $('#toggleBtn').click(function() {
        $('#sidebar').toggleClass('collapsed');
    });


    // Sample order data
    const orders = [
        { orderId: 'O001', userId: 'U001', orderStatus: 'Pending', placedAt: '2025-03-01' },
        { orderId: 'O002', userId: 'U002', orderStatus: 'Shipped', placedAt: '2025-03-02' },
        { orderId: 'O003', userId: 'U003', orderStatus: 'Delivered', placedAt: '2025-03-03' },
        { orderId: 'O004', userId: 'U004', orderStatus: 'Cancelled', placedAt: '2025-03-04' },
        { orderId: 'O005', userId: 'U005', orderStatus: 'Returned', placedAt: '2025-03-05' }
    ];

    // Function to populate the orders table
    function populateOrdersTable() {
        const tableBody = $('#ordersTable tbody');
        tableBody.empty(); // Clear existing rows

        // Loop through the orders array and add each order to the table
        orders.forEach(order => {
            const row = `<tr>
                            <td>${order.orderId}</td>
                            <td>${order.userId}</td>
                            <td>${order.orderStatus}</td>
                            <td>${order.placedAt}</td>
                            <td><button class="btn btn-primary btn-sm update-status-btn" data-order-id="${order.orderId}" data-bs-toggle="modal" data-bs-target="#updateStatusModal">Update Status</button></td>
                         </tr>`;
            tableBody.append(row);
        });
    }

    // Event listener for the update status button
    $(document).on('click', '.update-status-btn', function() {
        const orderId = $(this).data('order-id');
        const order = orders.find(o => o.orderId === orderId);
        // Set the current order's status in the modal
        $('#orderStatus').val(order.orderStatus);
    });

    // Event listener for the status update form submission
    $('#updateStatusForm').submit(function(e) {
        e.preventDefault();

        // Get the selected order status
        const newStatus = $('#orderStatus').val();

        // Find the order and update its status
        const orderId = $('.update-status-btn').data('order-id');
        const order = orders.find(o => o.orderId === orderId);
        order.orderStatus = newStatus;

        // Update the table with the new status
        populateOrdersTable();

        // Close the modal
        $('#updateStatusModal').modal('hide');
    });

    // Initial population of the orders table
    populateOrdersTable();
});
