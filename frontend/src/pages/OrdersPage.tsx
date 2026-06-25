import { useEffect, useState } from "react"
import AuthLayout from "../components/layouts/AuthLayout"
import { apiClient } from "../lib/axios";
import type { Order } from "../types/order";
import OrdersSection from "../components/sections/OrdersSection";
import { toast } from "react-toastify";

const OrdersPage = () => {

    const [orders, setOrders] = useState<Order[]>([]);

    useEffect(() => {
        apiClient.get('/orders').then((response) => {
            setOrders(response.data);
        })
            .catch((error) => {
                console.error("Failed to fetch orders:", error);
                toast.error("Failed to fetch orders");
            });
    }, []);

    return (
        <AuthLayout>
            <main className="w-full flex justify-center">
                <div className="container mx-auto p-4">
                    <h1 className="text-2xl font-bold mb-4">Your Orders</h1>
                    {
                        orders.length > 0 ? (
                            <OrdersSection orders={orders} />
                        ) : (
                            <div className="w-full min-h-screen flex items-center justify-center">
                                You have no orders.
                            </div>
                        )
                    }
                </div>
            </main>
        </AuthLayout>
    )
}

export default OrdersPage
