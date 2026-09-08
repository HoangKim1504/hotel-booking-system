import {
    createContext,
    useContext,
    useState,
} from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [token, setToken] = useState(() => localStorage.getItem("authToken"));
    const [username, setUsername] = useState(() => localStorage.getItem("username"));

    const login = (newToken, newUsername) => {
        localStorage.setItem("authToken", newToken);
        localStorage.setItem("username", newUsername);

        setToken(newToken);
        setUsername(newUsername);
    };

    const logout = () => {
        localStorage.removeItem("authToken");
        localStorage.removeItem("username");

        setToken(null);
        setUsername(null);
    };

    const isAuthenticated = Boolean(token);

    return (
        <AuthContext.Provider
            value={{
                token,
                username,
                isAuthenticated,
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