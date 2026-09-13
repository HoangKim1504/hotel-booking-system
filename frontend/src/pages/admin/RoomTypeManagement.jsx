import { useEffect, useState } from "react";

import { useAuth } from "../../context/AuthContext";
import { getErrorMessages } from "../../utils/apiErrorUtils";

import {
    getAdminRoomTypes,
    searchAdminRoomTypes,
    getAdminRoomTypeById,
    createAdminRoomType,
    updateAdminRoomType,
    deleteAdminRoomType,
} from "../../services/admin/adminRoomTypeService";

import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorPopup from "../../components/common/ErrorPopup";
import ConfirmPopup from "../../components/common/ConfirmPopup";
import SuccessPopup from "../../components/common/SuccessPopup";

import CreateRoomTypePopup from "../../components/admin/CreateRoomTypePopup";
import EditRoomTypePopup from "../../components/admin/EditRoomTypePopup";

function RoomTypeManagement() {
    const { token } = useAuth();

    const [roomTypes, setRoomTypes] = useState([]);

    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(10);
    const [totalPages, setTotalPages] = useState(1);
    const [totalRecords, setTotalRecords] = useState(0);

    const [sortOption, setSortOption] = useState("default");
    const [sortBy, setSortBy] = useState("roomTypeName");
    const [order, setOrder] = useState("ASC");

    const [searchForm, setSearchForm] = useState({
        roomTypeName: "",
    });

    const [searchCriteria, setSearchCriteria] = useState({
        roomTypeName: "",
    });

    const [editingRoomType, setEditingRoomType] = useState(null);
    const [showEditPopup, setShowEditPopup] = useState(false);
    const [editLoading, setEditLoading] = useState(false);
    const [editErrors, setEditErrors] = useState([]);

    const [roomTypeToDelete, setRoomTypeToDelete] = useState(null);
    const [deleteLoading, setDeleteLoading] = useState(false);

    const [showCreatePopup, setShowCreatePopup] = useState(false);
    const [createLoading, setCreateLoading] = useState(false);
    const [createErrors, setCreateErrors] = useState([]);

    const [refreshKey, setRefreshKey] = useState(0);

    const [successMessage, setSuccessMessage] = useState("");

    const getStatusClass = (status) => {
        switch (status) {
            case "ACTIVE":
                return "admin-status-active";

            case "INACTIVE":
                return "admin-status-inactive";

            default:
                return "";
        }
    };

    useEffect(() => {
        const loadRoomTypes = async () => {
            setLoading(true);

            try {
                const isSearching =
                    searchCriteria.roomTypeName !== "";

                let data;

                if (isSearching) {
                    data = await searchAdminRoomTypes({
                        roomTypeName: searchCriteria.roomTypeName,
                        page: currentPage,
                        size: pageSize,
                        sortBy,
                        order,
                        token,
                    });
                } else {
                    data = await getAdminRoomTypes({
                        page: currentPage,
                        size: pageSize,
                        sortBy,
                        order,
                        token,
                    });
                }

                setRoomTypes(data.data);
                setTotalPages(data.totalPages);
                setTotalRecords(data.totalRecords);
            } catch (error) {
                setErrors(getErrorMessages(error));
                setShowErrorPopup(true);
            } finally {
                setLoading(false);
            }
        };

        loadRoomTypes();
    }, [
        currentPage,
        pageSize,
        sortBy,
        order,
        token,
        searchCriteria,
        refreshKey,
    ]);

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
        });

        setSortBy("roomTypeName");
        setOrder("ASC");
        setCurrentPage(1);
    };

    const handleSearchReset = () => {
        setSearchForm({
            roomTypeName: "",
        });

        setSearchCriteria({
            roomTypeName: "",
        });

        setSortBy("roomTypeName");
        setOrder("ASC");
        setCurrentPage(1);
    };

    const handleSortChange = (event) => {
        const value = event.target.value;

        setSortOption(value);

        if (value === "default") {
            setSortBy("roomTypeName");
            setOrder("ASC");
            setCurrentPage(1);
            return;
        }

        const [selectedSortBy, selectedOrder] = value.split("-");

        setSortBy(selectedSortBy);
        setOrder(selectedOrder);
        setCurrentPage(1);
    };

    const handleEditRoomType = async (id) => {
        setEditLoading(true);
        setEditErrors([]);

        try {
            const data = await getAdminRoomTypeById({
                id,
                token,
            });

            setEditingRoomType(data);
            setShowEditPopup(true);
        } catch (error) {
            setErrors(getErrorMessages(error));
            setShowErrorPopup(true);
        } finally {
            setEditLoading(false);
        }
    };

    const handleUpdateRoomType = async (roomTypeData) => {
        if (!editingRoomType) {
            return;
        }

        setEditLoading(true);
        setEditErrors([]);

        try {
            await updateAdminRoomType({
                id: editingRoomType.id,
                roomTypeData,
                token,
            });

            setShowEditPopup(false);
            setEditingRoomType(null);
            setEditErrors([]);

            setSuccessMessage(
                `${roomTypeData.roomTypeName} updated successfully.`
            );

            setRefreshKey((prev) => prev + 1);
        } catch (error) {
            setEditErrors(getErrorMessages(error));
        } finally {
            setEditLoading(false);
        }
    };

    const handleCancelEdit = () => {
        setShowEditPopup(false);
        setEditingRoomType(null);
        setEditErrors([]);
    };

    const handleCreateRoomType = async (roomTypeData) => {
        setCreateLoading(true);
        setCreateErrors([]);

        try {
            await createAdminRoomType({
                roomTypeData,
                token,
            });

            setShowCreatePopup(false);
            setCreateErrors([]);

            setSuccessMessage(
                `${roomTypeData.roomTypeName} created successfully.`
            );

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

    const handleDeleteClick = (roomType) => {
        setRoomTypeToDelete(roomType);
    };

    const handleConfirmDelete = async () => {
        if (!roomTypeToDelete) {
            return;
        }

        setDeleteLoading(true);

        const deletedRoomTypeName =
            roomTypeToDelete.roomTypeName;

        try {
            await deleteAdminRoomType({
                id: roomTypeToDelete.id,
                token,
            });

            setRoomTypeToDelete(null);

            setSuccessMessage(
                `${deletedRoomTypeName} deleted successfully.`
            );

            if (roomTypes.length === 1 && currentPage > 1) {
                setCurrentPage((prev) => prev - 1);
            } else {
                setRefreshKey((prev) => prev + 1);
            }
        } catch (error) {
            setRoomTypeToDelete(null);

            setErrors(getErrorMessages(error));
            setShowErrorPopup(true);
        } finally {
            setDeleteLoading(false);
        }
    };

    return (
        <>
            <LoadingSpinner show={loading} />

            <div className="room-type-management">
                <div className="room-type-management-content">

                    <div className="room-type-management-card">

                        <div className="room-type-management-card-header">
                            <div>
                                <h4>Room Type Management</h4>

                                <span className="admin-total-records">
                                    Total: {totalRecords} room types
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
                                Create Room Type
                            </button>
                        </div>

                        <div className="admin-room-type-toolbar">

                            <form
                                className="admin-room-type-search"
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

                            <div className="admin-room-type-sort">
                                <label
                                    htmlFor="adminRoomTypeSort"
                                    className="admin-room-sort-label"
                                >
                                    Sort by
                                </label>

                                <select
                                    id="adminRoomTypeSort"
                                    className="admin-room-sort-select"
                                    value={`${sortBy}-${order}`}
                                    onChange={handleSortChange}
                                >
                                    <option value="default">
                                        Default
                                    </option>

                                    <option value="roomTypeName-ASC">
                                        Room Type Name: A - Z
                                    </option>

                                    <option value="roomTypeName-DESC">
                                        Room Type Name: Z - A
                                    </option>

                                    <option value="roomSize-ASC">
                                        Room Size: Low to High
                                    </option>

                                    <option value="roomSize-DESC">
                                        Room Size: High to Low
                                    </option>

                                    <option value="maximumPeople-ASC">
                                        Maximum People: Low to High
                                    </option>

                                    <option value="maximumPeople-DESC">
                                        Maximum People: High to Low
                                    </option>

                                    <option value="price-ASC">
                                        Price: Low to High
                                    </option>

                                    <option value="price-DESC">
                                        Price: High to Low
                                    </option>

                                    <option value="status-ASC">
                                        Status: A - Z
                                    </option>

                                    <option value="status-DESC">
                                        Status: Z - A
                                    </option>
                                </select>
                            </div>

                        </div>

                        <div className="table-responsive admin-room-type-table-wrapper">
                            <table className="table align-middle admin-room-type-table">

                                <colgroup>
                                    <col className="admin-col-room-type-name" />
                                    <col className="admin-col-room-size" />
                                    <col className="admin-col-maximum-people" />
                                    <col className="admin-col-room-type-price" />
                                    <col className="admin-col-room-type-status" />
                                    <col className="admin-col-room-type-actions" />
                                </colgroup>

                                <thead>
                                    <tr>
                                        <th>
                                            Room Type Name
                                        </th>

                                        <th className="text-center">
                                            Room Size
                                        </th>

                                        <th className="text-center">
                                            Maximum People
                                        </th>

                                        <th className="text-center">
                                            Price
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
                                    {roomTypes.map((roomType) => (
                                        <tr key={roomType.id}>

                                            <td className="admin-room-type-name">
                                                {roomType.roomTypeName}
                                            </td>

                                            <td className="text-center">
                                                {roomType.roomSize != null
                                                    ? `${roomType.roomSize} m²`
                                                    : "-"}
                                            </td>

                                            <td className="text-center">
                                                {roomType.maximumPeople}
                                            </td>

                                            <td className="text-center">
                                                {roomType.price != null
                                                    ? `$${Number(roomType.price).toLocaleString()}`
                                                    : "-"}
                                            </td>

                                            <td className="text-center">
                                                <span
                                                    className={`admin-status-badge ${getStatusClass(
                                                        roomType.status
                                                    )}`}
                                                >
                                                    {roomType.status}
                                                </span>
                                            </td>

                                            <td>
                                                <div className="admin-room-actions">

                                                    <button
                                                        type="button"
                                                        className="btn btn-sm btn-outline-primary"
                                                        onClick={() =>
                                                            handleEditRoomType(
                                                                roomType.id
                                                            )
                                                        }
                                                    >
                                                        Edit
                                                    </button>

                                                    <button
                                                        type="button"
                                                        className="btn btn-sm btn-outline-danger"
                                                        onClick={() =>
                                                            handleDeleteClick(
                                                                roomType
                                                            )
                                                        }
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

                        {roomTypes.length === 0 && !loading && (
                            <div className="text-center py-4 text-muted">
                                No room types found.
                            </div>
                        )}

                        {totalPages > 1 && (
                            <div className="admin-pagination">

                                <button
                                    type="button"
                                    className="btn btn-sm btn-outline-secondary"
                                    disabled={currentPage === 1}
                                    onClick={() =>
                                        handlePageChange(
                                            currentPage - 1
                                        )
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
                                    disabled={
                                        currentPage === totalPages
                                    }
                                    onClick={() =>
                                        handlePageChange(
                                            currentPage + 1
                                        )
                                    }
                                >
                                    Next
                                </button>

                            </div>
                        )}

                    </div>
                </div>
            </div>

            <CreateRoomTypePopup
                show={showCreatePopup}
                loading={createLoading}
                errors={createErrors}
                onCreate={handleCreateRoomType}
                onCancel={handleCancelCreate}
            />

            <EditRoomTypePopup
                show={showEditPopup}
                roomType={editingRoomType}
                loading={editLoading}
                errors={editErrors}
                onUpdate={handleUpdateRoomType}
                onCancel={handleCancelEdit}
            />

            <ConfirmPopup
                show={roomTypeToDelete !== null}
                title="Confirm Delete"
                message={
                    roomTypeToDelete
                        ? `Are you sure you want to delete ${roomTypeToDelete.roomTypeName}?`
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
                onCancel={() => setRoomTypeToDelete(null)}
            />

            <SuccessPopup
                show={Boolean(successMessage)}
                title="Success"
                message={successMessage}
                onClose={() => setSuccessMessage("")}
            />

            <ErrorPopup
                show={showErrorPopup}
                title="Unable to Process Room Type"
                errors={errors}
                onClose={() => setShowErrorPopup(false)}
            />
        </>
    );
}

export default RoomTypeManagement;