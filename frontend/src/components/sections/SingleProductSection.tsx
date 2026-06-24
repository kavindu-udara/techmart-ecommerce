import { useState } from "react"
import type { Product } from "../../types/product"
import { Button } from "../ui/button"
import { Input } from "../ui/input"
import { apiClient } from "../../lib/axios"
import { toast } from "react-toastify"

type Props = {
    product: Product
}

const SingleProductSection = ({ product }: Props) => {

    const [quantity, setQuantity] = useState<number>(1);

    const handleQuantityChange = (type: "increment" | "decrement") => {
        if (type === "increment") {
            setQuantity(Math.min(product.stockQuantity, quantity + 1));
        } else {
            setQuantity(Math.max(1, quantity - 1));
        }
    };

    const handleAddToCart = () => {
        if (quantity < 0 && quantity > product.stockQuantity) {
            alert("Invalid quantity");
        }

        apiClient.post("/cart/items", {
            productId: product.id,
            quantity: quantity
        }).then((response) => {
            console.log("Item added to cart:", response.data);
            alert("Item added to cart");
            toast.success("Item added to cart");
        }).catch(() => {
            alert("Failed to add item to cart");
        });

    }

    return (
        <div className="container mx-auto p-4 grid grid-cols-2 gap-8">
            <div>
                <img src={product.imageUrl} alt={product.name} className="w-full h-auto object-cover" />
            </div>
            <div className="flex flex-col justify-center gap-4">
                <h1>{product.name}</h1>
                <p>Price: ${product.price.toFixed(2)}</p>
                <p>Stock: {product.stockQuantity}</p>
                <div className="flex flex-row gap-2">
                    <Button variant={"secondary"} onClick={() => handleQuantityChange("decrement")}>
                        -
                    </Button>
                    <Input type="number" value={quantity} onChange={(e) => {
                        const value = parseInt(e.target.value);
                        if (!isNaN(value) && value >= 1 && value <= product.stockQuantity) {
                            setQuantity(value);
                        }
                    }} className="w-16 text-center" min={1} max={product.stockQuantity} />
                    <Button variant={"secondary"} onClick={() => handleQuantityChange("increment")}>
                        +
                    </Button>
                </div>
                <Button onClick={handleAddToCart}>Add to Cart</Button>
            </div>
        </div>
    )
}

export default SingleProductSection
