import { useState } from "react";
import { Outlet, useLocation } from "react-router-dom";

import AdminSidebar from "./AdminSidebar";

function AdminLayout() {
    const [isSidebarOpen, setIsSidebarOpen] = useState(true);

    const location = useLocation();

    const isRoomManagementPage = location.pathname === "/admin/rooms";

    return (
        <div
            className={`admin-layout ${
                isSidebarOpen ? "sidebar-open" : "sidebar-closed"
            }`}
        >
            <AdminSidebar
                isOpen={isSidebarOpen}
                onClose={() => setIsSidebarOpen(false)}
            />

            <main
                className={`admin-main-content ${
                    isRoomManagementPage
                        ? "room-management-no-scroll"
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