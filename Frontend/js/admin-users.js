$(document).ready(function () {
    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
    });

    // Load Users from Backend
    function loadUsers() {
        let selectedRole = $('#filterRole').val(); // Get selected role from dropdown
        $.ajax({
            url: `http://localhost:8080/api/v1/adminuser/getAll?role=${selectedRole}`,
            method: "GET",
            dataType: "json",
            success: function (response) {
                console.log("Response from server:", response); // Log the full response

                if (response.status === 200 && response.data && Array.isArray(response.data)) {
                    const tableBody = $("#userTable");
                    tableBody.empty(); // Clear the table body

                    response.data.forEach(user => {
                        let statusClass = user.status === "ACTIVE" ? "text-success" : "text-danger";
                        let statusButton = user.status === "ACTIVE"
                            ? `<button class="btn btn-danger btn-sm toggle-status" data-id="${user.userId}" data-status="INACTIVE">Deactivate</button>`
                            : `<button class="btn btn-success btn-sm toggle-status" data-id="${user.userId}" data-status="ACTIVE">Activate</button>`;

                        // Append the user row to the table
                        tableBody.append(`
                            <tr>
                                <td>${user.userId}</td>
                                <td>${user.fullName}</td>
                                <td>${user.email}</td>
                                <td>${user.role}</td>
                                <td class="${statusClass}">${user.status}</td>
                                <td>${statusButton}</td>
                            </tr>
                        `);
                    });
                } else {
                    console.warn("No users found or invalid response format.");
                    const tableBody = $("#userTable");
                    tableBody.empty(); // Clear the table body
                    tableBody.append('<tr><td colspan="6" class="text-center">No users found.</td></tr>');
                }
            },
            error: function (xhr, status, error) {
                console.error("Error fetching users:", error);
                console.log("Response:", xhr.responseText); // Log the response text for more details
            }
        });
    }

    // Change User Status
    $(document).on("click", ".toggle-status", function () {
        let userId = $(this).data("id");
        let newStatus = $(this).data("status");

        $.ajax({
            url: `http://localhost:8080/api/v1/adminuser/updateStatus/${userId}/${newStatus}`,
            method: "PUT",
            contentType: "application/json",
            success: function (response) {
                if (response.status === 200) {
                    loadUsers(); // Reload users after status update
                } else {
                    alert("Failed to update status!");
                }
            },
            error: function () {
                alert("Error updating status.");
            }
        });
    });

    // Filter Users by Role
    $('#filterRole').change(function () {
        loadUsers(); // Reload users when the role is changed
    });


    // Search Users by Full Name and Email
    $('#searchUser ').on('input', function () {
        const searchTerm = $(this).val().toLowerCase();
        if (searchTerm.length > 0) {
            $.ajax({
                url: `http://localhost:8080/api/v1/adminuser/search?term=${searchTerm}`,
                method: "GET",
                dataType: "json",
                success: function (response) {
                    const tableBody = $("#userTable");
                    tableBody.empty(); // Clear the table body

                    if (response.status === 200 && response.data && Array.isArray(response.data)) {
                        response.data.forEach(user => {
                            let statusClass = user.status === "ACTIVE" ? "text-success" : "text-danger";
                            let statusButton = user.status === "ACTIVE"
                                ? `<button class="btn btn-danger btn-sm toggle-status" data-id="${user.userId}" data-status="INACTIVE">Deactivate</button>`
                                : `<button class="btn btn-success btn-sm toggle-status" data-id="${user.userId}" data-status="ACTIVE">Activate</button>`;

                            // Append the user row to the table
                            tableBody.append(`
                        <tr>
                            <td>${user.userId}</td>
                            <td>${user.fullName}</td>
                            <td>${user.email}</td>
                            <td>${user.role}</td>
                            <td class="${statusClass}">${user.status}</td>
                            <td>${statusButton}</td>
                        </tr>
                    `);
                        });
                    } else {
                        tableBody.append('<tr><td colspan="6" class="text-center">No users found.</td></tr>');
                    }
                },
                error: function (xhr, status, error) {
                    console.error("Error fetching users:", error);
                    console.log("Response:", xhr.responseText); // Log the response text for more details
                }
            });
        } else {
            loadUsers(); // Reload all users if the search term is empty
        }
    });

    // Initial Load
    loadUsers();
});