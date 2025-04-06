$(document).ready(function () {
    // Sidebar Toggle Button
    $('#toggleBtn').click(function () {
        $('#sidebar').toggleClass('collapsed');
        $('.content').toggleClass('collapsed');
    });

    // Sample Reward Data (for illustration purposes, replace this with actual data)
    const rewardsData = [
        { rewardLevel: 'Silver', pointsEarned: 1000, pointsRedeemed: 300, availablePoints: 700, lastUpdated: '2025-03-01' },
        { rewardLevel: 'Gold', pointsEarned: 2000, pointsRedeemed: 500, availablePoints: 1500, lastUpdated: '2025-02-25' },
        { rewardLevel: 'Platinum', pointsEarned: 5000, pointsRedeemed: 1000, availablePoints: 4000, lastUpdated: '2025-02-15' }
    ];

    // Function to Populate Rewards Table
    function populateRewardsTable() {
        const tableBody = $('#rewardsTable tbody');
        tableBody.empty(); // Clear any existing rows

        rewardsData.forEach((reward) => {
            const row = `
                <tr>
                    <td>${reward.rewardLevel}</td>
                    <td>${reward.pointsEarned}</td>
                    <td>${reward.pointsRedeemed}</td>
                    <td>${reward.availablePoints}</td>
                    <td>${reward.lastUpdated}</td>
                </tr>
            `;
            tableBody.append(row);
        });
    }

    // Call the function to populate the rewards table on page load
    populateRewardsTable();

    // Show Reward Details in Modal
    $('#rewardsTable').on('click', 'tr', function () {
        const rewardLevel = $(this).find('td').eq(0).text();
        const pointsEarned = $(this).find('td').eq(1).text();
        const pointsRedeemed = $(this).find('td').eq(2).text();
        const availablePoints = $(this).find('td').eq(3).text();
        const lastUpdated = $(this).find('td').eq(4).text();

        // Populate modal with data
        $('#modalRewardLevel').text(rewardLevel);
        $('#modalPointsEarned').text(pointsEarned);
        $('#modalPointsRedeemed').text(pointsRedeemed);
        $('#modalAvailablePoints').text(availablePoints);
        $('#modalLastUpdated').text(lastUpdated);

        // Show the modal
        $('#rewardModal').modal('show');
    });

    // Handle Reward Redemption
    function redeemPoints() {
        const pointsToRedeem = parseInt($('#redeemPoints').val());

        if (isNaN(pointsToRedeem) || pointsToRedeem <= 0) {
            alert('Please enter a valid number of points to redeem.');
            return;
        }

        // Update rewards data (for demo purposes, we'll just subtract the points)
        rewardsData[0].availablePoints -= pointsToRedeem;

        // Update the table with the new available points
        populateRewardsTable();

        // Clear the input field
        $('#redeemPoints').val('');
    }

    // Bind the redeem function to the button
    $('.btn-primary').click(redeemPoints);
});
