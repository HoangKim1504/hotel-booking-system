import room1 from "../assets/images/room-1.jpg";
import room2 from "../assets/images/room-2.jpg";
import room3 from "../assets/images/room-3.jpg";
import room4 from "../assets/images/room-4.jpg";
import room5 from "../assets/images/room-5.jpg";
import room6 from "../assets/images/room-6.jpg";
import room7 from "../assets/images/room-7.jpg";
import room8 from "../assets/images/room-8.jpg";
import room9 from "../assets/images/room-9.jpg";
import room10 from "../assets/images/room-10.jpg";

const roomImages = [room1, room2, room3, room4, room5, room6, room7, room8, room9, room10];

export function getRoomImage(id) {


    let hash = 0;

    for (let i = 0; i < id.length; i++) {
        hash += id.charCodeAt(i);
    }

    return roomImages[hash % roomImages.length];
};