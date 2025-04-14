$(document).ready(function() {

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function() {
        $(".sidebar").toggleClass("collapsed");
    });

    // Populate Table
    function populateTable(transactions) {
        const tableBody = $('#transactionTable');
        tableBody.empty();
        if (transactions && transactions.length > 0) {
            transactions.forEach(transaction => {
                const row = `
                <tr>
                    <td>${transaction.transactionId}</td>
                    <td>${transaction.orderId}</td>
                    <td>${transaction.userId}</td>
                    <td>${transaction.paymentMethod}</td>
                    <td>${transaction.paymentStatus}</td>
                    <td>${transaction.refundStatus}</td>
                    <td>$${transaction.paidAmount.toFixed(2)}</td>
                    <td>${transaction.transactionDate}</td>
                    <td>
                        <button class="btn btn-info btn-sm view-btn" data-id="${transaction.transactionId}" data-bs-toggle="modal" data-bs-target="#transactionModal"><i class="bi bi-eye"></i> View</button>
                    </td>
                </tr>`;
                tableBody.append(row);
            });
        } else {
            tableBody.append('<tr><td colspan="9" class="text-center">No transactions found.</td></tr>');
        }
    }

    // View Transaction
    $(document).on("click", ".view-btn", function() {
        const transactionId = $(this).data("id");
        $.ajax({
            url: `http://localhost:8080/api/v1/admintransactions/get/${transactionId}`,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    const transaction = response.data;
                    $('#modalTransactionId').text(transaction.transactionId);
                    $('#modalOrderId').text(transaction.orderId);
                    $('#modalUserId').text(transaction.userId);
                    $('#modalPaymentMethod').text(transaction.paymentMethod);
                    $('#modalPaymentStatus').text(transaction.paymentStatus);
                    $('#modalRefundStatus').text(transaction.refundStatus);
                    $('#modalPaidAmount').text(transaction.paidAmount.toFixed(2));
                    $('#modalTransactionDate').text(transaction.transactionDate);
                    $('#transactionModal').modal('show');
                } else {
                    console.error("Failed to load transaction:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching transaction:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    });

    // Load Transactions from Backend
    function loadTransactions() {
        $.ajax({
            url: "http://localhost:8080/api/v1/admintransactions/getAll",
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateTable(response.data);
                } else {
                    console.error("Failed to load transactions:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching transactions:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // Filter Transactions by Order ID
    $('#searchTransaction').keyup(function() {
        const query = $(this).val();
        if(query){
            $.ajax({
                url: `http://localhost:8080/api/v1/admintransactions/byOrder/${query}`,
                method: "GET",
                dataType: "json",
                success: function(response) {
                    if (response.status === 200 && response.data) {
                        populateTable(response.data);
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Error filtering transactions by order ID:", error);
                    console.log("Response:", xhr.responseText);
                }
            });
        }else{
            loadTransactions();
        }
    });

    // Filter Transactions by Payment Status
    $('#filterPaymentStatus').change(function() {
        const status = $(this).val();
        if (status !== "all") {
            $.ajax({
                url: `http://localhost:8080/api/v1/admintransactions/byPaymentStatus/${status}`,
                method: "GET",
                dataType: "json",
                success: function(response) {
                    if (response.status === 200 && response.data) {
                        populateTable(response.data);
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Error filtering transactions by payment status:", error);
                    console.log("Response:", xhr.responseText);
                }
            });
        } else {
            loadTransactions();
        }
    });

    // Filter Transactions by Refund Status
    $('#filterRefundStatus').change(function() {
        const status = $(this).val();
        if (status !== "all") {
            $.ajax({
                url: `http://localhost:8080/api/v1/admintransactions/byRefundStatus/${status}`,
                method: "GET",
                dataType: "json",
                success: function(response) {
                    if (response.status === 200 && response.data) {
                        populateTable(response.data);
                    }
                },
                error: function(xhr, status, error) {
                    console.error("Error filtering transactions by refund status:", error);
                    console.log("Response:", xhr.responseText);
                }
            });
        } else {
            loadTransactions();
        }
    });

    // Initial Load
    loadTransactions();
});

// New JavaScript function to trigger transaction report download
function downloadTransactionReport() {
    window.location.href = "http://localhost:8080/api/v1/admintransactions/report/download";
}