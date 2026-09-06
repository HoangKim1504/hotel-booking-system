function ServiceSection() {

    // TODO: Replace mock service data with Spring Boot API
    // Example: GET /api/services
    const services = [
        {
            id: 1,
            name: "Rooms & Apartment",
            icon: "fa fa-hotel",
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
        {
            id: 2,
            name: "Food & Restaurant",
            icon: "fa fa-utensils",
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
        {
            id: 3,
            name: "Spa & Fitness",
            icon: "fa fa-spa",
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
        {
            id: 4,
            name: "Sports & Gaming",
            icon: "fa fa-swimmer",
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
        {
            id: 5,
            name: "Event & Party",
            icon: "fa fa-glass-cheers",
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
        {
            id: 6,
            name: "GYM & Yoga",
            icon: "fa fa-dumbbell",
            description:
                "Erat ipsum justo amet duo et elitr dolor, est duo duo eos lorem sed diam stet diam sed stet lorem.",
        },
    ];

    return (
        <div className="container-xxl py-5">
            <div className="container">

                <div className="text-center">
                    <h6 className="section-title text-center text-primary text-uppercase">
                        Our Services
                    </h6>

                    <h1 className="mb-5">
                        Explore Our{" "}
                        <span className="text-primary text-uppercase">
                            Services
                        </span>
                    </h1>
                </div>

                <div className="row g-4">

                    {services.map((service) => (
                        <div
                            key={service.id}
                            className="col-lg-4 col-md-6"
                        >
                            <div className="service-item rounded">

                                <div className="service-icon bg-transparent border rounded p-1">
                                    <div className="w-100 h-100 border rounded d-flex align-items-center justify-content-center">

                                        <i
                                            className={`${service.icon} fa-2x text-primary`}
                                        />

                                    </div>
                                </div>

                                <h5 className="mb-3">
                                    {service.name}
                                </h5>

                                <p className="text-body mb-0">
                                    {service.description}
                                </p>

                            </div>
                        </div>
                    ))}

                </div>
            </div>
        </div>
    );
}

export default ServiceSection;