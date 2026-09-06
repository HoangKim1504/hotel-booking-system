export async function getRoomTypes({
    page,
    size,
    sortBy,
    order,
}) {
    const params = new URLSearchParams({
        page,
        size,
    });

    if (sortBy) {
        params.append("sortBy", sortBy);
    }

    if (order) {
        params.append("order", order);
    }

    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

    const response = await fetch(
        `${API_BASE_URL}/api/room-types?${params}`
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}

export async function searchRoomTypes({
    checkInDate,
    checkOutDate,
    maximumPeople,
    page,
    size,
    sortBy,
    order,
}) {
    const params = new URLSearchParams({
        checkInDate,
        checkOutDate,
        maximumPeople,
        page,
        size,
    });

    if (sortBy) {
        params.append("sortBy", sortBy);
    }

    if (order) {
        params.append("order", order);
    }

    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

    const response = await fetch(
        `${API_BASE_URL}/api/room-types/search?${params}`
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}

export async function getRoomTypeById(id) {
    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

    const response = await fetch(
        `${API_BASE_URL}/api/room-types/${id}`
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}