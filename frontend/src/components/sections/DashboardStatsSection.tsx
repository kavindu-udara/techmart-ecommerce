import type { AdminDashboardResponseType } from "../../types/admin"
import StatCard from "../cards/StatCard";

type Props = {
    stats: AdminDashboardResponseType;
}

const DashboardStatsSection = ({ stats }: Props) => {
    return (
        <div className="container mx-auto grid grid-cols-6 gap-6 pt-5">
            <StatCard title="Total Users" value={stats.totalUsers} link="/admin/users" />
            <StatCard title="Total Orders" value={stats.totalOrders} link="/admin/orders" />
            <StatCard title="Total Revenue" value={stats.totalRevenue} link="/admin/revenue" />
            <StatCard title="Total Products" value={stats.totalProducts} link="/admin/products" />
            <StatCard title="Pending Orders" value={stats.pendingOrders} link="/admin/orders/pending" />
            <StatCard title="Completed Orders" value={stats.completedOrders} link="/admin/orders/completed" />
        </div>
    )
}

export default DashboardStatsSection
