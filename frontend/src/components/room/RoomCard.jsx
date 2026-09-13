import { Link, useLocation } from "react-router-dom";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

import { useAuth } from "../../context/AuthContext";
import { useCart } from "../../context/CartContext";
import { getErrorMessages } from "../../utils/apiErrorUtils";
import { getRoomImage } from "../../utils/roomImageUtils";

function RoomCard({ room }) {
    const location = useLocation();

    const roomImage = getRoomImage(room.id);

    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();
    const { addToCart } = useCart();
    const [addingToCart, setAddingToCart] = useState(false);
    const [cartErrors, setCartErrors] = useState([]);

    const { ddToCart } = useCart();

    const handleAddToCart = async () => {
        if (!isAuthenticated) {
            navigate("/login");
            return;
        }

        setAddingToCart(true);

        try {
            await addToCart(
                room.id,
                1
            );
        } catch (error) {
            setCartErrors(
                getErrorMessages(error)
            );
        } finally {
            setAddingToCart(false);
        }
    };

    return (
        <div className="col-lg-4 col-md-6 d-flex">
            <div className="room-item shadow rounded overflow-hidden d-flex flex-column w-100 h-100">

                {/* Room Image */}
                <div className="position-relative">

                    <img
                        className="img-fluid room-card-image"
                        src={roomImage}
                        alt={room.roomTypeName}
                    />

                    <small className="position-absolute start-0 top-100 translate-middle-y bg-primary text-white rounded py-1 px-3 ms-4">
                        ${room.price}/Night
                    </small>

                </div>

                {/* Room Information */}
                <div className="p-4 mt-2 d-flex flex-column flex-grow-1">

                    <div className="d-flex justify-content-between mb-3">

                        <h5 className="mb-0">
                            {room.roomTypeName}
                        </h5>

                    </div>

                    {/* Facilities */}
                     <div className="d-flex mb-3">

                         <small className="border-end me-3 pe-3">
                             <i className="fa fa-expand text-primary me-2" />
                             {room.roomSize} m²
                         </small>

                         <small>
                             <i className="fa fa-users text-primary me-2" />
                             {room.maximumPeople} People
                         </small>

                     </div>

                    {/* Facility */}
                    <p className="text-body mb-3 room-facility">
                        {room.facility}
                    </p>

                    {/* Buttons */}
                    <div className="d-flex justify-content-between mt-auto">
                        <Link
                            className="btn btn-sm btn-primary rounded py-2 px-4"
                            to={`/rooms/${room.id}`}
                            state={{
                                from: location.pathname + location.search,
                            }}
                        >
                            View Detail
                        </Link>

                       <button
                           type="button"
                           className="btn btn-dark room-add-cart-btn"
                           disabled={addingToCart}
                           onClick={handleAddToCart}
                       >
                           {addingToCart
                               ? "ADDING..."
                               : "ADD TO CART"}
                       </button>
                    </div>

                </div>

            </div>
        </div>
    );
}

export default RoomCard;