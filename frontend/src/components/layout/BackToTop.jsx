import { useEffect, useState } from "react";

function BackToTop() {
    const [showButton, setShowButton] = useState(false);

    useEffect(() => {
        const handleScroll = () => {
            setShowButton(window.scrollY > 300);
        };

        window.addEventListener("scroll", handleScroll);

        return () => {
            window.removeEventListener("scroll", handleScroll);
        };
    }, []);

    const handleBackToTop = () => {
        window.scrollTo({
            top: 0,
            behavior: "smooth",
        });
    };

    if (!showButton) {
        return null;
    }

    return (
        <button
            type="button"
            className="btn btn-lg btn-primary btn-lg-square back-to-top"
            onClick={handleBackToTop}
            aria-label="Back to top"
        >
            <i className="bi bi-arrow-up"></i>
        </button>
    );
}

export default BackToTop;