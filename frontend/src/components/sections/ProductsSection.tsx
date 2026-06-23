import { useEffect, useState } from 'react'
import { apiClient } from '../../lib/axios';
import type { Product } from '../../types/product';
import ProductCard from '../cards/ProductCard';

const ProductsSection = () => {

    const [products, setProducts] = useState<Product[]>([]);

    useEffect(() => {
        apiClient.get("/products")
            .then(response => {
                setProducts(response.data);
            })
            .catch(error => {
                console.error("Error fetching products:", error);
            });
    }, []);

    return (
        <section className="w-full grid grid-cols-3 gap-4 p-4 container mx-auto">
            {products.map(product => (
                <ProductCard key={product.id} product={product} />
            ))}
        </section>
    )
}

export default ProductsSection

