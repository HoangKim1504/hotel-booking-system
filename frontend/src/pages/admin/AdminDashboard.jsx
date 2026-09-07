import { useEffect, useState } from "react";

import { useAuth } from "../../context/AuthContext";
import { getAdminRooms } from "../../services/adminRoomService";
import { getErrorMessages } from "../../utils/apiErrorUtils";

import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorPopup from "../../components/common/ErrorPopup";

function AdminDashboard() {
    const { token } = useAuth();

    const [rooms, setRooms] = useState([]);

    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(1);
    const [totalRecords, setTotalRecords] = useState(0);

    const [sortBy] = useState("");
    const [order] = useState("ASC");

    useEffect(() => {
        const loadRooms = async () => {
            setLoading(true);

            try {
                const data = await getAdminRooms({
                    page: currentPage,
                    size: pageSize,
                    sortBy,
                    order,
                    token,
                });

                setRooms(data.data);
                setTotalPages(data.totalPages);
                setTotalRecords(data.totalRecords);
            } catch (error) {
                setErrors(getErrorMessages(error));
                setShowErrorPopup(true);
            } finally {
                setLoading(false);
            }
        };

        loadRooms();
    }, [currentPage, pageSize, sortBy, order, token]);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    return (
        <>
            <LoadingSpinner show={loading} />

            <div className="admin-dashboard">
                <div className="admin-dashboard-content">

                    <div className="admin-dashboard-header">
                        <div>
                            <h2>Hotel Management</h2>
                            <p>Manage all rooms in the hotel.</p>
                        </div>
                    </div>

                    {/* Room Management Card */}
                    <div className="admin-dashboard-card">

                        <div className="admin-dashboard-card-header">
                            <div>
                                <h4>Room Management</h4>

                                <span className="admin-total-records">
                                    Total: {totalRecords} rooms
                                </span>
                            </div>
                        </div>

                        <div className="table-responsive">
                            <table className="table align-middle admin-room-table">

                                <colgroup>
                                    <col className="admin-col-room-number" />
                                    <col className="admin-col-room-type" />
                                    <col className="admin-col-floor" />
                                    <col className="admin-col-status" />
                                    <col className="admin-col-actions" />
                                </colgroup>

                                <thead>
                                    <tr>
                                        <th>Room Number</th>
                                        <th>Room Type</th>
                                        <th>Floor</th>
                                        <th className="text-center">
                                            Status
                                        </th>
                                        <th className="text-center">
                                            Actions
                                        </th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {rooms.map((room) => (
                                        <tr key={room.id}>

                                            <td className="admin-room-number">
                                                {room.roomNumber}
                                            </td>

                                            <td>
                                                {room.roomTypeName}
                                            </td>

                                            <td>
                                                {room.floorNumber}
                                            </td>

                                            <td className="text-center">
                                                <span
                                                    className={`admin-status-badge ${
                                                        room.status === "ACTIVE"
                                                            ? "admin-status-active"
                                                            : "admin-status-maintenance"
                                                    }`}
                                                >
                                                    {room.status}
                                                </span>
                                            </td>

                                            <td>
                                                <div className="admin-room-actions">

                                                    <button
                                                        type="button"
                                                        className="btn btn-sm btn-outline-secondary"
                                                    >
                                                        View
                                                    </button>

                                                    <button
                                                        type="button"
                                                        className="btn btn-sm btn-outline-primary"
                                                    >
                                                        Edit
                                                    </button>

                                                    <button
                                                        type="button"
                                                        className="btn btn-sm btn-outline-danger"
                                                    >
                                                        Delete
                                                    </button>

                                                </div>
                                            </td>

                                        </tr>
                                    ))}
                                </tbody>

                            </table>
                        </div>

                        {rooms.length === 0 && !loading && (
                            <div className="text-center py-4 text-muted">
                                No rooms found.
                            </div>
                        )}

                        {/* Pagination */}
                        {totalPages > 1 && (
                            <div className="admin-pagination">
                                <button
                                    type="button"
                                    className="btn btn-sm btn-outline-secondary"
                                    disabled={currentPage === 1}
                                    onClick={() =>
                                        handlePageChange(currentPage - 1)
                                    }
                                >
                                    Previous
                                </button>

                                <span>
                                    Page {currentPage} of {totalPages}
                                </span>

                                <button
                                    type="button"
                                    className="btn btn-sm btn-outline-secondary"
                                    disabled={currentPage === totalPages}
                                    onClick={() =>
                                        handlePageChange(currentPage + 1)
                                    }
                                >
                                    Next
                                </button>
                            </div>
                        )}
                    </div>
                </div>
            </div>
            <ErrorPopup
                show={showErrorPopup}
                title="Unable to Load Rooms"
                errors={errors}
                onClose={() => setShowErrorPopup(false)}
            />
        </>
    );
}


export default AdminDashboard;