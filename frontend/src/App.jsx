import { useEffect, useState } from "react";
import { Routes, Route, useLocation } from "react-router-dom";
import { Navigate } from "react-router-dom";

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
import Login from "./pages/Login";
import SignUp from "./pages/SignUp";
import AdminHome from "./pages/admin/AdminHome";
import AdminLayout from "./components/admin/AdminLayout";
import RoomManagement from "./pages/admin/RoomManagement";
import UserManagement from "./pages/admin/UserManagement";
import RoomTypeManagement from "./pages/admin/RoomTypeManagement";
import BookingManagement from "./pages/admin/BookingManagement";
import BookingDetail from "./pages/BookingDetail";
import BookingHistory from "./pages/BookingHistory";
import RevenueStatistics from "./pages/admin/RevenueStatistics";
import AdminRoute from "./components/auth/AdminRoute";

function App() {
    const [loading, setLoading] = useState(true);

    const location = useLocation();

    const isAdminPage = location.pathname.startsWith("/admin");

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
            {!isAdminPage && <Navbar />}

            {/* Pages */}
            <Routes>

                <Route
                    path="/"
                    element={<Home />}
                />

                <Route element={<AdminRoute />}>
                    <Route
                        path="/admin"
                        element={<AdminLayout />}
                    >
                        <Route
                            index
                            element={<AdminHome />}
                        />

                        <Route
                            path="users"
                            element={<UserManagement />}
                        />

                        <Route
                            path="room-types"
                            element={<RoomTypeManagement />}
                        />

                        <Route
                            path="rooms"
                            element={<RoomManagement />}
                        />

                        <Route
                            path="bookings"
                            element={<BookingManagement />}
                        />

                        <Route
                            path="statistics"
                            element={<RevenueStatistics />}
                        />
                    </Route>
                </Route>

                <Route
                    path="/login"
                    element={<Login />}
                />

                <Route
                    path="/signup"
                    element={<SignUp />}
                />

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
                    path="/bookings"
                    element={<BookingHistory />}
                />

                <Route
                    path="/bookings/:bookingId"
                    element={<BookingDetail />}
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
            {!isAdminPage && <Footer />}

            {/* Back to top */}
            <BackToTop />

        </div>
    );
}

export default App;