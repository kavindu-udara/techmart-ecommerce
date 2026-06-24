import { useEffect, type ReactNode, useState } from "react"
import { apiClient } from "../../lib/axios";
import { useNavigate, useLocation } from "react-router";
import Header from "../common/Header";

const AuthLayout = ({ children }: { children: ReactNode }) => {
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const token = localStorage.getItem("token");

        // Skip auth check on login page
        if (location.pathname === "/login") {
            setIsLoading(false);
            return;
        }

        // If no token, redirect to login
        if (!token) {
            navigate("/login", { replace: true });
            setIsLoading(false);
            return;
        }

        // Verify token with API
        const verifyToken = async () => {
            try {
                await apiClient.get("/me");
                // Token is valid
                setIsLoading(false);
            } catch (error) {
                console.error("Authentication error", error);
                localStorage.removeItem("token");
                navigate("/login", { replace: true });
                setIsLoading(false);
            }
        };

        verifyToken();
    }, [navigate, location.pathname]);

    // Show loading state while checking authentication
    if (isLoading) {
        return (
            <div className="flex items-center justify-center h-screen">
                <div className="text-center">
                    <div className="spinner-border animate-spin" role="status">
                        <span className="sr-only">Loading...</span>
                    </div>
                    <p>Verifying authentication...</p>
                </div>
            </div>
        );
    }

    return (<>
        <Header />
        {children}
    </>)
}

export default AuthLayout;