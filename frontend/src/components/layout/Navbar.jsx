import { Link, NavLink } from "react-router-dom";

function Navbar() {
    return (
        <header className="container-fluid bg-dark px-0">
            <div className="row gx-0">

                {/* Logo desktop */}
                <div className="col-lg-3 bg-dark d-none d-lg-block">
                    <Link
                        to="/"
                        className="navbar-brand w-100 h-100 m-0 p-0 d-flex align-items-center justify-content-center"
                    >
                        <h1 className="m-0 text-primary text-uppercase">
                            Hotelier
                        </h1>
                    </Link>
                </div>

                <div className="col-lg-9">

                    {/* Contact + Social */}
                    <div className="row gx-0 bg-white d-none d-lg-flex">

                        <div className="col-lg-7 px-5 text-start">
                            <div className="h-100 d-inline-flex align-items-center py-2 me-4">
                                <i className="fa fa-envelope text-primary me-2"></i>
                                <p className="mb-0">info@example.com</p>
                            </div>

                            <div className="h-100 d-inline-flex align-items-center py-2">
                                <i className="fa fa-phone-alt text-primary me-2"></i>
                                <p className="mb-0">+012 345 6789</p>
                            </div>
                        </div>

                        <div className="col-lg-5 px-5 text-end">
                            <div className="d-inline-flex align-items-center py-2">

                                <a className="me-3" href="#">
                                    <i className="fab fa-facebook-f"></i>
                                </a>

                                <a className="me-3" href="#">
                                    <i className="fab fa-twitter"></i>
                                </a>

                                <a className="me-3" href="#">
                                    <i className="fab fa-linkedin-in"></i>
                                </a>

                                <a className="me-3" href="#">
                                    <i className="fab fa-instagram"></i>
                                </a>

                                <a href="#">
                                    <i className="fab fa-youtube"></i>
                                </a>

                            </div>
                        </div>
                    </div>

                    {/* Navbar */}
                    <nav className="navbar navbar-expand-lg bg-dark navbar-dark p-3 p-lg-0">

                        {/* Logo mobile */}
                        <Link
                            to="/"
                            className="navbar-brand d-block d-lg-none"
                        >
                            <h1 className="m-0 text-primary text-uppercase">
                                Hotelier
                            </h1>
                        </Link>

                        {/* Mobile menu button */}
                        <button
                            type="button"
                            className="navbar-toggler"
                            data-bs-toggle="collapse"
                            data-bs-target="#navbarCollapse"
                            aria-controls="navbarCollapse"
                            aria-expanded="false"
                            aria-label="Toggle navigation"
                        >
                            <span className="navbar-toggler-icon"></span>
                        </button>

                        <div
                            className="collapse navbar-collapse justify-content-between"
                            id="navbarCollapse"
                        >
                            <div className="navbar-nav me-auto py-0">

                                <NavLink
                                    to="/"
                                    className="nav-item nav-link"
                                >
                                    Home
                                </NavLink>

                                <NavLink
                                    to="/about"
                                    className="nav-item nav-link"
                                >
                                    About
                                </NavLink>

                                <NavLink
                                    to="/services"
                                    className="nav-item nav-link"
                                >
                                    Services
                                </NavLink>

                                <NavLink
                                    to="/rooms"
                                    className="nav-item nav-link"
                                >
                                    Rooms
                                </NavLink>

                                {/* Dropdown */}
                                <div className="nav-item dropdown">

                                    <a
                                        href="#"
                                        className="nav-link dropdown-toggle"
                                        data-bs-toggle="dropdown"
                                    >
                                        Pages
                                    </a>

                                    <div className="dropdown-menu rounded-0 m-0">

                                        <Link
                                            to="/booking"
                                            className="dropdown-item"
                                        >
                                            Booking
                                        </Link>

                                        <Link
                                            to="/team"
                                            className="dropdown-item"
                                        >
                                            Our Team
                                        </Link>

                                        <Link
                                            to="/testimonial"
                                            className="dropdown-item"
                                        >
                                            Testimonial
                                        </Link>

                                    </div>
                                </div>

                                <NavLink
                                    to="/contact"
                                    className="nav-item nav-link"
                                >
                                    Contact
                                </NavLink>

                            </div>

                            {/* TODO: Booking page will use room.id to load room data from Spring Boot API */}
                            <Link
                                to={`/booking`}
                                className="btn btn-sm btn-dark rounded py-2 px-4"
                            >
                                Book Now
                            </Link>

                        </div>
                    </nav>
                </div>
            </div>
        </header>
    );
}

export default Navbar;