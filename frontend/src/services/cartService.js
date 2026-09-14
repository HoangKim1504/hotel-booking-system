const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Get current user's cart
 */
export async function getCart(token) {
    const response = await fetch(
        `${API_BASE_URL}/api/cart`,
        {
            method: "GET",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}


/**
 * Add room type to cart
 */
export async function addCartItem({
    roomTypeId,
    quantity,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/cart/items`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
                items: [
                    {
                        roomTypeId,
                        quantity,
                    },
                ],
            }),
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}


/**
 * Update cart item quantity
 */
export async function updateCartItem({
    itemId,
    quantity,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/cart/items/${itemId}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
                quantity,
            }),
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}


/**
 * Remove cart item
 */
export async function deleteCartItem({
    itemId,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/cart/items/${itemId}`,
        {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}

/**
 * Clear all items in cart
 */
export async function clearCartItems(token) {
    const response = await fetch(
        `${API_BASE_URL}/api/cart/items`,
        {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}