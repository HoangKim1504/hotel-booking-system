import { useEffect, useState } from "react";

const BOOKING_STATUS_OPTIONS = [
    "PENDING",
    "PAID",
    "CONFIRMED",
    "CHECKED_IN",
    "COMPLETED",
    "CANCELLED",
    "EXPIRED",
    "REFUNDED",
];

function UpdateBookingStatusPopup({
    show,
    booking,
    loading,
    errors = [],
    onUpdate,
    onCancel,
}) {
    const [bookingStatus, setBookingStatus] = useState("");

    useEffect(() => {
        if (booking) {
            setBookingStatus(booking.bookingStatus ?? "");
        }
    }, [booking]);

    if (!show || !booking) {
        return null;
    }

    const hasChanges =
        bookingStatus !== booking.bookingStatus;

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!hasChanges || loading) {
            return;
        }

        onUpdate(bookingStatus);
    };

    return (
        <div className="booking-status-popup-overlay">
            <div className="booking-status-popup">

                <div className="booking-status-popup-header">
                    <div>
                        <h4>Update Booking Status</h4>

                        <p>
                            Change the current booking status.
                        </p>
                    </div>

                    <button
                        type="button"
                        className="booking-status-popup-close"
                        onClick={onCancel}
                        disabled={loading}
                    >
                        ×
                    </button>
                </div>

                {errors.length > 0 && (
                    <div className="booking-status-errors">
                        <div className="booking-status-errors-title">
                            Unable to update booking status
                        </div>

                        <ul>
                            {errors.map((error, index) => (
                                <li key={index}>
                                    {error}
                                </li>
                            ))}
                        </ul>
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="booking-status-form-group">
                        <label
                            htmlFor="bookingStatus"
                            className="form-label required-label"
                        >
                            Booking Status
                        </label>

                        <select
                            id="bookingStatus"
                            className="form-select"
                            value={bookingStatus}
                            onChange={(event) =>
                                setBookingStatus(event.target.value)
                            }
                            disabled={loading}
                            required
                        >
                            {BOOKING_STATUS_OPTIONS.map((status) => (
                                <option
                                    key={status}
                                    value={status}
                                >
                                    {status.replaceAll("_", " ")}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="booking-status-popup-actions">
                        <button
                            type="button"
                            className="btn btn-outline-secondary"
                            onClick={onCancel}
                            disabled={loading}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="btn btn-primary booking-status-update-btn"
                            disabled={loading || !hasChanges}
                        >
                            {loading
                                ? "Updating..."
                                : "Update"}
                        </button>
                    </div>
                </form>

            </div>
        </div>
    );
}

export default UpdateBookingStatusPopup;