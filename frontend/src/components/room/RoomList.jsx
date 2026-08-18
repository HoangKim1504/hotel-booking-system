import { Link } from "react-router-dom";

import room1 from "../../assets/images/room-1.jpg";
import room2 from "../../assets/images/room-2.jpg";
import room3 from "../../assets/images/room-3.jpg";

function RoomList() {

    // TODO: Replace mock room data with Spring Boot API
    const rooms = [
        {
            id: 1,
            name: "Junior Suite",
            price: 100,
            image: room1,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
            delay: "0.1s",
        },
        {
            id: 2,
            name: "Executive Suite",
            price: 100,
            image: room2,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
            delay: "0.3s",
        },
        {
            id: 3,
            name: "Super Deluxe",
            price: 100,
            image: room3,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
            delay: "0.6s",
        },
        {
            id: 4,
            name: "Super Deluxe",
            price: 100,
            image: room3,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
            delay: "0.6s",
        },
        {
            id: 5,
            name: "Junior Suite",
            price: 100,
            image: room1,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
            delay: "0.1s",
        },
        {
            id: 6,
            name: "Executive Suite",
            price: 100,
            image: room2,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
            delay: "0.3s",
        },
    ];

    return (
        <div className="container-xxl py-5">
            <div className="container">

                {/* Title */}
                <div
                    className="text-center wow fadeInUp"
                    data-wow-delay="0.1s"
                >
                    <h6 className="section-title text-center text-primary text-uppercase">
                        Our Rooms
                    </h6>

                    <h1 className="mb-5">
                        Explore Our{" "}
                        <span className="text-primary text-uppercase">
                            Rooms
                        </span>
                    </h1>
                </div>

                {/* Room List */}
                <div className="row g-4">
                    {rooms.map((room) => (
                        <div
                            key={room.id}
                            className="col-lg-4 col-md-6 wow fadeInUp"
                            data-wow-delay={room.delay}
                        >
                            <div className="room-item shadow rounded overflow-hidden">

                                {/* Image + Price */}
                                <div className="position-relative">
                                    <img
                                        className="img-fluid"
                                        src={room.image}
                                        alt={room.name}
                                    />

                                    <small className="position-absolute start-0 top-100 translate-middle-y bg-primary text-white rounded py-1 px-3 ms-4">
                                        ${room.price}/Night
                                    </small>
                                </div>

                                <div className="p-4 mt-2">

                                    {/* Name + Rating */}
                                    <div className="d-flex justify-content-between mb-3">
                                        <h5 className="mb-0">
                                            {room.name}
                                        </h5>

                                        <div className="ps-2">
                                            {[1, 2, 3, 4, 5].map((star) => (
                                                <small
                                                    key={star}
                                                    className="fa fa-star text-primary"
                                                ></small>
                                            ))}
                                        </div>
                                    </div>

                                    {/* Room Information */}
                                    <div className="d-flex mb-3">

                                        <small className="border-end me-3 pe-3">
                                            <i className="fa fa-bed text-primary me-2"></i>
                                            {room.beds} Bed
                                        </small>

                                        <small className="border-end me-3 pe-3">
                                            <i className="fa fa-bath text-primary me-2"></i>
                                            {room.baths} Bath
                                        </small>

                                        {room.wifi && (
                                            <small>
                                                <i className="fa fa-wifi text-primary me-2"></i>
                                                Wifi
                                            </small>
                                        )}

                                    </div>

                                    {/* Description */}
                                    <p className="text-body mb-3">
                                        {room.description}
                                    </p>

                                    {/* Buttons */}
                                    <div className="d-flex justify-content-between">

                                        <Link
                                            to={`/rooms/${room.id}`}
                                            className="btn btn-sm btn-primary rounded py-2 px-4"
                                        >
                                            View Detail
                                        </Link>

                                        <Link
                                            to={`/booking/${room.id}`}
                                            className="btn btn-sm btn-dark rounded py-2 px-4"
                                        >
                                            Book Now
                                        </Link>

                                    </div>

                                </div>
                            </div>
                        </div>
                    ))}
                </div>

            </div>
        </div>
    );
}

export default RoomList;