export type AdminDashboardResponseType = {
    totalUsers: number;
    totalOrders: number;
    totalRevenue: number;
    totalProducts: number;
    pendingOrders: number;
    completedOrders: number;
}

export type UserResponseType = {
    id: number;
    role : "ADMIN" | "CUSTOMER" ;
    email: string;
    createdAt: string;
}