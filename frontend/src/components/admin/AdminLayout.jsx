import { useEffect, useState } from "react";
import { Outlet, useLocation } from "react-router-dom";

import AdminSidebar from "./AdminSidebar";

function AdminLayout() {
    const [isSidebarOpen, setIsSidebarOpen] = useState(true);
    const [isReady, setIsReady] = useState(false);

    const location = useLocation();

    useEffect(() => {
        const frame = requestAnimationFrame(() => {
            setIsReady(true);
        });

        return () => cancelAnimationFrame(frame);
    }, []);

    const isNoScrollAdminPage =
        location.pathname === "/admin/rooms" ||
        location.pathname === "/admin/room-types" ||
        location.pathname === "/admin/users" ||
        location.pathname === "/admin/bookings";

    return (
        <div
            className={`admin-layout ${
                isSidebarOpen
                    ? "sidebar-open"
                    : "sidebar-closed"
            } ${isReady ? "admin-layout-ready" : ""}`}
        >
            <AdminSidebar
                isOpen={isSidebarOpen}
                onClose={() => setIsSidebarOpen(false)}
            />

            <main
                className={`admin-main-content ${
                        isNoScrollAdminPage
                            ? "admin-no-scroll"
                            : ""
                }`}
            >
                {!isSidebarOpen && (
                    <button
                        type="button"
                        className="admin-open-sidebar-btn"
                        onClick={() => setIsSidebarOpen(true)}
                    >
                        ☰ Menu
                    </button>
                )}

                <Outlet />
            </main>
        </div>
    );
}

export default AdminLayout;