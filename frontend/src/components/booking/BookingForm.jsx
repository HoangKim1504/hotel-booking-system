import { useState } from "react";
import { useNavigate } from "react-router-dom";

import about1 from "../../assets/images/about-1.jpg";
import about2 from "../../assets/images/about-2.jpg";
import about3 from "../../assets/images/about-3.jpg";
import about4 from "../../assets/images/about-4.jpg";

import { useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";

import { createBooking } from "../../services/bookingService";
import { getErrorMessages } from "../../utils/apiErrorUtils";
import { createPayment } from "../../services/paymentService";

import BookingConfirmPopup from "./BookingConfirmPopup";
import ErrorPopup from "../common/ErrorPopup";
import SuccessPopup from "../common/SuccessPopup";

function BookingForm() {
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        checkIn: "",
        checkOut: "",
        adults: "1",
        children: "0",
        paymentMethod: "",
    });

    const navigate = useNavigate();

    const {
        token,
    } = useAuth();

    const {
        cartItems,
        cartTotal,
        cartWarnings,
        loadCart,
    } = useCart();

    const [showConfirm, setShowConfirm] = useState(false);

    const [bookingLoading, setBookingLoading] = useState(false);

    const [bookingErrors, setBookingErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);
    const [showSuccessPopup, setShowSuccessPopup] = useState(false);

    const [createdBookingId, setCreatedBookingId] = useState(null);

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        if (cartItems.length === 0) {
            setBookingErrors([
                "Your cart is empty. Please add a room before booking.",
            ]);

            setShowErrorPopup(true);

            return;
        }

        setShowConfirm(true);
    };

    const handleConfirmBooking = async () => {
        setBookingLoading(true);

        try {
            const items = cartItems.map((item) => ({
                roomTypeId: item.roomTypeId,
                quantity: item.quantity,
                price: item.price,
            }));

            // 1. Create booking
            const booking = await createBooking({
                items,
                checkInDate: formData.checkIn,
                checkOutDate: formData.checkOut,
                token,
            });

            // 2. Create payment
            await createPayment({
                bookingId: booking.id,
                paymentMethod: formData.paymentMethod,
                token,
            });

            // 3. Save created booking id
            setCreatedBookingId(booking.id);

            setShowConfirm(false);
            setShowSuccessPopup(true);

        } catch (error) {
            setBookingErrors(
                getErrorMessages(error)
            );

            setShowConfirm(false);
            setShowErrorPopup(true);

        } finally {
            setBookingLoading(false);
        }
    };

    return (
        <div className="container-xxl py-5">
            <div className="container">

                {/* Title */}
                <div className="text-center">
                    <h6 className="section-title text-center text-primary text-uppercase">
                        Room Booking
                    </h6>

                    <h1 className="mb-5">
                        Book A{" "}
                        <span className="text-primary text-uppercase">
                            Luxury Room
                        </span>
                    </h1>
                </div>

                <div className="row g-5">

                    {/* Images */}
                    <div className="col-lg-6">
                        <div className="row g-3">

                            <div className="col-6 text-end">
                                <img
                                    className="img-fluid rounded w-75"
                                    src={about1}
                                    alt="Hotel"
                                    style={{
                                        marginTop: "25%",
                                    }}
                                />
                            </div>

                            <div className="col-6 text-start">
                                <img
                                    className="img-fluid rounded w-100"
                                    src={about2}
                                    alt="Hotel"
                                />
                            </div>

                            <div className="col-6 text-end">
                                <img
                                    className="img-fluid rounded w-50"
                                    src={about3}
                                    alt="Hotel"
                                />
                            </div>

                            <div className="col-6 text-start">
                                <img
                                    className="img-fluid rounded w-75"
                                    src={about4}
                                    alt="Hotel"
                                />
                            </div>

                        </div>
                    </div>

                    {/* Booking Form */}
                    <div className="col-lg-6">

                        <form onSubmit={handleSubmit}>
                            <div className="row g-3">

                                {/* Name */}
                                <div className="col-md-6">
                                    <div className="form-floating">

                                        <input
                                            type="text"
                                            className="form-control"
                                            id="name"
                                            name="name"
                                            placeholder="Your Name"
                                            value={formData.name}
                                            onChange={handleChange}
                                            required
                                        />

                                        <label
                                            htmlFor="name"
                                            className="required-label"
                                        >
                                            Your Name
                                        </label>

                                    </div>
                                </div>

                                {/* Email */}
                                <div className="col-md-6">
                                    <div className="form-floating">

                                        <input
                                            type="email"
                                            className="form-control"
                                            id="email"
                                            name="email"
                                            placeholder="Your Email"
                                            value={formData.email}
                                            onChange={handleChange}
                                            required
                                        />

                                        <label
                                            htmlFor="email"
                                            className="required-label"
                                        >
                                            Your Email
                                        </label>

                                    </div>
                                </div>

                                {/* Check In */}
                                <div className="col-md-6">
                                    <div className="form-floating">

                                        <input
                                            type="date"
                                            className="form-control"
                                            id="checkIn"
                                            name="checkIn"
                                            value={formData.checkIn}
                                            onChange={handleChange}
                                            required
                                        />

                                        <label
                                            htmlFor="checkIn"
                                            className="required-label"
                                        >
                                            Check In
                                        </label>

                                    </div>
                                </div>

                                {/* Check Out */}
                                <div className="col-md-6">
                                    <div className="form-floating">

                                        <input
                                            type="date"
                                            className="form-control"
                                            id="checkOut"
                                            name="checkOut"
                                            value={formData.checkOut}
                                            onChange={handleChange}
                                            required
                                        />

                                        <label
                                            htmlFor="checkOut"
                                            className="required-label"
                                        >
                                            Check Out
                                        </label>

                                    </div>
                                </div>

                                {/* Adult */}
                                <div className="col-md-6">
                                    <div className="form-floating">

                                        <select
                                            className="form-select"
                                            id="adults"
                                            name="adults"
                                            value={formData.adults}
                                            onChange={handleChange}
                                        >
                                            <option value="1">
                                                Adult 1
                                            </option>

                                            <option value="2">
                                                Adult 2
                                            </option>

                                            <option value="3">
                                                Adult 3
                                            </option>
                                        </select>

                                        <label htmlFor="adults">
                                            Select Adult
                                        </label>

                                    </div>
                                </div>

                                {/* Children */}
                                <div className="col-md-6">
                                    <div className="form-floating">

                                        <select
                                            className="form-select"
                                            id="children"
                                            name="children"
                                            value={formData.children}
                                            onChange={handleChange}
                                        >
                                            <option value="0">
                                                No Child
                                            </option>

                                            <option value="1">
                                                Child 1
                                            </option>

                                            <option value="2">
                                                Child 2
                                            </option>

                                            <option value="3">
                                                Child 3
                                            </option>
                                        </select>

                                        <label htmlFor="children">
                                            Select Child
                                        </label>

                                    </div>
                                </div>

                                {/* Payment Method */}
                                <div className="col-md-6">
                                    <div className="form-floating">

                                        <select
                                            className="form-select"
                                            id="paymentMethod"
                                            name="paymentMethod"
                                            value={formData.paymentMethod}
                                            onChange={handleChange}
                                            required
                                        >
                                            <option value="">
                                                Select payment method
                                            </option>

                                            <option value="CASH">
                                                Cash
                                            </option>

                                            <option value="ONLINE">
                                                Online
                                            </option>
                                        </select>

                                        <label
                                            htmlFor="paymentMethod"
                                            className="required-label"
                                        >
                                            Payment Method
                                        </label>

                                    </div>
                                </div>

                                {/* Submit */}
                                <div className="col-md-12">

                                    <button
                                        className="btn btn-primary w-100 py-3"
                                        type="submit"
                                    >
                                        Book Now
                                    </button>

                                </div>

                            </div>
                        </form>

                    </div>

                </div>
                <BookingConfirmPopup
                    show={showConfirm}
                    formData={formData}
                    cartItems={cartItems}
                    cartTotal={cartTotal}
                    loading={bookingLoading}
                    onConfirm={handleConfirmBooking}
                    onCancel={() =>
                        setShowConfirm(false)
                    }
                />

                <ErrorPopup
                    show={showErrorPopup}
                    title="Booking Failed"
                    errors={bookingErrors}
                    onClose={() =>
                        setShowErrorPopup(false)
                    }
                />

                <SuccessPopup
                    show={showSuccessPopup}
                    title="Booking Successful"
                    message="Your booking has been created successfully."
                    onClose={() => {
                        setShowSuccessPopup(false);

                        if (createdBookingId) {
                            navigate(
                                `/bookings/${createdBookingId}`
                            );
                        }
                    }}
                />
            </div>
        </div>
    );
}

export default BookingForm;