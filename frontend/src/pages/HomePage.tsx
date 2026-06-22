import Header from '../components/common/Header'
import AuthLayout from '../components/layouts/AuthLayout'

const HomePage = () => {
    return (
        <AuthLayout>
            <Header />
            <main className="w-full min-h-screen flex flex-col gap-3 justify-center items-center">
                Welcome to the Home Page
            </main>
        </AuthLayout>
    )
}

export default HomePage
