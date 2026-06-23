import { Button } from "../ui/button"

const Header = () => {

    const handleLogout = () => {
        localStorage.removeItem("token");
        window.location.href = "/login";
    }

  return (
    <header className="w-full border-b flex flex-row justify-between items-center p-3 shadow-sm">
      <h1>Header</h1>
      <Button onClick={handleLogout}>Logout</Button>
    </header>
  )
}

export default Header
