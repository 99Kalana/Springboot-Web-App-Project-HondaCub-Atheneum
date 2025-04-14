$(document).ready(function() {

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function() {
        $(".sidebar").toggleClass("collapsed");
    });

    // Populate Table
    function populateTable(rewards) {
        const tableBody = $('#rewardsTable');
        tableBody.empty();
        if (rewards && rewards.length > 0) {
            rewards.forEach(reward => {
                const row = `
                <tr>
                    <td>${reward.rewardId}</td>
                    <td>${reward.fullName}</td>
                    <td>${reward.points}</td>
                    <td>${reward.redeemedPoints}</td>
                    <td>${reward.rewardLevel}</td>
                    <td>${reward.lastUpdated}</td>
                    <td>
                        <button class="btn btn-info btn-sm view-btn" data-id="${reward.rewardId}" data-bs-toggle="modal" data-bs-target="#viewRewardModal"><i class="bi bi-eye"></i> View</button>
                        <button class="btn btn-warning btn-sm adjust-btn" data-id="${reward.rewardId}" data-bs-toggle="modal" data-bs-target="#adjustRewardModal"><i class="bi bi-pencil"></i> Adjust</button>
                    </td>
                </tr>`;
                tableBody.append(row);
            });
        } else {
            tableBody.append('<tr><td colspan="7" class="text-center">No rewards found.</td></tr>');
        }
    }

    // Load Rewards from Backend
    function loadRewards() {
        $.ajax({
            url: "http://localhost:8080/api/v1/adminrewards/getAll",
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateTable(response.data);
                } else {
                    console.error("Failed to load rewards:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching rewards:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    }

    // View Reward
    $(document).on("click", ".view-btn", function() {
        const rewardId = $(this).data("id");
        $.ajax({
            url: `http://localhost:8080/api/v1/adminrewards/get/${rewardId}`,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    const reward = response.data;
                    $('#viewId').text(reward.rewardId);
                    $('#viewName').text(reward.fullName); // Assuming userId is customer name for now
                    $('#viewPoints').text(reward.points);
                    $('#viewRedeemedPoints').text(reward.redeemedPoints);
                    $('#viewLevel').text(reward.rewardLevel);
                    $('#viewLastUpdated').text(reward.lastUpdated);
                    $('#viewRewardModal').modal('show');
                } else {
                    console.error("Failed to load reward:", response);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error fetching reward:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    });

    // Adjust Reward
    $(document).on("click", ".adjust-btn", function() {
        const rewardId = $(this).data("id");
        $('#adjustId').val(rewardId);
        $.ajax({
            url: `http://localhost:8080/api/v1/adminrewards/get/${rewardId}`,
            method: "GET",
            dataType: "json",
            success: function(response){
                if(response.status === 200 && response.data){
                    $('#adjustName').text(response.data.fullName);
                    $('#adjustPoints').val(response.data.points);
                    $('#adjustRewardModal').modal('show');
                }
                else{
                    console.error("Failed to load reward for adjustment:", response);
                }
            },
            error: function(xhr, status, error){
                console.error("Error fetching reward for adjustment:", error);
                console.log("Response:", xhr.responseText);
            }
        });

    });

    $("#saveAdjustment").click(function() {
        const rewardId = $('#adjustId').val();
        const points = $('#adjustPoints').val();
        $.ajax({
            url: `http://localhost:8080/api/v1/adminrewards/update/${rewardId}`,
            method: "PUT",
            contentType: "application/json",
            data: JSON.stringify({ points: parseInt(points) }),
            success: function(response) {
                if (response.status === 200) {
                    loadRewards();
                    $('#adjustRewardModal').modal('hide');
                } else {
                    alert("Failed to update reward.");
                }
            },
            error: function(xhr, status, error) {
                console.error("Error updating reward:", error);
                console.log("Response:", xhr.responseText);
                alert("Error updating reward.");
            }
        });
    });

    // Filter Rewards by Level
    $('#filterLevel').change(function() {
        const level = $(this).val();
        let url = "http://localhost:8080/api/v1/adminrewards/getAll";
        if (level !== "all") {
            url = `http://localhost:8080/api/v1/adminrewards/filter?level=${level}`;
        }
        $.ajax({
            url: url,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateTable(response.data);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error filtering rewards:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    });

    // Search Rewards by Customer Name
    $('#searchReward').keyup(function() {
        const query = $(this).val();
        $.ajax({
            url: `http://localhost:8080/api/v1/adminrewards/search?query=${query}`,
            method: "GET",
            dataType: "json",
            success: function(response) {
                if (response.status === 200 && response.data) {
                    populateTable(response.data);
                }
            },
            error: function(xhr, status, error) {
                console.error("Error searching rewards:", error);
                console.log("Response:", xhr.responseText);
            }
        });
    });

    // Initial Load
    loadRewards();
});