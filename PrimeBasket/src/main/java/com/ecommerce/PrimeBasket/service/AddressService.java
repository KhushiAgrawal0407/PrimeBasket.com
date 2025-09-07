package com.ecommerce.PrimeBasket.service;

import com.ecommerce.PrimeBasket.model.User;
import com.ecommerce.PrimeBasket.payload.AddressDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface AddressService {
    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressID);

    List<AddressDTO> getAddressByUser(User user);

    String deleteAddress(Long addressId);

    AddressDTO updateAddress(AddressDTO addressDTO, Long addressId);
}
