$(document).ready(function () {

    // Sample reward data to simulate the backend data
    const rewardsData = [
        {
            reward_id: 1,
            customer_name: 'John Doe',
            points: 1200,
            redeemed_points: 200,
            reward_level: 'Gold',
            last_updated: '2025-03-18 10:00:00'
        },
        {
            reward_id: 2,
            customer_name: 'Jane Smith',
            points: 500,
            redeemed_points: 50,
            reward_level: 'Silver',
            last_updated: '2025-03-17 14:30:00'
        },
        {
            reward_id: 3,
            customer_name: 'Alice Brown',
            points: 3000,
            redeemed_points: 0,
            reward_level: 'Platinum',
            last_updated: '2025-03-16 09:45:00'
        },
        {
            reward_id: 4,
            customer_name: 'Bob Johnson',
            points: 800,
            redeemed_points: 150,
            reward_level: 'Bronze',
            last_updated: '2025-03-15 18:20:00'
        }
    ];

    // Toggle Sidebar Functionality
    $("#toggleBtn").click(function () {
        $(".sidebar").toggleClass("collapsed");
    });

    // Function to load rewards data into the table
    function loadRewardsData() {
        let rewardsTableBody = $('#rewardsTable');
        rewardsTableBody.empty(); // Clear the table body before appending new data

        // Populate the table with sample data
        rewardsData.forEach(function (reward) {
            let row = `<tr>
                <td>${reward.reward_id}</td>
                <td>${reward.customer_name}</td>
                <td>${reward.points}</td>
                <td>${reward.redeemed_points}</td>
                <td>${reward.reward_level}</td>
                <td>${new Date(reward.last_updated).toLocaleString()}</td>
                <td>
                    <button class="btn btn-info" onclick="viewReward(${reward.reward_id})">View</button>
                    <button class="btn btn-warning" onclick="adjustReward(${reward.reward_id})">Adjust</button>
                </td>
            </tr>`;
            rewardsTableBody.append(row);
        });
    }

    // Fetch rewards data on page load
    loadRewardsData();

    // Search functionality
    $('#searchReward').on('input', function () {
        let searchValue = $(this).val().toLowerCase();

        // Filter rewards table based on search input
        $('#rewardsTable tr').each(function () {
            let name = $(this).find('td:nth-child(2)').text().toLowerCase();
            $(this).toggle(name.includes(searchValue));
        });
    });

    // Filter rewards by level
    $('#filterLevel').on('change', function () {
        let selectedLevel = $(this).val();

        $('#rewardsTable tr').each(function () {
            let level = $(this).find('td:nth-child(5)').text();
            if (selectedLevel === 'all' || level === selectedLevel) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });

    // View Reward details
    window.viewReward = function (rewardId) {
        // Find reward by ID from sample data
        const reward = rewardsData.find(r => r.reward_id === rewardId);

        // Populate the modal with reward details
        $('#viewId').text(reward.reward_id);
        $('#viewName').text(reward.customer_name);
        $('#viewPoints').text(reward.points);
        $('#viewRedeemedPoints').text(reward.redeemed_points);
        $('#viewLevel').text(reward.reward_level);
        $('#viewLastUpdated').text(new Date(reward.last_updated).toLocaleString());

        // Show the modal
        $('#viewRewardModal').modal('show');
    }

    // Adjust Reward points
    window.adjustReward = function (rewardId) {
        // Find reward by ID from sample data
        const reward = rewardsData.find(r => r.reward_id === rewardId);

        // Pre-fill the modal with reward data for adjustment
        $('#adjustId').val(reward.reward_id);
        $('#adjustName').text(reward.customer_name);
        $('#adjustPoints').val(reward.points); // Display current points for editing

        // Show the adjust modal
        $('#adjustRewardModal').modal('show');
    }

    // Save adjusted reward points
    $('#saveAdjustment').on('click', function () {
        let rewardId = $('#adjustId').val();
        let newPoints = $('#adjustPoints').val();

        // Update points in the sample data array
        let reward = rewardsData.find(r => r.reward_id == rewardId);
        reward.points = newPoints;

        // Close the modal and reload the rewards table
        $('#adjustRewardModal').modal('hide');
        loadRewardsData();
    });

});
