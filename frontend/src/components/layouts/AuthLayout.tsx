import { useEffect, type ReactNode } from "react"
import { apiClient } from "../../lib/axios";
import { useNavigate } from "react-router";

const AuthLayout = ({ children }: { children: ReactNode }) => {

    const navigate = useNavigate();

    // send api req to /me and if unauthorized, return to the login
    const token = localStorage.getItem("token");

    const handleAuthentication = () => {

        if (!token) {
            navigate("/login");
        } else {
            apiClient.get("/me")
                .then((response) => {
                    // token is valid, do nothing
                    console.log("User is authenticated", response.data);
                })
                .catch((error) => {
                    console.error("Authentication error", error);
                    // token is invalid, redirect to login
                    localStorage.removeItem("token");
                    navigate("/login");
                });
        }
    }

    useEffect(() => {
        handleAuthentication();
    }, [token]);

    return (
        <>
            {children}
        </>
    )
}

export default AuthLayout
