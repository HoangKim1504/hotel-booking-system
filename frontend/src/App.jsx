import { useEffect, useState } from "react";
import { Routes, Route } from "react-router-dom";

import LoadingSpinner from "./components/common/LoadingSpinner";
import Navbar from "./components/layout/Navbar";
import Footer from "./components/layout/Footer";
import BackToTop from "./components/layout/BackToTop";
import Booking from "./pages/Booking";
import Services from "./pages/Services";
import About from "./pages/About";
import Contact from "./pages/Contact";
import Team from "./pages/Team";
import Testimonial from "./pages/Testimonial";
import Rooms from "./pages/Rooms";
import RoomDetail from "./pages/RoomDetail";
import Home from "./pages/Home";

function App() {
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // Temporary loading effect
        // TODO: Replace with real loading state when calling Spring Boot API
        const timer = setTimeout(() => {
            setLoading(false);
        }, 1000);

        return () => clearTimeout(timer);
    }, []);

    return (
        <div className="container-fluid bg-white p-0">

            {/* Loading */}
            <LoadingSpinner show={loading} />

            {/* Header */}
            <Navbar />

            {/* Pages */}
            <Routes>

                <Route
                    path="/"
                    element={<Home />}
                />

                {/* TODO: Add routes when pages are created */}

                <Route
                    path="/rooms"
                    element={<Rooms />}
                />

                <Route
                    path="/rooms/:id"
                    element={<RoomDetail />}
                />

                <Route
                    path="/booking"
                    element={<Booking />}
                />

                <Route
                    path="/booking/:roomId"
                    element={<Booking />}
                />

                <Route
                    path="/about"
                    element={<About />}
                />

                <Route
                    path="/services"
                    element={<Services />}
                />

                <Route
                    path="/team"
                    element={<Team />}
                />

                <Route
                    path="/testimonial"
                    element={<Testimonial />}
                />

                <Route
                    path="/contact"
                    element={<Contact />}
                />

            </Routes>

            {/* Footer */}
            <Footer />

            {/* Back to top */}
            <BackToTop />

        </div>
    );
}

export default App;