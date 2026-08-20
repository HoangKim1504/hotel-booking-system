import PageHeader from "../components/layout/PageHeader";
import BookingSearch from "../components/booking/BookingSearch";
import TeamSection from "../components/team/TeamSection";
import Newsletter from "../components/common/Newsletter";

function Team() {
    return (
        <>
            <PageHeader title="Our Team" />

            <BookingSearch />

            <TeamSection limit={8} />

            <Newsletter />
        </>
    );
}

export default Team;