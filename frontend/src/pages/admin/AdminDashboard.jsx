import { useEffect, useState } from "react";

import { useAuth } from "../../context/AuthContext";
import { getErrorMessages } from "../../utils/apiErrorUtils";
import { getAdminRooms, searchAdminRooms } from "../../services/adminRoomService";

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

    const [sortBy, setSortBy] = useState("");
    const [order, setOrder] = useState("ASC");

    const [searchKeyword, setSearchKeyword] = useState("");

    const [searchForm, setSearchForm] = useState({
        roomTypeName: "",
        roomNumber: "",
        roomStatus: "",
    });

    const [searchCriteria, setSearchCriteria] = useState({
        roomTypeName: "",
        roomNumber: "",
        roomStatus: "",
    });

    useEffect(() => {
        const loadRooms = async () => {
            setLoading(true);

            try {
            const isSearching =
                searchCriteria.roomTypeName ||
                searchCriteria.roomNumber ||
                searchCriteria.roomStatus;

                let data;

                if (isSearching) {
                    data = await searchAdminRooms({
                        roomTypeName: searchCriteria.roomTypeName,
                        roomNumber: searchCriteria.roomNumber,
                        roomStatus: searchCriteria.roomStatus,
                        page: currentPage,
                        size: pageSize,
                        sortBy,
                        order,
                        token,
                    });
                } else {
                    data = await getAdminRooms({
                        page: currentPage,
                        size: pageSize,
                        sortBy,
                        order,
                        token,
                    });
                }

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
    }, [currentPage, pageSize, sortBy, order, token, searchCriteria]);

    const handlePageChange = (page) => {
        setCurrentPage(page);
    };

    const handleSearchChange = (event) => {
        const { name, value } = event.target;

        setSearchForm((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSearchSubmit = (event) => {
        event.preventDefault();

        setSearchCriteria({
            roomTypeName: searchForm.roomTypeName.trim(),
            roomNumber: searchForm.roomNumber,
            roomStatus: searchForm.roomStatus,
        });

        setSortBy("");
        setOrder("ASC");
        setCurrentPage(1);
    };

    const handleSearchReset = () => {
        setSearchForm({
            roomTypeName: "",
            roomNumber: "",
            roomStatus: ""
        });

        setSearchCriteria({
            roomTypeName: "",
            roomNumber: "",
            roomStatus: ""
        });

        setSortBy("");
        setOrder("ASC");
        setCurrentPage(1);
    };

    const handleSortChange = (event) => {
        const value = event.target.value;

        if (!value) {
            setSortBy("");
            setOrder("ASC");
            setCurrentPage(1);
            return;
        }

        const [selectedSortBy, selectedOrder] = value.split("-");

        setSortBy(selectedSortBy);
        setOrder(selectedOrder);
        setCurrentPage(1);
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

                        {/* Search and Sort */}
                        <div className="admin-room-toolbar">

                            <form
                                className="admin-room-search"
                                onSubmit={handleSearchSubmit}
                            >
                                <input
                                    type="text"
                                    name="roomTypeName"
                                    className="form-control"
                                    placeholder="Room type name"
                                    value={searchForm.roomTypeName}
                                    onChange={handleSearchChange}
                                />

                                <input
                                    type="number"
                                    name="roomNumber"
                                    className="form-control admin-room-number-search"
                                    placeholder="Room number"
                                    min="100"
                                    max="999"
                                    value={searchForm.roomNumber}
                                    onChange={handleSearchChange}
                                />

                                <select
                                    name="roomStatus"
                                    className="form-select admin-room-status-search"
                                    value={searchForm.roomStatus}
                                    onChange={handleSearchChange}
                                >
                                    <option value="">
                                        All Status
                                    </option>

                                    <option value="ACTIVE">
                                        Active
                                    </option>

                                    <option value="MAINTENANCE">
                                        Maintenance
                                    </option>

                                    <option value="OUT_OF_SERVICE">
                                        Out of Service
                                    </option>
                                </select>

                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                >
                                    Search
                                </button>

                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={handleSearchReset}
                                >
                                    Reset
                                </button>
                            </form>

                            <div className="admin-room-sort">
                                <label
                                    htmlFor="adminRoomSort"
                                    className="admin-room-sort-label"
                                >
                                    Sort by
                                </label>

                                <select
                                    id="adminRoomSort"
                                    className="admin-room-sort-select"
                                    value={
                                        sortBy
                                            ? `${sortBy}-${order}`
                                            : ""
                                    }
                                    onChange={handleSortChange}
                                >
                                    <option value="">
                                        Default
                                    </option>

                                    <option value="roomTypeName-ASC">
                                        Room Type Name: A - Z
                                    </option>

                                    <option value="roomTypeName-DESC">
                                        Room Type Name: Z - A
                                    </option>

                                    <option value="roomNumber-ASC">
                                        Room Number: Low to High
                                    </option>

                                    <option value="roomNumber-DESC">
                                        Room Number: High to Low
                                    </option>

                                    <option value="floorNumber-ASC">
                                        Floor: Low to High
                                    </option>

                                    <option value="floorNumber-DESC">
                                        Floor: High to Low
                                    </option>

                                </select>
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