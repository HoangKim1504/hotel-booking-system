import HeroCarousel from "../components/home/HeroCarousel";
import BookingSearch from "../components/booking/BookingSearch";
import AboutSection from "../components/home/AboutSection";
import RoomList from "../components/room/RoomList";
import VideoSection from "../components/home/VideoSection";
import ServiceSection from "../components/home/ServiceSection";
import Testimonial from "../components/home/Testimonial";
import TeamSection from "../components/home/TeamSection";
import Newsletter from "../components/home/Newsletter";

function Home() {
    return (
        <>
            <HeroCarousel />

            <BookingSearch />

            <AboutSection />

            <RoomList />

            <VideoSection />

            <ServiceSection />

            <Testimonial />

            <TeamSection />

            <Newsletter />
        </>
    );
}

export default Home;