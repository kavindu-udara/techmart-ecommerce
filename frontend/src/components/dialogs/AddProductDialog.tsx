import { useState } from "react"
import { Button } from "../ui/button"
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from "../ui/dialog"
import { Input } from "../ui/input"
import { Label } from "../ui/label"
import { toast } from "react-toastify"
import { apiClient } from "../../lib/axios"

type Props = {
    triggerRef: React.RefObject<HTMLButtonElement | null>;
}

const AddProductDialog = ({ triggerRef }: Props) => {

    const [formData, setFormData] = useState({
        name: "",
        price: 0,
        stockQuantity: 0,
        imageUrl: "",
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { id, value } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [id]: id === "price" || id === "stockQuantity" ? parseFloat(value) : value
        }));
    }

    const handleSubmit = () => {
        // Validate form data
        if (!formData.name || formData.price <= 0 || formData.stockQuantity < 0) {
            toast.error("Please fill in all fields correctly.");
            return;
        }

        apiClient.post("/admin/products", formData)
            .then((response) => {
                toast.success("Product created successfully!");
                triggerRef.current?.click(); // Close the dialog
                window.location.reload(); // Refresh the page to show the new product
            })
            .catch((error) => {
                console.error("Error creating product:", error);
                toast.error("Failed to create product. Please try again.");
            });
    }

    return (
        <Dialog>
            <DialogTrigger className="hidden" ref={triggerRef}>Open</DialogTrigger>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Create Product</DialogTitle>
                    <DialogDescription>
                        Fill in the details below to create a new product.
                    </DialogDescription>
                </DialogHeader>

                <div className="grid gap-4 py-4">
                    <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="name" className="text-right">Name</Label>
                        <Input id="name" type="text" className="col-span-3" value={formData.name} onChange={handleInputChange} />
                    </div>
                    <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="price" className="text-right">Price</Label>
                        <Input id="price" type="number" step="0.01" className="col-span-3" value={formData.price} onChange={handleInputChange} />
                    </div>
                    <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="stockQuantity" className="text-right">Stock </Label>
                        <Input id="stockQuantity" type="number" className="col-span-3" value={formData.stockQuantity} onChange={handleInputChange} />
                    </div>
                    <div className="grid grid-cols-4 items-center gap-4">
                        <Label htmlFor="imageUrl" className="text-right">Image URL</Label>
                        <Input id="imageUrl" type="text" className="col-span-3" value={formData.imageUrl} onChange={handleInputChange} />
                    </div>
                    <div className="flex justify-end">
                        <Button onClick={handleSubmit}>Create Product</Button>
                    </div>
                </div>
            </DialogContent>
        </Dialog>
    )
}

export default AddProductDialog
