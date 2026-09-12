import { useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";

import { getRoomTypeById } from "../services/roomService";
import { getErrorMessages } from "../utils/apiErrorUtils";
import { getRoomImage } from "../utils/roomImageUtils";

import LoadingSpinner from "../components/common/LoadingSpinner";
import ErrorPopup from "../components/common/ErrorPopup";
import PageHeader from "../components/layout/PageHeader";
import Newsletter from "../components/common/Newsletter";

function RoomDetail() {

    const { id } = useParams();
    const location = useLocation();

    // Có URL trước đó → quay lại đúng URL search
    const backToRooms = location.state?.from || "/rooms";

    const [room, setRoom] = useState(null);
    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);

    useEffect(() => {
        const loadRoomType = async () => {
            setLoading(true);

            try {
                const data = await getRoomTypeById(id);

                console.log("Room type detail:", data);

                setRoom(data);
            } catch (error) {
                console.error("Error fetching room type:", error);

                setErrors(getErrorMessages(error));
                setShowErrorPopup(true);
            } finally {
                setLoading(false);
            }
        };

        loadRoomType();
    }, [id]);

    if (loading) {
        return <LoadingSpinner />;
    }

    if (!room) {
        return (
            <>
                <PageHeader
                    title="Room Detail"
                    noBottomMargin
                />

                <div className="room-not-found-section">
                    <div className="room-not-found">

                        <div className="room-not-found-icon">
                            ?
                        </div>

                        <h2>Room Not Found</h2>

                        <p>
                            The room you are looking for does not exist
                            or is no longer available.
                        </p>

                        <Link
                            to={backToRooms}
                            className="btn btn-primary py-2 px-4"
                        >
                            Back to Rooms
                        </Link>

                    </div>
                </div>
            </>
        );
    }

    const roomImage = getRoomImage(room.id);

    return (
        <>
            <PageHeader title={room.roomTypeName} />

            <div className="container-xxl py-5">
                <div className="container">

                    <div className="row g-5">

                        {/* Room Image */}
                        <div className="col-lg-6">

                            <img
                                src={roomImage}
                                alt={room.roomTypeName}
                                className="room-detail-image"
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

                            {/* Price */}
                            <h4 className="text-primary mb-4">
                                ${room.price} / Night
                            </h4>

                            {/* Facilities */}
                            <div className="d-flex mb-4">

                                <span className="border-end me-3 pe-3">
                                    <i className="fa fa-expand text-primary me-2" />
                                    {room.roomSize} m²
                                </span>

                                <span>
                                    <i className="fa fa-users text-primary me-2" />
                                    {room.maximumPeople} People
                                </span>

                            </div>

                            {/* Description */}
                            <p className="mb-4">
                                {room.facility}
                            </p>

                            <div className="d-flex gap-3">
                                <Link
                                    to={backToRooms}
                                    className="btn btn-outline-secondary py-3 px-4"
                                >
                                    Back to Rooms
                                </Link>

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
            </div>

            <Newsletter />
        </>
    );
}

export default RoomDetail;