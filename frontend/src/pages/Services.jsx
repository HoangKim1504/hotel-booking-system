import PageHeader from "../components/layout/PageHeader";
import BookingSearch from "../components/booking/BookingSearch";
import ServiceSection from "../components/service/ServiceSection";
import TestimonialSection from "../components/common/TestimonialSection";
import Newsletter from "../components/common/Newsletter";

function Services() {
    return (
        <>
            <PageHeader title="Services" />

            <BookingSearch />

            <ServiceSection />

            <TestimonialSection />

            <Newsletter />
        </>
    );
}

export default Services;