import {
    createContext,
    useContext,
    useEffect,
    useMemo,
    useState,
} from "react";

import { useAuth } from "./AuthContext";

import {
    getCart,
    addCartItem,
    updateCartItem,
    deleteCartItem,
    clearCartItems,
} from "../services/cartService";

const CartContext = createContext(null);

export function CartProvider({ children }) {
    const {
        token,
        isAuthenticated,
    } = useAuth();

    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(false);

    const cartItems = cart?.items ?? [];
    const cartTotal = cart?.totalAmount ?? 0;
    const cartWarnings = cart?.warnings ?? [];

    const loadCart = async () => {
        if (!isAuthenticated || !token) {
            setCart(null);
            return;
        }

        setLoading(true);

        try {
            const data = await getCart(token);

            setCart(data);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (isAuthenticated && token) {
            loadCart();
        } else {
            setCart(null);
        }
    }, [isAuthenticated, token]);

    const addToCart = async (
        roomTypeId,
        quantity = 1
    ) => {
        const data = await addCartItem({
            roomTypeId,
            quantity,
            token,
        });

        setCart(data);

        return data;
    };

    const changeQuantity = async (
        itemId,
        quantity
    ) => {
        const data = await updateCartItem({
            itemId,
            quantity,
            token,
        });

        setCart(data);

        return data;
    };

    const removeFromCart = async (itemId) => {
        const data = await deleteCartItem({
            itemId,
            token,
        });

        setCart(data);

        return data;
    };

    const cartCount = useMemo(() => {
        if (!cart?.items) {
            return 0;
        }

        return cart.items.reduce(
            (total, item) =>
                total + item.quantity,
            0
        );
    }, [cart]);

    const clearCart = async () => {
        const updatedCart =
            await clearCartItems(token);

        setCart(updatedCart);

        return updatedCart;
    };

    return (
        <CartContext.Provider
            value={{
                cart,
                cartItems,
                cartTotal,
                cartWarnings,
                cartCount,
                loadCart,
                addToCart,
                changeQuantity,
                removeFromCart,
                clearCart,
            }}
        >
            {children}
        </CartContext.Provider>
    );
}

export function useCart() {
    return useContext(CartContext);
}