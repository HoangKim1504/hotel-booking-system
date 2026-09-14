import {
    createContext,
    useContext,
    useState,
} from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [token, setToken] = useState(
        () => localStorage.getItem("authToken")
    );

    const [username, setUsername] = useState(
        () => localStorage.getItem("username")
    );

    const [roles, setRoles] = useState(() => {
        const savedRoles =
            localStorage.getItem("roles");

        return savedRoles
            ? JSON.parse(savedRoles)
            : [];
    });

    const [permissions, setPermissions] =
        useState(() => {
            const savedPermissions =
                localStorage.getItem("permissions");

            return savedPermissions
                ? JSON.parse(savedPermissions)
                : [];
        });

    /**
     * Save authenticated user information
     */
    const login = (
        newToken,
        userInfo
    ) => {
        const newRoles =
            userInfo.roleCodes ?? [];

        const newPermissions =
            userInfo.permissionCodes ?? [];

        localStorage.setItem(
            "authToken",
            newToken
        );

        localStorage.setItem(
            "username",
            userInfo.username
        );

        localStorage.setItem(
            "roles",
            JSON.stringify(newRoles)
        );

        localStorage.setItem(
            "permissions",
            JSON.stringify(newPermissions)
        );

        setToken(newToken);
        setUsername(userInfo.username);
        setRoles(newRoles);
        setPermissions(newPermissions);
    };

    /**
     * Clear authenticated user information
     */
    const logout = () => {
        localStorage.removeItem("authToken");
        localStorage.removeItem("username");
        localStorage.removeItem("roles");
        localStorage.removeItem("permissions");

        setToken(null);
        setUsername(null);
        setRoles([]);
        setPermissions([]);
    };

    const isAuthenticated =
        Boolean(token);

    const isAdmin =
        roles.includes("ADMIN");

    return (
        <AuthContext.Provider
            value={{
                token,
                username,
                roles,
                permissions,
                isAuthenticated,
                isAdmin,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}