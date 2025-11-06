package com.ecommerce.PrimeBasket.payload;

import com.ecommerce.PrimeBasket.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private Set<Role> roles = new HashSet<>();
    private AddressDTO addressDTO;
    private CartDTO cartDTO;
}
