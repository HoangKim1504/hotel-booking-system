const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Create booking for current logged-in user
 */
export async function createBooking({
    items,
    checkInDate,
    checkOutDate,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/bookings`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
                items,
                checkInDate,
                checkOutDate,
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
 * Get booking detail of current user
 */
export async function getBookingById({
    bookingId,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/bookings/${bookingId}`,
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
 * Get booking list of current logged-in user
 */
export async function getBookings({
    page = 1,
    size = 10,
    status = "",
    token,
}) {
    const params = new URLSearchParams({
        page,
        size,
    });

    if (status) {
        params.append("status", status);
    }

    const response = await fetch(
        `${API_BASE_URL}/api/bookings?${params.toString()}`,
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
 * Cancel booking of current logged-in user
 */
export async function cancelBooking({
    bookingId,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/bookings/${bookingId}/cancel`,
        {
            method: "PUT",
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