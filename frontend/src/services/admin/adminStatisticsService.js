const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

/**
 * Get revenue statistics by year
 */
export async function getRevenueStatistics({
    year,
    token,
}) {
    const response = await fetch(
        `${API_BASE_URL}/api/admin/statistics/revenue?year=${year}`,
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