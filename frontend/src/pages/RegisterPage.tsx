import { Button } from "../components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "../components/ui/card";

const RegisterPage = () => {
    return (
        <main className="w-full min-h-screen flex flex-col gap-3 justify-center items-center">
            <Card className="w-1/3">
                <CardHeader>
                    <CardTitle>Register</CardTitle>
                    <CardDescription>Please fill in the details to create an account</CardDescription>
                </CardHeader>
                <CardContent className="flex flex-col gap-4">
                    {/* email */}
                    <div className="flex flex-col gap-2">
                        <label htmlFor="email">Email</label>
                        <input type="email" id="email" className="border border-gray-300 rounded-md py-2 px-4 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                    </div>
                    {/* password */}
                    <div className="flex flex-col gap-2">
                        <label htmlFor="password">Password</label>
                        <input type="password" id="password" className="border border-gray-300 rounded-md py-2 px-4 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                    </div>
                    {/* confirm password */}
                    <div className="flex flex-col gap-2">
                        <label htmlFor="confirmPassword">Confirm Password</label>
                        <input type="password" id="confirmPassword" className="border border-gray-300 rounded-md py-2 px-4 focus:outline-none focus:ring-2 focus:ring-blue-500" />
                    </div>
                </CardContent>
                <CardFooter className="flex justify-end">
                    <Button>Register</Button>
                </CardFooter>
            </Card>
            <a href="/login" className="text-blue-500 hover:underline">
                Already have an account? Login
            </a>
        </main>
    )
}

export default RegisterPage
