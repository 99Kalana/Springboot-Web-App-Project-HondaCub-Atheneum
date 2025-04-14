// Toggle Password Visibility using jQuery
$("#togglePassword").on("click", function () {
    let passwordField = $("#password");
    if (passwordField.attr("type") === "password") {
        passwordField.attr("type", "text");
        $(this).removeClass("bi-eye-slash").addClass("bi-eye");
    } else {
        passwordField.attr("type", "password");
        $(this).removeClass("bi-eye").addClass("bi-eye-slash");
    }
});

// Handle Role Selection using jQuery
function selectRole(role) {
    sessionStorage.setItem("userRole", role);
    $("#roleSelection").fadeOut();
    $("#loginContainer").removeClass("blur");
}

// Function to validate email format
function validateEmail(email) {
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return emailRegex.test(email);
}

// Function to validate password
/*function validatePassword(password) {
    if (!password.trim()) {
        return "Password cannot be blank.";
    }
    if (password.length < 6) {
        return "Password must be at least 6 characters long.";
    }
    return ""; // Returns an empty string if the password is valid
}*/

$("#loginForm").on("submit", function (e) {
    e.preventDefault();

    let role = sessionStorage.getItem("userRole");
    let email = $("#email").val();
    let password = $("#password").val();

    let isValid = true;
    let emailError = "";
    //let passwordError = validatePassword(password); // Call the password validation function

    // Validate email
    if (!email.trim()) {
        emailError = "Email cannot be blank.";
        isValid = false;
    } else if (!validateEmail(email)) {
        emailError = "Please enter a valid email address.";
        isValid = false;
    }

    // Update isValid based on password validation
    /*if (passwordError) {
        isValid = false;
    }*/

    // Display errors if any
    $("#email").removeClass("is-invalid");
    $("#password").removeClass("is-invalid");
    $("#email-error").text(emailError);
    //$("#password-error").text(passwordError);

    if (!isValid) {
        if (emailError) {
            $("#email").addClass("is-invalid");
        }
        /*if (passwordError) {
            $("#password").addClass("is-invalid");
        }*/
        return; // Stop form submission if validation fails
    }

    // Create the login object
    let loginUser = {
        email: email,
        password: password
    };

    // Send AJAX request to the backend
    $.ajax({
        url: "http://localhost:8080/api/v1/auth/authenticate",
        method: "POST",
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("authToken"),
            "Content-Type": "application/json"
        },
        data: JSON.stringify(loginUser),
        success: function (response) {
            if (response.data && response.data.token) {
                localStorage.setItem("authToken", response.data.token);

                let userRole = response.data.role;
                let userStatus = response.data.status;

                if (userStatus !== "ACTIVE") {
                    alert("Your account is inactive. Please contact support.");
                    return;
                }

                if (userRole === role) {
                    if (userRole === "ADMIN") {
                        window.location.href = "admin-dashboard.html";
                    } else if (userRole === "SELLER") {
                        window.location.href = "seller-dashboard.html";
                    } else if (userRole === "CUSTOMER") {
                        window.location.href = "customer-homepage.html";
                    } else {
                        alert("Role selection error!");
                    }
                } else {
                    alert("Role mismatch! Please make sure you selected the correct role.");
                }
            } else {
                alert("Login failed. Please check your credentials.");
            }
        },
        error: function (xhr, status, error) {
            alert("An error occurred during login: Check you Password or Email!" + error);
        }
    });
});

// QR Function
function startQRScanner() {
    $("#qrScannerModal").modal("show");
}

document.getElementById("qrInputFile").addEventListener("change", function (event) {
    const file = event.target.files[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = function () {
        const img = new Image();
        img.src = reader.result;
        img.onload = function () {
            const canvas = document.getElementById("qrCanvas");
            const ctx = canvas.getContext("2d");
            canvas.width = img.width;
            canvas.height = img.height;
            ctx.drawImage(img, 0, 0, img.width, img.height);

            const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
            const code = jsQR(imageData.data, canvas.width, canvas.height);

            if (code) {
                try {
                    let credentials = JSON.parse(code.data);
                    $("#email").val(credentials.email);
                    $("#password").val(credentials.password);
                    $("#qrScannerModal").modal("hide");
                } catch (error) {
                    alert("Invalid QR code format!");
                }
            } else {
                alert("No QR code detected!");
            }
        };
    };
    reader.readAsDataURL(file);
});