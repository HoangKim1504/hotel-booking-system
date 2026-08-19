import RoomCard from "./RoomCard";

import room1 from "../../assets/images/room-1.jpg";
import room2 from "../../assets/images/room-2.jpg";
import room3 from "../../assets/images/room-3.jpg";

function RoomList({ limit }) {

    // TODO: Replace mock room data with Spring Boot API
    // Example:
    // GET /api/rooms
    //
    // Search available rooms:
    // GET /api/rooms/available?checkIn=...&checkOut=...

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
            price: 100,
            image: room2,
            rating: 5,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
        {
            id: 3,
            name: "Super Deluxe",
            price: 100,
            image: room3,
            rating: 5,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
        {
            id: 4,
            name: "Super Deluxe",
            price: 100,
            image: room3,
            rating: 5,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
        {
            id: 5,
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
            id: 6,
            name: "Executive Suite",
            price: 100,
            image: room2,
            rating: 5,
            beds: 3,
            baths: 2,
            wifi: true,
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
    ];

    const displayedRooms = limit
        ? rooms.slice(0, limit)
        : rooms;

    return (
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

                    {displayedRooms.map((room) => (
                        <RoomCard
                            key={room.id}
                            room={room}
                        />
                    ))}

                </div>

                {displayedRooms.length === 0 && (
                    <div className="text-center">
                        <p>No rooms available.</p>
                    </div>
                )}

            </div>
        </div>
    );
}

export default RoomList;