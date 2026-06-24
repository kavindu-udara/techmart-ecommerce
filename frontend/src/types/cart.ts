export type CartItem = {
    itemId: number;
    productId: number;
    productName: string;
    productImage: string;
    unitPrice: number;
    quantity: number;
    subtotal: number;
}

export type Cart = {
    cartId: number;
    items: CartItem[];
    totalAmount: number;
    totalItems: number;
}
