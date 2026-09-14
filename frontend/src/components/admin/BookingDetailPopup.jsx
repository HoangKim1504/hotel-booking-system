function BookingDetailPopup({
    show,
    booking,
    loading,
    onClose,
}) {
    if (!show || !booking) {
        return null;
    }

    const formatAmount = (amount) => {
        if (amount == null) {
            return "-";
        }

        return `$${Number(amount).toLocaleString()}`;
    };

    const formatDateTime = (dateTime) => {
        if (!dateTime) {
            return "-";
        }

        return new Date(dateTime).toLocaleString();
    };

    return (
        <div className="booking-detail-overlay">
            <div className="booking-detail-popup">

                <div className="booking-detail-header">
                    <div>
                        <h4>Booking Detail</h4>

                        <p>
                            View booking information.
                        </p>
                    </div>

                    <button
                        type="button"
                        className="booking-detail-close"
                        onClick={onClose}
                        disabled={loading}
                    >
                        ×
                    </button>
                </div>

                <div className="booking-detail-grid">

                    <div className="booking-detail-item">
                        <span>Booking ID</span>
                        <strong>{booking.id}</strong>
                    </div>

                    <div className="booking-detail-item">
                        <span>Booking Status</span>
                        <strong>
                            {booking.bookingStatus}
                        </strong>
                    </div>

                    <div className="booking-detail-item">
                        <span>Check-in Date</span>
                        <strong>
                            {booking.checkInDate || "-"}
                        </strong>
                    </div>

                    <div className="booking-detail-item">
                        <span>Check-out Date</span>
                        <strong>
                            {booking.checkOutDate || "-"}
                        </strong>
                    </div>

                    <div className="booking-detail-item">
                        <span>Total Amount</span>
                        <strong>
                            {formatAmount(
                                booking.totalAmount
                            )}
                        </strong>
                    </div>

                    <div className="booking-detail-item">
                        <span>Payment Status</span>
                        <strong>
                            {booking.paymentStatus ?? "NONE"}
                        </strong>
                    </div>

                    <div className="booking-detail-item">
                        <span>Payment Method</span>
                        <strong>
                            {booking.paymentMethod || "-"}
                        </strong>
                    </div>

                    <div className="booking-detail-item">
                        <span>Created At</span>
                        <strong>
                            {formatDateTime(
                                booking.createdAt
                            )}
                        </strong>
                    </div>

                    <div className="booking-detail-item">
                        <span>Expired At</span>
                        <strong>
                            {formatDateTime(
                                booking.expiredAt
                            )}
                        </strong>
                    </div>

                </div>

                <div className="booking-detail-room-section">
                    <h5>Booked Room Types</h5>

                    <div className="table-responsive">
                        <table className="table booking-detail-room-table">
                            <thead>
                                <tr>
                                    <th>Room Type</th>

                                    <th className="text-center">
                                        Quantity
                                    </th>

                                    <th className="text-end">
                                        Price
                                    </th>
                                </tr>
                            </thead>

                            <tbody>
                                {booking.items?.map((item) => (
                                    <tr key={item.id}>
                                        <td>
                                            {item.roomTypeName}
                                        </td>

                                        <td className="text-center">
                                            {item.quantity}
                                        </td>

                                        <td className="text-end">
                                            {formatAmount(
                                                item.price
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>

                    {(!booking.items ||
                        booking.items.length === 0) && (
                        <div className="text-center text-muted py-3">
                            No booking items.
                        </div>
                    )}
                </div>

                <div className="booking-detail-actions">
                    <button
                        type="button"
                        className="btn btn-outline-secondary"
                        onClick={onClose}
                    >
                        Close
                    </button>
                </div>

            </div>
        </div>
    );
}

export default BookingDetailPopup;