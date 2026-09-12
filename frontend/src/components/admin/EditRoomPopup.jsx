import { useEffect, useState } from "react";

function EditRoomPopup({
    show,
    room,
    loading,
    errors = [],
    onUpdate,
    onCancel,
}) {
    const [formData, setFormData] = useState({
        roomTypeName: "",
        roomNumber: "",
        floorNumber: "",
        status: "",
    });

    const [initialData, setInitialData] = useState(null);

    useEffect(() => {
        if (!room) {
            return;
        }

        const roomData = {
            roomTypeName: room.roomTypeName || "",
            roomNumber: room.roomNumber || "",
            floorNumber: room.floorNumber || "",
            status: room.status || "",
        };

        setFormData(roomData);
        setInitialData(roomData);
    }, [room]);

    if (!show || !room) {
        return null;
    }

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        onUpdate({
            roomTypeName: formData.roomTypeName.trim(),
            roomNumber: Number(formData.roomNumber),
            floorNumber: Number(formData.floorNumber),
            status: formData.status,
        });
    };

    const hasChanges =
        initialData &&
        (
            formData.roomTypeName !== initialData.roomTypeName ||
            String(formData.roomNumber) !== String(initialData.roomNumber) ||
            String(formData.floorNumber) !== String(initialData.floorNumber) ||
            formData.status !== initialData.status
        );

    return (
        <div
            className="edit-room-popup-overlay"
            onClick={onCancel}
        >
            <div
                className="edit-room-popup"
                onClick={(event) => event.stopPropagation()}
            >
                <div className="edit-room-popup-header">
                    <div>
                        <h4>Edit Room</h4>
                        <span>
                            Update room information
                        </span>
                    </div>

                    <button
                        type="button"
                        className="edit-room-popup-close"
                        onClick={onCancel}
                    >
                        ×
                    </button>
                </div>

                <form
                    className="edit-room-popup-body"
                    onSubmit={handleSubmit}
                >
                    {errors.length > 0 && (
                        <div className="edit-room-errors">
                            <div className="edit-room-errors-title">
                                Unable to update room
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

                    <div className="mb-3">
                        <label
                            htmlFor="editRoomTypeName"
                            className="form-label required-label"
                        >
                            Room Type
                        </label>

                        <input
                            type="text"
                            id="editRoomTypeName"
                            name="roomTypeName"
                            className="form-control"
                            value={formData.roomTypeName}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="row g-3">
                        <div className="col-md-6">
                            <label
                                htmlFor="editRoomNumber"
                                className="form-label required-label"
                            >
                                Room Number
                            </label>

                            <input
                                type="number"
                                id="editRoomNumber"
                                name="roomNumber"
                                className="form-control"
                                min="100"
                                max="999"
                                value={formData.roomNumber}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="col-md-6">
                            <label
                                htmlFor="editFloorNumber"
                                className="form-label required-label"
                            >
                                Floor Number
                            </label>

                            <input
                                type="number"
                                id="editFloorNumber"
                                name="floorNumber"
                                className="form-control"
                                min="1"
                                max="9"
                                value={formData.floorNumber}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="mt-3">
                        <label
                            htmlFor="editRoomStatus"
                            className="form-label required-label"
                        >
                            Status
                        </label>

                        <select
                            id="editRoomStatus"
                            name="status"
                            className="form-select"
                            value={formData.status}
                            onChange={handleChange}
                            required
                        >
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
                    </div>

                    <div className="edit-room-popup-actions">
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
                            className="btn btn-primary edit-room-update-btn"
                            disabled={loading || !hasChanges}
                        >
                            {loading ? "UPDATING..." : "UPDATE"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default EditRoomPopup;