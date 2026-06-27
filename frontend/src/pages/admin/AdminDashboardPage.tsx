import { useEffect, useState } from "react"
import AuthLayout from "../../components/layouts/AuthLayout"
import { apiClient } from "../../lib/axios";
import type { AdminDashboardResponseType } from "../../types/admin";
import DashboardStatsSection from "../../components/sections/DashboardStatsSection";

const AdminDashboardPage = () => {

    const [isLoading, setIsLoading] = useState(true);
    const [dashboardData, setDashboardData] = useState<AdminDashboardResponseType | null>(null);

    useEffect(() => {
        setIsLoading(true);
        apiClient.get("/admin/dashboard").then((response) => {
            setDashboardData(response.data);
        }).catch((error) => {
            console.error("Error fetching admin dashboard data:", error);
        }).finally(() => {
            setIsLoading(false);
        });
    }, []);

    return (
        <AuthLayout>
            {
                isLoading ? (
                    <div className="w-full min-h-screen flex items-center justify-center">
                        Loading....
                    </div>
                ) : (
                    <>
                        {dashboardData ? (
                            <DashboardStatsSection stats={dashboardData} />
                        ) : (
                            <div className="text-red-500">Failed to load dashboard data.</div>
                        )}
                    </>
                )
            }
        </AuthLayout>
    )
}

export default AdminDashboardPage
