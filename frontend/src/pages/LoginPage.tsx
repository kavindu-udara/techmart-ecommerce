import { useState } from "react";
import { Button } from "../components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "../components/ui/card";
import { toast } from "react-toastify";
import { apiClient } from "../lib/axios";

const LoginPage = () => {

    const [formData, setFormData] = useState({
        email: "",
        password: ""
    });

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    }

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        
        // check if email and password are filled
        if (!formData.email || !formData.password) {
            toast.error("Please fill in all the fields");
            return;
        }

        apiClient.post("/login", {
            email: formData.email,
            password: formData.password
        })
        .then((response) => {
            // store the token in localStorage            
            localStorage.setItem("token", response.data.token);
            toast.success("Login successful!");
        })
        .catch((error) => {
            toast.error("Login failed. Please check your credentials and try again.");
        });

    }

    return (
        <main className="w-full min-h-screen flex flex-col gap-3 justify-center items-center">
            <Card className="w-1/3">
                <CardHeader>
                    <CardTitle>Login</CardTitle>
                    <CardDescription>Please fill in the details to log in to your account</CardDescription>
                </CardHeader>
                <CardContent className="flex flex-col gap-4">
                    {/* email */}
                    <div className="flex flex-col gap-2">
                        <label htmlFor="email">Email</label>
                        <input
                            type="email"
                            id="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            className="border border-gray-300 rounded-md py-2 px-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                    {/* password */}
                    <div className="flex flex-col gap-2">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            className="border border-gray-300 rounded-md py-2 px-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                </CardContent>
                <CardFooter className="flex justify-end">
                    <Button onClick={handleSubmit}>Login</Button>
                </CardFooter>
            </Card>
            <a href="/register" className="text-blue-500 hover:underline">
                Don't have an account? Register
            </a>
        </main>
    )
}

export default LoginPage
