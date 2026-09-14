import {
    useEffect,
    useState,
} from "react";

import {
    CartesianGrid,
    Line,
    LineChart,
    ResponsiveContainer,
    Tooltip,
    XAxis,
    YAxis,
} from "recharts";

import { useAuth } from "../../context/AuthContext";

import { getRevenueStatistics } from
    "../../services/admin/adminStatisticsService";

import { getErrorMessages } from
    "../../utils/apiErrorUtils";

import LoadingSpinner from
    "../../components/common/LoadingSpinner";

import ErrorPopup from
    "../../components/common/ErrorPopup";

const MONTH_NAMES = [
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec",
];

function RevenueStatistics() {
    const { token } = useAuth();

    const currentYear =
        new Date().getFullYear();

    const [year, setYear] =
        useState(currentYear);

    const [statistics, setStatistics] =
        useState(null);

    const [loading, setLoading] =
        useState(false);

    const [errors, setErrors] =
        useState([]);

    const [showErrorPopup, setShowErrorPopup] =
        useState(false);

    useEffect(() => {
        const loadStatistics = async () => {
            setLoading(true);

            try {
                const data =
                    await getRevenueStatistics({
                        year,
                        token,
                    });

                setStatistics(data);

            } catch (error) {
                setErrors(
                    getErrorMessages(error)
                );

                setShowErrorPopup(true);

            } finally {
                setLoading(false);
            }
        };

        loadStatistics();

    }, [year, token]);

    const chartData =
        MONTH_NAMES.map((monthName, index) => {
            const monthData =
                statistics?.monthlyRevenue?.find(
                    (item) =>
                        item.month === index + 1
                );

            return {
                month: monthName,
                revenue: Number(
                    monthData?.revenue ?? 0
                ),
            };
        });

    return (
        <>
            <LoadingSpinner show={loading} />

            <div className="revenue-statistics">

                <div className="revenue-statistics-content">

                    {/* Header */}
                    <div className="revenue-statistics-header">

                        <div>
                            <h2>
                                Revenue Statistics
                            </h2>

                            <p>
                                Overview of hotel revenue
                                and booking performance.
                            </p>
                        </div>

                        <div className="revenue-statistics-year">

                            <label htmlFor="statisticsYear">
                                Year
                            </label>

                            <select
                                id="statisticsYear"
                                className="form-select"
                                value={year}
                                onChange={(event) =>
                                    setYear(
                                        Number(
                                            event.target.value
                                        )
                                    )
                                }
                            >
                                <option value={currentYear}>
                                    {currentYear}
                                </option>

                                <option value={currentYear - 1}>
                                    {currentYear - 1}
                                </option>

                                <option value={currentYear - 2}>
                                    {currentYear - 2}
                                </option>

                            </select>

                        </div>

                    </div>

                    {/* Summary */}
                    <div className="revenue-summary-grid">

                        <div className="revenue-summary-card">
                            <div className="revenue-summary-icon">
                                <i className="fa fa-dollar-sign" />
                            </div>

                            <div>
                                <span>
                                    Total Revenue
                                </span>

                                <strong>
                                    $
                                    {Number(
                                        statistics?.totalRevenue ?? 0
                                    ).toLocaleString()}
                                </strong>
                            </div>
                        </div>

                        <div className="revenue-summary-card">
                            <div className="revenue-summary-icon">
                                <i className="fa fa-calendar-check" />
                            </div>

                            <div>
                                <span>
                                    Total Bookings
                                </span>

                                <strong>
                                    {statistics?.totalBookings ?? 0}
                                </strong>
                            </div>
                        </div>

                        <div className="revenue-summary-card">
                            <div className="revenue-summary-icon">
                                <i className="fa fa-check-circle" />
                            </div>

                            <div>
                                <span>
                                    Successful Payments
                                </span>

                                <strong>
                                    {statistics?.successfulPayments ?? 0}
                                </strong>
                            </div>
                        </div>

                        <div className="revenue-summary-card">
                            <div className="revenue-summary-icon">
                                <i className="fa fa-times-circle" />
                            </div>

                            <div>
                                <span>
                                    Cancelled Bookings
                                </span>

                                <strong>
                                    {statistics?.cancelledBookings ?? 0}
                                </strong>
                            </div>
                        </div>

                    </div>

                    {/* Chart */}
                    <div className="revenue-chart-card">

                        <div className="revenue-chart-header">
                            <div>
                                <h4>
                                    Revenue Overview
                                </h4>

                                <p>
                                    Monthly revenue in {year}
                                </p>
                            </div>

                            <strong>
                                $
                                {Number(
                                    statistics?.totalRevenue ?? 0
                                ).toLocaleString()}
                            </strong>
                        </div>

                        <div className="revenue-chart">

                            <ResponsiveContainer
                                width="100%"
                                height="100%"
                            >
                                <LineChart
                                    data={chartData}
                                    margin={{
                                        top: 10,
                                        right: 20,
                                        left: 10,
                                        bottom: 0,
                                    }}
                                >
                                    <CartesianGrid
                                        strokeDasharray="3 3"
                                        vertical={false}
                                    />

                                    <XAxis
                                        dataKey="month"
                                        tickLine={false}
                                    />

                                    <YAxis
                                        tickLine={false}
                                        tickFormatter={(value) =>
                                            `$${Number(
                                                value
                                            ).toLocaleString()}`
                                        }
                                    />

                                    <Tooltip
                                        formatter={(value) => [
                                            `$${Number(
                                                value
                                            ).toLocaleString()}`,
                                            "Revenue",
                                        ]}
                                    />

                                    <Line
                                        type="monotone"
                                        dataKey="revenue"
                                        stroke="#FEA116"
                                        strokeWidth={3}
                                        dot={{
                                            r: 4,
                                        }}
                                        activeDot={{
                                            r: 6,
                                        }}
                                    />

                                </LineChart>
                            </ResponsiveContainer>

                        </div>

                    </div>

                </div>

            </div>

            <ErrorPopup
                show={showErrorPopup}
                title="Unable to Load Statistics"
                errors={errors}
                onClose={() =>
                    setShowErrorPopup(false)
                }
            />
        </>
    );
}

export default RevenueStatistics;