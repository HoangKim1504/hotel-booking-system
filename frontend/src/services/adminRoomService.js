const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function getAdminRooms({
    page,
    size,
    sortBy,
    order,
    token,
}) {
    const params = new URLSearchParams({
        page,
        size
    });

    if (sortBy) {
        params.append("sortBy", sortBy);
    }

    if (order) {
        params.append("order", order);
    }

    const response = await fetch(
        `${API_BASE_URL}/api/admin/rooms?${params}`,
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