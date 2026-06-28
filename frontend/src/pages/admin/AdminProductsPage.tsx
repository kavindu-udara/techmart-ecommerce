import { useEffect, useRef, useState } from "react"
import AdminDashboardLayout from "../../components/layouts/AdminDashboardLayout"
import { apiClient } from "../../lib/axios";
import type { Product } from "../../types/product";
import { Button } from "../../components/ui/button";
import AddProductDialog from "../../components/dialogs/AddProductDialog";
import UpdateProductDialog from "../../components/dialogs/UpdateProductDialog";

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
                            <td className="border">
                                <Button onClick={() => handleProductUpdateDialog(product)}>Edit</Button>
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
