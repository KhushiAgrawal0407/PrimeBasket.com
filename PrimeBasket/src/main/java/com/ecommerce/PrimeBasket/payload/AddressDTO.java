package com.ecommerce.PrimeBasket.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long addressID;
    private String street;
    private String buildingName;
    private String city;
    private String state;
    private String country;
    private String pincode;
}
