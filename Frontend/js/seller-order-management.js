$(document).ready(function() {
    // Sidebar toggle
    $('#toggleBtn').click(function() {
        $('#sidebar').toggleClass('collapsed');
    });

    let currentOrderId = null; // Store the current orderId globally
    let searchTimeout; // To handle debounce for live search

    // Function to populate the orders table from the API
    function populateOrdersTable(orders) {
        const tableBody = $('#ordersTable tbody');
        tableBody.empty(); // Clear existing rows

        orders.forEach(order => {
            const row = `<tr>
                        <td>${order.orderId}</td>
                        <td>${order.userId}</td>
                        <td>${order.orderStatus}</td>
                        <td>${order.placedAt}</td>
                        <td>
                            <button class="btn btn-primary btn-sm update-status-btn" data-order-id="${order.orderId}" data-bs-toggle="modal" data-bs-target="#updateStatusModal">Update Status</button>
                        </td>
                     </tr>`;
            tableBody.append(row);
        });
    }

    // Function to fetch orders based on search and filter criteria
    function fetchOrders(orderId = '', status = '') {
        let url = 'http://localhost:8080/api/v1/seller/orders';
        const params = [];

        if (orderId) {
            params.push(`orderId=${orderId}`);
        }
        if (status) {
            params.push(`status=${status}`);
        }

        if (params.length > 0) {
            url += '?' + params.join('&');
        }

        $.ajax({
            url: url,
            type: 'GET',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function(response) {
                if (response.status === 200) {
                    populateOrdersTable(response.data);
                } else {
                    console.error('Failed to fetch orders:', response.message);
                    alert('Failed to fetch orders. Please try again.');
                }
            },
            error: function(error) {
                console.error('Error fetching orders:', error);
                alert('An error occurred while fetching orders.');
            }
        });
    }

    // Function to fetch all orders
    function fetchAllOrders() {
        fetchOrders(); // Call fetchOrders with no parameters to get all orders
    }

    // Event listener for the update status button
    $(document).on('click', '.update-status-btn', function() {
        currentOrderId = $(this).data('order-id'); // Store orderId globally
    });

    // Event listener for the status update form submission
    $('#updateStatusForm').submit(function(e) {
        e.preventDefault();

        if (currentOrderId) {
            const newStatus = $('#orderStatus').val();

            $.ajax({
                url: `http://localhost:8080/api/v1/seller/orders/${currentOrderId}/status?status=${newStatus}`,
                type: 'PUT',
                headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
                success: function(response) {
                    if (response.status === 200) {
                        fetchOrders($('#orderIdSearch').val(), $('#orderStatusFilter').val()); // Refresh the table with current filters
                        $('#updateStatusModal').modal('hide');
                    } else {
                        console.error('Failed to update status:', response.message);
                        alert('Failed to update status. Please try again.');
                    }
                },
                error: function(error) {
                    console.error('Error updating status:', error);
                    alert('An error occurred while updating status.');
                }
            });
        } else {
            console.error('Order ID is undefined.');
            alert('Order ID is undefined. Please try again.');
        }
    });

    // Event listener for the order ID search input
    $('#orderIdSearch').on('input', function() {
        const orderId = $(this).val();
        const status = $('#orderStatusFilter').val();

        // Clear previous timeout if any (debounce)
        clearTimeout(searchTimeout);

        // Set a new timeout to fetch after a short delay (e.g., 300ms)
        searchTimeout = setTimeout(function() {
            fetchOrders(orderId, status);
        }, 300);

        // If the search field is empty, immediately fetch all orders
        if (orderId.trim() === '') {
            fetchAllOrders();
        }
    });

    // Event listener for the order ID search input when Enter key is pressed
    $('#orderIdSearch').keypress(function(event) {
        if (event.which === 13) { // Enter key pressed
            clearTimeout(searchTimeout); // Clear any pending timeout
            const orderId = $(this).val();
            const status = $('#orderStatusFilter').val();
            fetchOrders(orderId, status);
        }
    });

    // Event listener for the order status filter dropdown
    $('#orderStatusFilter').change(function() {
        const orderId = $('#orderIdSearch').val();
        const status = $(this).val();
        fetchOrders(orderId, status);
    });

    // Initial population of the orders table
    fetchAllOrders();
});

// New JavaScript function to trigger seller order report download
function downloadSellerOrderReport() {
    const authToken = localStorage.getItem('authToken');
    if (authToken) {
        fetch('http://localhost:8080/api/v1/seller/orders/report/download', {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + authToken
            }
        })
            .then(response => response.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'seller_orders_report.pdf';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
            })
            .catch(error => {
                console.error('Error downloading order report:', error);
                alert('Failed to download order report.');
            });
    } else {
        alert('Authentication token not found. Please log in again.');
    }
}