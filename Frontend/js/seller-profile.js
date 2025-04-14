$(document).ready(function() {

    const sellerProfileForm = $("#sellerProfileForm");
    const sellerNameInput = $("#sellerName");
    const sellerPhoneInput = $("#sellerPhone");
    const currentPasswordInput = $("#currentPassword");
    const newPasswordInput = $("#newPassword");
    const confirmPasswordInput = $("#confirmPassword");

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

    sellerNameInput.on('focus', function() { clearError(sellerNameInput); });
    sellerPhoneInput.on('focus', function() { clearError(sellerPhoneInput); });
    currentPasswordInput.on('focus', function() { clearError(currentPasswordInput); });
    newPasswordInput.on('focus', function() { clearError(newPasswordInput); });
    confirmPasswordInput.on('focus', function() { clearError(confirmPasswordInput); });

    // Sidebar toggle
    $('#toggleBtn').click(function() {
        $('#sidebar').toggleClass('collapsed');
    });

    loadSellerProfile();

    function loadSellerProfile() {
        const token = localStorage.getItem('authToken');

        $.ajax({
            url: 'http://localhost:8080/api/v1/seller/profile',
            type: 'GET',
            headers: {
                'Authorization': 'Bearer ' + token
            },
            success: function (response) {
                if (response.status === 200) {
                    const userData = response.data;
                    $('#sellerName').val(userData.fullName);
                    $('#sellerEmail').val(userData.email);
                    $('#sellerPhone').val(userData.phone);
                } else {
                    alert('Failed to load profile.');
                }
            },
            error: function (error) {
                console.error('Error loading profile:', error);
                alert('An error occurred while loading profile.');
            }
        });
    }

    $('#saveProfileBtn').click(function () {

        let isValid = true;

        // Validate Full Name
        if (sellerNameInput.val().trim() === "") {
            displayError(sellerNameInput, "Full name is required.");
            isValid = false;
        } else if (sellerNameInput.val().trim().length < 3 || sellerNameInput.val().trim().length > 100) {
            displayError(sellerNameInput, "Full name must be between 3 and 100 characters.");
            isValid = false;
        } else {
            clearError(sellerNameInput);
        }

        // Validate Phone Number
        const phoneRegex = /^\d{10}$/;
        if (sellerPhoneInput.val().trim() === "") {
            displayError(sellerPhoneInput, "Phone number is required.");
            isValid = false;
        } else if (!phoneRegex.test(sellerPhoneInput.val().trim())) {
            displayError(sellerPhoneInput, "Phone number must be a 10-digit number.");
            isValid = false;
        } else {
            clearError(sellerPhoneInput);
        }

        // Validate New Password (only if it's being changed)
        if (newPasswordInput.val().trim() !== "") {
            if (newPasswordInput.val().trim().length < 6) {
                displayError(newPasswordInput, "New password must be at least 6 characters long.");
                isValid = false;
            } else {
                clearError(newPasswordInput);
            }

            // Validate Confirm New Password (only if new password is entered)
            if (confirmPasswordInput.val().trim() === "") {
                displayError(confirmPasswordInput, "Please confirm the new password.");
                isValid = false;
            } else if (confirmPasswordInput.val() !== newPasswordInput.val()) {
                displayError(confirmPasswordInput, "Confirm new password does not match the new password.");
                isValid = false;
            } else {
                clearError(confirmPasswordInput);
            }

            // If a new password is being set, current password is also required
            if (currentPasswordInput.val().trim() === "") {
                displayError(currentPasswordInput, "Current password is required to change the password.");
                isValid = false;
            } else {
                clearError(currentPasswordInput);
            }
        } else {
            // If new password is not being changed, clear error for confirm password
            clearError(confirmPasswordInput);
            clearError(currentPasswordInput);
        }

        if (!isValid) {
            return; // Stop the function if validation fails
        }


        const token = localStorage.getItem('authToken');
        const newPassword = $('#newPassword').val();
        const confirmPassword = $('#confirmPassword').val();
        const currentPassword = $('#currentPassword').val();

        if (newPassword && newPassword !== confirmPassword) {
            alert("New passwords do not match.");
            return;
        }

        const userData = {
            fullName: $('#sellerName').val(),
            email: $('#sellerEmail').val(),
            phone: $('#sellerPhone').val(),
            role: 'SELLER',
            currentPassword: currentPassword,
            password: newPassword
        };

        $.ajax({
            url: 'http://localhost:8080/api/v1/seller/profile/update',
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(userData),
            headers: {
                'Authorization': 'Bearer ' + token
            },
            success: function (response) {
                if (response.status === 200) {
                    alert('Profile updated successfully.');
                    generateAndShowQRCode(userData.email, userData.password); // Call the QR code generation function
                    $('#currentPassword').val('');
                    $('#newPassword').val('');
                    $('#confirmPassword').val('');
                    $('#sellerName').val(userData.fullName);
                    $('#sellerEmail').val(userData.email);
                    $('#sellerPhone').val(userData.phone);
                } else if (response.status === 409) {
                    alert('Email already exists.');
                } else if (response.status === 401){
                    alert('Incorrect current password.');
                } else {
                    alert('Failed to update profile.');
                }
            },
            error: function (error) {
                console.error('Error updating profile:', error);
                alert('An error occurred while updating profile.');
            }
        });
    });

    function generateAndShowQRCode(email, password) {
        const qrCodeData = JSON.stringify({ email: email, password: password });

        $('#qrCode').empty();
        new QRCode(document.getElementById("qrCode"), {
            text: qrCodeData,
            width: 128,
            height: 128,
            colorDark: "#000000",
            colorLight: "#ffffff",
            correctLevel: QRCode.CorrectLevel.H
        });

        $('#qrCodeModal').modal({
            backdrop: 'static',
            keyboard: false
        });
        $('#qrCodeModal').modal('show');
    }

    $('#closeQRCodeModal').click(function () {
        $('#qrCodeModal').modal('hide');
        window.location.href = 'login.html';
    });

    $('#downloadQR').click(function () {
        const canvas = $('#qrCode canvas')[0];
        const link = document.createElement('a');
        link.href = canvas.toDataURL("image/png");
        link.download = 'qrcode.png';
        link.click();
    });

    $("#showPassword").on("change", function() {
        let currentPasswordField = $("#currentPassword");
        let passwordField = $("#newPassword");
        let confirmPasswordField = $("#confirmPassword");

        if ($(this).prop("checked")) {
            currentPasswordField.attr("type", "text");
            passwordField.attr("type", "text");
            confirmPasswordField.attr("type", "text");
        } else {
            currentPasswordField.attr("type", "password");
            passwordField.attr("type", "password");
            confirmPasswordField.attr("type", "password");
        }
    });

});