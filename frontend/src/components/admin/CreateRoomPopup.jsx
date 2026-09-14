import { useEffect, useState } from "react";

const initialFormData = {
    roomTypeName: "",
    roomNumber: "",
    floorNumber: "",
};

function CreateRoomPopup({
    show,
    roomTypes = [],
    roomTypesLoading,
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

    const handleSubmit = (event) => {
        event.preventDefault();

        onCreate({
            roomTypeName: formData.roomTypeName.trim(),
            roomNumber: Number(formData.roomNumber),
            floorNumber: Number(formData.floorNumber),
        });
    };

    const canCreate =
        formData.roomTypeName.trim() !== "" &&
        formData.roomNumber !== "" &&
        formData.floorNumber !== "";

    return (
        <div
            className="create-room-popup-overlay"
            onClick={onCancel}
        >
            <div
                className="create-room-popup"
                onClick={(event) => event.stopPropagation()}
            >
                <div className="create-room-popup-header">
                    <div>
                        <h4>Create Room</h4>
                        <span>
                            Add a new room to the hotel.
                        </span>
                    </div>

                    <button
                        type="button"
                        className="create-room-popup-close"
                        onClick={onCancel}
                    >
                        ×
                    </button>
                </div>

                <form
                    className="create-room-popup-body"
                    onSubmit={handleSubmit}
                >
                    {errors.length > 0 && (
                        <div className="create-room-errors">
                            <div className="create-room-errors-title">
                                Unable to create room
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
                            htmlFor="createRoomTypeName"
                            className="form-label required-label"
                        >
                            Room Type
                        </label>

                        <select
                            id="createRoomTypeName"
                            name="roomTypeName"
                            className="form-select"
                            value={formData.roomTypeName}
                            onChange={handleChange}
                            disabled={roomTypesLoading}
                            required
                        >
                            <option value="">
                                {roomTypesLoading
                                    ? "Loading room types..."
                                    : "Select room type"}
                            </option>

                            {roomTypes.map((roomType) => (
                                <option
                                    key={roomType.id}
                                    value={roomType.roomTypeName}
                                >
                                    {roomType.roomTypeName}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="row g-3">
                        <div className="col-md-6">
                            <label
                                htmlFor="createRoomNumber"
                                className="form-label required-label"
                            >
                                Room Number
                            </label>

                            <input
                                type="number"
                                id="createRoomNumber"
                                name="roomNumber"
                                className="form-control"
                                placeholder="Example: 107"
                                min="100"
                                max="999"
                                value={formData.roomNumber}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="col-md-6">
                            <label
                                htmlFor="createFloorNumber"
                                className="form-label required-label"
                            >
                                Floor Number
                            </label>

                            <input
                                type="number"
                                id="createFloorNumber"
                                name="floorNumber"
                                className="form-control"
                                placeholder="Example: 1"
                                min="1"
                                max="9"
                                value={formData.floorNumber}
                                onChange={handleChange}
                                required
                            />
                        </div>
                    </div>

                    <div className="create-room-popup-actions">
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
                            className="btn btn-primary create-room-submit-btn"
                            disabled={loading || !canCreate}
                        >
                            {loading ? "CREATING..." : "CREATE"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default CreateRoomPopup;