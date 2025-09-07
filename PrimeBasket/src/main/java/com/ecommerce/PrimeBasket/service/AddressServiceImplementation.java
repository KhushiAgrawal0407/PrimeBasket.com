package com.ecommerce.PrimeBasket.service;

import com.ecommerce.PrimeBasket.exceptions.APIException;
import com.ecommerce.PrimeBasket.exceptions.ResourceNotFoundException;
import com.ecommerce.PrimeBasket.model.Address;
import com.ecommerce.PrimeBasket.model.User;
import com.ecommerce.PrimeBasket.payload.AddressDTO;
import com.ecommerce.PrimeBasket.repository.AddressRepository;
import com.ecommerce.PrimeBasket.repository.UserRepository;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImplementation implements AddressService{

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public AddressDTO createAddress(AddressDTO addressDTO, User user) {
        Address address = modelMapper.map(addressDTO, Address.class);

        address.setUser(user);

        List<Address> addressList = user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        Address savedAddress = addressRepository.save(address);
        return modelMapper.map(savedAddress, AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addressList = addressRepository.findAll();
        if(addressList.isEmpty()){
            throw new APIException("no address exists!");
        }
        List<AddressDTO> addressDTOList = addressList.stream()
                .map(address -> modelMapper.map(address,AddressDTO.class))
                .collect(Collectors.toList());
        return addressDTOList;
    }

    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAddressByUser(User user) {
        List<Address> addressList = user.getAddresses();
        List<AddressDTO> addressDTOList= addressList.stream()
                .map(address -> modelMapper.map(address,AddressDTO.class))
                .collect(Collectors.toList());
        return addressDTOList;
    }

    @Override
    public AddressDTO updateAddress(AddressDTO addressDTO, Long addressId) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address", "addressId", addressId));

        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setPincode(addressDTO.getPincode());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());
        addressFromDatabase.setCountry(addressDTO.getCountry());

        Address updatedAddress = addressRepository.save(addressFromDatabase);

        User user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(updatedAddress,AddressDTO.class);
    }

    @Override
    public String deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(()->new ResourceNotFoundException("Address", "addressId", addressId));

        User user = address.getUser();
        user.getAddresses().removeIf(ad->ad.getAddressId().equals(addressId));
        userRepository.save(user);

        addressRepository.delete(address);

        return "Address deleted successfully with address id: " + addressId;
    }
}
