function SuccessPopup({
    show,
    title = "Success",
    message,
    onClose,
}) {
    if (!show) {
        return null;
    }

    return (
        <div
            className="success-popup-overlay"
            onClick={onClose}
        >
            <div
                className="success-popup"
                onClick={(event) => event.stopPropagation()}
            >
                <div className="success-popup-header">
                    <div className="success-popup-title">
                        <div className="success-popup-icon">
                            ✓
                        </div>

                        <h5 className="mb-0">
                            {title}
                        </h5>
                    </div>

                    <button
                        type="button"
                        className="success-popup-close"
                        onClick={onClose}
                    >
                        ×
                    </button>
                </div>

                <div className="success-popup-body">
                    <p>{message}</p>
                </div>
            </div>
        </div>
    );
}

export default SuccessPopup;