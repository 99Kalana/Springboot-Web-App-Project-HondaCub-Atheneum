$(document).ready(function () {
    // Sample data for dashboard cards
    const totalProducts = 50;
    const pendingOrders = 12;
    const salesRevenue = 8500;

    // Update dashboard cards
    $(".card.bg-info h3").text(totalProducts);
    $(".card.bg-warning h3").text(pendingOrders);
    $(".card.bg-success h3").text(`$${salesRevenue}`);

    // Sample data for recent orders
    const recentOrders = [
        { orderId: "ORD201", customer: "Mark Wilson", date: "2025-03-17", status: "Completed", total: "$200.00" },
        { orderId: "ORD202", customer: "Lisa Brown", date: "2025-03-16", status: "Pending", total: "$150.00" },
        { orderId: "ORD203", customer: "David Clark", date: "2025-03-15", status: "Cancelled", total: "$0.00" }
    ];

    // Populate recent orders table
    const $orderTableBody = $("table tbody");
    $orderTableBody.empty();

    recentOrders.forEach(order => {
        let statusBadge;
        switch (order.status) {
            case "Completed":
                statusBadge = '<span class="badge bg-success">Completed</span>';
                break;
            case "Pending":
                statusBadge = '<span class="badge bg-warning">Pending</span>';
                break;
            case "Cancelled":
                statusBadge = '<span class="badge bg-danger">Cancelled</span>';
                break;
        }

        const row = `
            <tr>
                <td>${order.orderId}</td>
                <td>${order.customer}</td>
                <td>${order.date}</td>
                <td>${statusBadge}</td>
                <td>${order.total}</td>
            </tr>
        `;
        $orderTableBody.append(row);
    });

    // Sidebar toggle functionality (Keep only one)
    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
        $(".content").toggleClass("expanded");
    });
});
