import { useState } from "react";

function BookingSearch() {
    const [formData, setFormData] = useState({
        checkIn: "",
        checkOut: "",
        adults: "1",
        children: "0",
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

        console.log("Search room:", formData);

        // TODO: Call Spring Boot room search API
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