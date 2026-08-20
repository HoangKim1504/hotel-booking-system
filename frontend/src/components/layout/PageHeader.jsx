import { Link } from "react-router-dom";
import carouselImage from "../../assets/images/carousel-1.jpg";

function PageHeader({ title }) {
    return (
        <div
            className="container-fluid page-header mb-5 p-0"
            style={{
                backgroundImage: `url(${carouselImage})`,
            }}
        >
            <div className="container-fluid page-header-inner py-5">
                <div className="container text-center pb-5">

                    <h1 className="display-3 text-white mb-3 animated slideInDown">
                        {title}
                    </h1>

                    <nav aria-label="breadcrumb">
                        <ol className="breadcrumb justify-content-center text-uppercase">

                            <li className="breadcrumb-item">
                                <Link to="/">Home</Link>
                            </li>

                            <li
                                className="breadcrumb-item text-white active"
                                aria-current="page"
                            >
                                {title}
                            </li>

                        </ol>
                    </nav>

                </div>
            </div>
        </div>
    );
}

export default PageHeader;