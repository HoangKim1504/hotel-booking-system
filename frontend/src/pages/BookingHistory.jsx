import {
    useEffect,
    useState,
} from "react";

import { useNavigate } from "react-router-dom";

import { useAuth } from "../context/AuthContext";

import {
    cancelBooking,
    getBookings,
} from "../services/bookingService";

import { getErrorMessages } from "../utils/apiErrorUtils";

import LoadingSpinner from "../components/common/LoadingSpinner";
import ErrorPopup from "../components/common/ErrorPopup";
import ConfirmPopup from "../components/common/ConfirmPopup";
import SuccessPopup from "../components/common/SuccessPopup";

const BOOKING_STATUSES = [
    "PENDING",
    "PAID",
    "CONFIRMED",
    "CHECKED_IN",
    "COMPLETED",
    "CANCELLED",
    "EXPIRED",
    "REFUNDED",
];

function BookingHistory() {
    const navigate = useNavigate();

    const { token } = useAuth();

    const [bookings, setBookings] = useState([]);

    const [loading, setLoading] = useState(false);

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(10);

    const [totalPages, setTotalPages] = useState(0);
    const [totalRecords, setTotalRecords] = useState(0);

    const [statusForm, setStatusForm] = useState("");
    const [statusCriteria, setStatusCriteria] = useState("");

    const [bookingToCancel, setBookingToCancel] =
        useState(null);

    const [cancelLoading, setCancelLoading] =
        useState(false);

    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] =
        useState(false);

    const [showSuccessPopup, setShowSuccessPopup] =
        useState(false);

    const [refreshKey, setRefreshKey] = useState(0);

    useEffect(() => {
        const loadBookings = async () => {
            setLoading(true);

            try {
                const data = await getBookings({
                    page: currentPage,
                    size: pageSize,
                    status: statusCriteria,
                    token,
                });

                setBookings(
                    data.data ?? data.content ?? []
                );

                setTotalPages(
                    data.totalPages ?? 0
                );

                setTotalRecords(
                    data.totalRecords ??
                    data.totalElements ??
                    0
                );

            } catch (error) {
                setErrors(
                    getErrorMessages(error)
                );

                setShowErrorPopup(true);

            } finally {
                setLoading(false);
            }
        };

        loadBookings();

    }, [
        currentPage,
        pageSize,
        statusCriteria,
        token,
        refreshKey,
    ]);

    const handleSearch = () => {
        setCurrentPage(1);
        setStatusCriteria(statusForm);
    };

    const handleReset = () => {
        setStatusForm("");
        setStatusCriteria("");
        setCurrentPage(1);
    };

    const handleView = (bookingId) => {
        navigate(`/bookings/${bookingId}`);
    };

    const handleConfirmCancel = async () => {
        if (!bookingToCancel) {
            return;
        }

        setCancelLoading(true);

        try {
            await cancelBooking({
                bookingId: bookingToCancel.bookingId,
                token,
            });

            setBookingToCancel(null);

            setShowSuccessPopup(true);

            setRefreshKey((prev) => prev + 1);

        } catch (error) {
            // Close cancel confirmation popup
            setBookingToCancel(null);

            setErrors(
                getErrorMessages(error)
            );

            setShowErrorPopup(true);

        } finally {
            setCancelLoading(false);
        }
    };

    const canCancelBooking = (status) =>
        ["PENDING", "PAID", "CONFIRMED"].includes(status);

    const formatDate = (date) => {
        if (!date) {
            return "-";
        }

        return new Date(
            `${date}T00:00:00`
        ).toLocaleDateString("en-GB");
    };

    return (
        <>
            <LoadingSpinner show={loading} />

            <div className="my-bookings-page">
                <div className="my-bookings-container">

                    <div className="my-bookings-header">
                        <div>

                            <h2>My Bookings</h2>

                            <p>
                                Total: {totalRecords} bookings
                            </p>
                        </div>
                    </div>

                    <div className="my-bookings-card">

                        <div className="my-bookings-toolbar">

                            <select
                                className="form-select"
                                value={statusForm}
                                onChange={(event) =>
                                    setStatusForm(
                                        event.target.value
                                    )
                                }
                            >
                                <option value="">
                                    All Booking Status
                                </option>

                                {BOOKING_STATUSES.map((status) => (
                                    <option
                                        key={status}
                                        value={status}
                                    >
                                        {status.replaceAll("_", " ")}
                                    </option>
                                ))}
                            </select>

                            <button
                                type="button"
                                className="btn btn-primary"
                                onClick={handleSearch}
                            >
                                Search
                            </button>

                            <button
                                type="button"
                                className="btn btn-outline-secondary"
                                onClick={handleReset}
                            >
                                Reset
                            </button>

                        </div>

                        {bookings.length === 0 && !loading ? (
                            <div className="my-bookings-empty">
                                You don't have any bookings yet.
                            </div>
                        ) : (
                            <div className="table-responsive">

                                <table className="table my-bookings-table">

                                    <colgroup>
                                        <col className="my-bookings-col-check-in" />
                                        <col className="my-bookings-col-check-out" />
                                        <col className="my-bookings-col-total" />
                                        <col className="my-bookings-col-payment" />
                                        <col className="my-bookings-col-status" />
                                        <col className="my-bookings-col-actions" />
                                    </colgroup>

                                    <thead>
                                        <tr>
                                            <th>Check In</th>
                                            <th>Check Out</th>
                                            <th>Total</th>
                                            <th>Payment</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>

                                    <tbody>
                                        {bookings.map((booking) => (
                                            <tr key={booking.bookingId}>

                                                <td>
                                                    {formatDate(
                                                        booking.checkInDate
                                                    )}
                                                </td>

                                                <td>
                                                    {formatDate(
                                                        booking.checkOutDate
                                                    )}
                                                </td>

                                                <td className="my-bookings-total">
                                                    $
                                                    {Number(
                                                        booking.totalAmount
                                                    ).toLocaleString()}
                                                </td>

                                                <td>
                                                    <span
                                                        className={`my-bookings-payment my-bookings-payment-${(
                                                            booking.paymentStatus ?? "NONE"
                                                        ).toLowerCase()}`}
                                                    >
                                                        {booking.paymentStatus ?? "NONE"}
                                                    </span>
                                                </td>

                                                <td>
                                                    <span
                                                        className={`my-bookings-status my-bookings-status-${booking.bookingStatus?.toLowerCase()}`}
                                                    >
                                                        {booking.bookingStatus?.replaceAll(
                                                            "_",
                                                            " "
                                                        )}
                                                    </span>
                                                </td>

                                                <td>
                                                    <div className="my-bookings-actions">

                                                        <button
                                                            type="button"
                                                            className="btn btn-sm btn-outline-primary"
                                                            onClick={() =>
                                                                handleView(
                                                                    booking.bookingId
                                                                )
                                                            }
                                                        >
                                                            View
                                                        </button>

                                                        <button
                                                            type="button"
                                                            className="btn btn-sm btn-outline-warning"
                                                            disabled={
                                                                !canCancelBooking(
                                                                    booking.bookingStatus
                                                                )
                                                            }
                                                            onClick={() =>
                                                                setBookingToCancel(
                                                                    booking
                                                                )
                                                            }
                                                        >
                                                            Cancel
                                                        </button>

                                                    </div>
                                                </td>

                                            </tr>
                                        ))}
                                    </tbody>

                                </table>

                            </div>
                        )}

                        {totalPages > 1 && (
                            <div className="my-bookings-pagination">

                                <button
                                    type="button"
                                    className="btn btn-outline-secondary btn-sm"
                                    disabled={currentPage === 1}
                                    onClick={() =>
                                        setCurrentPage(
                                            (prev) => prev - 1
                                        )
                                    }
                                >
                                    Previous
                                </button>

                                <span>
                                    Page {currentPage} of {totalPages}
                                </span>

                                <button
                                    type="button"
                                    className="btn btn-outline-secondary btn-sm"
                                    disabled={
                                        currentPage === totalPages
                                    }
                                    onClick={() =>
                                        setCurrentPage(
                                            (prev) => prev + 1
                                        )
                                    }
                                >
                                    Next
                                </button>

                            </div>
                        )}

                    </div>

                </div>
            </div>

            <ConfirmPopup
                show={bookingToCancel !== null}
                title="Cancel Booking"
                message="Are you sure you want to cancel this booking?"
                confirmText={
                    cancelLoading
                        ? "Cancelling..."
                        : "Cancel Booking"
                }
                cancelText="Back"
                loading={cancelLoading}
                onConfirm={handleConfirmCancel}
                onCancel={() =>
                    setBookingToCancel(null)
                }
            />

            <SuccessPopup
                show={showSuccessPopup}
                title="Booking Cancelled"
                message="Your booking has been cancelled successfully."
                onClose={() =>
                    setShowSuccessPopup(false)
                }
            />

            <ErrorPopup
                show={showErrorPopup}
                title="Unable to Process Booking"
                errors={errors}
                onClose={() =>
                    setShowErrorPopup(false)
                }
            />
        </>
    );
}

export default BookingHistory;