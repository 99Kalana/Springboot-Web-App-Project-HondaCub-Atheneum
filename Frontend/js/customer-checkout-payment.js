$(document).ready(function () {
    // Sample order details
    let orderID = 1001;
    let userID = 501;
    let totalAmount = 250.75;
    $("#total-amount").text(totalAmount.toFixed(2));

    // Payment method selection logic
    $("input[name='paymentMethod']").change(function () {
        let paymentMethod = $(this).val();
        $(".card-details, .paypal-details").hide();

        if (paymentMethod === "Credit Card" || paymentMethod === "Debit Card") {
            $(".card-details").slideDown();
        } else if (paymentMethod === "PayPal") {
            $(".paypal-details").slideDown();
        }
    });

    // Confirm order logic
    $("#confirm-order").click(function () {
        let paymentMethod = $("input[name='paymentMethod']:checked").val();
        if (!paymentMethod) {
            alert("Please select a payment method.");
            return;
        }

        let paymentStatus = "Completed";
        let refundStatus = "None";
        let transactionDate = new Date().toISOString().slice(0, 19).replace('T', ' ');

        let transactionData = {
            transaction_id: Math.floor(Math.random() * 10000),
            order_id: orderID,
            user_id: userID,
            payment_method: paymentMethod,
            payment_status: paymentStatus,
            refund_status: refundStatus,
            paid_amount: totalAmount,
            transaction_date: transactionDate
        };

        console.log("Transaction Data:", transactionData);
        alert("Payment Successful! Your order has been placed.");
    });
});
