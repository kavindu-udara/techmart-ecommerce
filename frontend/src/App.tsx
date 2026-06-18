import ProductCard from "./components/cards/ProductCard";
import Header from "./components/common/Header";
import { Button } from "./components/ui/button"
import { apiClient } from "./lib/axios"

function App() {

  const handleOrderClick = async() => {
    const payload = {
      userId : 1,
      items: [
        {productId:1, quantity: 1}
      ],
    }

    await apiClient.post("/orders", payload).then(res => {
      console.log("Order placed successfully:", res.data)
    }).catch(err => {
      console.error("Error placing order:", err)
    });

  }

  const handleMetricsClick = async() => {
    await apiClient.get("/metrics").then(res => {
      console.log("Metrics fetched successfully:", res.data)
    }).catch(err => {
      console.error("Error fetching metrics:", err)
    });
  }


  return (
    <main className="min-h-screen flex flex-col items-center gap-5">
      <Header />
      <h1 className="text-center font-bold">Products</h1>
      <div className="grid grid-cols-4 gap-4 container">
        <ProductCard />
        <ProductCard />
        <ProductCard />
        <ProductCard />
      </div>

      <div className="flex flex-col gap-5 mt-10 items-center">
        <Button onClick={handleOrderClick}>Place Test Order</Button>
        <Button onClick={handleMetricsClick} variant={"secondary"}>Fetch Metrics</Button>
      </div>
    </main>
  )
}

export default App
