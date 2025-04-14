$(document).ready(function () {
    const baseUrl = 'http://localhost:8080/api/v1';

    function loadRewards() {
        $.ajax({
            url: `${baseUrl}/seller/rewards`,
            type: 'GET',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    let rows = '';
                    response.data.forEach(reward => {
                        rows += `
                            <tr data-reward-id="${reward.rewardId}">
                                <td>${reward.rewardLevel}</td>
                                <td>${reward.points}</td>
                                <td>${reward.redeemedPoints}</td>
                                <td>${reward.points - reward.redeemedPoints}</td>
                                <td>${reward.lastUpdated}</td>
                            </tr>
                        `;
                    });
                    $('#rewardsTable tbody').html(rows);
                } else {
                    console.error('Failed to load rewards:', response);
                    alert('Failed to load rewards.');
                }
            },
            error: function (error) {
                console.error('Error fetching rewards:', error);
                alert('An error occurred while fetching rewards.');
            }
        });
    }

    $('#rewardsTable').on('click', 'tr', function () {

        $('#rewardsTable tr').removeClass('selected'); // Remove selected class from all rows
        $(this).addClass('selected'); // Add selected class to the clicked row
        const rewardId = $(this).data('rewardId');

        $.ajax({
            url: `${baseUrl}/seller/rewards/${rewardId}`,
            type: "GET",
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const reward = response.data;
                    $('#modalRewardLevel').text(reward.rewardLevel);
                    $('#modalPointsEarned').text(reward.points);
                    $('#modalPointsRedeemed').text(reward.redeemedPoints);
                    $('#modalAvailablePoints').text(reward.points - reward.redeemedPoints);
                    $('#modalLastUpdated').text(reward.lastUpdated);

                    $('#rewardModal').modal('show');
                } else {
                    console.error('Failed to load reward details:', response);
                    alert('Failed to load reward details.');
                }
            },
            error: function (error) {
                console.error('Error fetching reward details:', error);
                alert('An error occurred while fetching reward details.');
            }
        });
    });

    // Handle Reward Redemption
    function redeemPoints() {
        const pointsToRedeem = parseInt($('#redeemPoints').val());
        const rewardId = $('#rewardsTable tr.selected').data('rewardId');

        if (isNaN(pointsToRedeem) || pointsToRedeem <= 0) {
            alert('Please enter a valid number of points to redeem.');
            return;
        }

        $.ajax({
            url: `${baseUrl}/seller/rewards/${rewardId}/redeem?pointsToRedeem=${pointsToRedeem}`,
            type: 'PUT',
            headers: { 'Authorization': 'Bearer ' + localStorage.getItem('authToken') },
            success: function (response) {
                if (response.status === 200) {
                    alert('Points redeemed successfully!');
                    loadRewards();
                    $('#redeemPoints').val('');
                } else {
                    alert('Failed to redeem points. Insufficient points or reward not found.');
                }
            },
            error: function (error) {
                console.error('Error redeeming points:', error);
                alert('An error occurred while redeeming points.');
            }
        });
    }

    loadRewards();

    $('#toggleBtn').click(function () {
        $('#sidebar').toggleClass('collapsed');
        $('.content').toggleClass('collapsed');
    });

    // Bind the redeem function to the button using jQuery
    $('.btn-primary').click(redeemPoints);
});