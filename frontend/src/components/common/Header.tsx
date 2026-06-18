import { Button } from "../ui/button"

const Header = () => {
  return (
    <header className="w-full border-b flex flex-row justify-between items-center p-3 shadow-sm">
      <h1>Header</h1>
      <Button>Logout</Button>
    </header>
  )
}

export default Header
