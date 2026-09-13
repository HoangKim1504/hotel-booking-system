import { Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

function AdminHome() {
    const { username } = useAuth();

    return (
        <div className="admin-home">
            <div className="admin-home-content">

                <div className="admin-home-welcome">
                    <h1>Welcome back, {username}</h1>

                    <p>
                        Manage your hotel system from the admin panel.
                    </p>
                </div>

                <div className="admin-home-menu">

                    <Link
                        to="/admin/users"
                        className="admin-home-card"
                    >
                        <h3>User Management</h3>

                        <p>
                            Manage hotel users and accounts.
                        </p>

                        <span>Open Management →</span>
                    </Link>

                    <Link
                        to="/admin/room-types"
                        className="admin-home-card"
                    >
                        <h3>Room Type Management</h3>

                        <p>
                            Manage room types and room information.
                        </p>

                        <span>Open Management →</span>
                    </Link>

                    <Link
                        to="/admin/rooms"
                        className="admin-home-card"
                    >
                        <h3>Room Management</h3>

                        <p>
                            Manage rooms, floors and room status.
                        </p>

                        <span>Open Management →</span>
                    </Link>

                    <Link
                        to="/admin/bookings"
                        className="admin-home-card"
                    >
                        <h3>Booking Management</h3>

                        <p>
                            Manage customer hotel bookings.
                        </p>

                        <span>Open Management →</span>
                    </Link>

                </div>

            </div>
        </div>
    );
}

export default AdminHome;