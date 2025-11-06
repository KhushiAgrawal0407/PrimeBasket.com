package com.ecommerce.PrimeBasket.controller;

import com.ecommerce.PrimeBasket.config.AppConstants;
import com.ecommerce.PrimeBasket.payload.AnalyticsResponse;
import com.ecommerce.PrimeBasket.payload.OrderResponse;
import com.ecommerce.PrimeBasket.payload.OrderResponseDTO;
import com.ecommerce.PrimeBasket.service.AnalyticsService;
import com.ecommerce.PrimeBasket.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/admin/app/analytics")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        AnalyticsResponse analyticsResponse = analyticsService.getAnalyticsData();
        return new ResponseEntity<>(analyticsResponse, HttpStatus.OK);
    }
}
