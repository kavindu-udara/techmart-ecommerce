import { useEffect } from "react"
import AuthLayout from "../../components/layouts/AuthLayout"
import { apiClient } from "../../lib/axios";

const AdminDashboardPage = () => {

    useEffect(() => {
        apiClient.get("/admin/dashboard").then((response) => {
            console.log("Admin dashboard data:", response.data);
        }).catch((error) => {
            console.error("Error fetching admin dashboard data:", error);
        });
    }, []);

    return (
        <AuthLayout>
            <main>
                admin dashboard
            </main>
        </AuthLayout>
    )
}

export default AdminDashboardPage
