$(document).ready(function () {
    const baseUrl = 'http://localhost:8080/api/v1';

    function loadDashboardData() {
        $.ajax({
            url: `${baseUrl}/seller/dashboard`,
            type: 'GET',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const dashboardData = response.data;

                    $(".card.bg-info h3").text(dashboardData.totalProducts);
                    $(".card.bg-warning h3").text(dashboardData.pendingOrders);
                    $(".card.bg-success h3").text(`$${dashboardData.totalSales}`);

                    const $orderTableBody = $("table tbody");
                    $orderTableBody.empty();

                    dashboardData.orders.forEach(order => {
                        let statusBadge;
                        switch (order.orderStatus) {
                            case "DELIVERED":
                                statusBadge = '<span class="badge bg-success">Delivered</span>';
                                break;
                            case "PENDING":
                                statusBadge = '<span class="badge bg-warning">Pending</span>';
                                break;
                            case "CANCELLED":
                                statusBadge = '<span class="badge bg-danger">Cancelled</span>';
                                break;
                            case "SHIPPED":
                                statusBadge = '<span class="badge bg-info">Shipped</span>';
                                break;
                            default:
                                statusBadge = `<span class="badge bg-secondary">${order.orderStatus}</span>`;
                        }

                        // Null check for orderDetails
                        const orderTotal = (order.orderDetails && order.orderDetails.length > 0)
                            ? order.orderDetails.reduce((total, detail) => total + (detail.quantity * detail.price), 0)
                            : 0;

                        // Null check for fullName
                        const customerName = order.fullName ? order.fullName : "Unknown";

                        const row = `
                            <tr>
                                <td>${order.orderId}</td>
                                <td>${customerName}</td>
                                <td>${order.placedAt}</td>
                                <td>${statusBadge}</td>

                            </tr>
                        `;
                        $orderTableBody.append(row);
                    });
                } else {
                    console.error('Failed to load dashboard data:', response);
                    alert('Failed to load dashboard data.');
                }
            },
            error: function (error) {
                console.error('Error fetching dashboard data:', error);
                alert('An error occurred while fetching dashboard data.');
            }
        });
    }

    loadDashboardData();

    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
        $(".content").toggleClass("expanded");
    });
});