$(document).ready(function () {
    const baseUrl = 'http://localhost:8080/api/v1/customer/rewards';
    const rewardCardContainer = $("#reward-card-container");
    const rewardImageContainer = $("#reward-image-container");

    const rewardImages = {
        bronze: 'images/bronze.png',
        silver: 'images/silver.png',
        gold: 'images/gold.png',
        platinum: 'images/platinum.png'
    };

    function fetchCustomerRewards() {
        $.ajax({
            url: baseUrl,
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + localStorage.getItem('authToken') // Assuming you store the token
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const rewardData = response.data;
                    let rewardClass = rewardData.rewardLevel ? rewardData.rewardLevel.toLowerCase() : '';

                    // Display the reward image
                    if (rewardImages[rewardClass]) {
                        rewardImageContainer.html(`<img src="${rewardImages[rewardClass]}" alt="${rewardData.rewardLevel}" class="reward-image mx-auto">`);
                    } else {
                        rewardImageContainer.empty(); // Clear any previous image if level is not found
                    }

                    // Display the reward card
                    rewardCardContainer.empty().append(`
    <div class="mx-auto reward-card">
        <div class="reward-level ${rewardClass}">${rewardData.rewardLevel || ''}</div>
        <div class="points-info">
            <div><strong>Total Points:</strong> ${rewardData.points || 0}</div>
            <div><strong>Redeemed Points:</strong> ${rewardData.redeemedPoints || 0}</div>
        </div>
        <div class="last-updated">Last Updated: ${rewardData.lastUpdated || ''}</div>
    </div>
`);
                } else {
                    console.error('Failed to fetch customer rewards:', response);
                    alert('Failed to fetch reward details.');
                }
            },
            error: function (error) {
                console.error('Error fetching customer rewards:', error);
                alert('An error occurred while fetching reward details.');
            }
        });
    }

    // Fetch customer rewards when the page loads
    fetchCustomerRewards();
});