export type OrderItem = {
  productId: number;
  productName: string;
  productImage: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
}

export type Order = {
  id: number;
  totalAmount: number;
  status: "PENDING" | "COMPLETED" | "CANCELLED";
  createdAt: Date;
  items: OrderItem[];
}
