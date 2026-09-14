import { useEffect, useState } from "react";

const initialFormData = {
    roomTypeName: "",
    roomSize: "",
    facility: "",
    maximumPeople: "",
    price: "",
    status: "",
};

function EditRoomTypePopup({
    show,
    roomType,
    loading,
    errors = [],
    onUpdate,
    onCancel,
}) {
    const [formData, setFormData] =
        useState(initialFormData);

    const [initialData, setInitialData] =
        useState(initialFormData);

    useEffect(() => {
        if (!roomType) {
            return;
        }

        const data = {
            roomTypeName:
                roomType.roomTypeName ?? "",

            roomSize:
                roomType.roomSize ?? "",

            facility:
                roomType.facility ?? "",

            maximumPeople:
                roomType.maximumPeople ?? "",

            price:
                roomType.price ?? "",

            status:
                roomType.status ?? "",
        };

        setFormData(data);
        setInitialData(data);
    }, [roomType]);

    if (!show || !roomType) {
        return null;
    }

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const hasChanges =
        String(formData.roomTypeName) !==
            String(initialData.roomTypeName) ||

        String(formData.roomSize) !==
            String(initialData.roomSize) ||

        String(formData.facility) !==
            String(initialData.facility) ||

        String(formData.maximumPeople) !==
            String(initialData.maximumPeople) ||

        String(formData.price) !==
            String(initialData.price) ||

        String(formData.status) !==
            String(initialData.status);

    const requiredFieldsValid =
        formData.roomTypeName.trim() !== "" &&
        formData.facility.trim() !== "" &&
        formData.maximumPeople !== "" &&
        formData.price !== "" &&
        formData.status !== "";

    const canUpdate =
        hasChanges &&
        requiredFieldsValid;

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!canUpdate || loading) {
            return;
        }

        onUpdate({
            roomTypeName:
                formData.roomTypeName.trim(),

            roomSize:
                formData.roomSize === ""
                    ? null
                    : Number(formData.roomSize),

            facility:
                formData.facility.trim(),

            maximumPeople:
                Number(formData.maximumPeople),

            price:
                Number(formData.price),

            status:
                formData.status,
        });
    };

    return (
        <div className="room-type-popup-overlay">
            <div className="room-type-popup">

                <div className="room-type-popup-header">
                    <div>
                        <h4>Edit Room Type</h4>

                        <p>
                            Update room type information.
                        </p>
                    </div>

                    <button
                        type="button"
                        className="room-type-popup-close"
                        onClick={onCancel}
                        disabled={loading}
                    >
                        ×
                    </button>
                </div>

                {errors.length > 0 && (
                    <div className="room-type-form-errors">
                        <div className="room-type-form-errors-title">
                            Unable to update room type
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

                    <div className="room-type-form-group">
                        <label
                            htmlFor="editRoomTypeName"
                            className="form-label required-label"
                        >
                            Room Type Name
                        </label>

                        <input
                            id="editRoomTypeName"
                            type="text"
                            name="roomTypeName"
                            className="form-control"
                            maxLength="100"
                            value={formData.roomTypeName}
                            onChange={handleChange}
                            disabled={loading}
                            required
                        />
                    </div>

                    <div className="room-type-form-row">

                        <div className="room-type-form-group">
                            <label
                                htmlFor="editRoomSize"
                                className="form-label"
                            >
                                Room Size
                            </label>

                            <input
                                id="editRoomSize"
                                type="number"
                                name="roomSize"
                                className="form-control"
                                min="0.01"
                                max="1000"
                                step="0.01"
                                value={formData.roomSize}
                                onChange={handleChange}
                                disabled={loading}
                            />
                        </div>

                        <div className="room-type-form-group">
                            <label
                                htmlFor="editMaximumPeople"
                                className="form-label required-label"
                            >
                                Maximum People
                            </label>

                            <input
                                id="editMaximumPeople"
                                type="number"
                                name="maximumPeople"
                                className="form-control"
                                min="1"
                                max="10"
                                value={formData.maximumPeople}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                    </div>

                    <div className="room-type-form-row">

                        <div className="room-type-form-group">
                            <label
                                htmlFor="editPrice"
                                className="form-label required-label"
                            >
                                Price
                            </label>

                            <input
                                id="editPrice"
                                type="number"
                                name="price"
                                className="form-control"
                                min="0.01"
                                step="0.01"
                                value={formData.price}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                        <div className="room-type-form-group">
                            <label
                                htmlFor="editRoomTypeStatus"
                                className="form-label required-label"
                            >
                                Status
                            </label>

                            <select
                                id="editRoomTypeStatus"
                                name="status"
                                className="form-select"
                                value={formData.status}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            >
                                <option value="">
                                    Select status
                                </option>

                                <option value="ACTIVE">
                                    Active
                                </option>

                                <option value="INACTIVE">
                                    Inactive
                                </option>
                            </select>
                        </div>

                    </div>

                    <div className="room-type-form-group">
                        <label
                            htmlFor="editFacility"
                            className="form-label required-label"
                        >
                            Facility
                        </label>

                        <textarea
                            id="editFacility"
                            name="facility"
                            className="form-control room-type-facility-input"
                            value={formData.facility}
                            onChange={handleChange}
                            disabled={loading}
                            required
                        />
                    </div>

                    <div className="room-type-popup-actions">
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
                            className="btn btn-primary room-type-submit-btn"
                            disabled={
                                loading ||
                                !canUpdate
                            }
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

export default EditRoomTypePopup;