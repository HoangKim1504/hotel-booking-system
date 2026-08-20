import about1 from "../../assets/images/about-1.jpg";
import about2 from "../../assets/images/about-2.jpg";
import about3 from "../../assets/images/about-3.jpg";
import about4 from "../../assets/images/about-4.jpg";

function AboutSection() {

    // TODO: Replace mock hotel statistics with Spring Boot API if needed
    // Example: GET /api/statistics/overview
    const statistics = [
        {
            id: 1,
            icon: "fa fa-hotel",
            value: 1234,
            label: "Rooms",
        },
        {
            id: 2,
            icon: "fa fa-users-cog",
            value: 1234,
            label: "Staffs",
        },
        {
            id: 3,
            icon: "fa fa-users",
            value: 1234,
            label: "Clients",
        },
    ];

    return (
        <div className="container-xxl py-5">
            <div className="container">

                <div className="row g-5 align-items-center">

                    {/* About Content */}
                    <div className="col-lg-6">

                        <h6 className="section-title text-start text-primary text-uppercase">
                            About Us
                        </h6>

                        <h1 className="mb-4">
                            Welcome to{" "}
                            <span className="text-primary text-uppercase">
                                Hotelier
                            </span>
                        </h1>

                        {/* TODO: Replace mock hotel description with real data
                            or Spring Boot API if hotel information becomes dynamic */}
                        <p className="mb-4">
                            Tempor erat elitr rebum at clita.
                            Diam dolor diam ipsum sit.
                            Aliqu diam amet diam et eos.
                            Clita erat ipsum et lorem et sit,
                            sed stet lorem sit clita duo justo
                            magna dolore erat amet.
                        </p>

                        {/* Statistics */}
                        <div className="row g-3 pb-4">

                            {statistics.map((item) => (
                                <div
                                    key={item.id}
                                    className="col-sm-4"
                                >
                                    <div className="border rounded p-1">

                                        <div className="border rounded text-center p-4">

                                            <i
                                                className={`${item.icon} fa-2x text-primary mb-2`}
                                            />

                                            <h2 className="mb-1">
                                                {item.value}
                                            </h2>

                                            <p className="mb-0">
                                                {item.label}
                                            </p>

                                        </div>
                                    </div>
                                </div>
                            ))}

                        </div>

                    </div>

                    {/* About Images */}
                    <div className="col-lg-6">

                        <div className="row g-3">

                            <div className="col-6 text-end">
                                <img
                                    className="img-fluid rounded w-75"
                                    src={about1}
                                    alt="Hotel view"
                                    style={{
                                        marginTop: "25%",
                                    }}
                                />
                            </div>

                            <div className="col-6 text-start">
                                <img
                                    className="img-fluid rounded w-100"
                                    src={about2}
                                    alt="Hotel room"
                                />
                            </div>

                            <div className="col-6 text-end">
                                <img
                                    className="img-fluid rounded w-50"
                                    src={about3}
                                    alt="Hotel interior"
                                />
                            </div>

                            <div className="col-6 text-start">
                                <img
                                    className="img-fluid rounded w-75"
                                    src={about4}
                                    alt="Hotel service"
                                />
                            </div>

                        </div>

                    </div>

                </div>

            </div>
        </div>
    );
}

export default AboutSection;