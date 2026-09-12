const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function getAdminRoomTypes({
    page,
    size,
    sortBy,
    order,
    token,
}) {
    const params = new URLSearchParams({
        page,
        size,
        order,
    });

    if (sortBy) {
        params.append("sortBy", sortBy);
    }

    const response = await fetch(
        `${API_BASE_URL}/api/admin/room-types?${params}`,
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