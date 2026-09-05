import RoomCard from "./RoomCard";
import LoadingSpinner from "../common/LoadingSpinner";
import ErrorPopup from "../common/ErrorPopup";
import { useEffect, useRef, useState } from "react";

import room1 from "../../assets/images/room-1.jpg";
import room2 from "../../assets/images/room-2.jpg";
import room3 from "../../assets/images/room-3.jpg";

function RoomList({ limit }) {

    const [apiRoomTypes, setApiRoomTypes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(9);
    const [totalPages, setTotalPages] = useState(1);

    const roomListRef = useRef(null);

    useEffect(() => {
        setLoading(true);

        const params = new URLSearchParams({
            page: currentPage,
            size: pageSize,
        });

        fetch(`http://localhost:8080/api/room-types?${params}`)
            .then(async (response) => {
                const data = await response.json();

                if (!response.ok) {
                    const errorMessages = data.errors
                        ? Object.values(data.errors)
                        : [data.message || "Something went wrong"];

                    setErrors(errorMessages);
                    setShowErrorPopup(true);

                    return null;
                }

                return data;
            })
            .then((data) => {
                if (!data) {
                    return;
                }

                setApiRoomTypes(data.data);
                setTotalPages(data.totalPages);
            })
            .catch((error) => {
                console.error("Error fetching room types:", error);

                setErrors(["Unable to connect to server"]);
                setShowErrorPopup(true);
            })
            .finally(() => {
                setLoading(false);
            });

    }, [currentPage, pageSize]);

    const displayedRoomTypes = limit
        ? apiRoomTypes.slice(0, limit)
        : apiRoomTypes;

    const getVisiblePages = () => {
        const maxVisiblePages = 5;

        let startPage = Math.max(
            1,
            currentPage - Math.floor(maxVisiblePages / 2)
        );

        let endPage = startPage + maxVisiblePages - 1;

        if (endPage > totalPages) {
            endPage = totalPages;

            startPage = Math.max(
                1,
                endPage - maxVisiblePages + 1
            );
        }

        return Array.from(
            { length: endPage - startPage + 1 },
            (_, index) => startPage + index
        );
    };

    const visiblePages = getVisiblePages();

    const handlePageChange = (page) => {
        setCurrentPage(page);

        roomListRef.current?.scrollIntoView({
            behavior: "smooth",
            block: "start",
        });
    };

    return (
        <>
            <LoadingSpinner show={loading} />

            <ErrorPopup
                show={showErrorPopup}
                title="Unable to load rooms"
                errors={errors}
                onClose={() => setShowErrorPopup(false)}
            />

            <div
                ref={roomListRef}
                className="container-xxl py-5"
            >
                <div className="container">

                    {/* Title */}
                    <div className="text-center">

                        <h6 className="section-title text-center text-primary text-uppercase">
                            Our Rooms
                        </h6>

                        <h1 className="mb-5">
                            Explore Our{" "}
                            <span className="text-primary text-uppercase">
                                Rooms
                            </span>
                        </h1>

                    </div>

                    {/* Room List */}
                    <div className="row g-4">

                        {displayedRoomTypes.map((room) => (
                            <RoomCard
                                key={room.id}
                                room={room}
                            />
                        ))}

                    </div>

                    {displayedRoomTypes.length === 0 && (
                        <div className="text-center">
                            <p>No rooms available.</p>
                        </div>
                    )}

                    {/* Pagination */}
                    {!limit && totalPages > 1 && (
                        <nav className="mt-5">
                            <ul className="pagination justify-content-center">

                                {/* Previous */}
                                <li
                                    className={`page-item ${
                                        currentPage === 1 ? "disabled" : ""
                                    }`}
                                >
                                    <button
                                        type="button"
                                        className="page-link rounded mx-1"
                                        onClick={() => handlePageChange(currentPage - 1)}
                                        disabled={currentPage === 1}
                                    >
                                        &laquo;
                                    </button>
                                </li>

                                {/* Page Numbers */}
                                {visiblePages.map((page) => (
                                    <li
                                        key={page}
                                        className={`page-item ${
                                            currentPage === page ? "active" : ""
                                        }`}
                                    >
                                        <button
                                            type="button"
                                            className="page-link rounded mx-1"
                                            onClick={() => handlePageChange(page)}
                                        >
                                            {page}
                                        </button>
                                    </li>
                                ))}

                                {/* Next */}
                                <li
                                    className={`page-item ${
                                        currentPage === totalPages ? "disabled" : ""
                                    }`}
                                >
                                    <button
                                        type="button"
                                        className="page-link rounded mx-1"
                                        onClick={() => handlePageChange(currentPage + 1)}
                                        disabled={currentPage === totalPages}
                                    >
                                        &raquo;
                                    </button>
                                </li>

                            </ul>
                        </nav>
                    )}

                </div>
            </div>
        </>
    );
}

export default RoomList;