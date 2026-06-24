import { useNavigate } from "react-router"
import { Button } from "../ui/button"
import { ShoppingCart } from "lucide-react"

const Header = () => {

    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/login");
    }

    return (
        <header className="w-full border-b flex flex-row justify-between items-center p-3 shadow-sm">
            <h1 className="text-xl font-bold cursor-pointer" onClick={() => navigate("/")}>TechMart</h1>
            <div className="flex flex-row gap-2 items-center">
                <Button onClick={() => navigate("/cart")} className="mr-2">
                    <ShoppingCart />
                </Button>
                <Button onClick={handleLogout}>Logout</Button>
            </div>
        </header>
    )
}

export default Header
