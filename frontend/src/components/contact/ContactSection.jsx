import { useState } from "react";

function ContactSection() {
    const [formData, setFormData] = useState({
        name: "",
        email: "",
        subject: "",
        message: "",
    });

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = (event) => {
        event.preventDefault();

        console.log("Contact data:", formData);

        // TODO: Send contact form data to Spring Boot API
        // Example:
        // POST /api/contact
        //
        // axios.post("http://localhost:8080/api/contact", formData)
        //     .then((response) => {
        //         console.log("Message sent:", response.data);
        //
        //         setFormData({
        //             name: "",
        //             email: "",
        //             subject: "",
        //             message: "",
        //         });
        //     })
        //     .catch((error) => {
        //         console.error("Failed to send message:", error);
        //     });
    };

    return (
        <div className="container-xxl py-5">
            <div className="container">

                {/* Title */}
                <div className="text-center">
                    <h6 className="section-title text-center text-primary text-uppercase">
                        Contact Us
                    </h6>

                    <h1 className="mb-5">
                        <span className="text-primary text-uppercase">
                            Contact
                        </span>{" "}
                        For Any Query
                    </h1>
                </div>

                <div className="row g-4">

                    {/* Contact Information */}
                    <div className="col-12">
                        <div className="row gy-4">

                            {/* TODO: Replace mock contact information with real hotel data
                                or Spring Boot API if contact information becomes dynamic */}

                            <div className="col-md-4">
                                <h6 className="section-title text-start text-primary text-uppercase">
                                    Booking
                                </h6>

                                <p>
                                    <i className="fa fa-envelope-open text-primary me-2" />
                                    book@example.com
                                </p>
                            </div>

                            <div className="col-md-4">
                                <h6 className="section-title text-start text-primary text-uppercase">
                                    General
                                </h6>

                                <p>
                                    <i className="fa fa-envelope-open text-primary me-2" />
                                    info@example.com
                                </p>
                            </div>

                            <div className="col-md-4">
                                <h6 className="section-title text-start text-primary text-uppercase">
                                    Technical
                                </h6>

                                <p>
                                    <i className="fa fa-envelope-open text-primary me-2" />
                                    tech@example.com
                                </p>
                            </div>

                        </div>
                    </div>

                    {/* Google Map */}
                    <div className="col-md-6">

                        {/* TODO: Replace template location with real hotel location */}
                        <iframe
                            className="position-relative rounded w-100 h-100"
                            src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3001156.4288297426!2d-78.01371936852176!3d42.72876761954724!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x4ccc4bf0f123a5a9%3A0xddcfc6c1de189567!2sNew%20York%2C%20USA!5e0!3m2!1sen!2sbd!4v1603794290143!5m2!1sen!2sbd"
                            style={{
                                minHeight: "350px",
                                border: 0,
                            }}
                            allowFullScreen
                            loading="lazy"
                            title="Hotel location"
                        />

                    </div>

                    {/* Contact Form */}
                    <div className="col-md-6">

                        <form onSubmit={handleSubmit}>
                            <div className="row g-3">

                                {/* Name */}
                                <div className="col-md-6">
                                    <div className="form-floating">

                                        <input
                                            type="text"
                                            className="form-control"
                                            id="contactName"
                                            name="name"
                                            placeholder="Your Name"
                                            value={formData.name}
                                            onChange={handleChange}
                                            required
                                        />

                                        <label htmlFor="contactName">
                                            Your Name
                                        </label>

                                    </div>
                                </div>

                                {/* Email */}
                                <div className="col-md-6">
                                    <div className="form-floating">

                                        <input
                                            type="email"
                                            className="form-control"
                                            id="contactEmail"
                                            name="email"
                                            placeholder="Your Email"
                                            value={formData.email}
                                            onChange={handleChange}
                                            required
                                        />

                                        <label htmlFor="contactEmail">
                                            Your Email
                                        </label>

                                    </div>
                                </div>

                                {/* Subject */}
                                <div className="col-12">
                                    <div className="form-floating">

                                        <input
                                            type="text"
                                            className="form-control"
                                            id="contactSubject"
                                            name="subject"
                                            placeholder="Subject"
                                            value={formData.subject}
                                            onChange={handleChange}
                                            required
                                        />

                                        <label htmlFor="contactSubject">
                                            Subject
                                        </label>

                                    </div>
                                </div>

                                {/* Message */}
                                <div className="col-12">
                                    <div className="form-floating">

                                        <textarea
                                            className="form-control"
                                            id="contactMessage"
                                            name="message"
                                            placeholder="Leave a message here"
                                            value={formData.message}
                                            onChange={handleChange}
                                            style={{
                                                height: "150px",
                                            }}
                                            required
                                        />

                                        <label htmlFor="contactMessage">
                                            Message
                                        </label>

                                    </div>
                                </div>

                                {/* Submit */}
                                <div className="col-12">
                                    <button
                                        className="btn btn-primary w-100 py-3"
                                        type="submit"
                                    >
                                        Send Message
                                    </button>
                                </div>

                            </div>
                        </form>

                    </div>

                </div>
            </div>
        </div>
    );
}

export default ContactSection;