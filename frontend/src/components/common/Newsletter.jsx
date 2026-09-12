import { useState } from "react";

function Newsletter() {
    const [email, setEmail] = useState("");

    const handleSubmit = (event) => {
        event.preventDefault();

        console.log("Newsletter email:", email);

        // TODO: Send newsletter subscription email to Spring Boot API if needed
        // Example:
        // axios.post("http://localhost:8080/api/newsletter", {
        //     email: email,
        // });

        setEmail("");
    };

    return (
        <div
            className="container newsletter mt-5 wow fadeIn"
            data-wow-delay="0.1s"
        >
            <div className="row justify-content-center">
                <div className="col-lg-10 border rounded p-1">

                    <div className="border rounded text-center p-1">

                        <div className="bg-white rounded text-center p-5">

                            <h4 className="mb-4">
                                Subscribe Our{" "}
                                <span className="text-primary text-uppercase">
                                    Newsletter
                                </span>
                            </h4>

                            <form onSubmit={handleSubmit}>

                                <div
                                    className="position-relative mx-auto"
                                    style={{ maxWidth: "400px" }}
                                >
                                    <input
                                        className="form-control w-100 py-3 ps-4 pe-5"
                                        type="email"
                                        placeholder="Enter your email"
                                        value={email}
                                        onChange={(event) =>
                                            setEmail(event.target.value)
                                        }
                                        required
                                    />

                                    <button
                                        type="submit"
                                        className="btn btn-primary py-2 px-3 position-absolute top-0 end-0 mt-2 me-2"
                                    >
                                        Submit
                                    </button>
                                </div>

                            </form>

                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
}

export default Newsletter;