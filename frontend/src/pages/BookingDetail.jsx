import {
    useEffect,
    useState,
} from "react";

import {
    useNavigate,
    useParams,
} from "react-router-dom";

import { useAuth } from "../context/AuthContext";

import { getBookingById } from "../services/bookingService";
import { getErrorMessages } from "../utils/apiErrorUtils";

import LoadingSpinner from "../components/common/LoadingSpinner";
import ErrorPopup from "../components/common/ErrorPopup";

function BookingDetail() {
    const { bookingId } = useParams();

    const navigate = useNavigate();

    const { token } = useAuth();

    const [booking, setBooking] = useState(null);
    const [loading, setLoading] = useState(false);

    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] =
        useState(false);

    useEffect(() => {
        const loadBookingDetail = async () => {
            setLoading(true);

            try {
                const data = await getBookingById({
                    bookingId,
                    token,
                });

                setBooking(data);

            } catch (error) {
                setErrors(
                    getErrorMessages(error)
                );

                setShowErrorPopup(true);

            } finally {
                setLoading(false);
            }
        };

        loadBookingDetail();

    }, [bookingId, token]);

    if (loading) {
        return (
            <LoadingSpinner show={true} />
        );
    }

    if (!booking) {
        return null;
    }

    const getBookingStatusClass = (status) => {
        switch (status) {
            case "CONFIRMED":
                return "booking-detail-status-confirmed";

            case "PAID":
                return "booking-detail-status-paid";

            case "PENDING":
                return "booking-detail-status-pending";

            case "CANCELLED":
                return "booking-detail-status-cancelled";

            case "COMPLETED":
                return "booking-detail-status-completed";

            default:
                return "booking-detail-status-default";
        }
    };

    const formatDate = (date) => {
        if (!date) {
            return "-";
        }

        return new Date(`${date}T00:00:00`).toLocaleDateString(
            "en-GB"
        );
    };

    return (
        <>
            <div className="user-booking-detail-page">
                <div className="user-booking-detail-container">

                    {/* Page Header */}
                    <div className="user-booking-detail-page-header">
                        <div>
                            <h2>Booking Detail</h2>

                            <p>
                                Your reservation information and room summary.
                            </p>
                        </div>

                        <button
                            type="button"
                            className="btn btn-outline-primary"
                            onClick={() => navigate("/bookings")}
                        >
                            <i className="fa fa-arrow-left me-2" />
                            Back To My Bookings
                        </button>
                    </div>

                    <div className="user-booking-detail-card">

                        {/* Booking Header */}
                        <div className="user-booking-summary-header">
                            <div>
                                <span className="user-booking-summary-label">
                                    Booking Status
                                </span>

                                <span
                                    className={`user-booking-status-badge ${getBookingStatusClass(
                                        booking.bookingStatus
                                    )}`}
                                >
                                    {booking.bookingStatus?.replaceAll(
                                        "_",
                                        " "
                                    )}
                                </span>
                            </div>

                            <div className="user-booking-payment-summary">
                                <span>Payment</span>

                                <strong>
                                    {booking.paymentStatus ?? "NONE"}
                                </strong>

                                <small>
                                    {booking.paymentMethod ?? "-"}
                                </small>
                            </div>
                        </div>

                        {/* Stay Information */}
                        <div className="user-booking-stay-section">

                            <div className="user-booking-date-box">
                                <div className="user-booking-date-icon">
                                    <i className="fa fa-calendar-check" />
                                </div>

                                <div>
                                    <span>Check In</span>

                                    <strong>
                                        {formatDate(
                                            booking.checkInDate
                                        )}
                                    </strong>
                                </div>
                            </div>

                            <div className="user-booking-date-arrow">
                                <i className="fa fa-arrow-right" />
                            </div>

                            <div className="user-booking-date-box">
                                <div className="user-booking-date-icon">
                                    <i className="fa fa-calendar-times" />
                                </div>

                                <div>
                                    <span>Check Out</span>

                                    <strong>
                                        {formatDate(
                                            booking.checkOutDate
                                        )}
                                    </strong>
                                </div>
                            </div>

                        </div>

                        {/* Booked Rooms */}
                        <div className="user-booking-rooms-section">

                            <div className="user-booking-section-title">
                                <div>
                                    <h4>Booked Rooms</h4>

                                    <p>
                                        Rooms included in this reservation
                                    </p>
                                </div>
                            </div>

                            <div className="user-booking-room-count">
                                {booking.items?.length ?? 0} Room Type
                                {(booking.items?.length ?? 0) !== 1
                                    ? "s"
                                    : ""}
                            </div>

                            <div className="user-booking-room-list">
                                {booking.items?.map((item) => (
                                    <div
                                        key={item.id}
                                        className="user-booking-room-row"
                                    >
                                        <div className="user-booking-room-icon">
                                            <i className="fa fa-bed" />
                                        </div>

                                        <div className="user-booking-room-info">
                                            <strong>
                                                {item.roomTypeName}
                                            </strong>

                                            <span>
                                                ${Number(item.price).toLocaleString()}
                                                {" / night × "}
                                                {item.quantity}
                                            </span>
                                        </div>

                                        <div className="user-booking-room-price">
                                            $
                                            {Number(item.price).toLocaleString()}
                                        </div>
                                    </div>
                                ))}
                            </div>

                        </div>

                        {/* Total Amount */}
                        <div className="user-booking-total-section">

                            <div className="user-booking-total-label">
                                <span>Total Amount</span>

                                <small>
                                    Total price for your reservation
                                </small>
                            </div>

                            <strong className="user-booking-total-price">
                                ${Number(booking.totalAmount).toLocaleString()}
                            </strong>

                        </div>

                    </div>

                </div>
            </div>

            <ErrorPopup
                show={showErrorPopup}
                title="Unable to Load Booking"
                errors={errors}
                onClose={() => {
                    setShowErrorPopup(false);
                    navigate("/");
                }}
            />
        </>
    );
}

export default BookingDetail;