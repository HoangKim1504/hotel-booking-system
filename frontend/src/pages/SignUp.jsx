import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import PageHeader from "../components/layout/PageHeader";
import ErrorPopup from "../components/common/ErrorPopup";

import { register as registerApi } from "../services/authService";
import { getErrorMessages } from "../utils/apiErrorUtils";

function SignUp() {
    const navigate = useNavigate();

    const [loading, setLoading] = useState(false);
    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);

    const [formData, setFormData] = useState({
        username: "",
        password: "",
        confirmPassword: "",
        fullName: "",
        gender: "",
        dateOfBirth: "",
        email: "",
        phoneNumber: "",
        address: "",
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

        // Confirm password is only validated on FE
        if (formData.password !== formData.confirmPassword) {
            setErrors(["Passwords do not match."]);
            setShowErrorPopup(true);
            return;
        }

        setLoading(true);

        try {
            await registerApi({
                username: formData.username,
                password: formData.password,
                fullName: formData.fullName,
                gender: formData.gender,
                dateOfBirth: formData.dateOfBirth,
                email: formData.email,
                phoneNumber: formData.phoneNumber,
                address: formData.address,
            });

            navigate("/login");

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
            <PageHeader title="Sign Up" />

            <div className="container-xxl py-5">
                <div className="container">
                    <div className="login-wrapper">
                        <div className="signup-card">

                            <div className="text-center mb-4">
                                <h6 className="section-title text-center text-primary text-uppercase">
                                    Create Account
                                </h6>

                                <h2 className="mb-3">
                                    Sign Up
                                </h2>

                                <p className="text-muted mb-0">
                                    Create an account to start booking your stay.
                                </p>
                            </div>

                            <form
                                onSubmit={handleSubmit}
                                className="signup-form"
                            >
                                <div className="row g-3">

                                    {/* Username */}
                                    <div className="col-md-6">
                                        <label
                                            htmlFor="username"
                                            className="form-label required-label"
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

                                    {/* Full Name */}
                                    <div className="col-md-6">
                                        <label
                                            htmlFor="fullName"
                                            className="form-label required-label"
                                        >
                                            Full Name
                                        </label>

                                        <input
                                            type="text"
                                            id="fullName"
                                            name="fullName"
                                            className="form-control login-input"
                                            placeholder="Enter your full name"
                                            value={formData.fullName}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    {/* Gender */}
                                    <div className="col-md-6">
                                        <label
                                            htmlFor="gender"
                                            className="form-label required-label"
                                        >
                                            Gender
                                        </label>

                                        <select
                                            id="gender"
                                            name="gender"
                                            className="form-select login-input"
                                            value={formData.gender}
                                            onChange={handleChange}
                                            required
                                        >
                                            <option value="">Select gender</option>
                                            <option value="MALE">Male</option>
                                            <option value="FEMALE">Female</option>
                                            <option value="OTHER">Other</option>
                                        </select>
                                    </div>

                                    {/* Date of Birth */}
                                    <div className="col-md-6">
                                        <label
                                            htmlFor="dateOfBirth"
                                            className="form-label required-label"
                                        >
                                            Date of Birth
                                        </label>

                                        <input
                                            type="date"
                                            id="dateOfBirth"
                                            name="dateOfBirth"
                                            className="form-control login-input"
                                            value={formData.dateOfBirth}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    {/* Email */}
                                    <div className="col-md-6">
                                        <label
                                            htmlFor="email"
                                            className="form-label required-label"
                                        >
                                            Email
                                        </label>

                                        <input
                                            type="email"
                                            id="email"
                                            name="email"
                                            className="form-control login-input"
                                            placeholder="Enter your email"
                                            value={formData.email}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    {/* Phone Number */}
                                    <div className="col-md-6">
                                        <label
                                            htmlFor="phoneNumber"
                                            className="form-label required-label"
                                        >
                                            Phone Number
                                        </label>

                                        <input
                                            type="tel"
                                            id="phoneNumber"
                                            name="phoneNumber"
                                            className="form-control login-input"
                                            placeholder="Enter your phone number"
                                            value={formData.phoneNumber}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    {/* Address */}
                                    <div className="col-12">
                                        <label
                                            htmlFor="address"
                                            className="form-label"
                                        >
                                            Address
                                        </label>

                                        <input
                                            type="text"
                                            id="address"
                                            name="address"
                                            className="form-control login-input"
                                            placeholder="Enter your address"
                                            value={formData.address}
                                            onChange={handleChange}
                                        />
                                    </div>

                                    {/* Password */}
                                    <div className="col-md-6">
                                        <label
                                            htmlFor="password"
                                            className="form-label required-label"
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

                                    {/* Confirm Password */}
                                    <div className="col-md-6">
                                        <label
                                            htmlFor="confirmPassword"
                                            className="form-label required-label"
                                        >
                                            Confirm Password
                                        </label>

                                        <input
                                            type="password"
                                            id="confirmPassword"
                                            name="confirmPassword"
                                            className="form-control login-input"
                                            placeholder="Confirm your password"
                                            value={formData.confirmPassword}
                                            onChange={handleChange}
                                            required
                                        />
                                    </div>

                                    <div className="col-12 mt-4">
                                        <button
                                            type="submit"
                                            className="btn btn-primary w-100 py-3"
                                            disabled={loading}
                                        >
                                            {loading ? "SIGNING UP..." : "SIGN UP"}
                                        </button>
                                    </div>

                                </div>

                                <div className="text-center mt-4">
                                    <span className="text-muted">
                                        Already have an account?{" "}
                                    </span>

                                    <Link
                                        to="/login"
                                        className="auth-link"
                                    >
                                        Login
                                    </Link>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <ErrorPopup
                show={showErrorPopup}
                title="Sign Up Failed"
                errors={errors}
                onClose={() => setShowErrorPopup(false)}
            />
        </>
    );
}

export default SignUp;