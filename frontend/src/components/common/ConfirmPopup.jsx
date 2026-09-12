function ConfirmPopup({
    show,
    title = "Confirmation",
    message,
    confirmText = "Confirm",
    cancelText = "Cancel",
    loading = false,
    onConfirm,
    onCancel,
}) {
    if (!show) {
        return null;
    }

    return (
        <div
            className="confirm-popup-overlay"
            onClick={onCancel}
        >
            <div
                className="confirm-popup"
                onClick={(event) => event.stopPropagation()}
            >
                <div className="confirm-popup-header">
                    <div className="confirm-popup-icon">?</div>

                    <h5 className="mb-0">
                        {title}
                    </h5>
                </div>

                <div className="confirm-popup-body">
                    <p>{message}</p>

                    <div className="confirm-popup-actions">
                        <button
                            type="button"
                            className="btn btn-outline-secondary"
                            onClick={onCancel}
                            disabled={loading}
                        >
                            {cancelText}
                        </button>

                        <button
                            type="button"
                            className="btn btn-primary"
                            onClick={onConfirm}
                            disabled={loading}
                        >
                            {confirmText}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default ConfirmPopup;