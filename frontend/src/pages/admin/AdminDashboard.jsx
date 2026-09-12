import { useEffect, useState } from "react";

import { useAuth } from "../../context/AuthContext";
import { getErrorMessages } from "../../utils/apiErrorUtils";
import {
    getAdminRooms,
    searchAdminRooms,
    getAdminRoomById,
    updateAdminRoom,
    deleteAdminRoom,
    createAdminRoom,
} from "../../services/adminRoomService";

import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorPopup from "../../components/common/ErrorPopup";
import EditRoomPopup from "../../components/admin/EditRoomPopup";
import ConfirmPopup from "../../components/common/ConfirmPopup";
import CreateRoomPopup from "../../components/admin/CreateRoomPopup";

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

    const [editingRoom, setEditingRoom] = useState(null);
    const [showEditPopup, setShowEditPopup] = useState(false);
    const [editLoading, setEditLoading] = useState(false);
    const [editErrors, setEditErrors] = useState([]);
    const [refreshKey, setRefreshKey] = useState(0);

    const [roomToDelete, setRoomToDelete] = useState(null);
    const [deleteLoading, setDeleteLoading] = useState(false);

    const [showCreatePopup, setShowCreatePopup] = useState(false);
    const [createLoading, setCreateLoading] = useState(false);
    const [createErrors, setCreateErrors] = useState([]);

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
    }, [currentPage, pageSize, sortBy, order, token, searchCriteria, refreshKey]);

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

    const handleEditRoom = async (id) => {
        setEditLoading(true);
        setEditErrors([]);

        try {
            const data = await getAdminRoomById({
                id,
                token,
            });

            setEditingRoom(data);
            setShowEditPopup(true);
        } catch (error) {
            setErrors(getErrorMessages(error));
            setShowErrorPopup(true);
        } finally {
            setEditLoading(false);
        }
    };

   const handleUpdateRoom = async (roomData) => {
       if (!editingRoom) {
           return;
       }

       setEditLoading(true);
       setEditErrors([]);

       try {
           await updateAdminRoom({
               id: editingRoom.id,
               roomData,
               token,
           });

           setShowEditPopup(false);
           setEditingRoom(null);
           setEditErrors([]);

           // Reload current table
           setRefreshKey((prev) => prev + 1);
       } catch (error) {
           setEditErrors(getErrorMessages(error));
       } finally {
           setEditLoading(false);
       }
   };

    const handleCancelEdit = () => {
        setShowEditPopup(false);
        setEditingRoom(null);
        setEditErrors([]);
    };

    const handleDeleteClick = (room) => {
        setRoomToDelete(room);
    };

    const handleConfirmDelete = async () => {
        if (!roomToDelete) {
            return;
        }

        setDeleteLoading(true);

        try {
            await deleteAdminRoom({
                id: roomToDelete.id,
                token,
            });

            setRoomToDelete(null);

            /*
             * Nếu xóa record cuối cùng của page hiện tại,
             * quay về page trước.
             */
            if (rooms.length === 1 && currentPage > 1) {
                setCurrentPage((prev) => prev - 1);
            } else {
                // Reload lại page hiện tại
                setRefreshKey((prev) => prev + 1);
            }
        } catch (error) {
            // Đóng confirm trước để ErrorPopup không bị che
            setRoomToDelete(null);

            setErrors(getErrorMessages(error));
            setShowErrorPopup(true);
        } finally {
            setDeleteLoading(false);
        }
    };

    const handleCreateRoom = async (roomData) => {
        setCreateLoading(true);
        setCreateErrors([]);

        try {
            await createAdminRoom({
                roomData,
                token,
            });

            setShowCreatePopup(false);
            setCreateErrors([]);

            setRefreshKey((prev) => prev + 1);
        } catch (error) {
            setCreateErrors(getErrorMessages(error));
        } finally {
            setCreateLoading(false);
        }
    };

    const handleCancelCreate = () => {
        setShowCreatePopup(false);
        setCreateErrors([]);
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

                            <button
                                type="button"
                                className="btn btn-primary admin-create-room-btn"
                                onClick={() => {
                                    setCreateErrors([]);
                                    setShowCreatePopup(true);
                                }}
                            >
                                Create Room
                            </button>
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
                                        <th className="text-center">
                                            Floor
                                        </th>
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

                                            <td className="text-center">
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
                                                        className="btn btn-sm btn-outline-primary"
                                                        onClick={() => handleEditRoom(room.id)}
                                                    >
                                                        Edit
                                                    </button>

                                                     <button
                                                         type="button"
                                                         className="btn btn-sm btn-outline-danger"
                                                         onClick={() => handleDeleteClick(room)}
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
            <CreateRoomPopup
                show={showCreatePopup}
                loading={createLoading}
                errors={createErrors}
                onCreate={handleCreateRoom}
                onCancel={handleCancelCreate}
            />
            <EditRoomPopup
                show={showEditPopup}
                room={editingRoom}
                loading={editLoading}
                errors={editErrors}
                onUpdate={handleUpdateRoom}
                onCancel={handleCancelEdit}
            />
            <ConfirmPopup
                show={roomToDelete !== null}
                title="Confirm Delete"
                message={
                    roomToDelete
                        ? `Are you sure you want to delete room ${roomToDelete.roomNumber}?`
                        : ""
                }
                confirmText={
                    deleteLoading
                        ? "Deleting..."
                        : "Delete"
                }
                cancelText="Cancel"
                loading={deleteLoading}
                onConfirm={handleConfirmDelete}
                onCancel={() => setRoomToDelete(null)}
            />
            <ErrorPopup
                show={showErrorPopup}
                title="Unable to Process Room"
                errors={errors}
                onClose={() => setShowErrorPopup(false)}
            />
        </>
    );
}


export default AdminDashboard;