import type { CartItem } from "../../types/cart"

type Props = {
    item: CartItem;
}

const CartItemCard = ({ item }: Props) => {
    return (
        <div key={item.itemId} className="border p-4 rounded">
            <h2 className="text-lg font-semibold">{item.productName}</h2>
            <p>Price: ${item.unitPrice.toFixed(2)}</p>
            <p>Quantity: {item.quantity}</p>
            <p>Total: ${item.subtotal.toFixed(2)}</p>
        </div>
    )
}

export default CartItemCard
