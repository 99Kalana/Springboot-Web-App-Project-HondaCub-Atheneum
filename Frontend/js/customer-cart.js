$(document).ready(function () {
    // Sample cart data simulating the "cart" table structure
    const cartData = [
        { part_id: 101, part_name: 'Engine Oil', unit_price: 15.99, quantity: 2 },
        { part_id: 102, part_name: 'Brake Pads', unit_price: 25.49, quantity: 1 },
        { part_id: 103, part_name: 'Spark Plug', unit_price: 5.99, quantity: 4 }
    ];

    // Load cart data into the table
    function loadCart() {
        let cartItems = "";
        let totalAmount = 0;

        cartData.forEach(item => {
            const totalPrice = item.unit_price * item.quantity;
            totalAmount += totalPrice;

            cartItems += `
                <tr>
                    <td>${item.part_id}</td>
                    <td>${item.part_name}</td>
                    <td>$${item.unit_price.toFixed(2)}</td>
                    <td>
                        <input type="number" class="quantity-input" min="1" value="${item.quantity}" data-id="${item.part_id}">
                    </td>
                    <td>$${totalPrice.toFixed(2)}</td>
                    <td>
                        <button class="btn btn-info btn-sm btn-update" data-id="${item.part_id}">Update</button>
                        <button class="btn btn-danger btn-sm btn-remove" data-id="${item.part_id}">Remove</button>
                    </td>
                </tr>
            `;
        });

        $("#cart-items").html(cartItems);
        $("#total-amount").text(`Total: $${totalAmount.toFixed(2)}`);
    }

    // Update item quantity
    $(document).on("click", ".btn-update", function () {
        const partId = $(this).data("id");
        const newQuantity = $(`input[data-id=${partId}]`).val();

        cartData.forEach(item => {
            if (item.part_id === partId) {
                item.quantity = parseInt(newQuantity);
            }
        });

        loadCart();
    });

    // Remove item from cart
    $(document).on("click", ".btn-remove", function () {
        const partId = $(this).data("id");
        const itemIndex = cartData.findIndex(item => item.part_id === partId);

        if (itemIndex !== -1) {
            cartData.splice(itemIndex, 1);
        }

        loadCart();
    });

    // Initial load
    loadCart();
});
