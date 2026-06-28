import { useEffect, useRef, useState } from "react"
import AdminDashboardLayout from "../../components/layouts/AdminDashboardLayout"
import { apiClient } from "../../lib/axios";
import type { Product } from "../../types/product";
import { Button } from "../../components/ui/button";
import AddProductDialog from "../../components/dialogs/AddProductDialog";
import UpdateProductDialog from "../../components/dialogs/UpdateProductDialog";
import { toast } from "react-toastify";

const AdminProductsPage = () => {

    const createProductDialogTriggerRef = useRef<HTMLButtonElement>(null);
    const updateProductDialogTriggerRef = useRef<HTMLButtonElement>(null);

    const [products, setProducts] = useState<Product[]>([]);
    const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);

    useEffect(() => {
        apiClient.get("/products").then((response) => {
            setProducts(response.data);
        }).catch((error) => {
            console.error("Error fetching products:", error);
        });
    }, []);

    const handleProductUpdateDialog = (product: Product) => {
        setSelectedProduct(product);
        updateProductDialogTriggerRef.current?.click();
    };

    const handleProductDelete = (productId: number) => {
        if (!confirm("Are you sure you want to delete this product?")) {
            return;
        }

        apiClient.delete(`/admin/products/${productId}`)
            .then(() => {
                toast.success("Product deleted successfully!");
                setProducts(prevProducts => prevProducts.filter(product => product.id !== productId));
            })
            .catch((error) => {
                console.error("Error deleting product:", error);
                toast.error("Failed to delete product. Please try again.");
            });
    }

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
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {products.map((product) => (
                        <tr key={product.id}>
                            <td className="border">{product.id}</td>
                            <td className="border">{product.name}</td>
                            <td className="border">${product.price.toFixed(2)}</td>
                            <td className="border">{product.stockQuantity}</td>
                            <td className="border flex flex-wrap gap-5">
                                <Button onClick={() => handleProductUpdateDialog(product)}>Edit</Button>
                                <Button variant="destructive" onClick={() => handleProductDelete(product.id)}>Delete</Button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <AddProductDialog triggerRef={createProductDialogTriggerRef} />
            {
                selectedProduct && (
                    <UpdateProductDialog product={selectedProduct} triggerRef={updateProductDialogTriggerRef} />
                )
            }

        </AdminDashboardLayout>
    )
}

export default AdminProductsPage
