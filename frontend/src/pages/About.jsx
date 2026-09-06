import PageHeader from "../components/layout/PageHeader";
import BookingSearch from "../components/booking/BookingSearch";
import AboutSection from "../components/about/AboutSection";
import TeamSection from "../components/team/TeamSection";
import Newsletter from "../components/common/Newsletter";

function About() {
    return (
        <>
            <PageHeader title="About Us" />

            <BookingSearch />

            <AboutSection />

            <TeamSection />

            <Newsletter />
        </>
    );
}

export default About;