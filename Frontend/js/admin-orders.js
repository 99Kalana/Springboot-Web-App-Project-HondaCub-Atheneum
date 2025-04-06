$(document).ready(function() {
    // Example data (Replace with actual data from your database)
    const orders = [
        { id: 'O001', customerName: 'John Doe', date: '2025-02-15', status: 'Pending', total: '150.00' },
        { id: 'O002', customerName: 'Jane Smith', date: '2025-02-14', status: 'Shipped', total: '120.00' },
        { id: 'O003', customerName: 'Mike Johnson', date: '2025-02-10', status: 'Cancelled', total: '0.00' },
        { id: 'O004', customerName: 'Emily Davis', date: '2025-02-08', status: 'Completed', total: '200.00' }
    ];

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
    });

    // Function to show order details in the View modal
    function viewOrder(order) {
        $('#viewOrderId').text(order.id);
        $('#viewCustomerName').text(order.customerName);
        $('#viewOrderDate').text(order.date);
        $('#viewOrderStatus').text(order.status);
        $('#viewOrderTotal').text(order.total);
        $('#viewOrderModal').modal('show');
    }

    // Function to show the Update modal and populate with order data
    function showUpdateModal(order) {
        $('#updateOrderId').val(order.id);
        $('#updateOrderStatus').val(order.status);
        $('#updateOrderModal').modal('show');
    }

    // Function to show the Cancel modal and set the order ID
    function showCancelModal(order) {
        $('#cancelOrderId').text(order.id);
        $('#cancelOrderModal').modal('show');

        // Handle the cancel confirmation
        $('#cancelOrderButton').off('click').on('click', function() {
            order.status = 'Cancelled';
            populateTable(orders);  // Refresh table after cancellation
            $('#cancelOrderModal').modal('hide');
            alert(`Order ${order.id} has been cancelled.`);
        });
    }

    // Populate the orders table
    function populateTable(filteredOrders) {
        const tableBody = $('#orderTable');
        tableBody.empty(); // Clear existing rows
        filteredOrders.forEach(order => {
            const row = `
                <tr>
                    <td>${order.id}</td>
                    <td>${order.customerName}</td>
                    <td>${order.date}</td>
                    <td>${order.status}</td>
                    <td>${order.total}</td>
                    <td>
                        <button class="btn btn-info btn-sm view-order-btn" data-order='${JSON.stringify(order)}'>View</button>
                        <button class="btn btn-warning btn-sm update-order-btn" data-order='${JSON.stringify(order)}'>Update</button>
                        <button class="btn btn-danger btn-sm cancel-order-btn" data-order='${JSON.stringify(order)}'>Cancel</button>
                    </td>
                </tr>`;
            tableBody.append(row);
        });

        // Bind click event for the View button
        $('.view-order-btn').off('click').on('click', function() {
            const orderData = $(this).data('order');
            viewOrder(orderData);
        });

        // Bind click event for the Update button
        $('.update-order-btn').off('click').on('click', function() {
            const orderData = $(this).data('order');
            showUpdateModal(orderData);
        });

        // Bind click event for the Cancel button
        $('.cancel-order-btn').off('click').on('click', function() {
            const orderData = $(this).data('order');
            showCancelModal(orderData);
        });
    }

    // Handle Update Order button click
    $('#updateOrderButton').on('click', function() {
        const orderId = $('#updateOrderId').val();
        const newStatus = $('#updateOrderStatus').val();

        // Update the order status in the data array
        const order = orders.find(o => o.id === orderId);
        if (order) {
            order.status = newStatus;
            populateTable(orders);  // Refresh table after update
            $('#updateOrderModal').modal('hide');
            alert(`Order ${orderId} status updated to ${newStatus}.`);
        }
    });

    // Filter orders by status
    $('#filterStatus').change(function() {
        const status = $(this).val();
        const filteredOrders = status === 'all' ? orders : orders.filter(order => order.status.toLowerCase() === status.toLowerCase());
        populateTable(filteredOrders);
    });

    // Search orders by order ID or customer name
    $('#searchOrder').keyup(function() {
        const query = $(this).val().toLowerCase();
        const filteredOrders = orders.filter(order =>
            order.id.toLowerCase().includes(query) ||
            order.customerName.toLowerCase().includes(query)
        );
        populateTable(filteredOrders);
    });

    // Initial population of the table
    populateTable(orders);
});
