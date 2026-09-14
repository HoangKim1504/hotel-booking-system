const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Create payment for booking
 */
export async function createPayment({
    bookingId,
    paymentMethod,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/bookings/${bookingId}/payments`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
                paymentMethod,
            }),
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}