import Header from '../components/common/Header'
import AuthLayout from '../components/layouts/AuthLayout'
import ProductsSection from '../components/sections/ProductsSection'

const HomePage = () => {
    return (
        <AuthLayout>
            <Header />
            <ProductsSection />
        </AuthLayout>
    )
}

export default HomePage
