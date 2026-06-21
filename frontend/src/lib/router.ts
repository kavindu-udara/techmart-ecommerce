import {
  createBrowserRouter
} from "react-router";
import RootDataLoader from "../components/loaders/RootDataLoader";
import App from "../App";
import LoginPage from "../pages/LoginPage";
import RegisterPage from "../pages/RegisterPage";

const router = createBrowserRouter([
  {
    path: "/",
    Component: App,
    loader: RootDataLoader,
  },{
    path: "/login",
    Component: LoginPage,
    loader: RootDataLoader,
  }, {
    path: "/register",
    Component: RegisterPage,
    loader: RootDataLoader
  }
]);

export default router;