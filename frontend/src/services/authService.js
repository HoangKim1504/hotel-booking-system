const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function login(username, password) {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            username,
            password,
        }),
    });

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}