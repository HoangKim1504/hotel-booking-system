import { useEffect, useMemo, useState } from "react";

import { useAuth } from "../../context/AuthContext";
import { getErrorMessages } from "../../utils/apiErrorUtils";

import {
    getAdminUsers,
    getAdminUserById,
    createAdminUser,
    updateAdminUser,
    deleteAdminUser,
} from "../../services/admin/adminUserService";

import LoadingSpinner from "../../components/common/LoadingSpinner";
import ErrorPopup from "../../components/common/ErrorPopup";
import ConfirmPopup from "../../components/common/ConfirmPopup";
import SuccessPopup from "../../components/common/SuccessPopup";
import CreateUserPopup from "../../components/admin/CreateUserPopup";
import EditUserPopup from "../../components/admin/EditUserPopup";

function UserManagement() {
    const { token } = useAuth();

    const [users, setUsers] = useState([]);

    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);

    const [searchForm, setSearchForm] = useState("");
    const [searchKeyword, setSearchKeyword] = useState("");

    const [sortOption, setSortOption] = useState("default");

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(10);

    const [userToDelete, setUserToDelete] = useState(null);
    const [deleteLoading, setDeleteLoading] = useState(false);

    const [refreshKey, setRefreshKey] = useState(0);

    const [successMessage, setSuccessMessage] = useState("");

    const [showCreatePopup, setShowCreatePopup] = useState(false);
    const [createLoading, setCreateLoading] = useState(false);
    const [createErrors, setCreateErrors] = useState([]);

    const [editingUser, setEditingUser] = useState(null);
    const [showEditPopup, setShowEditPopup] = useState(false);
    const [editLoading, setEditLoading] = useState(false);
    const [editErrors, setEditErrors] = useState([]);

    useEffect(() => {
        const loadUsers = async () => {
            setLoading(true);

            try {
                const data = await getAdminUsers({
                    token,
                });

                setUsers(data);
            } catch (error) {
                setErrors(getErrorMessages(error));
                setShowErrorPopup(true);
            } finally {
                setLoading(false);
            }
        };

        loadUsers();
    }, [token, refreshKey]);

    const filteredAndSortedUsers = useMemo(() => {
        let result = [...users];

        if (searchKeyword) {
            const keyword =
                searchKeyword.toLowerCase();

            result = result.filter((user) =>
                user.username
                    ?.toLowerCase()
                    .includes(keyword) ||

                user.fullName
                    ?.toLowerCase()
                    .includes(keyword) ||

                user.email
                    ?.toLowerCase()
                    .includes(keyword) ||

                user.phoneNumber
                    ?.toLowerCase()
                    .includes(keyword)
            );
        }

        switch (sortOption) {
            case "username-ASC":
                result.sort((a, b) =>
                    a.username.localeCompare(b.username)
                );
                break;

            case "username-DESC":
                result.sort((a, b) =>
                    b.username.localeCompare(a.username)
                );
                break;

            case "fullName-ASC":
                result.sort((a, b) =>
                    a.fullName.localeCompare(b.fullName)
                );
                break;

            case "fullName-DESC":
                result.sort((a, b) =>
                    b.fullName.localeCompare(a.fullName)
                );
                break;

            case "email-ASC":
                result.sort((a, b) =>
                    a.email.localeCompare(b.email)
                );
                break;

            case "email-DESC":
                result.sort((a, b) =>
                    b.email.localeCompare(a.email)
                );
                break;

            case "enabled-ASC":
                result.sort(
                    (a, b) =>
                        Number(a.enabled) -
                        Number(b.enabled)
                );
                break;

            case "enabled-DESC":
                result.sort(
                    (a, b) =>
                        Number(b.enabled) -
                        Number(a.enabled)
                );
                break;

            default:
                result.sort((a, b) =>
                    a.username.localeCompare(b.username)
                );
        }

        return result;
    }, [
        users,
        searchKeyword,
        sortOption,
    ]);

    const totalRecords =
        filteredAndSortedUsers.length;

    const totalPages = Math.max(
        1,
        Math.ceil(totalRecords / pageSize)
    );

    const paginatedUsers =
        filteredAndSortedUsers.slice(
            (currentPage - 1) * pageSize,
            currentPage * pageSize
        );

    const handleSearchSubmit = (event) => {
        event.preventDefault();

        setSearchKeyword(searchForm.trim());
        setSortOption("default");
        setCurrentPage(1);
    };

    const handleSearchReset = () => {
        setSearchForm("");
        setSearchKeyword("");
        setSortOption("default");
        setCurrentPage(1);
    };

    const handleSortChange = (event) => {
        setSortOption(event.target.value);
        setCurrentPage(1);
    };

    const handleDeleteClick = (user) => {
        setUserToDelete(user);
    };

    const handleConfirmDelete = async () => {
        if (!userToDelete) {
            return;
        }

        setDeleteLoading(true);

        const deletedUsername =
            userToDelete.username;

        try {
            await deleteAdminUser({
                id: userToDelete.id,
                token,
            });

            setUserToDelete(null);

            setSuccessMessage(
                `User ${deletedUsername} deleted successfully.`
            );

            if (
                paginatedUsers.length === 1 &&
                currentPage > 1
            ) {
                setCurrentPage(
                    (prev) => prev - 1
                );
            }

            setRefreshKey(
                (prev) => prev + 1
            );
        } catch (error) {
            setUserToDelete(null);

            setErrors(
                getErrorMessages(error)
            );

            setShowErrorPopup(true);
        } finally {
            setDeleteLoading(false);
        }
    };

    const handleCreateUser = async (userData) => {
        setCreateLoading(true);
        setCreateErrors([]);

        try {
            await createAdminUser({
                userData,
                token,
            });

            setShowCreatePopup(false);
            setCreateErrors([]);

            setSuccessMessage(
                `User ${userData.username} created successfully.`
            );

            setRefreshKey((prev) => prev + 1);
        } catch (error) {
            setCreateErrors(
                getErrorMessages(error)
            );
        } finally {
            setCreateLoading(false);
        }
    };

    const handleCancelCreate = () => {
        setShowCreatePopup(false);
        setCreateErrors([]);
    };

    const handleEditUser = async (id) => {
        setEditLoading(true);
        setEditErrors([]);

        try {
            const data = await getAdminUserById({
                id,
                token,
            });

            setEditingUser(data);
            setShowEditPopup(true);
        } catch (error) {
            setErrors(
                getErrorMessages(error)
            );

            setShowErrorPopup(true);
        } finally {
            setEditLoading(false);
        }
    };

    const handleUpdateUser = async (userData) => {
        if (!editingUser) {
            return;
        }

        setEditLoading(true);
        setEditErrors([]);

        try {
            await updateAdminUser({
                id: editingUser.id,
                userData,
                token,
            });

            const updatedUsername =
                editingUser.username;

            setShowEditPopup(false);
            setEditingUser(null);
            setEditErrors([]);

            setSuccessMessage(
                `User ${updatedUsername} updated successfully.`
            );

            setRefreshKey((prev) => prev + 1);
        } catch (error) {
            setEditErrors(
                getErrorMessages(error)
            );
        } finally {
            setEditLoading(false);
        }
    };

    const handleCancelEdit = () => {
        setShowEditPopup(false);
        setEditingUser(null);
        setEditErrors([]);
    };

    const getStatusClass = (enabled) => {
        return enabled
            ? "admin-status-active"
            : "admin-status-inactive";
    };

    return (
        <>
            <LoadingSpinner show={loading} />

            <div className="user-management">
                <div className="user-management-content">

                    <div className="user-management-card">

                        <div className="user-management-card-header">
                            <div>
                                <h4>User Management</h4>

                                <span className="admin-total-records">
                                    Total: {totalRecords} users
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
                                Create User
                            </button>
                        </div>

                        <div className="admin-user-toolbar">

                            <form
                                className="admin-user-search"
                                onSubmit={handleSearchSubmit}
                            >
                                <input
                                    type="text"
                                    className="form-control"
                                    placeholder="Username, name, email or phone"
                                    value={searchForm}
                                    onChange={(event) =>
                                        setSearchForm(
                                            event.target.value
                                        )
                                    }
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

                            <div className="admin-user-sort">
                                <label
                                    htmlFor="adminUserSort"
                                    className="admin-room-sort-label"
                                >
                                    Sort by
                                </label>

                                <select
                                    id="adminUserSort"
                                    className="admin-room-sort-select"
                                    value={sortOption}
                                    onChange={handleSortChange}
                                >
                                    <option value="default">
                                        Default
                                    </option>

                                    <option value="username-ASC">
                                        Username: A - Z
                                    </option>

                                    <option value="username-DESC">
                                        Username: Z - A
                                    </option>

                                    <option value="fullName-ASC">
                                        Full Name: A - Z
                                    </option>

                                    <option value="fullName-DESC">
                                        Full Name: Z - A
                                    </option>

                                    <option value="email-ASC">
                                        Email: A - Z
                                    </option>

                                    <option value="email-DESC">
                                        Email: Z - A
                                    </option>

                                    <option value="enabled-DESC">
                                        Status: Enabled First
                                    </option>

                                    <option value="enabled-ASC">
                                        Status: Disabled First
                                    </option>
                                </select>
                            </div>

                        </div>

                        <div className="table-responsive admin-user-table-wrapper">
                            <table className="table align-middle admin-user-table">

                                <thead>
                                    <tr>
                                        <th>Username</th>
                                        <th>Full Name</th>

                                        <th className="text-center">
                                            Gender
                                        </th>

                                        <th>Email</th>

                                        <th className="text-center">
                                            Status
                                        </th>

                                        <th className="text-center">
                                            Roles
                                        </th>

                                        <th className="text-center">
                                            Actions
                                        </th>
                                    </tr>
                                </thead>

                                <tbody>
                                    {paginatedUsers.map((user) => (
                                        <tr key={user.id}>

                                            <td className="admin-user-username">
                                                {user.username}
                                            </td>

                                            <td>
                                                {user.fullName}
                                            </td>

                                            <td className="text-center">
                                                {user.gender}
                                            </td>

                                            <td>
                                                {user.email}
                                            </td>

                                            <td className="text-center">
                                                <span
                                                    className={`admin-status-badge ${getStatusClass(
                                                        user.enabled
                                                    )}`}
                                                >
                                                    {user.enabled
                                                        ? "ENABLED"
                                                        : "DISABLED"}
                                                </span>
                                            </td>

                                            <td className="text-center">
                                                {user.roles?.join(", ") || "-"}
                                            </td>

                                            <td>
                                                <div className="admin-room-actions">

                                                    <button
                                                        type="button"
                                                        className="btn btn-sm btn-outline-primary"
                                                        onClick={() =>
                                                            handleEditUser(user.id)
                                                        }
                                                    >
                                                        Edit
                                                    </button>

                                                    <button
                                                        type="button"
                                                        className="btn btn-sm btn-outline-danger"
                                                        onClick={() =>
                                                            handleDeleteClick(
                                                                user
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

                        {paginatedUsers.length === 0 && !loading && (
                            <div className="text-center py-4 text-muted">
                                No users found.
                            </div>
                        )}

                        {totalPages > 1 && (
                            <div className="admin-pagination">

                                <button
                                    type="button"
                                    className="btn btn-sm btn-outline-secondary"
                                    disabled={currentPage === 1}
                                    onClick={() =>
                                        setCurrentPage(
                                            (prev) => prev - 1
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
                                        setCurrentPage(
                                            (prev) => prev + 1
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

            <ConfirmPopup
                show={userToDelete !== null}
                title="Confirm Delete"
                message={
                    userToDelete
                        ? `Are you sure you want to delete user ${userToDelete.username}?`
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
                onCancel={() =>
                    setUserToDelete(null)
                }
            />

            <SuccessPopup
                show={Boolean(successMessage)}
                title="Success"
                message={successMessage}
                onClose={() =>
                    setSuccessMessage("")
                }
            />

            <ErrorPopup
                show={showErrorPopup}
                title="Unable to Process User"
                errors={errors}
                onClose={() =>
                    setShowErrorPopup(false)
                }
            />

            <CreateUserPopup
                show={showCreatePopup}
                loading={createLoading}
                errors={createErrors}
                onCreate={handleCreateUser}
                onCancel={handleCancelCreate}
            />

            <EditUserPopup
                show={showEditPopup}
                user={editingUser}
                loading={editLoading}
                errors={editErrors}
                onUpdate={handleUpdateUser}
                onCancel={handleCancelEdit}
            />
        </>
    );
}

export default UserManagement;