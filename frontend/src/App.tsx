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
    <main className="flex justify-center items-center min-h-screen">
      <div className="flex flex-col gap-5">
        <h1>Welcome to Vite</h1>
        <Button onClick={handleOrderClick}>Place Test Order</Button>
        <Button onClick={handleMetricsClick} variant={"secondary"}>Fetch Metrics</Button>
      </div>
    </main>
  )
}

export default App
