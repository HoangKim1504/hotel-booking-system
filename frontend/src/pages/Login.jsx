import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import PageHeader from "../components/layout/PageHeader";
import ErrorPopup from "../components/common/ErrorPopup";

import { login as loginApi } from "../services/authService";
import { getErrorMessages } from "../utils/apiErrorUtils";
import { useAuth } from "../context/AuthContext";

function Login() {
    const { login } = useAuth();
    const navigate = useNavigate();

    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);
    const [formData, setFormData] = useState({
        username: "",
        password: "",
    });

    const handleChange = (event) => {
        const { name, value } = event.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        setLoading(true);

        try {
            const data = await loginApi(
                formData.username,
                formData.password
            );

           login(data.token, formData.username);

            navigate("/");

            window.scrollTo({
                top: 0,
                left: 0,
                behavior: "auto",
            });
        } catch (error) {
            setErrors(getErrorMessages(error));
            setShowErrorPopup(true);
        } finally {
            setLoading(false);
        }
    };

    return (
        <>
            <PageHeader title="Login" />

            <div className="container-xxl py-5">
                <div className="container">
                    <div className="login-wrapper">
                        <div className="login-card">
                            <div className="text-center mb-4">
                                <h6 className="section-title text-center text-primary text-uppercase">
                                    Welcome Back
                                </h6>

                                <h2 className="mb-3">
                                    Login To Your Account
                                </h2>

                                <p className="text-muted mb-0">
                                    Enter your account information to continue.
                                </p>
                            </div>

                            <form onSubmit={handleSubmit}>
                                <div className="mb-3">
                                    <label
                                        htmlFor="username"
                                        className="form-label"
                                    >
                                        Username
                                    </label>

                                    <input
                                        type="text"
                                        id="username"
                                        name="username"
                                        className="form-control login-input"
                                        placeholder="Enter your username"
                                        value={formData.username}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="mb-4">
                                    <label
                                        htmlFor="password"
                                        className="form-label"
                                    >
                                        Password
                                    </label>

                                    <input
                                        type="password"
                                        id="password"
                                        name="password"
                                        className="form-control login-input"
                                        placeholder="Enter your password"
                                        value={formData.password}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <button
                                    type="submit"
                                    className="btn btn-primary w-100 py-3"
                                    disabled={loading}
                                >
                                    {loading ? "LOGGING IN..." : "LOGIN"}
                                </button>

                                <div className="text-center mt-4">
                                    <span className="text-muted">
                                        Don't have an account?{" "}
                                    </span>

                                    <Link
                                        to="/signup"
                                        className="auth-link"
                                    >
                                        Sign Up
                                    </Link>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <ErrorPopup
                show={showErrorPopup}
                title="Login Failed"
                errors={errors}
                onClose={() => setShowErrorPopup(false)}
            />
        </>
    );
}

export default Login;