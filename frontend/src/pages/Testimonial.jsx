import PageHeader from "../components/layout/PageHeader";
import BookingSearch from "../components/booking/BookingSearch";
import TestimonialSection from "../components/common/TestimonialSection";
import Newsletter from "../components/common/Newsletter";

function Testimonial() {
    return (
        <>
            <PageHeader title="Testimonial" />

            <BookingSearch />

            <TestimonialSection />

            <Newsletter />
        </>
    );
}

export default Testimonial;