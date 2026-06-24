import { useParams } from "react-router";
import AuthLayout from "../components/layouts/AuthLayout";
import { useEffect, useState } from "react";
import { apiClient } from "../lib/axios";
import type { Product } from "../types/product";
import SingleProductSection from "../components/sections/SingleProductSection";

const SingleProductPage = () => {
    const { id } = useParams();

    const [product, setProduct] = useState<Product | null>(null);

    useEffect(() => {
        apiClient.get(`/products/${id}`)
            .then(response => {
                setProduct(response.data);  
            })
            .catch(error => {
                console.error("Error fetching product:", error);
            });
    }, []);

    return (
        <AuthLayout>
            <main className="w-full flex flex-col items-center  min-h-screen">
                {
                    product ? (
                        <SingleProductSection product={product} />
                    ) : (
                        <div className="w-full min-h-screen flex items-center justify-center">
                            Loading....
                        </div>
                    )
                }
            </main>
        </AuthLayout>
    )
}

export default SingleProductPage
