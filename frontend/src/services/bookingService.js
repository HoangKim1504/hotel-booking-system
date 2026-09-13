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