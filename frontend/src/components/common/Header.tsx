import { Button } from "../ui/button"
import { ShoppingCart } from "lucide-react"

const Header = () => {

    const handleLogout = () => {
        localStorage.removeItem("token");
        window.location.href = "/login";
    }

    return (
        <header className="w-full border-b flex flex-row justify-between items-center p-3 shadow-sm">
            <h1 className="text-xl font-bold">TechMart</h1>
            <div className="flex flex-row gap-2 items-center">
                <Button onClick={() => window.location.href = "/"} className="mr-2">
                    <ShoppingCart />
                </Button>
                <Button onClick={handleLogout}>Logout</Button>
            </div>
        </header>
    )
}

export default Header
