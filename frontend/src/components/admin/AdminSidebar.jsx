import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";

import { useAuth } from "../../context/AuthContext";
import ConfirmPopup from "../common/ConfirmPopup";

function AdminSidebar({
    isOpen,
    onClose,
}) {
    const navigate = useNavigate();

    const {
        username,
        logout,
    } = useAuth();

    const [showLogoutConfirm, setShowLogoutConfirm] =
        useState(false);

    const handleConfirmLogout = () => {
        logout();
        setShowLogoutConfirm(false);
        navigate("/");
        window.scrollTo({
            top: 0,
            left: 0,
            behavior: "auto",
        });
    };

    return (
        <>
            <aside
                className={`admin-sidebar ${
                    isOpen ? "open" : "closed"
                }`}
            >
                {/* Header */}
                <div className="admin-sidebar-header">
                    <div className="admin-sidebar-logo">
                        <h3>HOTELIER</h3>
                        <span>ADMIN PANEL</span>
                    </div>

                    <button
                        type="button"
                        className="admin-sidebar-toggle-btn"
                        onClick={onClose}
                        aria-label="Close sidebar"
                    >
                        ×
                    </button>
                </div>

                {/* Welcome */}
                <div className="admin-sidebar-user">
                    <span className="admin-sidebar-welcome-label">
                        Welcome,
                    </span>

                    <span className="admin-sidebar-welcome-name">
                        {username}
                    </span>
                </div>

                {/* Navigation */}
                <nav className="admin-sidebar-nav">
                    <NavLink
                        to="/admin/users"
                        className={({ isActive }) =>
                            `admin-sidebar-link ${
                                isActive ? "active" : ""
                            }`
                        }
                    >
                        User Management
                    </NavLink>

                    <NavLink
                        to="/admin/room-types"
                        className={({ isActive }) =>
                            `admin-sidebar-link ${
                                isActive ? "active" : ""
                            }`
                        }
                    >
                        Room Type Management
                    </NavLink>

                    <NavLink
                        to="/admin/rooms"
                        className={({ isActive }) =>
                            `admin-sidebar-link ${
                                isActive ? "active" : ""
                            }`
                        }
                    >
                        Room Management
                    </NavLink>

                    <NavLink
                        to="/admin/bookings"
                        className={({ isActive }) =>
                            `admin-sidebar-link ${
                                isActive ? "active" : ""
                            }`
                        }
                    >
                        Booking Management
                    </NavLink>
                </nav>

                {/* Footer buttons */}
                <div className="admin-sidebar-footer">
                    <button
                        type="button"
                        className="admin-view-site-btn"
                        onClick={() => navigate("/rooms")}
                    >
                        View Rooms
                    </button>

                    <button
                        type="button"
                        className="admin-logout-btn"
                        onClick={() => setShowLogoutConfirm(true)}
                    >
                        Logout
                    </button>
                </div>
            </aside>

            <ConfirmPopup
                show={showLogoutConfirm}
                title="Confirm Logout"
                message="Are you sure you want to log out?"
                confirmText="Logout"
                cancelText="Cancel"
                onConfirm={handleConfirmLogout}
                onCancel={() => setShowLogoutConfirm(false)}
            />
        </>
    );
}

export default AdminSidebar;