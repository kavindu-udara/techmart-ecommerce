import { useEffect, useState } from "react"
import AdminDashboardLayout from "../../components/layouts/AdminDashboardLayout"
import { apiClient } from "../../lib/axios";
import type { AdminOrderResponseType } from "../../types/admin";

const AdminOrdersPage = () => {

    const [orders, setOrders] = useState<AdminOrderResponseType[]>([]);

    useEffect(() => {
        apiClient.get("/admin/orders").then((response) => {
            setOrders(response.data);
        }).catch((error) => {
            console.error("Error fetching admin orders data:", error);
        });
    }, []);

  return (
    <AdminDashboardLayout title="Orders">
      <table className="container mx-auto divide-y divide-gray-200">
        <thead>
          <tr className="bg-gray-50">
            <th>ID</th>
            <th>User Email</th>
            <th>Item Count</th>
            <th>Total Amount</th>
            <th>Status</th>
            <th>Created At</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((order) => (
            <tr key={order.id}>
              <td className="border">{order.id}</td>
              <td className="border">{order.userEmail}</td>
              <td className="border">{order.itemCount}</td>
              <td className="border">${order.totalAmount.toFixed(2)}</td>
              <td className="border">{order.status}</td>
              <td className="border">{new Date(order.createdAt).toLocaleDateString()}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </AdminDashboardLayout>
  )
}

export default AdminOrdersPage
