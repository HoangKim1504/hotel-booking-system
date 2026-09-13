import { useEffect, useState } from "react";

import { useAuth } from "../../context/AuthContext";
import { getErrorMessages } from "../../utils/apiErrorUtils";

import {
    getAdminBookings,
    getAdminBookingById,
    updateAdminBookingStatus,
    cancelAdminBooking,
    deleteAdminBooking,
} from "../../services/admin/adminBookingService";

import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorPopup from "../../components/common/ErrorPopup";
import ConfirmPopup from "../../components/common/ConfirmPopup";
import SuccessPopup from "../../components/common/SuccessPopup";
import BookingDetailPopup from "../../components/admin/BookingDetailPopup";
import UpdateBookingStatusPopup from "../../components/admin/UpdateBookingStatusPopup";

function BookingManagement() {
    const { token } = useAuth();

    const [bookings, setBookings] = useState([]);

    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(1);
    const [totalRecords, setTotalRecords] = useState(0);

    const [statusForm, setStatusForm] = useState("");
    const [statusCriteria, setStatusCriteria] = useState("");

    const [bookingToCancel, setBookingToCancel] = useState(null);
    const [cancelLoading, setCancelLoading] = useState(false);

    const [bookingToDelete, setBookingToDelete] = useState(null);
    const [deleteLoading, setDeleteLoading] = useState(false);

    const [refreshKey, setRefreshKey] = useState(0);

    const [successMessage, setSuccessMessage] = useState("");

    const [selectedBooking, setSelectedBooking] = useState(null);
    const [showDetailPopup, setShowDetailPopup] = useState(false);
    const [detailLoading, setDetailLoading] = useState(false);

    const [bookingToUpdateStatus, setBookingToUpdateStatus] = useState(null);
    const [statusLoading, setStatusLoading] = useState(false);
    const [statusErrors, setStatusErrors] = useState([]);

    useEffect(() => {
        const loadBookings = async () => {
            setLoading(true);

            try {
                const data = await getAdminBookings({
                    page: currentPage,
                    size: pageSize,
                    status: statusCriteria,
                    token,
                });

                setBookings(data.data);
                setTotalPages(data.totalPages);
                setTotalRecords(data.totalRecords);
            } catch (error) {
                setErrors(getErrorMessages(error));
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

    const handleSearchSubmit = (event) => {
        event.preventDefault();

        setStatusCriteria(statusForm);
        setCurrentPage(1);
    };

    const handleSearchReset = () => {
        setStatusForm("");
        setStatusCriteria("");
        setCurrentPage(1);
    };

    const getUserDisplayName = (booking) => {
        if (booking.fullName?.trim()) {
            return {
                text: booking.fullName,
                isUsername: false,
            };
        }

        if (booking.username?.trim()) {
            return {
                text: `@${booking.username}`,
                isUsername: true,
            };
        }

        return {
            text: "-",
            isUsername: false,
        };
    };

    const getBookingStatusClass = (status) => {
        switch (status) {
            case "PENDING":
                return "admin-booking-status-pending";

            case "PAID":
                return "admin-booking-status-paid";

            case "CONFIRMED":
                return "admin-booking-status-confirmed";

            case "CHECKED_IN":
                return "admin-booking-status-checked-in";

            case "COMPLETED":
                return "admin-booking-status-completed";

            case "CANCELLED":
                return "admin-booking-status-cancelled";

            case "EXPIRED":
                return "admin-booking-status-expired";

            case "REFUNDED":
                return "admin-booking-status-refunded";

            default:
                return "";
        }
    };

    const getPaymentStatusClass = (status) => {
        switch (status) {
            case "PENDING":
                return "admin-payment-status-pending";

            case "SUCCESS":
                return "admin-payment-status-success";

            case "FAILED":
                return "admin-payment-status-failed";

            case "REFUNDED":
                return "admin-payment-status-refunded";

            default:
               return "admin-payment-status-none";
        }
    };

    const getBookingUserName = (booking) => {
        if (booking.fullName?.trim()) {
            return booking.fullName;
        }

        if (booking.username?.trim()) {
            return `@${booking.username}`;
        }

        return "this user";
    };

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

    const handleCancelClick = (booking) => {
        setBookingToCancel(booking);
    };

    const handleConfirmCancel = async () => {
        if (!bookingToCancel) {
            return;
        }

        setCancelLoading(true);

        const bookingId = bookingToCancel.bookingId;

        try {
            await cancelAdminBooking({
                bookingId,
                userId: bookingToCancel.userId,
                token,
            });

            setBookingToCancel(null);

            setSuccessMessage(
                `Booking ${bookingId} cancelled successfully.`
            );

            setRefreshKey((prev) => prev + 1);
        } catch (error) {
            setBookingToCancel(null);

            setErrors(getErrorMessages(error));
            setShowErrorPopup(true);
        } finally {
            setCancelLoading(false);
        }
    };

    const handleDeleteClick = (booking) => {
        setBookingToDelete(booking);
    };

    const handleConfirmDelete = async () => {
        if (!bookingToDelete) {
            return;
        }

        setDeleteLoading(true);

        const bookingId = bookingToDelete.bookingId;

        try {
            await deleteAdminBooking({
                bookingId,
                userId: bookingToDelete.userId,
                token,
            });

            setBookingToDelete(null);

            setSuccessMessage(
                `Booking ${bookingId} deleted successfully.`
            );

            if (bookings.length === 1 && currentPage > 1) {
                setCurrentPage((prev) => prev - 1);
            } else {
                setRefreshKey((prev) => prev + 1);
            }
        } catch (error) {
            setBookingToDelete(null);

            setErrors(getErrorMessages(error));
            setShowErrorPopup(true);
        } finally {
            setDeleteLoading(false);
        }
    };

    const handleViewBooking = async (booking) => {
        setDetailLoading(true);

        try {
            const data = await getAdminBookingById({
                bookingId: booking.bookingId,
                userId: booking.userId,
                token,
            });

            setSelectedBooking(data);
            setShowDetailPopup(true);
        } catch (error) {
            setErrors(getErrorMessages(error));
            setShowErrorPopup(true);
        } finally {
            setDetailLoading(false);
        }
    };

    const handleCloseDetail = () => {
        setShowDetailPopup(false);
        setSelectedBooking(null);
    };

    const handleStatusClick = (booking) => {
        setStatusErrors([]);
        setBookingToUpdateStatus(booking);
    };

    const handleUpdateBookingStatus = async (bookingStatus) => {
        if (!bookingToUpdateStatus) {
            return;
        }

        setStatusLoading(true);
        setStatusErrors([]);

        try {
            await updateAdminBookingStatus({
                bookingId: bookingToUpdateStatus.bookingId,
                userId: bookingToUpdateStatus.userId,
                bookingStatus,
                token,
            });

            setBookingToUpdateStatus(null);

            setSuccessMessage(
                `Booking status updated to ${bookingStatus.replaceAll("_", " ")} successfully.`
            );

            setRefreshKey((prev) => prev + 1);
        } catch (error) {
            setStatusErrors(
                getErrorMessages(error)
            );
        } finally {
            setStatusLoading(false);
        }
    };

    const handleCancelStatusUpdate = () => {
        setBookingToUpdateStatus(null);
        setStatusErrors([]);
    };

    return (
        <>
            <LoadingSpinner show={loading} />

            <div className="booking-management">
                <div className="booking-management-content">

                    <div className="booking-management-card">

                        <div className="booking-management-card-header">
                            <div>
                                <h4>Booking Management</h4>

                                <span className="admin-total-records">
                                    Total: {totalRecords} bookings
                                </span>
                            </div>
                        </div>

                        {/* Filter */}
                        <div className="admin-booking-toolbar">

                            <form
                                className="admin-booking-search"
                                onSubmit={handleSearchSubmit}
                            >
                                <select
                                    className="form-select admin-booking-status-search"
                                    value={statusForm}
                                    onChange={(event) =>
                                        setStatusForm(event.target.value)
                                    }
                                >
                                    <option value="">
                                        All Booking Status
                                    </option>

                                    <option value="PENDING">
                                        Pending
                                    </option>

                                    <option value="PAID">
                                        Paid
                                    </option>

                                    <option value="CONFIRMED">
                                        Confirmed
                                    </option>

                                    <option value="CHECKED_IN">
                                        Checked In
                                    </option>

                                    <option value="COMPLETED">
                                        Completed
                                    </option>

                                    <option value="CANCELLED">
                                        Cancelled
                                    </option>

                                    <option value="EXPIRED">
                                        Expired
                                    </option>

                                    <option value="REFUNDED">
                                        Refunded
                                    </option>
                                </select>

                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                >
                                    Search
                                </button>

                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={handleSearchReset}
                                >
                                    Reset
                                </button>
                            </form>

                        </div>

                        {/* Table */}
                        <div className="table-responsive admin-booking-table-wrapper">
                            <table className="table align-middle admin-booking-table">

                                <colgroup>
                                    <col className="admin-col-booking-user" />
                                    <col className="admin-col-booking-status" />
                                    <col className="admin-col-booking-total" />
                                    <col className="admin-col-payment-status" />
                                    <col className="admin-col-payment-method" />
                                    <col className="admin-col-booking-created" />
                                    <col className="admin-col-booking-actions" />
                                </colgroup>

                                <thead>
                                    <tr>
                                        <th>Full Name</th>

                                        <th className="text-center">
                                            Booking Status
                                        </th>

                                        <th className="text-center">
                                            Total
                                        </th>

                                        <th className="text-center">
                                            Payment Status
                                        </th>

                                        <th className="text-center">
                                            Method
                                        </th>

                                        <th className="text-center">
                                            Created At
                                        </th>

                                        <th className="text-center">
                                            Actions
                                        </th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {bookings.map((booking) => {
                                        const userDisplay = getUserDisplayName(booking);

                                        return (
                                            <tr key={booking.bookingId}>

                                                <td
                                                    className={
                                                        userDisplay.isUsername
                                                            ? "admin-booking-username-fallback"
                                                            : "admin-booking-user-name"
                                                    }
                                                    title={
                                                        userDisplay.isUsername
                                                            ? `Username: ${booking.username}`
                                                            : booking.fullName
                                                    }
                                                >
                                                    {userDisplay.text}
                                                </td>

                                                <td className="text-center">
                                                    <span
                                                        className={`admin-booking-status-badge ${getBookingStatusClass(
                                                            booking.bookingStatus
                                                        )}`}
                                                    >
                                                        {booking.bookingStatus}
                                                    </span>
                                                </td>

                                                <td className="text-center">
                                                    {formatAmount(
                                                        booking.totalAmount
                                                    )}
                                                </td>

                                                <td className="text-center">
                                                    <span
                                                        className={`admin-payment-status-badge ${getPaymentStatusClass(
                                                            booking.paymentStatus
                                                        )}`}
                                                    >
                                                        {booking.paymentStatus ?? "NONE"}
                                                    </span>
                                                </td>

                                                <td className="text-center">
                                                    {booking.paymentMethod || "-"}
                                                </td>

                                                <td className="text-center">
                                                    {formatDateTime(
                                                        booking.createdAt
                                                    )}
                                                </td>

                                                <td>
                                                    <div className="admin-booking-actions">
                                                        <button
                                                            type="button"
                                                            className="btn btn-sm btn-outline-primary"
                                                            onClick={() =>
                                                                handleViewBooking(booking)
                                                            }
                                                        >
                                                            View
                                                        </button>

                                                        <button
                                                            type="button"
                                                            className="btn btn-sm btn-outline-secondary"
                                                            onClick={() =>
                                                                handleStatusClick(booking)
                                                            }
                                                        >
                                                            Status
                                                        </button>

                                                        <button
                                                            type="button"
                                                            className="btn btn-sm btn-outline-warning"
                                                            disabled={
                                                                booking.bookingStatus === "CANCELLED" ||
                                                                booking.bookingStatus === "COMPLETED" ||
                                                                booking.bookingStatus === "EXPIRED" ||
                                                                booking.bookingStatus === "REFUNDED"
                                                            }
                                                            onClick={() =>
                                                                handleCancelClick(
                                                                    booking
                                                                )
                                                            }
                                                        >
                                                            Cancel
                                                        </button>

                                                        <button
                                                            type="button"
                                                            className="btn btn-sm btn-outline-danger"
                                                            onClick={() =>
                                                                handleDeleteClick(
                                                                    booking
                                                                )
                                                            }
                                                        >
                                                            Delete
                                                        </button>

                                                    </div>
                                                </td>

                                            </tr>
                                        );
                                    })}
                                </tbody>

                            </table>
                        </div>

                        {bookings.length === 0 && !loading && (
                            <div className="text-center py-4 text-muted">
                                No bookings found.
                            </div>
                        )}

                        {/* Pagination */}
                        {totalPages > 1 && (
                            <div className="admin-pagination">

                                <button
                                    type="button"
                                    className="btn btn-sm btn-outline-secondary"
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
                                    className="btn btn-sm btn-outline-secondary"
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
                title="Confirm Cancel"
                message={
                    bookingToCancel
                        ? `Are you sure you want to cancel booking of ${getBookingUserName(
                              bookingToCancel
                          )}?`
                        : ""
                }
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

            <BookingDetailPopup
                show={showDetailPopup}
                booking={selectedBooking}
                loading={detailLoading}
                onClose={handleCloseDetail}
            />

            <UpdateBookingStatusPopup
                show={bookingToUpdateStatus !== null}
                booking={bookingToUpdateStatus}
                loading={statusLoading}
                errors={statusErrors}
                onUpdate={handleUpdateBookingStatus}
                onCancel={handleCancelStatusUpdate}
            />

            <ConfirmPopup
                show={bookingToDelete !== null}
                title="Confirm Delete"
                message={
                    bookingToDelete
                        ? `Are you sure you want to delete booking ${bookingToDelete.bookingId}?`
                        : ""
                }
                confirmText={
                    deleteLoading
                        ? "Deleting..."
                        : "Delete"
                }
                cancelText="Cancel"
                loading={deleteLoading}
                onConfirm={handleConfirmDelete}
                onCancel={() =>
                    setBookingToDelete(null)
                }
            />

            <SuccessPopup
                show={Boolean(successMessage)}
                title="Success"
                message={successMessage}
                onClose={() =>
                    setSuccessMessage("")
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

export default BookingManagement;