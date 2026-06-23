import type { Product } from "../../types/product";
import {
    Card,
    CardContent,
    CardFooter,
} from "../ui/card";

type Props = {
    product: Product;
}

const ProductCard = ({ product }: Props) => {
    return (
        <Card key={product.id} className="flex flex-col">
            <img src={product.imageUrl} alt={product.name} className="w-full h-48 object-cover mb-4" />
            <CardContent>
                <h2 className="text-lg font-semibold">{product.name}</h2>
                <p className="text-gray-500">Stock: {product.stockQuantity}</p>
            </CardContent>
            <CardFooter>
                <p className="text-gray-600">${product.price.toFixed(2)}</p>
            </CardFooter>
        </Card>
    )
}

export default ProductCard
