$(document).ready(function () {
    const authToken = localStorage.getItem('authToken');

    // Function to generate status badge
    function getStatusBadge(status) {
        switch (status) {
            case 'PENDING': return '<span class="status pending">Pending</span>';
            case 'SHIPPED': return '<span class="status shipped">Shipped</span>';
            case 'DELIVERED': return '<span class="status delivered">Delivered</span>';
            case 'CANCELLED': return '<span class="status cancelled">Cancelled</span>';
            case 'RETURNED': return '<span class="status returned">Returned</span>';
            default: return '<span class="status pending">Pending</span>';
        }
    }

    // Function to load order history from the backend (modified for status filter)
    function loadOrderHistory(orderId = null, orderStatus = null) {
        let url = 'http://localhost:8080/api/v1/customer/orders';
        let queryParams = [];

        if (orderId) {
            queryParams.push(`orderId=${orderId}`);
        }
        if (orderStatus) {
            queryParams.push(`status=${orderStatus}`);
        }

        if (queryParams.length > 0) {
            url += '?' + queryParams.join('&');
        }

        $.ajax({
            url: url,
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + authToken
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const orders = response.data;
                    const detailPromises = orders.map(order => {
                        return $.ajax({
                            url: `http://localhost:8080/api/v1/customer/orders/${order.orderId}/details`,
                            type: 'GET',
                            headers: {
                                'Authorization': 'Bearer ' + authToken
                            }
                        }).done(response => {
                            console.log(`Order ID: ${order.orderId}, Response:`, JSON.stringify(response, null, 2));
                        }).fail((xhr, status, error) => {
                            console.error(`Failed to fetch details for Order ID: ${order.orderId}`, error);
                        });
                    });

                    Promise.all(detailPromises)
                        .then(detailResponses => {
                            let orderRows = '';
                            orders.forEach((order, index) => {
                                const detailsResponse = detailResponses[index];

                                // Debugging: Inspect detailsResponse
                                console.log('Details Response:', detailsResponse);

                                // Check if the response is successful, contains data, and data is an array
                                if (detailsResponse && detailsResponse.status === 200 && Array.isArray(detailsResponse.data)) {
                                    const itemsList = detailsResponse.data.map(item => {
                                        return `<li>${item.sparePartName} (x${item.quantity}) - $${item.price.toFixed(2)}</li>`;
                                    }).join('');

                                    const totalPrice = detailsResponse.data.reduce((total, item) => total + (item.price * item.quantity), 0).toFixed(2);

                                    // Debugging logs
                                    console.log('Order ID:', order.orderId);
                                    console.log('Items List:', itemsList);
                                    console.log('Total Price:', totalPrice);

                                    // Parse and format the placedAt date:
                                    let formattedDate = order.placedAt;

                                    if (order.placedAt && !isNaN(new Date(order.placedAt).getTime())) {
                                        const placedAtDate = new Date(order.placedAt);
                                        formattedDate = placedAtDate.toLocaleDateString('en-US', {
                                            year: 'numeric',
                                            month: 'long',
                                            day: 'numeric',
                                            hour: '2-digit',
                                            minute: '2-digit',
                                            second: '2-digit'
                                        });
                                    }

                                    orderRows += `
                                    <tr>
                                        <td>${order.orderId}</td>
                                        <td>${getStatusBadge(order.orderStatus)}</td>
                                        <td>${formattedDate}</td>
                                        <td><ul>${itemsList}</ul></td>
                                        <td>$${totalPrice}</td>
                                        <td><button class="btn btn-sm btn-primary print-bill-btn" data-order-id="${order.orderId}">Print Bill</button></td>
                                    </tr>
                                `;
                                } else {
                                    console.error('Failed to load order details for order ID:', order.orderId, detailsResponse);
                                    orderRows += `
                                    <tr>
                                        <td>${order.orderId}</td>
                                        <td>${getStatusBadge(order.orderStatus)}</td>
                                        <td>${order.placedAt}</td>
                                        <td colspan="2">Failed to load order details.</td>
                                    </tr>
                                `;
                                }
                            });
                            // Debugging log for final order rows
                            console.log('Final Order Rows:', orderRows);
                            $('#order-history').html(orderRows);
                        })
                        .catch(error => {
                            console.error('Error fetching order details:', error);
                            $('#order-history').html('<tr><td colspan="5" class="text-center">Failed to load order details.</td></tr>');
                        });
                } else {
                    console.error('Failed to load order history:', response);
                    $('#order-history').html('<tr><td colspan="5" class="text-center">No order history found.</td></tr>');
                }
            },
            error: function (xhr, status, error) {
                console.error('Error fetching order history:', error);
                $('#order-history').html('<tr><td colspan="5" class="text-center">Failed to load order history.</td></tr>');
            }
        });
    }

    // Load order history when the page is ready
    loadOrderHistory();

    // Event listener for print bill button
    $(document).on('click', '.print-bill-btn', function () {
        const orderId = $(this).data('order-id');
        // Implement the logic to generate and download the PDF bill
        generateAndDownloadBill(orderId);
    });

    // Function to generate and download the PDF bill
    function generateAndDownloadBill(orderId) {
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/orders/${orderId}/bill`, // Backend endpoint to generate bill
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + authToken
            },
            xhrFields: {
                responseType: 'blob' // Important: To receive binary data (PDF)
            },
            success: function (blob) {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = `order-bill-${orderId}.pdf`;
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
            },
            error: function (xhr, status, error) {
                console.error('Failed to generate bill:', error);
                alert('Failed to generate bill. Please try again.');
            }
        });
    }

    // Event listener for Enter key press on the search input
    $('#orderIdSearch').keypress(function (event) {
        if (event.which === 13) {
            const orderId = $('#orderIdSearch').val();
            const orderStatus = $('#orderStatusFilter').val();
            loadOrderHistory(orderId, orderStatus);
        }
    });

    // Event listener for input change to load all orders when empty
    $('#orderIdSearch').on('input', function() {
        if ($(this).val() === '') {
            loadOrderHistory(null, $('#orderStatusFilter').val());
        }
    });

    // Event listener for order status filter change
    $('#orderStatusFilter').change(function () {
        const orderStatus = $(this).val();
        loadOrderHistory(null, orderStatus);
    });

});