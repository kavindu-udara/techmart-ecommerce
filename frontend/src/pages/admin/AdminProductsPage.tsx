import { useEffect, useRef, useState } from "react"
import AdminDashboardLayout from "../../components/layouts/AdminDashboardLayout"
import { apiClient } from "../../lib/axios";
import type { Product } from "../../types/product";
import { Button } from "../../components/ui/button";
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from "../../components/ui/dialog"
import { Label } from "../../components/ui/label";
import { Input } from "../../components/ui/input";
import { toast } from "react-toastify";

const AdminProductsPage = () => {

    const createProductDialogTriggerRef = useRef<HTMLButtonElement>(null);

    const [products, setProducts] = useState<Product[]>([]);

    useEffect(() => {
        apiClient.get("/products").then((response) => {
            setProducts(response.data);
        }).catch((error) => {
            console.error("Error fetching products:", error);
        });
    }, []);

    return (
        <AdminDashboardLayout title="Products">
            <div className="container mx-0-auto flex justify-end mb-4">
                <Button onClick={() => createProductDialogTriggerRef.current?.click()}>Add New Product</Button>
            </div>
            <table className="container mx-auto divide-y divide-gray-200">
                <thead>
                    <tr className="bg-gray-50">
                        <th>ID</th>
                        <th>Name</th>
                        <th>Price</th>
                        <th>Stock</th>
                    </tr>
                </thead>
                <tbody>
                    {products.map((product) => (
                        <tr key={product.id}>
                            <td className="border">{product.id}</td>
                            <td className="border">{product.name}</td>
                            <td className="border">${product.price.toFixed(2)}</td>
                            <td className="border">{product.stockQuantity}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <AddProductDialog triggerRef={createProductDialogTriggerRef} />
        </AdminDashboardLayout>
    )
}

export default AdminProductsPage

const AddProductDialog = ({triggerRef} : {triggerRef: React.RefObject<HTMLButtonElement | null>}) => {

    const [formData, setFormData] = useState({
        name: "",
        price: 0,
        stockQuantity: 0
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
                    <div className="flex justify-end">
                        <Button onClick={handleSubmit}>Create Product</Button>
                    </div>
                </div>

            </DialogContent>
        </Dialog>
    )
}
