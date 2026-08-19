import { Link } from "react-router-dom";

function RoomCard({ room }) {
    return (
        <div className="col-lg-4 col-md-6">
            <div className="room-item shadow rounded overflow-hidden">

                {/* Room Image */}
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

                {/* Room Information */}
                <div className="p-4 mt-2">

                    <div className="d-flex justify-content-between mb-3">

                        <h5 className="mb-0">
                            {room.name}
                        </h5>

                        <div className="ps-2">

                            {Array.from(
                                { length: room.rating },
                                (_, index) => (
                                    <small
                                        key={index}
                                        className="fa fa-star text-primary"
                                    />
                                )
                            )}

                        </div>

                    </div>

                    {/* Facilities */}
                    <div className="d-flex mb-3">

                        <small className="border-end me-3 pe-3">
                            <i className="fa fa-bed text-primary me-2" />
                            {room.beds} Bed
                        </small>

                        <small className="border-end me-3 pe-3">
                            <i className="fa fa-bath text-primary me-2" />
                            {room.baths} Bath
                        </small>

                        {room.wifi && (
                            <small>
                                <i className="fa fa-wifi text-primary me-2" />
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
                            className="btn btn-sm btn-primary rounded py-2 px-4"
                            to={`/rooms/${room.id}`}
                        >
                            View Detail
                        </Link>

                        <Link
                            className="btn btn-sm btn-dark rounded py-2 px-4"
                            to={`/booking/${room.id}`}
                        >
                            Book Now
                        </Link>

                    </div>

                </div>

            </div>
        </div>
    );
}

export default RoomCard;