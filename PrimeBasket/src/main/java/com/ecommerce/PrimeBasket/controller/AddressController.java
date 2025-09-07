package com.ecommerce.PrimeBasket.controller;

import com.ecommerce.PrimeBasket.model.User;
import com.ecommerce.PrimeBasket.payload.AddressDTO;
import com.ecommerce.PrimeBasket.payload.CartDTO;
import com.ecommerce.PrimeBasket.service.AddressService;
import com.ecommerce.PrimeBasket.util.AuthUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    @Autowired
    AuthUtil authUtil;

    @Autowired
    AddressService addressService;

    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User user = authUtil.loggedInUser();
        AddressDTO createdAddressDTO = addressService.createAddress(addressDTO, user);
        return new ResponseEntity<>(createdAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getAllAddresses() {
        List<AddressDTO> addressDTOS = addressService.getAllAddresses();
        return new ResponseEntity<>(addressDTOS, HttpStatus.OK);
    }

    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getAddressById(@PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @GetMapping("/users/addresses")
    public ResponseEntity<List<AddressDTO>> getAddressByUser() {
        User user = authUtil.loggedInUser();
        List<AddressDTO> addressDTOList = addressService.getAddressByUser(user);
        return new ResponseEntity<>(addressDTOList, HttpStatus.OK);
    }

    @PutMapping("/addresses/{addressId}")   //addressDto is with which it will be updated.
    public ResponseEntity<AddressDTO> updateAddress(@Valid @RequestBody AddressDTO addressDTO, @PathVariable Long addressId){
        AddressDTO updatedAddressDTO = addressService.updateAddress(addressDTO, addressId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<String> deleteAddress(@PathVariable Long addressId) {
        String status = addressService.deleteAddress(addressId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}
