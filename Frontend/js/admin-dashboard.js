$(document).ready(function() {
    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function() {
        $(".sidebar").toggleClass("collapsed");
    });

    // Load Dashboard Data
    function loadDashboardData() {
        $.ajax({
            url: "http://localhost:8080/api/v1/admindashboard/data",
            method: "GET",
            dataType: "json",
            success: function(data) {
                $("#totalUsers").text(data.totalUsers);
                $("#totalOrders").text(data.totalOrders);
                $("#totalSpareParts").text(data.totalSpareParts);
                $("#totalTransactions").text("$" + data.totalTransactions.toFixed(2));
            },
            error: function(xhr, status, error) {
                console.error("Error fetching dashboard data:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // Populate Recent Orders Table
    function populateRecentOrdersTable(orders) {
        const tableBody = $('#recentOrdersTable tbody');
        tableBody.empty();
        if (orders && orders.length > 0) {
            orders.forEach(order => {
                const status = order.orderStatus.toUpperCase(); // Normalize to uppercase
                let badgeClass = '';

                switch (status) {
                    case 'DELIVERED':
                        badgeClass = 'success';
                        break;
                    case 'PENDING':
                        badgeClass = 'warning';
                        break;
                    case 'CANCELLED':
                    case 'RETURNED':
                        badgeClass = 'danger';
                        break;
                    case 'SHIPPED':
                        badgeClass = 'info';
                        break;
                    default:
                        badgeClass = 'secondary'; // Default badge color
                }

                const row = `
                <tr>
                    <td>${order.orderId}</td>
                    <td>${order.fullName}</td>
                    <td>${order.placedAt}</td>
                    <td><span class="badge bg-${badgeClass}">${order.orderStatus}</span></td>

                </tr>
            `;
                tableBody.append(row);
            });
        } else {
            tableBody.append('<tr><td colspan="5" class="text-center">No recent orders found.</td></tr>');
        }
    }

    // Load Recent Orders
    function loadRecentOrders() {
        $.ajax({
            url: "http://localhost:8080/api/v1/adminorders/getAll",
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateRecentOrdersTable(response.data);
                } else {
                    console.error("Failed to load recent orders:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching recent orders:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // Backup Database Functionality
    $("#backupDatabaseBtn").click(function() {
        $.ajax({
            url: "http://localhost:8080/api/v1/admin/backup",
            method: "GET",
            xhrFields: {
                responseType: 'blob' // Important for handling binary data
            },
            success: function(data, status, xhr) {
                const filenameHeader = xhr.getResponseHeader('Content-Disposition');
                let filename = 'database_backup.sql'; // Default filename if header is missing or malformed

                if (filenameHeader && filenameHeader.indexOf('filename=') > -1) {
                    filename = filenameHeader.split('filename=')[1].split(';')[0].trim();
                    // Some servers might include extra parameters after filename, so we split by ';' and take the first part
                }

                const url = window.URL.createObjectURL(data);
                const downloadLink = document.createElement('a');
                downloadLink.href = url;
                downloadLink.download = filename;
                document.body.appendChild(downloadLink);

                downloadLink.click();

                // Clean up the temporary URL after a short delay
                setTimeout(() => {
                    window.URL.revokeObjectURL(url);
                    document.body.removeChild(downloadLink);
                }, 100);
            },
            error: function(xhr, status, error) {
                console.error("Error backing up database:", error);
                alert("Failed to backup database.");
            }
        });
    });

    // Initial Load
    loadDashboardData();
    loadRecentOrders();
});