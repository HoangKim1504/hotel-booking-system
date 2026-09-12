import testimonial1 from "../../assets/images/testimonial-1.jpg";
import testimonial2 from "../../assets/images/testimonial-2.jpg";
import testimonial3 from "../../assets/images/testimonial-3.jpg";

function TestimonialSection() {

    // TODO: Replace mock testimonial data with Spring Boot API
    // Example: GET /api/reviews
    const testimonials = [
        {
            id: 1,
            name: "Client Name",
            profession: "Profession",
            image: testimonial1,
            comment:
                "Tempor stet labore dolor clita stet diam amet ipsum dolor duo ipsum rebum stet dolor amet diam stet. Est stet ea lorem amet est kasd kasd et erat magna eos.",
        },
        {
            id: 2,
            name: "Client Name",
            profession: "Profession",
            image: testimonial2,
            comment:
                "Tempor stet labore dolor clita stet diam amet ipsum dolor duo ipsum rebum stet dolor amet diam stet. Est stet ea lorem amet est kasd kasd et erat magna eos.",
        },
        {
            id: 3,
            name: "Client Name",
            profession: "Profession",
            image: testimonial3,
            comment:
                "Tempor stet labore dolor clita stet diam amet ipsum dolor duo ipsum rebum stet dolor amet diam stet. Est stet ea lorem amet est kasd kasd et erat magna eos.",
        },
    ];

    return (
        <div
            className="container-xxl testimonial mt-5 py-5 bg-dark"
            style={{
                marginBottom: "90px",
            }}
        >
            <div className="container">

                <div
                    id="testimonialCarousel"
                    className="carousel slide"
                    data-bs-ride="carousel"
                >
                    <div className="carousel-inner py-5">

                        {testimonials.map((testimonial, index) => (
                            <div
                                key={testimonial.id}
                                className={`carousel-item ${
                                    index === 0 ? "active" : ""
                                }`}
                            >
                                <div className="row justify-content-center">

                                    <div className="col-lg-7">

                                        <div className="testimonial-item position-relative bg-white rounded overflow-hidden p-4">

                                            <p>
                                                {testimonial.comment}
                                            </p>

                                            <div className="d-flex align-items-center">

                                                <img
                                                    className="img-fluid flex-shrink-0 rounded"
                                                    src={testimonial.image}
                                                    alt={testimonial.name}
                                                    style={{
                                                        width: "45px",
                                                        height: "45px",
                                                    }}
                                                />

                                                <div className="ps-3">

                                                    <h6 className="fw-bold mb-1">
                                                        {testimonial.name}
                                                    </h6>

                                                    <small>
                                                        {testimonial.profession}
                                                    </small>

                                                </div>

                                            </div>

                                            <i className="fa fa-quote-right fa-3x text-primary position-absolute end-0 bottom-0 me-4 mb-n1" />

                                        </div>

                                    </div>

                                </div>
                            </div>
                        ))}

                    </div>

                    {/* Previous */}
                    <button
                        className="carousel-control-prev"
                        type="button"
                        data-bs-target="#testimonialCarousel"
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

                    {/* Next */}
                    <button
                        className="carousel-control-next"
                        type="button"
                        data-bs-target="#testimonialCarousel"
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
        </div>
    );
}

export default TestimonialSection;