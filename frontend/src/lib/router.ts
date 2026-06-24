import {
  createBrowserRouter
} from "react-router";
import RootDataLoader from "../components/loaders/RootDataLoader";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";
import HomePage from "../pages/HomePage";
import SingleProductPage from "../pages/SingleProductPage";
import CartPage from "../pages/CartPage";

const router = createBrowserRouter([
  {
    path: "/",
    Component: HomePage,
    loader: RootDataLoader,
  },{
    path: "/login",
    Component: LoginPage,
    loader: RootDataLoader,
  }, {
    path: "/register",
    Component: RegisterPage,
    loader: RootDataLoader
  },{
    path: "/products/:id",
    Component: SingleProductPage,
    loader: RootDataLoader
  }, {
    path: "/cart",
    Component:CartPage,
    loader: RootDataLoader
  }
]);

export default router;