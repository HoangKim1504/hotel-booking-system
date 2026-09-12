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

export async function searchAdminRooms({
    roomTypeName,
    roomStatus,
    roomNumber,
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

    if (roomTypeName) {
        params.append("roomTypeName", roomTypeName);
    }

    if (roomStatus) {
        params.append("roomStatus", roomStatus);
    }

    if (roomNumber) {
        params.append("roomNumber", roomNumber);
    }

    if (sortBy) {
        params.append("sortBy", sortBy);
    }

    if (order) {
        params.append("order", order);
    }

    const response = await fetch(
        `${API_BASE_URL}/api/admin/rooms/search?${params}`,
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

export async function getAdminRoomById({
    id,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/rooms/${id}`,
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


export async function updateAdminRoom({
    id,
    roomData,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/rooms/${id}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(roomData),
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}

export async function deleteAdminRoom({
    id,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/rooms/${id}`,
        {
            method: "DELETE",
            headers: {
                Authorization: `Bearer ${token}`,
            },
        }
    );

    const text = await response.text();

    let data = null;

    if (text) {
        try {
            data = JSON.parse(text);
        } catch {
            data = {
                message: text,
            };
        }
    }

    if (!response.ok) {
        throw data || {
            message: "Unable to delete room",
        };
    }

    return data;
}