function BookingConfirmPopup({
    show,
    formData,
    cartItems,
    cartTotal,
    numberOfNights,
    estimatedTotal,
    loading,
    onConfirm,
    onCancel,
}) {
    if (!show) {
        return null;
    }

    return (
        <div className="booking-confirm-overlay">
            <div className="booking-confirm-popup">

                {/* Header */}
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

                {/* Booking Information */}
                <div className="booking-confirm-user-info">

                    <div className="booking-confirm-info-item">
                        <span>Name</span>
                        <strong>
                            {formData.name}
                        </strong>
                    </div>

                    <div className="booking-confirm-info-item">
                        <span>Email</span>
                        <strong>
                            {formData.email}
                        </strong>
                    </div>

                    <div className="booking-confirm-info-item">
                        <span>Check In</span>
                        <strong>
                            {formData.checkIn}
                        </strong>
                    </div>

                    <div className="booking-confirm-info-item">
                        <span>Check Out</span>
                        <strong>
                            {formData.checkOut}
                        </strong>
                    </div>

                    <div className="booking-confirm-info-item">
                        <span>Adults</span>
                        <strong>
                            {formData.adults}
                        </strong>
                    </div>

                    <div className="booking-confirm-info-item">
                        <span>Children</span>
                        <strong>
                            {formData.children}
                        </strong>
                    </div>

                    <div className="booking-confirm-info-item">
                        <span>Payment Method</span>
                        <strong>
                            {formData.paymentMethod}
                        </strong>
                    </div>

                    <div className="booking-confirm-info-item">
                        <span>Total nights</span>
                        <strong>
                            {numberOfNights} night
                            {numberOfNights !== 1 ? "s" : ""}
                        </strong>
                    </div>

                </div>

                {/* Selected Rooms */}
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
                                    $
                                    {Number(
                                        item.price
                                    ).toLocaleString()}
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

                {/* Total */}
                <div className="booking-confirm-total">

                    <div>
                        <span>Estimated Total</span>

                    </div>

                    <strong>
                        ${Number(
                            estimatedTotal
                        ).toLocaleString()}
                    </strong>

                </div>

                {/* Actions */}
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
}

export default BookingConfirmPopup;