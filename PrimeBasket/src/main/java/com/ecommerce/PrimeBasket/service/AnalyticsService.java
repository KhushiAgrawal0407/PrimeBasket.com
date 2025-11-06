package com.ecommerce.PrimeBasket.service;

import com.ecommerce.PrimeBasket.payload.AnalyticsResponse;
import org.springframework.stereotype.Service;

@Service
public interface AnalyticsService {
    AnalyticsResponse getAnalyticsData();
}
