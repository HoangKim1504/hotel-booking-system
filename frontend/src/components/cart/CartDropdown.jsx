import { useNavigate } from "react-router-dom";
import { useState } from "react";

import { useCart } from "../../context/CartContext";
import { getErrorMessages } from "../../utils/apiErrorUtils";

import ErrorPopup from "../common/ErrorPopup";

function CartDropdown({
    show,
    onClose,
}) {
    const navigate = useNavigate();

    const {
        cartItems,
        cartTotal,
        loading,
        removeFromCart,
    } = useCart();

    const [removeErrors, setRemoveErrors] = useState([]);
    const [showRemoveError, setShowRemoveError] = useState(false);

    if (!show) {
        return null;
    }

    const handleViewCart = () => {
        onClose();
        navigate("/cart");
    };

    const handleBooking = () => {
        onClose();
        navigate("/booking");
    };

    const handleRemoveItem = async (itemId) => {
        try {
            await removeFromCart(itemId);
        } catch (error) {
            setRemoveErrors(
                getErrorMessages(error)
            );

            setShowRemoveError(true);
        }
    };

    const handleConfirmRemove = async () => {
        if (!itemToRemove) {
            return;
        }

        setRemoving(true);

        try {
            await removeFromCart(itemToRemove.id);

            setItemToRemove(null);
        } catch (error) {
            setRemoveErrors(
                getErrorMessages(error)
            );

            setShowRemoveError(true);
        } finally {
            setRemoving(false);
        }
    };

    return (
        <div className="navbar-cart-dropdown">

            <div className="navbar-cart-dropdown-header">
                <h5>Your Cart</h5>

                <button
                    type="button"
                    className="navbar-cart-dropdown-close"
                    onClick={onClose}
                >
                    ×
                </button>
            </div>

            {loading ? (
                <div className="navbar-cart-dropdown-empty">
                    Loading cart...
                </div>
            ) : cartItems.length === 0 ? (
                <div className="navbar-cart-dropdown-empty">
                    Your cart is empty.
                </div>
            ) : (
                <>
                    <div className="navbar-cart-items">

                        {cartItems.map((item) => (
                            <div
                                key={item.id}
                                className="navbar-cart-item"
                            >
                                <div className="navbar-cart-item-info">
                                    <strong>
                                        {item.roomTypeName}
                                    </strong>

                                    <span>
                                        ${Number(item.price).toLocaleString()}
                                        {" × "}
                                        {item.quantity}
                                    </span>
                                </div>

                                <div className="navbar-cart-item-right">
                                    <div className="navbar-cart-item-subtotal">
                                        ${Number(item.subtotal).toLocaleString()}
                                    </div>

                                    <button
                                        type="button"
                                        className="navbar-cart-remove-btn"
                                        title="Remove from cart"
                                        onClick={() =>
                                            handleRemoveItem(item.id)
                                        }
                                    >
                                        <i className="fa fa-trash" />
                                    </button>
                                </div>
                            </div>
                        ))}

                    </div>

                    <div className="navbar-cart-total">
                        <span>Total</span>

                        <strong>
                            $
                            {Number(
                                cartTotal
                            ).toLocaleString()}
                        </strong>
                    </div>

                    <div className="navbar-cart-actions">
                        <button
                            type="button"
                            className="btn btn-outline-primary"
                            onClick={handleViewCart}
                        >
                            View Cart
                        </button>

                        <button
                            type="button"
                            className="btn btn-primary"
                            onClick={handleBooking}
                            disabled={cartItems.length === 0}
                        >
                            Book Now
                        </button>
                    </div>
                </>
            )}

        </div>
    );
}

export default CartDropdown;