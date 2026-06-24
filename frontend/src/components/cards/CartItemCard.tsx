import { toast } from "react-toastify";
import { apiClient } from "../../lib/axios";
import type { CartItem } from "../../types/cart"
import { Button } from "../ui/button";
import { useNavigate } from "react-router";

type Props = {
    item: CartItem;
    handleUpdate: (itemId: number) => void;
}

const CartItemCard = ({ item, handleUpdate }: Props) => {

    const navigate = useNavigate();

    const handleRemoveItem = () => {
        apiClient.delete(`/cart/items/${item.itemId}`)
            .then((response) => {
                console.log("Item removed from cart:", response.data);
                toast.success("Item removed from cart");
                // refresh the page to update the cart items
                navigate(0);
            })
            .catch((error) => {
                console.error("Failed to remove item from cart:", error);
                alert("Failed to remove item from cart");
            });
    }

    return (
        <div key={item.itemId} className="border p-4 rounded flex justify-between items-center">
            <div>
                <h2 className="text-lg font-semibold">{item.productName}</h2>
                <p>Price: ${item.unitPrice.toFixed(2)}</p>
                <p>Quantity: {item.quantity}</p>
                <p>Total: ${item.subtotal.toFixed(2)}</p>
            </div>
            <div className="flex flex-col gap-3">
                <Button variant={"destructive"} onClick={handleRemoveItem}>
                    Remove
                </Button>
                <Button variant={"secondary"} className="ml-2" onClick={() => handleUpdate(item.productId)}>
                    Update Quantity
                </Button>
            </div>
        </div>
    )
}

export default CartItemCard
