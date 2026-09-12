import PageHeader from "../components/layout/PageHeader";
import BookingSearch from "../components/booking/BookingSearch";
import ContactSection from "../components/contact/ContactSection";
import Newsletter from "../components/common/Newsletter";

function Contact() {
    return (
        <>
            <PageHeader title="Contact" />

            <BookingSearch />

            <ContactSection />

            <Newsletter />
        </>
    );
}

export default Contact;