import { useNavigate } from "react-router";
import { Button } from "../ui/button";
import AuthLayout from "./AuthLayout";

type Props = {
    children?: React.ReactNode;
    title?: string;
}

const AdminDashboardLayout = ({ children, title }: Props) => {

    const navigate = useNavigate();

    return (
        <AuthLayout>
            <div className="flex flex-wrap gap-3 container mx-auto p-5">
                <Button onClick={() => navigate("/admin/dashboard")}>Go back</Button>
                <h1 className="font-semibold text-2xl">{title || ""}</h1>
            </div>
            {children}
        </AuthLayout>
    )
}

export default AdminDashboardLayout
