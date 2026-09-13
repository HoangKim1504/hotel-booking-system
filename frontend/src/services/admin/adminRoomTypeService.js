const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Get all room types
 */
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


/**
 * Search room types by room type name
 */
export async function searchAdminRoomTypes({
    roomTypeName,
    page,
    size,
    sortBy,
    order,
    token,
}) {
    const params = new URLSearchParams({
        page,
        size,
        sortBy,
        order,
    });

    if (roomTypeName) {
        params.append("roomTypeName", roomTypeName);
    }

    const response = await fetch(
        `${API_BASE_URL}/api/admin/room-types/search?${params}`,
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
 * Get room type detail by id
 */
export async function getAdminRoomTypeById({
    id,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/room-types/${id}`,
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
 * Create room type
 */
export async function createAdminRoomType({
    roomTypeData,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/room-types`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(roomTypeData),
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}


/**
 * Update room type
 */
export async function updateAdminRoomType({
    id,
    roomTypeData,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/room-types/${id}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(roomTypeData),
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}


/**
 * Delete room type
 */
export async function deleteAdminRoomType({
    id,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/room-types/${id}`,
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
            message: "Unable to delete room type",
        };
    }
}