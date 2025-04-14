$(document).ready(function () {
    const authToken = localStorage.getItem('authToken');

    // Function to load cart data from the backend
    function loadCart() {
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/cart`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200 && response.data) {
                    const cartItems = response.data;
                    let cartTableRows = "";
                    let totalAmount = 0;

                    cartItems.forEach(item => {
                        const totalPrice = item.sparePart.price * item.quantity;
                        totalAmount += totalPrice;

                        cartTableRows += `
                            <tr>
                                <td>${item.sparePart.partId}</td>
                                <td>${item.sparePart.partName}</td>
                                <td>$${item.sparePart.price.toFixed(2)}</td>
                                <td>
                                    <input type="number" class="quantity-input" min="1" value="${item.quantity}" data-cart-id="${item.cartId}">
                                </td>
                                <td>$${totalPrice.toFixed(2)}</td>
                                <td>
                                    <button class="btn btn-info btn-sm btn-update" data-cart-id="${item.cartId}">Update</button>
                                    <button class="btn btn-danger btn-sm btn-remove" data-cart-id="${item.cartId}">Remove</button>
                                </td>
                            </tr>
                        `;
                    });

                    $("#cart-items").html(cartTableRows);
                    $("#total-amount").text(`Total: $${totalAmount.toFixed(2)}`);
                } else {
                    console.error("Failed to load cart:", response);
                    $("#cart-items").html("<tr><td colspan='6' class='text-center'>Your cart is empty.</td></tr>");
                    $("#total-amount").text("Total: $0.00");
                }
            },
            error: function (xhr, status, error) {
                console.error("Error loading cart:", error);
                $("#cart-items").html("<tr><td colspan='6' class='text-center'>Failed to load cart.</td></tr>");
                $("#total-amount").text("Total: $0.00");
            }
        });
    }

    // Update item quantity in the backend
    $(document).on("click", ".btn-update", function () {
        const cartId = $(this).data("cart-id");
        const newQuantity = $(`input[data-cart-id=${cartId}]`).val();

        $.ajax({
            url: `http://localhost:8080/api/v1/customer/cart/${cartId}`,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify({
                quantity: parseInt(newQuantity)
            }),
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200) {
                    loadCart(); // Reload cart after update
                } else {
                    alert("Failed to update cart item.");
                }
            },
            error: function (xhr, status, error) {
                console.error("Error updating cart item:", error);
                alert("An error occurred while updating the cart item.");
            }
        });
    });

    // Remove item from cart in the backend
    $(document).on("click", ".btn-remove", function () {
        const cartId = $(this).data("cart-id");

        $.ajax({
            url: `http://localhost:8080/api/v1/customer/cart/${cartId}`,
            type: 'DELETE',
            headers: {
                'Authorization': `Bearer ${authToken}`
            },
            success: function (response) {
                if (response.status === 200) {
                    loadCart(); // Reload cart after removal
                    currentCartCount();
                } else {
                    alert("Failed to remove cart item.");
                }
            },
            error: function (xhr, status, error) {
                console.error("Error removing cart item:", error);
                alert("An error occurred while removing the cart item.");
            }
        });
    });

    //Current Cart Count
    function currentCartCount() {
        $.ajax({
            url: `http://localhost:8080/api/v1/customer/cart/count`,
            type: 'GET',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`
            },
            success: function (response) {
                if (response) {
                    const cartCount = response; // Directly use the response as the count
                    $('#cart-count').text(cartCount);
                    if (cartCount > 0) {
                        $('#cart-count').show();
                    } else {
                        $('#cart-count').hide();
                    }
                } else {
                    console.error("Failed to get cart count:", response);
                }
            },
            error: function (xhr, status, error) {
                console.error("Error getting cart count:", error);
            }
        });
    }

    // Proceed to Checkout button click handler
    $(".btn-success").click(function () {
        if ($("#cart-items tr").length === 0 || ($("#cart-items tr").text().trim() === 'Your cart is empty.')) {
            alert("Your cart is empty.");
        } else {
            window.location.href = "customer-checkout-payment.html";
        }
    });

    // Initial load
    currentCartCount();
    loadCart();

});