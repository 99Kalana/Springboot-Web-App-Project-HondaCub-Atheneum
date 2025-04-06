$(document).ready(function () {
    // Sample reward data (this should be replaced with actual data from your database)
    let rewardData = {
        reward_level: "Gold",
        points: 2500,
        redeemed_points: 500,
        last_updated: "2025-03-20 14:30:00"
    };

    // Assign class based on reward level
    let rewardClass = rewardData.reward_level.toLowerCase();

    // Append data to the table
    $("#reward-items").append(`
        <tr>
            <td class="reward-level ${rewardClass}">${rewardData.reward_level}</td>
            <td>${rewardData.points}</td>
            <td>${rewardData.redeemed_points}</td>
            <td>${rewardData.last_updated}</td>
        </tr>
    `);
});
