package com.ecommerce.PrimeBasket.repository;

import com.ecommerce.PrimeBasket.model.AppRole;
import com.ecommerce.PrimeBasket.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
