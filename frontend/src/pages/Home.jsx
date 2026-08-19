import HeroCarousel from "../components/home/HeroCarousel";
import VideoSection from "../components/home/VideoSection";

import BookingSearch from "../components/booking/BookingSearch";
import AboutSection from "../components/about/AboutSection";
import RoomList from "../components/room/RoomList";
import ServiceSection from "../components/service/ServiceSection";
import TeamSection from "../components/team/TeamSection";

import TestimonialSection from "../components/common/TestimonialSection";
import Newsletter from "../components/common/Newsletter";

function Home() {
    return (
        <>
            <HeroCarousel />

            <BookingSearch />

            <AboutSection />

            <RoomList />

            <VideoSection />

            <ServiceSection />

            <TestimonialSection />

            <TeamSection />

            <Newsletter />
        </>
    );
}

export default Home;