import { Link } from "react-router-dom";

import carousel1 from "../../assets/images/carousel-1.jpg";
import carousel2 from "../../assets/images/carousel-2.jpg";

function HeroCarousel() {
    const slides = [
        {
            id: 1,
            image: carousel1,
            subtitle: "Luxury Living",
            title: "Discover A Brand Luxurious Hotel",
        },
        {
            id: 2,
            image: carousel2,
            subtitle: "Luxury Living",
            title: "Discover A Brand Luxurious Hotel",
        },
    ];

    return (
        <div className="container-fluid p-0 mb-5">
            <div
                id="header-carousel"
                className="carousel slide"
                data-bs-ride="carousel"
            >
                <div className="carousel-inner">
                    {slides.map((slide, index) => (
                        <div
                            key={slide.id}
                            className={`carousel-item ${
                                index === 0 ? "active" : ""
                            }`}
                        >
                            <img
                                className="w-100"
                                src={slide.image}
                                alt={slide.title}
                            />

                            <div className="carousel-caption d-flex flex-column align-items-center justify-content-center">
                                <div
                                    className="p-3"
                                    style={{ maxWidth: "700px" }}
                                >
                                    <h6 className="section-title text-white text-uppercase mb-3">
                                        {slide.subtitle}
                                    </h6>

                                    <h1 className="display-3 text-white mb-4">
                                        {slide.title}
                                    </h1>

                                    <Link
                                        to="/rooms"
                                        className="btn btn-primary py-md-3 px-md-5 me-3"
                                    >
                                        Our Rooms
                                    </Link>

                                    <Link
                                        to="/booking"
                                        className="btn btn-light py-md-3 px-md-5"
                                    >
                                        Book A Room
                                    </Link>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>

                <button
                    className="carousel-control-prev"
                    type="button"
                    data-bs-target="#header-carousel"
                    data-bs-slide="prev"
                >
                    <span
                        className="carousel-control-prev-icon"
                        aria-hidden="true"
                    />

                    <span className="visually-hidden">
                        Previous
                    </span>
                </button>

                <button
                    className="carousel-control-next"
                    type="button"
                    data-bs-target="#header-carousel"
                    data-bs-slide="next"
                >
                    <span
                        className="carousel-control-next-icon"
                        aria-hidden="true"
                    />

                    <span className="visually-hidden">
                        Next
                    </span>
                </button>
            </div>
        </div>
    );
}

export default HeroCarousel;