$(document).ready(function() {
    // Sample Data for Transactions (You can replace this with actual data from your server)
    var transactions = [
        {
            transaction_id: 'T001',
            order_id: 'O001',
            user_id: 'U001',
            payment_method: 'Credit Card',
            payment_status: 'Completed',
            refund_status: 'None',
            paid_amount: '150.00',
            transaction_date: '2025-03-01 14:30:00'
        },
        {
            transaction_id: 'T002',
            order_id: 'O002',
            user_id: 'U002',
            payment_method: 'Debit Card',
            payment_status: 'Pending',
            refund_status: 'None',
            paid_amount: '200.00',
            transaction_date: '2025-03-05 10:00:00'
        },
        {
            transaction_id: 'T003',
            order_id: 'O003',
            user_id: 'U003',
            payment_method: 'PayPal',
            payment_status: 'Failed',
            refund_status: 'Full',
            paid_amount: '120.00',
            transaction_date: '2025-03-10 16:45:00'
        }
    ];

    // Loop through the transactions array and create table rows
    for (var i = 0; i < transactions.length; i++) {
        var transaction = transactions[i];
        var row = `
            <tr>
                <td>${transaction.transaction_id}</td>
                <td>${transaction.order_id}</td>
                <td>${transaction.user_id}</td>
                <td>${transaction.payment_method}</td>
                <td>${transaction.payment_status}</td>
                <td>${transaction.refund_status}</td>
                <td>$${transaction.paid_amount}</td>
                <td>${transaction.transaction_date}</td>
                <td><button class="btn btn-info view-btn" data-transaction-id="${transaction.transaction_id}">View</button></td>
            </tr>
        `;
        $('#transactionsTable tbody').append(row);
    }

    // View button functionality
    $(document).on('click', '.view-btn', function() {
        var transactionId = $(this).data('transaction-id');

        // Find the transaction by its ID
        var transaction = transactions.find(function(t) {
            return t.transaction_id === transactionId;
        });

        // Populate the modal with transaction details
        $('#modalTransactionId').text(transaction.transaction_id);
        $('#modalOrderId').text(transaction.order_id);
        $('#modalUserId').text(transaction.user_id);
        $('#modalPaymentMethod').text(transaction.payment_method);
        $('#modalPaymentStatus').text(transaction.payment_status);
        $('#modalRefundStatus').text(transaction.refund_status);
        $('#modalPaidAmount').text(transaction.paid_amount);
        $('#modalTransactionDate').text(transaction.transaction_date);

        // Show the modal
        $('#transactionModal').modal('show');
    });

    // Sidebar Toggle Functionality
    $('#toggleBtn').click(function () {
        $('#sidebar').toggleClass('collapsed');
        $('.content').toggleClass('collapsed');
    });
});
