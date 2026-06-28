import { useNavigate } from "react-router";
import AuthLayout from "../../components/layouts/AuthLayout"
import { Button } from "../../components/ui/button"

const AdminUsersPage = () => {

    const navigate = useNavigate();

  return (
    <AuthLayout>
        <Button onClick={() => navigate(-1)}>Go back</Button>
      users
    </AuthLayout>
  )
}

export default AdminUsersPage
