// admin-orders.js

$(document).ready(function() {

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function() {
        $(".sidebar").toggleClass("collapsed");
    });

    // Populate Table
    function populateTable(orders) {
        const tableBody = $('#orderTable');
        tableBody.empty();
        if (orders && orders.length > 0) {
            orders.forEach(order => {
                const row = `
                    <tr>
                        <td>${order.orderId}</td>
                        <td>${order.fullName}</td>
                        <td>${order.placedAt}</td>
                        <td>${order.orderStatus}</td>
                        <td>$${order.totalAmount ? order.totalAmount.toFixed(2) : 'N/A'}</td>
                        <td>
                            <button class="btn btn-primary btn-sm view-btn" data-id="${order.orderId}" data-bs-toggle="modal" data-bs-target="#viewOrderModal"><i class="bi bi-eye"></i> View</button>
                            <button class="btn btn-warning btn-sm update-btn" data-id="${order.orderId}" data-bs-toggle="modal" data-bs-target="#updateOrderModal"><i class="bi bi-arrow-clockwise"></i> Update</button>
                            <button class="btn btn-danger btn-sm cancel-btn" data-id="${order.orderId}" data-bs-toggle="modal" data-bs-target="#cancelOrderModal"><i class="bi bi-x-circle"></i> Cancel</button>
                        </td>
                    </tr>`;
                tableBody.append(row);
            });
        } else {
            tableBody.append('<tr><td colspan="6" class="text-center">No orders found.</td></tr>');
        }
    }

    // Calculate Total Amount (No longer needed on the frontend as the backend provides it)
    // function calculateTotalAmount(orderDetailIds) {
    //     return "N/A";
    // }

    // Load Orders from Backend
    function loadOrders() {
        $.ajax({
            url: "http://localhost:8080/api/v1/adminorders/getAll",
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateTable(response.data);
                } else {
                    console.error("Failed to load orders:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching orders:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // View Order
    $(document).on("click", ".view-btn", function() {
        const orderId = $(this).data("id");
        $.ajax({
            url: `http://localhost:8080/api/v1/adminorders/get/${orderId}`,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    const order = response.data;
                    $('#viewOrderId').text(order.orderId);
                    $('#viewCustomerName').text(order.fullName);
                    $('#viewOrderDate').text(order.placedAt);
                    $('#viewOrderStatus').text(order.orderStatus);
                    $('#viewOrderTotal').text(order.totalAmount ? order.totalAmount.toFixed(2) : 'N/A');
                    $('#viewOrderModal').modal('show');
                } else {
                    console.error("Failed to load order:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching order:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    });

    // Update Order Status
    $(document).on("click", ".update-btn", function() {
        const orderId = $(this).data("id");
        $('#updateOrderId').val(orderId);
        $('#updateOrderModal').modal('show');
    });

    $("#updateOrderButton").click(function() {
        const orderId = $('#updateOrderId').val();
        const status = $('#updateOrderStatus').val();
        $.ajax({
            url: `http://localhost:8080/api/v1/adminorders/update/${orderId}`,
            method: "PUT",
            contentType: "application/json",
            data: JSON.stringify({ orderStatus: status }),
            success: function(response) {
                if (response.status === 200) {
                    loadOrders();
                    $('#updateOrderModal').modal('hide');
                } else {
                    alert("Failed to update order status.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error updating order status:", error);
                console.log("Response:", xhr.responseText);
                alert("Error updating order status.");
            }
        });
    });

    // Cancel Order
    $(document).on("click", ".cancel-btn", function() {
        const orderId = $(this).data("id");
        $('#cancelOrderId').text(orderId);
        $('#cancelOrderModal').modal('show');
    });

    $("#cancelOrderButton").click(function() {
        const orderId = $('#cancelOrderId').text();
        $.ajax({
            url: `http://localhost:8080/api/v1/adminorders/cancel/${orderId}`,
            method: "DELETE",
            success: function(response) {
                if (response.status === 200) {
                    loadOrders();
                    $('#cancelOrderModal').modal('hide');
                } else {
                    alert("Failed to cancel order.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error cancelling order:", error);
                console.log("Response:", xhr.responseText);
                alert("Error cancelling order.");
            }
        });
    });

    // Filter Orders by Status
    $('#filterStatus').change(function() {
        const status = $(this).val();
        let url = "http://localhost:8080/api/v1/adminorders/getAll";
        if (status !== "all") {
            url = `http://localhost:8080/api/v1/adminorders/filter?status=${status}`;
        }
        $.ajax({
            url: url,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateTable(response.data);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error filtering orders:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    });

    // Search Orders by Customer Name
    $('#searchOrder').keyup(function() {
        const query = $(this).val();
        $.ajax({
            url: `http://localhost:8080/api/v1/adminorders/search?query=${query}`,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateTable(response.data);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error searching orders:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    });

    // Initial Load
    loadOrders();
});

// New JavaScript function to trigger order report download
function downloadOrderReport() {
    window.location.href = "http://localhost:8080/api/v1/adminorders/report/download";
}