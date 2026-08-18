import team1 from "../../assets/images/team-1.jpg";
import team2 from "../../assets/images/team-2.jpg";
import team3 from "../../assets/images/team-3.jpg";
import team4 from "../../assets/images/team-4.jpg";

function TeamSection() {

    // TODO: Replace mock staff data with Spring Boot API if needed
    const members = [
        {
            id: 1,
            name: "Full Name",
            designation: "Designation",
            image: team1,
        },
        {
            id: 2,
            name: "Full Name",
            designation: "Designation",
            image: team2,
        },
        {
            id: 3,
            name: "Full Name",
            designation: "Designation",
            image: team3,
        },
        {
            id: 4,
            name: "Full Name",
            designation: "Designation",
            image: team4,
        },
    ];

    return (
        <div className="container-xxl py-5">
            <div className="container">

                <div className="text-center">

                    <h6 className="section-title text-center text-primary text-uppercase">
                        Our Team
                    </h6>

                    <h1 className="mb-5">
                        Explore Our{" "}
                        <span className="text-primary text-uppercase">
                            Staffs
                        </span>
                    </h1>

                </div>

                <div className="row g-4">

                    {members.map((member) => (
                        <div
                            key={member.id}
                            className="col-lg-3 col-md-6"
                        >
                            <div className="rounded shadow overflow-hidden">

                                <div className="position-relative">

                                    <img
                                        className="img-fluid"
                                        src={member.image}
                                        alt={member.name}
                                    />

                                    <div className="position-absolute start-50 top-100 translate-middle d-flex align-items-center">

                                        <a
                                            className="btn btn-square btn-primary mx-1"
                                            href="#"
                                            aria-label="Facebook"
                                        >
                                            <i className="fab fa-facebook-f" />
                                        </a>

                                        <a
                                            className="btn btn-square btn-primary mx-1"
                                            href="#"
                                            aria-label="Twitter"
                                        >
                                            <i className="fab fa-twitter" />
                                        </a>

                                        <a
                                            className="btn btn-square btn-primary mx-1"
                                            href="#"
                                            aria-label="Instagram"
                                        >
                                            <i className="fab fa-instagram" />
                                        </a>

                                    </div>

                                </div>

                                <div className="text-center p-4 mt-3">

                                    <h5 className="fw-bold mb-0">
                                        {member.name}
                                    </h5>

                                    <small>
                                        {member.designation}
                                    </small>

                                </div>

                            </div>
                        </div>
                    ))}

                </div>

            </div>
        </div>
    );
}

export default TeamSection;