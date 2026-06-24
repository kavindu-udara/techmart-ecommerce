import type { Cart } from "../../types/cart"
import CartItemCard from "../cards/CartItemCard";
import { Button } from "../ui/button";

type Props = {
    cart: Cart;
}

const CartSection = ({ cart }: Props) => {
    return (
        <div className="container mx-auto p-4">
            {
                cart.items.length > 0 ? (
                    <div className="w-full flex flex-col gap-4">
                        {cart.items.map(item => (
                            <CartItemCard key={item.itemId} item={item} />
                        ))}
                    </div>
                ) : (
                    <div className="w-full min-h-screen flex items-center justify-center">
                        Your cart is empty.
                    </div>
                )
            }

            <div className="w-full flex flex-col items-end mt-4">
                <h2 className="text-xl font-bold">Total: ${cart.totalAmount.toFixed(2)}</h2>
                <Button>Place Order</Button>
            </div>

        </div>
    )
}

export default CartSection
