$(document).ready(function () {
    const baseUrl = 'http://localhost:8080/api/v1'; // Define the base URL
    let allTransactions = []; // Store all fetched transactions

    // Function to load transactions from the backend
    function loadTransactions(search) {
        let url = `${baseUrl}/seller/transactions`;
        if (search) {
            url += `?orderId=${search}`; // Append the orderId as a query parameter
        }

        $.ajax({
            url: url,
            type: 'GET',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    allTransactions = response.data; // Update allTransactions with the new data
                    displayTransactions(allTransactions);
                } else {
                    console.error('Failed to load transactions:', response);
                    alert('Failed to load transactions.');
                }
            },
            error: function (error) {
                console.error('Error fetching transactions:', error);
                alert('An error occurred while fetching transactions.');
            }
        });
    }

    // Function to display transactions in the table
    function displayTransactions(transactions) {
        let rows = '';
        transactions.forEach(transaction => {
            rows += `
                    <tr>
                        <td>${transaction.transactionId}</td>
                        <td>${transaction.orderId}</td>
                        <td>${transaction.userId}</td>
                        <td>${transaction.paymentMethod}</td>
                        <td>${transaction.paymentStatus}</td>
                        <td>${transaction.refundStatus}</td>
                        <td>$${transaction.paidAmount}</td>
                        <td>${transaction.transactionDate}</td>
                        <td>${transaction.shippingAddress}</td>
                        <td>${transaction.contactNumber}</td>
                        <td><button class="btn btn-info view-btn" data-transaction-id="${transaction.transactionId}">View</button></td>
                    </tr>
                `;
        });
        $('#transactionsBody').html(rows); // Target the tbody with id 'transactionsBody'
    }

    // Event listener for the search input
    $('#searchOrderId').on('input', function() {
        const searchTerm = $(this).val().trim();
        loadTransactions(searchTerm); // Load data based on input
    });

    // Event listener for pressing Enter in the search input
    $('#searchOrderId').on('keypress', function(event) {
        if (event.key === 'Enter') {
            const searchTerm = $(this).val().trim();
            loadTransactions(searchTerm); // Load data on Enter press
        }
    });

    // View button functionality
    $(document).on('click', '.view-btn', function () {
        const transactionId = $(this).data('transaction-id');

        // Find the transaction in the stored data
        const transaction = allTransactions.find(t => t.transactionId === transactionId);

        if (transaction) {
            // Populate the modal with transaction details
            $('#modalTransactionId').text(transaction.transactionId);
            $('#modalOrderId').text(transaction.orderId);
            $('#modalUserId').text(transaction.userId);
            $('#modalPaymentMethod').text(transaction.paymentMethod);
            $('#modalPaymentStatus').text(transaction.paymentStatus);
            $('#modalRefundStatus').text(transaction.refundStatus);
            $('#modalPaidAmount').text(transaction.paidAmount);
            $('#modalTransactionDate').text(transaction.transactionDate);
            $('#modalShippingAddress').text(transaction.shippingAddress);
            $('#modalContactNumber').text(transaction.contactNumber);

            // Show the modal
            $('#transactionModal').modal('show');
        } else {
            console.error('Transaction not found in loaded data:', transactionId);
            alert('Transaction details not found.');
        }
    });



    // Load all transactions on page load (initial load with empty search term)
    loadTransactions('');

    // Sidebar Toggle Functionality
    $('#toggleBtn').click(function () {
        $('#sidebar').toggleClass('collapsed');
        $('.content').toggleClass('collapsed');
    });
});


// New JavaScript function to trigger seller transaction report download
function downloadSellerTransactionReport() {
    const authToken = localStorage.getItem('authToken');
    if (authToken) {
        fetch(`http://localhost:8080/api/v1/seller/transactions/report/download`, {
            method: 'GET',
            headers: {
                'Authorization': 'Bearer ' + authToken // Include the Authorization header
            }
        })
            .then(response => response.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'seller_transaction_report.pdf';
                document.body.appendChild(a);
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
            })
            .catch(error => {
                console.error('Error downloading report:', error);
                alert('Failed to download transaction report.');
            });
    } else {
        alert('Authentication token not found. Please log in again.');
    }
}