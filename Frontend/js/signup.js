$(document).ready(function () {
    const developerAdminPassword = "123";

    // Show Admin Verification Modal
    $("#adminSignupBtn").click(function () {
        $("#adminModal").css("display", "flex").hide().fadeIn();
    });

    // Close Modal Button and Cancel Button
    $("#closeModalBtn, #cancelModal").click(function () {
        $("#adminModal").fadeOut();
    });

    // Close QR Code Modal when clicking on the close button
    $("#closeQRCodeModal").click(function () {
        //$("#qrCodeModal").fadeOut();
        $("#qrCodeModal").fadeOut(function () {
            window.location.href = "login.html";
        });
    });

    // Verify Admin Password
    $("#verifyAdmin").click(function () {
        let enteredPassword = $("#adminPass").val();
        if (enteredPassword === developerAdminPassword) {
            $("#adminModal").fadeOut();

            // Hide normal signup button
            $("#normalSignupBtn").addClass("hidden");

            // Change heading to Admin Sign Up
            $(".signup-box h3").text("Admin Sign Up");

            // Set role to admin and disable changing it
            $("#role").html('<option value="admin">Admin</option>').prop("disabled", true);

            // Show only Admin Signup Button
            $("#adminSignupFinalBtn").removeClass("hidden");

            // Hide the "Sign Up as Admin" button to prevent reopening the modal
            $("#adminSignupBtn").addClass("hidden");

            // Enable the admin sign-up button
            $("#adminSignupFinalBtn").prop('disabled', false);
        } else {
            $("#errorMessage").removeClass("hidden");
        }
    });

    // Handle sign-up form submission (for both normal and admin)
    $("#signupForm").submit(function (e) {
        e.preventDefault(); // Prevent the default form submission

        let role = $("#role").val();
        let name = $("#fullName").val();
        let email = $("#email").val();
        let phone = $("#phone").val();
        let password = $("#password").val();
        let confirmPassword = $("#confirmPassword").val();

        // Validate password confirmation
        if (password !== confirmPassword) {
            alert("Passwords do not match!");
            return;
        }

        // Generate QR Code Data
        let userData = JSON.stringify({ email: email, password: password });
        $("#qrCode").html(""); // Clear old QR codes
        new QRCode($("#qrCode")[0], userData); // Generate the QR code

        // Wait for the QR code to be generated
        setTimeout(() => {
            // Get the QR code as a data URL
            let canvas = $("#qrCode canvas")[0];
            let qrCodeDataUrl = canvas.toDataURL("image/png");

            // Create the user object
            const user = {
                fullName: name,
                email: email,
                phone: phone,
                password: password,
                role: role.toUpperCase(), // Ensure the role is uppercase
                qrCode: qrCodeDataUrl // Include the QR code data URL
            };

            // Send AJAX request to the backend
            $.ajax({
                url: "http://localhost:8080/api/v1/user/register",
                method: "POST",
                contentType: "application/json",
                data: JSON.stringify(user),
                success: function (response) {
                    alert("User registered successfully!");
                    if (response && response.data) {
                        let token = response.data.token;
                        console.log("Token:", token);
                        localStorage.setItem("authToken", token);
                        sessionStorage.setItem("authToken", token);
                    }
                    // Show the QR Code Modal
                    $("#qrCodeModal").css("display", "flex").hide().fadeIn().attr("aria-hidden", "false")
                },
                error: function (xhr) {
                    let errorMessage = "An error occurred";
                    if (xhr.responseJSON && xhr.responseJSON.message) {
                        errorMessage = xhr.responseJSON.message;
                    } else if (xhr.statusText) {
                        errorMessage = xhr.statusText;
                    }
                    alert("Registration failed: " + errorMessage);
                }
            });
        }, 100); // Wait for 100ms to ensure QR code is generated
    });

    // Admin final sign-up submission for the admin role
    $("#adminSignupFinalBtn").click(function (e) {
        e.preventDefault(); // Prevent default button behavior
        // Trigger the same form submission process for admin sign-up
        $("#signupForm").submit();
    });

    // QR Code Download Function
    function downloadQRCode() {
        let canvas = $("#qrCode canvas")[0];
        let img = canvas.toDataURL("image/png");

        let a = document.createElement("a"); // Create a temporary link
        a.href = img;
        a.download = "QRCode.png";
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }

    // Attach download function to the button
    $("#downloadQR").click(downloadQRCode);


    // Toggle password visibility
    $("#showPassword").on("change", function() {
        let passwordField = $("#password");
        let confirmPasswordField = $("#confirmPassword");

        if ($(this).prop("checked")) {
            // Show password
            passwordField.attr("type", "text");
            confirmPasswordField.attr("type", "text");
        } else {
            // Hide password
            passwordField.attr("type", "password");
            confirmPasswordField.attr("type", "password");
        }
    });


});