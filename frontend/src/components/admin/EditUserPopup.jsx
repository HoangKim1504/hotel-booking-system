import { useEffect, useState } from "react";

const initialFormData = {
    username: "",
    password: "",
    fullName: "",
    gender: "",
    dateOfBirth: "",
    email: "",
    phoneNumber: "",
    address: "",
    enabled: true,
};

function EditUserPopup({
    show,
    user,
    loading,
    errors = [],
    onUpdate,
    onCancel,
}) {
    const [formData, setFormData] = useState(initialFormData);
    const [initialData, setInitialData] = useState(initialFormData);

    useEffect(() => {
        if (!user) {
            return;
        }

        const data = {
            username: user.username ?? "",
            password: "",
            fullName: user.fullName ?? "",
            gender: user.gender ?? "",
            dateOfBirth: user.dateOfBirth ?? "",
            email: user.email ?? "",
            phoneNumber: user.phoneNumber ?? "",
            address: user.address ?? "",
            enabled: user.enabled ?? true,
        };

        setFormData(data);
        setInitialData(data);
    }, [user]);

    if (!show || !user) {
        return null;
    }

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]:
                name === "enabled"
                    ? value === "true"
                    : value,
        }));
    };

    const hasChanges =
        formData.password !== "" ||

        formData.fullName !== initialData.fullName ||

        formData.gender !== initialData.gender ||

        formData.dateOfBirth !== initialData.dateOfBirth ||

        formData.email !== initialData.email ||

        formData.phoneNumber !== initialData.phoneNumber ||

        formData.address !== initialData.address ||

        formData.enabled !== initialData.enabled;

    const requiredFieldsValid =
        formData.password !== "" &&
        formData.fullName.trim() !== "" &&
        formData.gender !== "" &&
        formData.email.trim() !== "";

    const canUpdate =
        hasChanges &&
        requiredFieldsValid;

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!canUpdate || loading) {
            return;
        }

        onUpdate({
            password: formData.password,

            fullName:
                formData.fullName.trim(),

            gender:
                formData.gender,

            dateOfBirth:
                formData.dateOfBirth === ""
                    ? null
                    : formData.dateOfBirth,

            email:
                formData.email.trim(),

            phoneNumber:
                formData.phoneNumber.trim() === ""
                    ? null
                    : formData.phoneNumber.trim(),

            address:
                formData.address.trim() === ""
                    ? null
                    : formData.address.trim(),

            enabled:
                formData.enabled,
        });
    };

    return (
        <div className="user-popup-overlay">
            <div className="user-popup">

                <div className="user-popup-header">
                    <div>
                        <h4>Edit User</h4>

                        <p>
                            Update user information.
                        </p>
                    </div>

                    <button
                        type="button"
                        className="user-popup-close"
                        onClick={onCancel}
                        disabled={loading}
                    >
                        ×
                    </button>
                </div>

                {errors.length > 0 && (
                    <div className="user-form-errors">
                        <div className="user-form-errors-title">
                            Unable to update user
                        </div>

                        <ul>
                            {errors.map((error, index) => (
                                <li key={index}>
                                    {error}
                                </li>
                            ))}
                        </ul>
                    </div>
                )}

                <form onSubmit={handleSubmit}>

                    <div className="user-form-row">

                        <div className="user-form-group">
                            <label
                                htmlFor="editUsername"
                                className="form-label"
                            >
                                Username
                            </label>

                            <input
                                id="editUsername"
                                type="text"
                                className="form-control"
                                value={formData.username}
                                disabled
                            />
                        </div>

                        <div className="user-form-group">
                            <label
                                htmlFor="editPassword"
                                className="form-label"
                            >
                                New Password
                            </label>

                            <input
                                id="editPassword"
                                type="password"
                                name="password"
                                className="form-control"
                                placeholder="Enter new password"
                                value={formData.password}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                    </div>

                    <div className="user-form-row">

                        <div className="user-form-group">
                            <label
                                htmlFor="editFullName"
                                className="form-label required-label"
                            >
                                Full Name
                            </label>

                            <input
                                id="editFullName"
                                type="text"
                                name="fullName"
                                className="form-control"
                                value={formData.fullName}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                        <div className="user-form-group">
                            <label
                                htmlFor="editGender"
                                className="form-label required-label"
                            >
                                Gender
                            </label>

                            <select
                                id="editGender"
                                name="gender"
                                className="form-select"
                                value={formData.gender}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            >
                                <option value="">
                                    Select gender
                                </option>

                                <option value="MALE">
                                    Male
                                </option>

                                <option value="FEMALE">
                                    Female
                                </option>

                                <option value="OTHER">
                                    Other
                                </option>
                            </select>
                        </div>

                    </div>

                    <div className="user-form-row">

                        <div className="user-form-group">
                            <label
                                htmlFor="editDateOfBirth"
                                className="form-label"
                            >
                                Date of Birth
                            </label>

                            <input
                                id="editDateOfBirth"
                                type="date"
                                name="dateOfBirth"
                                className="form-control"
                                value={formData.dateOfBirth}
                                onChange={handleChange}
                                disabled={loading}
                            />
                        </div>

                        <div className="user-form-group">
                            <label
                                htmlFor="editPhoneNumber"
                                className="form-label"
                            >
                                Phone Number
                            </label>

                            <input
                                id="editPhoneNumber"
                                type="text"
                                name="phoneNumber"
                                className="form-control"
                                value={formData.phoneNumber}
                                onChange={handleChange}
                                disabled={loading}
                            />
                        </div>

                    </div>

                    <div className="user-form-row">

                        <div className="user-form-group">
                            <label
                                htmlFor="editEmail"
                                className="form-label required-label"
                            >
                                Email
                            </label>

                            <input
                                id="editEmail"
                                type="email"
                                name="email"
                                className="form-control"
                                value={formData.email}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                        <div className="user-form-group">
                            <label
                                htmlFor="editEnabled"
                                className="form-label required-label"
                            >
                                Status
                            </label>

                            <select
                                id="editEnabled"
                                name="enabled"
                                className="form-select"
                                value={String(formData.enabled)}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            >
                                <option value="true">
                                    Enabled
                                </option>

                                <option value="false">
                                    Disabled
                                </option>
                            </select>
                        </div>

                    </div>

                    <div className="user-form-group">
                        <label
                            htmlFor="editAddress"
                            className="form-label"
                        >
                            Address
                        </label>

                        <input
                            id="editAddress"
                            type="text"
                            name="address"
                            className="form-control"
                            value={formData.address}
                            onChange={handleChange}
                            disabled={loading}
                        />
                    </div>

                    <div className="user-popup-actions">
                        <button
                            type="button"
                            className="btn btn-outline-secondary"
                            onClick={onCancel}
                            disabled={loading}
                        >
                            Cancel
                        </button>

                        <button
                            type="submit"
                            className="btn btn-primary user-submit-btn"
                            disabled={loading || !canUpdate}
                        >
                            {loading
                                ? "Updating..."
                                : "Update"}
                        </button>
                    </div>

                </form>
            </div>
        </div>
    );
}

export default EditUserPopup;