import { Link } from "react-router-dom";

function Footer() {
    return (
        <footer
            className="container-fluid bg-dark text-light footer wow fadeIn"
            data-wow-delay="0.1s"
        >
            <div className="container pb-5">
                <div className="row g-5">

                    {/* Hotel Information */}
                    <div className="col-md-6 col-lg-4">
                        <div className="bg-primary rounded p-4">

                            <Link to="/">
                                <h1 className="text-white text-uppercase mb-3">
                                    Hotelier
                                </h1>
                            </Link>

                            <p className="text-white mb-0">
                                Find the perfect room for your stay and enjoy
                                a comfortable hotel booking experience.
                            </p>
                        </div>
                    </div>

                    {/* Contact */}
                    <div className="col-md-6 col-lg-3">

                        <h6 className="section-title text-start text-primary text-uppercase mb-4">
                            Contact
                        </h6>

                        {/* TODO: Replace mock hotel information with real data or Spring Boot API if needed */}
                        <p className="mb-2">
                            <i className="fa fa-map-marker-alt me-3"></i>
                            123 Street, New York, USA
                        </p>

                        <p className="mb-2">
                            <i className="fa fa-phone-alt me-3"></i>
                            +012 345 67890
                        </p>

                        <p className="mb-2">
                            <i className="fa fa-envelope me-3"></i>
                            info@example.com
                        </p>

                        {/* TODO: Add real social media URLs */}
                        <div className="d-flex pt-2">

                            <a
                                className="btn btn-outline-light btn-social"
                                href="#"
                                aria-label="Twitter"
                            >
                                <i className="fab fa-twitter"></i>
                            </a>

                            <a
                                className="btn btn-outline-light btn-social"
                                href="#"
                                aria-label="Facebook"
                            >
                                <i className="fab fa-facebook-f"></i>
                            </a>

                            <a
                                className="btn btn-outline-light btn-social"
                                href="#"
                                aria-label="YouTube"
                            >
                                <i className="fab fa-youtube"></i>
                            </a>

                            <a
                                className="btn btn-outline-light btn-social"
                                href="#"
                                aria-label="LinkedIn"
                            >
                                <i className="fab fa-linkedin-in"></i>
                            </a>

                        </div>
                    </div>

                    {/* Company + Services */}
                    <div className="col-lg-5 col-md-12">

                        <div className="row gy-5 g-4">

                            {/* Company */}
                            <div className="col-md-6">

                                <h6 className="section-title text-start text-primary text-uppercase mb-4">
                                    Company
                                </h6>

                                <Link
                                    className="btn btn-link"
                                    to="/about"
                                >
                                    About Us
                                </Link>

                                <Link
                                    className="btn btn-link"
                                    to="/contact"
                                >
                                    Contact Us
                                </Link>

                                {/* TODO: Create these pages if needed */}
                                <Link
                                    className="btn btn-link"
                                    to="/privacy"
                                >
                                    Privacy Policy
                                </Link>

                                <Link
                                    className="btn btn-link"
                                    to="/terms"
                                >
                                    Terms & Condition
                                </Link>

                                <Link
                                    className="btn btn-link"
                                    to="/support"
                                >
                                    Support
                                </Link>

                            </div>

                            {/* Services */}
                            <div className="col-md-6">

                                <h6 className="section-title text-start text-primary text-uppercase mb-4">
                                    Services
                                </h6>

                                {/* TODO: Replace mock services with Spring Boot API if services become dynamic */}
                                <Link className="btn btn-link" to="/services">
                                    Food & Restaurant
                                </Link>

                                <Link className="btn btn-link" to="/services">
                                    Spa & Fitness
                                </Link>

                                <Link className="btn btn-link" to="/services">
                                    Sports & Gaming
                                </Link>

                                <Link className="btn btn-link" to="/services">
                                    Event & Party
                                </Link>

                                <Link className="btn btn-link" to="/services">
                                    GYM & Yoga
                                </Link>

                            </div>
                        </div>
                    </div>

                </div>
            </div>

            {/* Copyright */}
            <div className="container">
                <div className="copyright">
                    <div className="row">

                        <div className="col-md-6 text-center text-md-start mb-3 mb-md-0">

                            &copy;{" "}
                            <Link
                                className="border-bottom"
                                to="/"
                            >
                                Hotelier
                            </Link>
                            , All Right Reserved.

                            {" "}

                            {/*
                                Keep this attribution when using
                                the free version of the template.
                            */}
                            Designed By{" "}
                            <a
                                className="border-bottom"
                                href="https://htmlcodex.com"
                                target="_blank"
                                rel="noreferrer"
                            >
                                HTML Codex
                            </a>

                        </div>

                        {/* Footer Menu */}
                        <div className="col-md-6 text-center text-md-end">

                            <div className="footer-menu">

                                <Link to="/">
                                    Home
                                </Link>

                                {/* TODO: Create these pages if needed */}
                                <Link to="/cookies">
                                    Cookies
                                </Link>

                                <Link to="/support">
                                    Help
                                </Link>

                                <Link to="/faq">
                                    FAQs
                                </Link>

                            </div>

                        </div>

                    </div>
                </div>
            </div>

        </footer>
    );
}

export default Footer;