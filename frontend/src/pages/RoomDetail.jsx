import { Link, useParams } from "react-router-dom";

import PageHeader from "../components/layout/PageHeader";
import Newsletter from "../components/common/Newsletter";

import room1 from "../assets/images/room-1.jpg";
import room2 from "../assets/images/room-2.jpg";
import room3 from "../assets/images/room-3.jpg";

function RoomDetail() {

    const { id } = useParams();

    // TODO: Replace mock room data with Spring Boot API
    // GET /api/rooms/{id}
    const rooms = [
        {
            id: 1,
            name: "Junior Suite",
            price: 100,
            image: room1,
            rating: 5,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
        {
            id: 2,
            name: "Executive Suite",
            price: 120,
            image: room2,
            rating: 5,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "A comfortable executive suite with modern facilities and spacious accommodation.",
        },
        {
            id: 3,
            name: "Super Deluxe",
            price: 150,
            image: room3,
            rating: 5,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Our premium deluxe room provides a luxurious and relaxing hotel experience.",
        },
    ];

    // TODO: Remove this mock lookup when data is loaded from Spring Boot
    const room = rooms.find(
        (item) => item.id === Number(id)
    );

    if (!room) {
        return (
            <>
                <PageHeader title="Room Detail" />

                <div className="container py-5 text-center">
                    <h2>Room not found</h2>

                    <Link
                        to="/rooms"
                        className="btn btn-primary mt-3"
                    >
                        Back to Rooms
                    </Link>
                </div>
            </>
        );
    }

    return (
        <>
            <PageHeader title={room.name} />

            <div className="container-xxl py-5">
                <div className="container">

                    <div className="row g-5">

                        {/* Room Image */}
                        <div className="col-lg-6">

                            <img
                                src={room.image}
                                alt={room.name}
                                className="img-fluid rounded w-100"
                            />

                        </div>

                        {/* Room Information */}
                        <div className="col-lg-6">

                            <h6 className="section-title text-start text-primary text-uppercase">
                                Room Detail
                            </h6>

                            <h1 className="mb-3">
                                {room.name}
                            </h1>

                            {/* Rating */}
                            <div className="mb-3">

                                {Array.from(
                                    { length: room.rating },
                                    (_, index) => (
                                        <i
                                            key={index}
                                            className="fa fa-star text-primary me-1"
                                        />
                                    )
                                )}

                            </div>

                            {/* Price */}
                            <h4 className="text-primary mb-4">
                                ${room.price} / Night
                            </h4>

                            {/* Facilities */}
                            <div className="d-flex mb-4">

                                <span className="border-end me-3 pe-3">
                                    <i className="fa fa-bed text-primary me-2" />

                                    {room.beds} Bed
                                </span>

                                <span className="border-end me-3 pe-3">
                                    <i className="fa fa-bath text-primary me-2" />

                                    {room.baths} Bath
                                </span>

                                {room.wifi && (
                                    <span>
                                        <i className="fa fa-wifi text-primary me-2" />
                                        Wifi
                                    </span>
                                )}

                            </div>

                            {/* Description */}
                            <p className="mb-4">
                                {room.description}
                            </p>

                            {/* TODO: Add more room information from Spring Boot
                                such as:
                                - room type
                                - capacity
                                - status
                                - amenities
                                - available rooms
                            */}

                            <Link
                                to={`/booking/${room.id}`}
                                className="btn btn-primary py-3 px-5"
                            >
                                Book Now
                            </Link>

                        </div>

                    </div>

                </div>
            </div>

            <Newsletter />
        </>
    );
}

export default RoomDetail;