import RoomCard from "./RoomCard";
import LoadingSpinner from "../common/LoadingSpinner";
import ErrorPopup from "../common/ErrorPopup";
import { useEffect, useState } from "react";

import room1 from "../../assets/images/room-1.jpg";
import room2 from "../../assets/images/room-2.jpg";
import room3 from "../../assets/images/room-3.jpg";

function RoomList({ limit }) {

    const [apiRoomTypes, setApiRoomTypes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);

    useEffect(() => {
        const params = new URLSearchParams({
            page: 1,
            size: 9
        });

        fetch(`http://localhost:8080/api/room-types?${params}`)
            .then(async (response) => {
                const data = await response.json();

                 if (!response.ok) {
                     const errorMessages = data.errors
                         ? Object.values(data.errors)
                         : [data.message || "Something went wrong"];

                     setErrors(errorMessages);
                     setShowErrorPopup(true);

                     return;
                 }

                return data;
            })
            .then((data) => {
                setApiRoomTypes(data.data);
            })
            .catch((error) => {
                console.error("Error fetching room types:", error);
            })
            .finally(() => {
                setLoading(false);
            });
    }, []);

    const displayedRoomTypes = limit
        ? apiRoomTypes.slice(0, limit)
        : apiRoomTypes;

    return (
        <>
            <LoadingSpinner show={loading} />

            <ErrorPopup
                show={showErrorPopup}
                title="Unable to load rooms"
                errors={errors}
                onClose={() => setShowErrorPopup(false)}
            />

            <div className="container-xxl py-5">
                <div className="container">

                    {/* Title */}
                    <div className="text-center">

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

                        {displayedRoomTypes.map((room) => (
                            <RoomCard
                                key={room.id}
                                room={room}
                            />
                        ))}

                    </div>

                    {displayedRoomTypes.length === 0 && (
                        <div className="text-center">
                            <p>No rooms available.</p>
                        </div>
                    )}

                </div>
            </div>
        </>
    );
}

export default RoomList;