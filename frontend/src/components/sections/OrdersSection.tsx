import type { Order } from "../../types/order"
import OrderItemCard from "../cards/OrderItemCard"

type Props = {
    orders: Order[]
}

const OrdersSection = ({ orders }: Props) => {
    return (
        <div className=" w-full flex flex-col gap-4">
            {orders.map((order: Order) => (
                <OrderItemCard key={order.id} order={order} />
            ))}
        </div>
    )
}

export default OrdersSection
