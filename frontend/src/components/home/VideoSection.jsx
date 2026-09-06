import { useState } from "react";
import { Link } from "react-router-dom";

function VideoSection() {
    const [videoOpen, setVideoOpen] = useState(false);

    const videoUrl = "https://www.youtube.com/embed/DWRcNpR6Kdc";

    return (
        <>
            <div className="container-xxl py-5 px-0">
                <div className="row g-0">

                    <div className="col-md-6 bg-dark d-flex align-items-center">

                        <div className="p-5">

                            <h6 className="section-title text-start text-white text-uppercase mb-3">
                                Luxury Living
                            </h6>

                            <h1 className="text-white mb-4">
                                Discover A Brand Luxurious Hotel
                            </h1>

                            <p className="text-white mb-4">
                                Tempor erat elitr rebum at clita.
                                Diam dolor diam ipsum sit.
                                Aliqu diam amet diam et eos.
                            </p>

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

                    <div className="col-md-6">
                        <div className="video">

                            <button
                                type="button"
                                className="btn-play"
                                data-bs-toggle="modal"
                                data-bs-target="#videoModal"
                                onClick={() => setVideoOpen(true)}
                                aria-label="Play video"
                            >
                                <span />
                            </button>

                        </div>
                    </div>

                </div>
            </div>

            <div
                className="modal fade"
                id="videoModal"
                tabIndex="-1"
                aria-hidden="true"
            >
                <div className="modal-dialog modal-lg">

                    <div className="modal-content rounded-0">

                        <div className="modal-header">

                            <h5 className="modal-title">
                                Youtube Video
                            </h5>

                            <button
                                type="button"
                                className="btn-close"
                                data-bs-dismiss="modal"
                                aria-label="Close"
                                onClick={() => setVideoOpen(false)}
                            />

                        </div>

                        <div className="modal-body">

                            <div className="ratio ratio-16x9">

                                {videoOpen && (
                                    <iframe
                                        src={videoUrl}
                                        title="Hotel video"
                                        allow="autoplay; encrypted-media"
                                        allowFullScreen
                                    />
                                )}

                            </div>

                        </div>

                    </div>
                </div>
            </div>
        </>
    );
}

export default VideoSection;