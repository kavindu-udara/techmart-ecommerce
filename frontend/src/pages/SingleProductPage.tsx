import { useParams } from "react-router";
import AuthLayout from "../components/layouts/AuthLayout";

const SingleProductPage = () => {
    const { id } = useParams();

  return (
    <AuthLayout>
      single product page for product id: {id}
    </AuthLayout>
  )
}

export default SingleProductPage
