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

    const response = await fetch(
        `http://localhost:8080/api/room-types?${params}`
    );

    const data = await response.json();

    if (!response.ok) {
        throw data;
    }

    return data;
}