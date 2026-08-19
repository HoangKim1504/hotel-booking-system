import PageHeader from "../components/layout/PageHeader";
import BookingSearch from "../components/booking/BookingSearch";
import RoomList from "../components/room/RoomList";
import TestimonialSection from "../components/common/TestimonialSection";
import Newsletter from "../components/common/Newsletter";

function Rooms() {
    return (
        <>
            <PageHeader title="Rooms" />

            <BookingSearch />

            <RoomList />

            <TestimonialSection />

            <Newsletter />
        </>
    );
}

export default Rooms;