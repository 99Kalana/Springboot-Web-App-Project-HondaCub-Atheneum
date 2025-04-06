
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

    // Handle Login Submission using jQuery
    $("#loginForm").on("submit", function (e) {
    e.preventDefault();
    let role = sessionStorage.getItem("userRole");
    let email = $("#email").val();
    let password = $("#password").val();

    // Create the login object
    let loginUser  = {
    email: email,
    password: password
};

    // Send AJAX request to the backend
    $.ajax({
    url: "http://localhost:8080/api/v1/auth/authenticate",
    method: "POST",
    contentType: "application/json",
    data: JSON.stringify(loginUser ),
    success: function (response) {
    // Store the token in localStorage
    localStorage.setItem("authToken", response.data.token);

    // Redirect based on role
    if (role === "ADMIN") {
    window.location.href = "admin-dashboard.html";
} else if (role === "SELLER") {
    window.location.href = "seller-dashboard.html";
} else {
    window.location.href = "customer-homepage.html";
}
},
    error: function (xhr) {
    // Handle error response
    alert("Login failed: " + xhr.responseJSON.message);
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
