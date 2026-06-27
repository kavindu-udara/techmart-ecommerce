import type { AdminDashboardResponseType } from "../../types/admin"
import StatCard from "../cards/StatCard";

type Props = {
    stats : AdminDashboardResponseType;
}

const DashboardStatsSection = ({stats} : Props) => {
  return (
    <div className="container mx-auto grid grid-cols-6 gap-6 pt-5">
      <StatCard title="Total Users" value={stats.totalUsers} />
      <StatCard title="Total Orders" value={stats.totalOrders} />
      <StatCard title="Total Revenue" value={stats.totalRevenue} />
        <StatCard title="Total Products" value={stats.totalProducts} />
        <StatCard title="Pending Orders" value={stats.pendingOrders} />
        <StatCard title="Completed Orders" value={stats.completedOrders} />
    </div>
  )
}

export default DashboardStatsSection
