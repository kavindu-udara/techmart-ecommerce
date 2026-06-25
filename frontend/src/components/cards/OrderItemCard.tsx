import type { Order, OrderItem } from "../../types/order"
import { Badge } from "../ui/badge";

type Props = {
    order: Order;
}

const OrderItemCard = ({ order }: Props) => {
    return (
        <div key={order.id} className="border p-4 rounded">
            <h2 className="text-lg font-semibold">Order ID: {order.id}</h2>
            <p>Date: {new Date(order.createdAt).toLocaleDateString()}</p>
            <p>Total Amount: ${order.totalAmount.toFixed(2)}</p>
            <div className="mt-2">
                <h3 className="font-semibold">Items:</h3>
                <ul className="list-disc list-inside">
                    {order.items.map((item: OrderItem) => (
                        <li key={item.productId}>
                            {item.productName} - Quantity: {item.quantity} - Price: ${item.unitPrice.toFixed(2)}
                        </li>
                    ))}
                </ul>
            </div>
            <Badge variant={order.status === "CANCELLED" ? "destructive" : order.status === "PENDING" ? "secondary" : "default"} className="absolute top-5 right-5">
                {order.status}
            </Badge>
        </div>
    )
}

export default OrderItemCard
