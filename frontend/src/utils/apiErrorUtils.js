export function getErrorMessages(error) {
    if (error?.errors) {
        return Object.values(error.errors);
    }

    if (error?.message) {
        return [error.message];
    }

    if (error?.error) {
        return [error.error];
    }

    return ["Unable to connect to server"];
}