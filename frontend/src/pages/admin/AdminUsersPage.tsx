import { useNavigate } from "react-router";
import AuthLayout from "../../components/layouts/AuthLayout"
import { Button } from "../../components/ui/button"
import { useEffect, useState } from "react";
import { apiClient } from "../../lib/axios";
import type { UserResponseType } from "../../types/admin";

const AdminUsersPage = () => {

    const navigate = useNavigate();

    const [users, setUsers] = useState<UserResponseType[]>([]);

    useEffect(() => {
        apiClient.get("admin/users").then((response) => {
            setUsers(response.data);
            console.log("Users fetched:", response.data);
        }).catch((error) => {
            console.error("Error fetching users:", error);
        });
    }, []);

    return (
        <AuthLayout>
            <div className="flex flex-wrap gap-3 container mx-auto p-5">
                <Button onClick={() => navigate("/admin/dashboard")}>Go back</Button>
                <h1 className="font-semibold text-2xl">Users</h1>
            </div>
            <table className="container mx-auto divide-y divide-gray-200">
                <thead>
                    <tr className="bg-gray-50">
                        <th>ID</th>
                        <th>Email</th>
                        <th>Role</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map((user) => (
                        <tr key={user.id}>
                            <td className="border">{user.id}</td>
                            <td className="border">{user.email}</td>
                            <td className="border">{user.role}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </AuthLayout>
    )
}

export default AdminUsersPage
