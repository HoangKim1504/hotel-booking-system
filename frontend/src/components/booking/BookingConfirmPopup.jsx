return (
    <div className="booking-confirm-overlay">
        <div className="booking-confirm-popup">

            <div className="booking-confirm-header">
                <div>
                    <h4>Confirm Booking</h4>
                    <p>
                        Please review your booking information
                        before confirming.
                    </p>
                </div>

                <button
                    type="button"
                    className="booking-confirm-close"
                    onClick={onCancel}
                    disabled={loading}
                >
                    ×
                </button>
            </div>

            <div className="booking-confirm-user-info">

                <div className="booking-confirm-info-item">
                    <span>Name</span>
                    <strong>{formData.name}</strong>
                </div>

                <div className="booking-confirm-info-item">
                    <span>Email</span>
                    <strong>{formData.email}</strong>
                </div>

                <div className="booking-confirm-info-item">
                    <span>Check In</span>
                    <strong>{formData.checkIn}</strong>
                </div>

                <div className="booking-confirm-info-item">
                    <span>Check Out</span>
                    <strong>{formData.checkOut}</strong>
                </div>

                <div className="booking-confirm-info-item">
                    <span>Adults</span>
                    <strong>{formData.adults}</strong>
                </div>

                <div className="booking-confirm-info-item">
                    <span>Children</span>
                    <strong>{formData.children}</strong>
                </div>

            </div>

            <div className="booking-confirm-room-section">
                <h5>Selected Rooms</h5>

                {cartItems.map((item) => (
                    <div
                        key={item.id}
                        className="booking-confirm-room-item"
                    >
                        <div>
                            <strong>
                                {item.roomTypeName}
                            </strong>

                            <span>
                                ${Number(item.price).toLocaleString()}
                                {" × "}
                                {item.quantity}
                            </span>
                        </div>

                        <strong>
                            $
                            {Number(
                                item.subtotal
                            ).toLocaleString()}
                        </strong>
                    </div>
                ))}
            </div>

            <div className="booking-confirm-total">
                <span>Total</span>

                <strong>
                    ${Number(cartTotal).toLocaleString()}
                </strong>
            </div>

            <div className="booking-confirm-actions">
                <button
                    type="button"
                    className="btn btn-outline-secondary"
                    onClick={onCancel}
                    disabled={loading}
                >
                    Back
                </button>

                <button
                    type="button"
                    className="btn btn-primary"
                    onClick={onConfirm}
                    disabled={loading}
                >
                    {loading
                        ? "Booking..."
                        : "Confirm Booking"}
                </button>
            </div>

        </div>
    </div>
);