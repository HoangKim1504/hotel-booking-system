const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Get all users
 */
export async function getAdminUsers({ token }) {
    const response = await fetch(
        `${API_BASE_URL}/api/users`,
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
 * Get user detail by id
 */
export async function getAdminUserById({
    id,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/users/${id}`,
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
 * Create user
 */
export async function createAdminUser({
    userData,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/users`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(userData),
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}


/**
 * Update user
 */
export async function updateAdminUser({
    id,
    userData,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/users/${id}`,
        {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(userData),
        }
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}


/**
 * Delete user
 */
export async function deleteAdminUser({
    id,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/users/${id}`,
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
            message: "Unable to delete user",
        };
    }
}