import { useEffect, useState } from "react";

const initialFormData = {
    roomTypeName: "",
    roomSize: "",
    facility: "",
    maximumPeople: "",
    price: "",
};

function CreateRoomTypePopup({
    show,
    loading,
    errors = [],
    onCreate,
    onCancel,
}) {
    const [formData, setFormData] =
        useState(initialFormData);

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
        formData.roomTypeName.trim() !== "" &&
        formData.roomSize !== "" &&
        formData.facility.trim() !== "" &&
        formData.maximumPeople !== "" &&
        formData.price !== "";

    const handleSubmit = (event) => {
        event.preventDefault();

        if (!canCreate || loading) {
            return;
        }

        onCreate({
            roomTypeName:
                formData.roomTypeName.trim(),

            roomSize:
                Number(formData.roomSize),

            facility:
                formData.facility.trim(),

            maximumPeople:
                Number(formData.maximumPeople),

            price:
                Number(formData.price),
        });
    };

    return (
        <div className="room-type-popup-overlay">
            <div className="room-type-popup">

                <div className="room-type-popup-header">
                    <div>
                        <h4>Create Room Type</h4>

                        <p>
                            Add a new room type to the hotel.
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
                            Unable to create room type
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
                            htmlFor="createRoomTypeName"
                            className="form-label required-label"
                        >
                            Room Type Name
                        </label>

                        <input
                            id="createRoomTypeName"
                            type="text"
                            name="roomTypeName"
                            className="form-control"
                            placeholder="Enter room type name"
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
                                htmlFor="createRoomSize"
                                className="form-label required-label"
                            >
                                Room Size
                            </label>

                            <input
                                id="createRoomSize"
                                type="number"
                                name="roomSize"
                                className="form-control"
                                placeholder="Example: 35"
                                min="0.01"
                                max="1000"
                                step="0.01"
                                value={formData.roomSize}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                        <div className="room-type-form-group">
                            <label
                                htmlFor="createMaximumPeople"
                                className="form-label required-label"
                            >
                                Maximum People
                            </label>

                            <input
                                id="createMaximumPeople"
                                type="number"
                                name="maximumPeople"
                                className="form-control"
                                placeholder="Example: 2"
                                min="1"
                                max="10"
                                value={formData.maximumPeople}
                                onChange={handleChange}
                                disabled={loading}
                                required
                            />
                        </div>

                    </div>

                    <div className="room-type-form-group">
                        <label
                            htmlFor="createPrice"
                            className="form-label required-label"
                        >
                            Price
                        </label>

                        <input
                            id="createPrice"
                            type="number"
                            name="price"
                            className="form-control"
                            placeholder="Enter room price"
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
                            htmlFor="createFacility"
                            className="form-label required-label"
                        >
                            Facility
                        </label>

                        <textarea
                            id="createFacility"
                            name="facility"
                            className="form-control room-type-facility-input"
                            placeholder="Example: WiFi, TV, Air Conditioner, Mini Bar"
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
                                !canCreate
                            }
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

export default CreateRoomTypePopup;