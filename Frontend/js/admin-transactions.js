$(document).ready(function () {

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
    });

    const transactions = [
        { id: 101, orderId: 1, userId: 10, method: 'Credit Card', status: 'Completed', refund: 'None', amount: 150.00, date: '2025-03-18' },
        { id: 102, orderId: 2, userId: 11, method: 'PayPal', status: 'Pending', refund: 'None', amount: 100.00, date: '2025-03-17' },
        { id: 103, orderId: 3, userId: 12, method: 'Debit Card', status: 'Failed', refund: 'None', amount: 200.00, date: '2025-03-16' },
    ];

    function populateTable(data) {
        const tableBody = $('#transactionTable');
        tableBody.empty();
        data.forEach(txn => {
            tableBody.append(`
                <tr>
                    <td>${txn.id}</td>
                    <td>${txn.orderId}</td>
                    <td>${txn.userId}</td>
                    <td>${txn.method}</td>
                    <td>${txn.status}</td>
                    <td>${txn.refund}</td>
                    <td>$${txn.amount.toFixed(2)}</td>
                    <td>${txn.date}</td>
                    <td>
                        <button class="btn btn-info btn-sm" onclick="viewTransaction(${txn.id})">View</button>
                    </td>
                </tr>
            `);
        });
    }

    window.viewTransaction = function (transactionId) {
        const txn = transactions.find(t => t.id === transactionId);

        if (txn) {
            $('#modalTransactionId').text(txn.id);
            $('#modalOrderId').text(txn.orderId);
            $('#modalUserId').text(txn.userId);
            $('#modalPaymentMethod').text(txn.method);
            $('#modalPaymentStatus').text(txn.status);
            $('#modalRefundStatus').text(txn.refund);
            $('#modalPaidAmount').text(txn.amount.toFixed(2));
            $('#modalTransactionDate').text(txn.date);

            // Show the modal
            $('#transactionModal').modal('show');
        }
    };

    function filterTransactions() {
        const searchQuery = $('#searchTransaction').val().toLowerCase();
        const paymentStatus = $('#filterPaymentStatus').val();
        const refundStatus = $('#filterRefundStatus').val();

        const filtered = transactions.filter(txn =>
            txn.orderId.toString().includes(searchQuery) &&
            (paymentStatus === 'all' || txn.status === paymentStatus) &&
            (refundStatus === 'all' || txn.refund === refundStatus)
        );

        populateTable(filtered);
    }

    $('#searchTransaction, #filterPaymentStatus, #filterRefundStatus').on('input change', filterTransactions);

    populateTable(transactions);
});
