import PageHeader from "../components/layout/PageHeader";
import BookingSearch from "../components/booking/BookingSearch";
import BookingForm from "../components/booking/BookingForm";
import Newsletter from "../components/home/Newsletter";

function Booking() {
    return (
        <>
            <PageHeader title="Booking" />

            <BookingSearch />

            <BookingForm />

            <Newsletter />
        </>
    );
}

export default Booking;