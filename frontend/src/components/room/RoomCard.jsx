import { Link } from "react-router-dom";

import defaultRoomImage from "../../assets/images/room-1.jpg";

function RoomCard({ room }) {
    return (
        <div className="col-lg-4 col-md-6">
            <div className="room-item shadow rounded overflow-hidden">

                {/* Room Image */}
                <div className="position-relative">

                    <img
                        className="img-fluid"
                        src={defaultRoomImage}
                        alt={room.roomTypeName}
                    />

                    <small className="position-absolute start-0 top-100 translate-middle-y bg-primary text-white rounded py-1 px-3 ms-4">
                        ${room.price}/Night
                    </small>

                </div>

                {/* Room Information */}
                <div className="p-4 mt-2">

                    <div className="d-flex justify-content-between mb-3">

                        <h5 className="mb-0">
                            {room.roomTypeName}
                        </h5>

                    </div>

                    {/* Facilities */}
                     <div className="d-flex mb-3">

                         <small className="border-end me-3 pe-3">
                             <i className="fa fa-expand text-primary me-2" />
                             {room.roomSize} m²
                         </small>

                         <small>
                             <i className="fa fa-users text-primary me-2" />
                             {room.maximumPeople} People
                         </small>

                     </div>

                    {/* Facility */}
                    <p className="text-body mb-3">
                        {room.facility}
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