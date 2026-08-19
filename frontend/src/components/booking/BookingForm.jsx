import { useState } from "react";

import about1 from "../../assets/images/about-1.jpg";
import about2 from "../../assets/images/about-2.jpg";
import about3 from "../../assets/images/about-3.jpg";
import about4 from "../../assets/images/about-4.jpg";

function BookingForm() {
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        checkIn: "",
        checkOut: "",
        adults: "1",
        children: "0",
        roomId: "",
        specialRequest: "",
    });

    // TODO: Replace mock room data with Spring Boot API
    const rooms = [
        {
            id: 1,
            name: "Junior Suite",
        },
        {
            id: 2,
            name: "Executive Suite",
        },
        {
            id: 3,
            name: "Super Deluxe",
        },
    ];

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        console.log("Booking data:", formData);

        // TODO: Send booking data to Spring Boot API
        // Example:
        //
        // axios.post("http://localhost:8080/api/bookings", formData)
        //     .then((response) => {
        //         console.log(response.data);
        //     })
        //     .catch((error) => {
        //         console.error(error);
        //     });
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

                                        <label htmlFor="name">
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

                                        <label htmlFor="email">
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

                                        <label htmlFor="checkIn">
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

                                        <label htmlFor="checkOut">
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

                                {/* Room */}
                                <div className="col-12">
                                    <div className="form-floating">

                                        <select
                                            className="form-select"
                                            id="roomId"
                                            name="roomId"
                                            value={formData.roomId}
                                            onChange={handleChange}
                                            required
                                        >
                                            <option value="">
                                                Select room
                                            </option>

                                            {rooms.map((room) => (
                                                <option
                                                    key={room.id}
                                                    value={room.id}
                                                >
                                                    {room.name}
                                                </option>
                                            ))}
                                        </select>

                                        <label htmlFor="roomId">
                                            Select A Room
                                        </label>

                                    </div>
                                </div>

                                {/* Special Request */}
                                <div className="col-12">
                                    <div className="form-floating">

                                        <textarea
                                            className="form-control"
                                            id="specialRequest"
                                            name="specialRequest"
                                            placeholder="Special Request"
                                            value={formData.specialRequest}
                                            onChange={handleChange}
                                            style={{
                                                height: "100px",
                                            }}
                                        />

                                        <label htmlFor="specialRequest">
                                            Special Request
                                        </label>

                                    </div>
                                </div>

                                {/* Submit */}
                                <div className="col-12">

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
            </div>
        </div>
    );
}

export default BookingForm;