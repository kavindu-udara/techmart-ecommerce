package com.techmart.dto;

import java.math.BigDecimal;

public class AdminDashboardResponse {
    private long totalUsers;
    private long totalOrders;
    private long totalProducts;
    private BigDecimal totalRevenue;
    private long pendingOrders;
    private long completedOrders;

    public AdminDashboardResponse() {}

    public AdminDashboardResponse(long totalUsers, long totalOrders, long totalProducts,
                                  BigDecimal totalRevenue, long pendingOrders, long completedOrders) {
        this.totalUsers = totalUsers;
        this.totalOrders = totalOrders;
        this.totalProducts = totalProducts;
        this.totalRevenue = totalRevenue;
        this.pendingOrders = pendingOrders;
        this.completedOrders = completedOrders;
    }

    public long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(long completedOrders) {
        this.completedOrders = completedOrders;
    }

    public long getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }
}
