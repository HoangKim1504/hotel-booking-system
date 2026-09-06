import { useEffect, useRef, useState } from "react";
import { useSearchParams } from "react-router-dom";

import RoomCard from "./RoomCard";
import LoadingSpinner from "../common/LoadingSpinner";
import ErrorPopup from "../common/ErrorPopup";
import { getRoomTypes, searchRoomTypes } from "../../services/roomService";
import { getErrorMessages } from "../../utils/apiErrorUtils";

function RoomList({ limit }) {

    const [apiRoomTypes, setApiRoomTypes] = useState([]);
    const [loading, setLoading] = useState(true);
    const [errors, setErrors] = useState([]);
    const [showErrorPopup, setShowErrorPopup] = useState(false);
    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(9);
    const [totalPages, setTotalPages] = useState(1);
    const [sortBy, setSortBy] = useState("");
    const [order, setOrder] = useState("");

    const [searchParams] = useSearchParams();
    const checkInDate = searchParams.get("checkInDate");
    const checkOutDate = searchParams.get("checkOutDate");
    const maximumPeople = searchParams.get("maximumPeople");

    const roomListRef = useRef(null);

    useEffect(() => {
            setSortBy("");
            setOrder("");
            setCurrentPage(1);
    }, [checkInDate, checkOutDate, maximumPeople]);

    useEffect(() => {
        const loadRoomTypes = async () => {
            setLoading(true);

            try {
                let data;

                if (isSearching) {
                    data = await searchRoomTypes({
                        checkInDate,
                        checkOutDate,
                        maximumPeople: Number(maximumPeople),
                        page: currentPage,
                        size: pageSize,
                        sortBy,
                        order,
                    });
                } else {
                    data = await getRoomTypes({
                        page: currentPage,
                        size: pageSize,
                        sortBy,
                        order,
                    });
                }

                setApiRoomTypes(data.data);
                setTotalPages(data.totalPages);

            } catch (error) {
                console.error("Error fetching rooms:", error);

                setErrors(getErrorMessages(error));
                setShowErrorPopup(true);

            } finally {
                setLoading(false);
            }
        };

        loadRoomTypes();

    }, [currentPage, pageSize, sortBy, order, checkInDate, checkOutDate, maximumPeople]);

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

    const handleSortChange = (event) => {
        const value = event.target.value;

        // Default - không sort
        if (!value) {
            setSortBy("");
            setOrder("");
            setCurrentPage(1);
            return;
        }

        const [selectedSortBy, selectedOrder] = value.split("-");

        setSortBy(selectedSortBy);
        setOrder(selectedOrder);

        // Khi đổi sort thì quay về page 1
        setCurrentPage(1);
    };

    const isSearching = checkInDate && checkOutDate && maximumPeople;

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

                    {/* Sort by */}
                     <div className="room-sort-wrapper">
                         <label
                             htmlFor="roomSort"
                             className="room-sort-label"
                         >
                             Sort by
                         </label>

                         <select
                             id="roomSort"
                             className="room-sort-select"
                             onChange={handleSortChange}
                             value={sortBy && order ? `${sortBy}-${order}` : ""}
                         >
                             <option value="">Default</option>

                             <option value="roomTypeName-ASC">
                                 Name: A - Z
                             </option>

                             <option value="roomTypeName-DESC">
                                 Name: Z - A
                             </option>

                             <option value="price-ASC">
                                 Price: Low to High
                             </option>

                             <option value="price-DESC">
                                 Price: High to Low
                             </option>

                             <option value="roomSize-ASC">
                                 Size: Small to Large
                             </option>

                             <option value="roomSize-DESC">
                                 Size: Large to Small
                             </option>

                             <option value="maximumPeople-ASC">
                                 Capacity: Low to High
                             </option>

                             <option value="maximumPeople-DESC">
                                 Capacity: High to Low
                             </option>
                         </select>
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