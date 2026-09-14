const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Get booking list
 */
export async function getAdminBookings({
    page,
    size,
    status,
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
        `${API_BASE_URL}/api/admin/bookings?${params}`,
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
 * Get booking detail
 */
export async function getAdminBookingById({
    bookingId,
    userId,
    token,
}) {
    const params = new URLSearchParams({
        userId,
    });

    const response = await fetch(
        `${API_BASE_URL}/api/admin/bookings/${bookingId}?${params}`,
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
 * Update booking status
 */
export async function updateAdminBookingStatus({
    bookingId,
    userId,
    bookingStatus,
    token,
}) {
    const params = new URLSearchParams({
        userId,
    });

    const response = await fetch(
        `${API_BASE_URL}/api/admin/bookings/${bookingId}/status?${params}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
                bookingStatus,
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
 * Cancel booking
 */
export async function cancelAdminBooking({
    bookingId,
    userId,
    token,
}) {
    const params = new URLSearchParams({
        userId,
    });

    const response = await fetch(
        `${API_BASE_URL}/api/admin/bookings/${bookingId}/cancel?${params}`,
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


/**
 * Delete booking
 */
export async function deleteAdminBooking({
    bookingId,
    userId,
    token,
}) {
    const params = new URLSearchParams({
        userId,
    });

    const response = await fetch(
        `${API_BASE_URL}/api/admin/bookings/${bookingId}?${params}`,
        {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    if (!response.ok) {
        const text = await response.text();

        if (text) {
            try {
                throw JSON.parse(text);
            } catch (error) {
                if (
                    typeof error === "object" &&
                    error !== null
                ) {
                    throw error;
                }
            }
        }

        throw {
            message: "Unable to delete booking",
        };
    }
}