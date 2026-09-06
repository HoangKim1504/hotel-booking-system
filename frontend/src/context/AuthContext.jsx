import {
    createContext,
    useContext,
    useState,
} from "react";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
    const [token, setToken] = useState(() => localStorage.getItem("authToken"));

    const login = (newToken) => {
        localStorage.setItem("authToken", newToken);
        setToken(newToken);
    };

    const logout = () => {
        localStorage.removeItem("authToken");
        setToken(null);
    };

    const isAuthenticated = Boolean(token);

    return (
        <AuthContext.Provider
            value={{
                token,
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