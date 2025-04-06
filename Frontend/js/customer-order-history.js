$(document).ready(function () {
    // Sample Data (This would be replaced by a real AJAX call to fetch data from the backend)
    var orders = [
        {
            order_id: 'O001',
            order_status: 'Shipped',
            placed_at: '2025-02-20',
            items: [
                { part_name: 'Honda Super Cub Engine', quantity: 1, price: 1500.00 },
                { part_name: 'Exhaust Pipe', quantity: 1, price: 200.00 }
            ],
            total_price: 1700.00
        },
        {
            order_id: 'O002',
            order_status: 'Pending',
            placed_at: '2025-02-25',
            items: [
                { part_name: 'Honda Super Cub Seat', quantity: 2, price: 120.00 }
            ],
            total_price: 240.00
        },
        {
            order_id: 'O003',
            order_status: 'Delivered',
            placed_at: '2025-02-18',
            items: [
                { part_name: 'Honda Super Cub Tires', quantity: 4, price: 50.00 }
            ],
            total_price: 200.00
        }
    ];

    // Function to generate status badge
    function getStatusBadge(status) {
        switch (status) {
            case 'Pending': return '<span class="status pending">Pending</span>';
            case 'Shipped': return '<span class="status shipped">Shipped</span>';
            case 'Delivered': return '<span class="status delivered">Delivered</span>';
            case 'Cancelled': return '<span class="status cancelled">Cancelled</span>';
            case 'Returned': return '<span class="status returned">Returned</span>';
            default: return '<span class="status pending">Pending</span>';
        }
    }

    // Populate the table with order history data
    orders.forEach(function(order) {
        var itemsList = order.items.map(function(item) {
            return `<li>${item.part_name} (x${item.quantity}) - $${item.price.toFixed(2)}</li>`;
        }).join('');

        var orderRow = `
            <tr>
                <td>${order.order_id}</td>
                <td>${getStatusBadge(order.order_status)}</td>
                <td>${order.placed_at}</td>
                <td><ul>${itemsList}</ul></td>
                <td>$${order.total_price.toFixed(2)}</td>
            </tr>
        `;

        $('#order-history').append(orderRow);
    });
});
