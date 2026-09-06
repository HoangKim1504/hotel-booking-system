import { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { searchRoomTypes } from "../../services/roomService";

const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
};

const addDays = (date, days) => {
    const newDate = new Date(date);
    newDate.setDate(newDate.getDate() + days);

    return newDate;
};

function BookingSearch() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();

    const today = new Date();
    const defaultCheckIn = formatDate(today);
    const defaultCheckOut = formatDate(addDays(today, 1));

    const [formData, setFormData] = useState({
        checkIn: searchParams.get("checkInDate") || defaultCheckIn,
        checkOut: searchParams.get("checkOutDate") || defaultCheckOut,
        adults: searchParams.get("adults") || "1",
        children: searchParams.get("children") || "0",
    });

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        const maximumPeople = Number(formData.adults) + Number(formData.children);

        const params = new URLSearchParams({
            checkInDate: formData.checkIn,
            checkOutDate: formData.checkOut,
            adults: formData.adults,
            children: formData.children,
            maximumPeople: maximumPeople,
        });

        navigate(`/rooms?${params}`);
    };

    return (
        <div className="container-fluid booking pb-5 wow fadeIn">
            <div className="container">
                <div
                    className="bg-white shadow"
                    style={{ padding: "35px" }}
                >
                    <form onSubmit={handleSubmit}>
                        <div className="row g-2">

                            <div className="col-md-10">
                                <div className="row g-2">

                                    {/* Check in */}
                                    <div className="col-md-3">
                                        <input
                                            type="date"
                                            name="checkIn"
                                            className="form-control"
                                            value={formData.checkIn}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    {/* Check out */}
                                    <div className="col-md-3">
                                        <input
                                            type="date"
                                            name="checkOut"
                                            className="form-control"
                                            value={formData.checkOut}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    {/* Adult */}
                                    <div className="col-md-3">
                                        <select
                                            name="adults"
                                            className="form-select"
                                            value={formData.adults}
                                            onChange={handleChange}
                                        >
                                            <option value="1">Adult 1</option>
                                            <option value="2">Adult 2</option>
                                            <option value="3">Adult 3</option>
                                            <option value="4">Adult 4</option>
                                        </select>
                                    </div>

                                    {/* Child */}
                                    <div className="col-md-3">
                                        <select
                                            name="children"
                                            className="form-select"
                                            value={formData.children}
                                            onChange={handleChange}
                                        >
                                            <option value="0">No Child</option>
                                            <option value="1">Child 1</option>
                                            <option value="2">Child 2</option>
                                            <option value="3">Child 3</option>
                                        </select>
                                    </div>

                                </div>
                            </div>

                            {/* Search button */}
                            <div className="col-md-2">
                                <button
                                    type="submit"
                                    className="btn btn-primary w-100 h-100"
                                >
                                    Search
                                </button>
                            </div>

                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}

export default BookingSearch;