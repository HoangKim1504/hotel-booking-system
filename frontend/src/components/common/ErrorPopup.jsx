import { useEffect } from "react";

function ErrorPopup({
    show,
    title = "Error",
    errors = [],
    onClose,
}) {

    useEffect(() => {
        if (!show) {
            return;
        }

        const handleKeyDown = (event) => {
            if (event.key === "Escape") {
                onClose();
            }
        };

        document.addEventListener("keydown", handleKeyDown);

        return () => {
            document.removeEventListener("keydown", handleKeyDown);
        };
    }, [show, onClose]);

    if (!show) {
        return null;
    }

    const errorMessages = Array.isArray(errors)
        ? errors
        : [errors];

    return (
        <>
            {/* Click outside to close */}
            <div
                className="error-popup-overlay"
                onClick={onClose}
            />

            <div className="error-popup">

                {/* Header */}
                <div className="error-popup-header">

                    <h5 className="error-popup-title">
                        <span className="error-popup-icon">
                            !
                        </span>

                        {title}
                    </h5>

                    <button
                        type="button"
                        className="error-popup-close"
                        onClick={onClose}
                        aria-label="Close"
                    >
                        &times;
                    </button>

                </div>

                {/* Body */}
                <div className="error-popup-body">

                    {errorMessages.length === 1 ? (
                        <p>{errorMessages[0]}</p>
                    ) : (
                        <ul>
                            {errorMessages.map((message, index) => (
                                <li key={index}>
                                    {message}
                                </li>
                            ))}
                        </ul>
                    )}

                </div>
            </div>
        </>
    );
}

export default ErrorPopup;