$(document).ready(function () {

    const shippingAddressInput = $("#shippingAddress");
    const contactNumberInput = $("#contactNumber");

    const cardNumberInput = $("#cardNumber");
    const cardHolderNameInput = $("#cardHolderName");
    const cardExpiryInput = $("#cardExpiry");
    const cardCVVInput = $("#cardCVV");
    const paypalEmailInput = $("#paypalEmail");
    const cardDetailsForm = $("#cardDetailsForm");

    cardNumberInput.on('input', function() {
        let value = $(this).val().replace(/\D/g, ''); // Remove non-digit characters
        let formattedValue = '';
        for (let i = 0; i < value.length; i++) {
            if (i > 0 && i % 4 === 0 && i < 16) {
                formattedValue += '-';
            }
            formattedValue += value[i];
        }
        $(this).val(formattedValue);
        validateCardNumber(); // Re-validate on input
    });


    function validateShippingAddress() {
        const shippingAddress = shippingAddressInput.val().trim();
        if (shippingAddress === "") {
            displayError(shippingAddressInput, "Please enter a shipping address.");
            return false;
        } else if (shippingAddress.length > 255) {
            displayError(shippingAddressInput, "Shipping address cannot exceed 255 characters.");
            return false;
        }
        clearError(shippingAddressInput);
        return true;
    }

    function validateContactNumber() {
        const contactNumber = contactNumberInput.val().trim();
        const contactNumberRegex = /^\d{10}$/;
        if (!contactNumberRegex.test(contactNumber)) {
            displayError(contactNumberInput, "Please enter a 10-digit contact number.");
            return false;
        }
        clearError(contactNumberInput);
        return true;
    }

    // Function to display validation errors
    function displayError(inputElement, message) {
        inputElement.addClass("is-invalid");
        inputElement.next(".invalid-feedback").text(message).show();
    }

    // Function to clear validation errors
    function clearError(inputElement) {
        inputElement.removeClass("is-invalid");
        inputElement.next(".invalid-feedback").hide();
    }

    function validateCardNumber() {
        const cardNumber = cardNumberInput.val().trim();
        const cardNumberRegex = /^(?:(?:\d{4}-){3}\d{4})?$/;
        if (!cardNumberRegex.test(cardNumber)) {
            displayError(cardNumberInput, "Please enter a valid card number (e.g., 1234-1234-1234-1234).");
            return false;
        }
        clearError(cardNumberInput);
        return true;
    }

    function validateCardHolderName() {
        const cardHolderName = cardHolderNameInput.val().trim();
        const cardHolderNameRegex = /^[A-Za-z\s]+$/;
        if (!cardHolderNameRegex.test(cardHolderName)) {
            displayError(cardHolderNameInput, "Please enter a valid card holder name (letters only).");
            return false;
        }
        clearError(cardHolderNameInput);
        return true;
    }

    function validateCardExpiry() {
        const cardExpiry = cardExpiryInput.val().trim();
        const cardExpiryRegex = /^(0[1-9]|1[0-2])\/\d{2}$/;
        if (!cardExpiryRegex.test(cardExpiry)) {
            displayError(cardExpiryInput, "Please enter a valid expiration date (MM/YY).");
            return false;
        }
        clearError(cardExpiryInput);
        return true;
    }

    function validateCardCVV() {
        const cardCVV = cardCVVInput.val().trim();
        const cardCVVRegex = /^\d{3}$/;
        if (!cardCVVRegex.test(cardCVV)) {
            displayError(cardCVVInput, "Please enter a valid CVV (3 digits).");
            return false;
        }
        clearError(cardCVVInput);
        return true;
    }

    function validatePaypalEmail() {
        const paypalEmail = paypalEmailInput.val().trim();
        const paypalEmailRegex = /^[a-zA-Z0-9._-]+@paypal\.com$/;
        if (!paypalEmailRegex.test(paypalEmail)) {
            displayError(paypalEmailInput, "Please enter a valid PayPal email (e.g., user@paypal.com).");
            return false;
        }
        clearError(paypalEmailInput);
        return true;
    }

    shippingAddressInput.on('input', validateShippingAddress);
    contactNumberInput.on('input', validateContactNumber);

    cardNumberInput.on('input', validateCardNumber);
    cardHolderNameInput.on('input', validateCardHolderName);
    cardExpiryInput.on('input', validateCardExpiry);
    cardCVVInput.on('input', validateCardCVV);
    paypalEmailInput.on('input', validatePaypalEmail);

    //=================================================

    const authToken = localStorage.getItem('authToken');
    let cartItems = [];
    let totalAmount = 0;
    let availablePoints = 0; // Initialize available points

    // Function to load cart data and display order summary
    function loadCartForCheckout() {
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/cart`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    cartItems = response.data;
                    let orderSummaryRows = "";

                    cartItems.forEach(item => {
                        const totalPrice = item.sparePart.price * item.quantity;
                        totalAmount += totalPrice;

                        orderSummaryRows += `
                                <tr>
                                    <td>${item.sparePart.partName}</td>
                                    <td>$${item.sparePart.price.toFixed(2)}</td>
                                    <td>${item.quantity}</td>
                                    <td>$${totalPrice.toFixed(2)}</td>
                                </tr>
                            `;
                    });

                    $("#order-summary-items").html(orderSummaryRows);
                    $("#total-amount").text(totalAmount.toFixed(2));
                    $("#final-amount").text(totalAmount.toFixed(2)); // Initialize final amount
                    loadAvailablePoints(); // Load available points after cart data

                    // Show checkout indicator if cart has items
                    if (cartItems.length > 0) {
                        $("#checkout-indicator").show();
                    } else {
                        $("#checkout-indicator").hide();
                    }
                } else {
                    console.error("Failed to load cart for checkout:", response);
                    $("#order-summary-items").html("<tr><td colspan='4' class='text-center'>Your cart is empty.</td></tr>");
                    $("#total-amount").text("0.00");
                    $("#final-amount").text("0.00");
                    $("#checkout-indicator").hide();
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading cart for checkout:", error);
                $("#order-summary-items").html("<tr><td colspan='4' class='text-center'>Failed to load cart.</td></tr>");
                $("#total-amount").text("0.00");
                $("#final-amount").text("0.00");
                $("#checkout-indicator").hide();
            }
        });
    }

    // Function to load available points
    function loadAvailablePoints() {
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/rewards`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    availablePoints = response.data.points;
                    $("#available-points").text(availablePoints);
                } else {
                    console.error("Failed to load available points:", response);
                    $("#available-points").text("0");
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading available points:", error);
                $("#available-points").text("0");
            }
        });
    }

    // Payment method selection logic
    $("input[name='paymentMethod']").change(function () {
        let paymentMethod = $(this).val();
        $(".card-details, .paypal-details").hide();

        if (paymentMethod === "CREDIT_CARD" || paymentMethod === "DEBIT_CARD") {
            $(".card-details").slideDown();
        } else if (paymentMethod === "PAYPAL") {
            $(".paypal-details").slideDown();
        }
    });

    // Function to update final amount based on redeemed points
    function updateFinalAmount() {
        let redeemedPoints = parseInt($("#redeemPoints").val()) || 0;

        if (redeemedPoints < 0) {
            $("#redeemPoints").val(0);
            redeemedPoints = 0;
        }

        if (redeemedPoints > availablePoints) {
            alert("You cannot redeem more points than you have.");
            $("#redeemPoints").val(availablePoints);
            redeemedPoints = availablePoints;
        }

        let discountAmount = 0;
        if (redeemedPoints >= 50) {
            const discountPercentage = (redeemedPoints / 50) * 0.02;
            discountAmount = totalAmount * discountPercentage;
        }

        const finalAmount = Math.max(0, totalAmount - discountAmount);
        $("#final-amount").text(finalAmount.toFixed(2));
        $("#discount-amount").text(discountAmount.toFixed(2));

        // Show/hide the discount row
        if (discountAmount > 0) {
            $(".discount-row").slideDown();
        } else {
            $(".discount-row").slideUp();
        }
    }

    // Redeem points input change listener
    $("#redeemPoints").on('input', updateFinalAmount);

    let isProcessing = false;

    // Confirm order logic
    $("#confirm-order").click(function () {
        let paymentMethod = $("input[name='paymentMethod']:checked").val();
        let shippingAddress = $("#shippingAddress").val();
        let contactNumber = $("#contactNumber").val();
        let redeemedPoints = parseInt($("#redeemPoints").val()) || 0;
        let finalAmount = parseFloat($("#final-amount").text());

        let isValid = true;

        if (!paymentMethod) {
            alert("Please select a payment method.");
            return;
        }
        // Validate Shipping Address
        if (!validateShippingAddress()) {
            isValid = false;
        }

        // Validate Contact Number
        if (!validateContactNumber()) {
            isValid = false;
        }
        if (redeemedPoints > availablePoints) {
            alert("You cannot redeem more points than you have.");
            return;
        }
        if (redeemedPoints < 0) {
            alert("Please enter a valid number of points to redeem.");
            return;
        }

        //==================================================
        // Validate payment details based on the selected method
        if (paymentMethod === "CREDIT_CARD" || paymentMethod === "DEBIT_CARD") {
            isValid = validateCardNumber() && validateCardHolderName() && validateCardExpiry() && validateCardCVV();
        } else if (paymentMethod === "PAYPAL") {
            isValid = validatePaypalEmail();
        }

        if (!isValid) {
            return; // Stop the function if payment details are invalid
        }
        //==================================================

        let paymentStatus = "COMPLETED";
        let refundStatus = "NONE";
        let transactionDate = new Date().toISOString().slice(0, 19).replace('T', ' ');

        let transactionData = {
            paymentMethod: paymentMethod,
            paymentStatus: paymentStatus,
            refundStatus: refundStatus,
            paidAmount: finalAmount,
            transactionDate: transactionDate,
            shippingAddress: shippingAddress,
            contactNumber: contactNumber,
            redeemedPoints: redeemedPoints, // Include redeemed points
            authorizationHeader: 'Bearer ' + authToken // Correctly formatted header
        };

        console.log("Transaction Data:", transactionData);

        $(this).prop('disabled', true); // Disable the button

        // Send transaction data to backend
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/transactions`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(transactionData),
            headers: {
                'Authorization': 'Bearer ' + authToken // Correctly formatted header
            },
            success: function (response, textStatus, jqXHR) {
                $(this).prop('disabled', false); // Re-enable the button

                if (jqXHR.status === 201) { // Check HTTP status code
                    alert("Payment Successful! Your order has been placed.");
                    alert("A bill with your order details has been sent to your email address.");
                    window.location.href = "customer-order-history.html";
                } else {
                    alert("Payment Failed. Please try again.");
                }
            },
            error: function (xhr, status, error) {
                $(this).prop('disabled', false); // Re-enable the button
                console.error("Error processing transaction:", error);
                alert("An error occurred during payment processing.");
            }
        });
    });

    // Cancel order logic
    $("#cancel-order").click(function () {
        window.location.href = "customer-cart.html";
    });

    // Load cart data on page load
    loadCartForCheckout();
});