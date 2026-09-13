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
};

function CreateUserPopup({
    show,
    loading,
    errors = [],
    onCreate,
    onCancel,
}) {
    const [formData, setFormData] = useState(initialFormData);

    useEffect(() => {
        if (!show) {
            setFormData(initialFormData);
        }
    }, [show]);

    if (!show) {
        return null;
    }

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const canCreate =
        formData.username.trim() !== "" &&
        formData.password !== "" &&
        formData.fullName.trim() !== "" &&
        formData.gender !== "" &&
        formData.dateOfBirth !== "" &&
        formData.email.trim() !== "" &&
        formData.phoneNumber.trim() !== "";

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!canCreate || loading) {
            return;
        }

        onCreate({
            username: formData.username.trim(),
            password: formData.password,
            fullName: formData.fullName.trim(),
            gender: formData.gender,
            dateOfBirth: formData.dateOfBirth,
            email: formData.email.trim(),
            phoneNumber: formData.phoneNumber.trim(),
            address:
                formData.address.trim() === ""
                    ? null
                    : formData.address.trim(),
        });
    };

    return (
        <div className="user-popup-overlay">
            <div className="user-popup">

                <div className="user-popup-header">
                    <div>
                        <h4>Create User</h4>

                        <p>
                            Add a new user to the system.
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
                            Unable to create user
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
                                htmlFor="createUsername"
                                className="form-label required-label"
                            >
                                Username
                            </label>

                            <input
                                id="createUsername"
                                type="text"
                                name="username"
                                className="form-control"
                                placeholder="Enter username"
                                value={formData.username}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                        <div className="user-form-group">
                            <label
                                htmlFor="createPassword"
                                className="form-label required-label"
                            >
                                Password
                            </label>

                            <input
                                id="createPassword"
                                type="password"
                                name="password"
                                className="form-control"
                                placeholder="Enter password"
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
                                htmlFor="createFullName"
                                className="form-label required-label"
                            >
                                Full Name
                            </label>

                            <input
                                id="createFullName"
                                type="text"
                                name="fullName"
                                className="form-control"
                                placeholder="Enter full name"
                                value={formData.fullName}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                        <div className="user-form-group">
                            <label
                                htmlFor="createGender"
                                className="form-label required-label"
                            >
                                Gender
                            </label>

                            <select
                                id="createGender"
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
                                htmlFor="createDateOfBirth"
                                className="form-label required-label"
                            >
                                Date of Birth
                            </label>

                            <input
                                id="createDateOfBirth"
                                type="date"
                                name="dateOfBirth"
                                className="form-control"
                                value={formData.dateOfBirth}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                        <div className="user-form-group">
                            <label
                                htmlFor="createPhoneNumber"
                                className="form-label required-label"
                            >
                                Phone Number
                            </label>

                            <input
                                id="createPhoneNumber"
                                type="text"
                                name="phoneNumber"
                                className="form-control"
                                placeholder="Example: 0912345678"
                                value={formData.phoneNumber}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                    </div>

                    <div className="user-form-group">
                        <label
                            htmlFor="createEmail"
                            className="form-label required-label"
                        >
                            Email
                        </label>

                        <input
                            id="createEmail"
                            type="email"
                            name="email"
                            className="form-control"
                            placeholder="Enter email"
                            value={formData.email}
                            onChange={handleChange}
                            disabled={loading}
                            required
                        />
                    </div>

                    <div className="user-form-group">
                        <label
                            htmlFor="createAddress"
                            className="form-label"
                        >
                            Address
                        </label>

                        <input
                            id="createAddress"
                            type="text"
                            name="address"
                            className="form-control"
                            placeholder="Enter address"
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
                            disabled={loading || !canCreate}
                        >
                            {loading
                                ? "Creating..."
                                : "Create"}
                        </button>
                    </div>

                </form>
            </div>
        </div>
    );
}

export default CreateUserPopup;