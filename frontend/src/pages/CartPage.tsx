import { useEffect, useState } from 'react'
import AuthLayout from '../components/layouts/AuthLayout'
import type { Cart } from '../types/cart';
import { apiClient } from '../lib/axios';
import CartSection from '../components/sections/CartSection';

const CartPage = () => {

    const [cart, setCart] = useState<Cart | null>(null);

    useEffect(() => {
        apiClient.get("/cart").then(response => {
            setCart(response.data);
        }).catch(error => {
            console.error("Error fetching cart:", error);
        });
    }, []);

  return (
    <AuthLayout>
      <main className='w-full flex justify-center min-h-screen'>
      {
        cart ? (
            <CartSection cart={cart} />
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

export default CartPage
