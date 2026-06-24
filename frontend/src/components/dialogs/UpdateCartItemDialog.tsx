import React, { useMemo, useState } from "react";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "../ui/dialog";
import type { Product } from "../../types/product";
import { apiClient } from "../../lib/axios";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { toast } from "react-toastify";
import { useNavigate } from "react-router";

type Props = {
    triggerRef: React.RefObject<HTMLButtonElement | null>;
    itemId: number | null;
}

const UpdateCartItemDialog = ({ triggerRef, itemId }: Props) => {

    const navigate = useNavigate();

    const [quantity, setQuantity] = useState<number>(1);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [item, setItem] = useState<Product | null>(null);

    useMemo(() => {
        if (itemId === null) {
            setItem(null);
            return;
        }

        setIsLoading(true);
        apiClient.get(`/products/${itemId}`)
            .then(response => {
                setItem(response.data);
            })
            .catch(error => {
                console.error("Error fetching item details:", error);
            }).finally(() => {
                setIsLoading(false);
            });
    }, [itemId]);


    const handleQuantityChange = (type: "increment" | "decrement") => {
        if (type === "increment") {
            setQuantity(Math.min(item?.stockQuantity || 1, quantity + 1));
        } else {
            setQuantity(Math.max(1, quantity - 1));
        }
    };

    const handleUpdate = () => {
        if (itemId === null) {
            toast.error("No item selected.");
            return;
        }

        if (quantity < 1 || (item && quantity > item.stockQuantity)) {
            toast.error("Invalid quantity");
            return;
        }

        apiClient.put(`/cart/items/${itemId}`, {
            quantity: quantity
        }).then((response) => {
            console.log("Item updated in cart:", response.data);
            toast.success("Item updated in cart");
            navigate(0);
        }).catch(() => {
            toast.error("Failed to update item in cart");
        });
    };

    return (
        <Dialog>
            <DialogTrigger ref={triggerRef} className="hidden">Open</DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Update cart item</DialogTitle>
                </DialogHeader>
                <div>
                    {isLoading ? (
                        <p>Loading item details...</p>
                    ) : item ? (
                        <div className="flex flex-col gap-4">
                            <p>Product Name: {item.name}</p>
                            <p>Price: ${item.price.toFixed(2)}</p>
                            <p>Stock Quantity: {item.stockQuantity}</p>
                            <div className="w-full flex flex-row items-center gap-2">
                                <Button variant={"secondary"} onClick={() => handleQuantityChange("decrement")}>
                                    -
                                </Button>
                                <Input type="number" value={quantity} min={1} max={item.stockQuantity} className="w-full text-center mx-2" />
                                <Button variant={"secondary"} onClick={() => handleQuantityChange("increment")}>
                                    +
                                </Button>
                            </div>
                            <Button onClick={handleUpdate}>Update</Button>
                        </div>
                    ) : (
                        <p>No item selected.</p>
                    )}
                </div>
            </DialogContent>

        </Dialog>
    )
}

export default UpdateCartItemDialog
