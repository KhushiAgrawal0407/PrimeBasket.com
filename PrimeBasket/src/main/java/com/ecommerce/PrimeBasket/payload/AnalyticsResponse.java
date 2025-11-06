package com.ecommerce.PrimeBasket.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsResponse {
    private String productCount;
    private String totalOrders;
    private String totalRevenue;
}
