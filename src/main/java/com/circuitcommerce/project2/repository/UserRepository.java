package com.circuitcommerce.project2.repository;

import java.util.Optional;

import com.circuitcommerce.project2.dto.UserOrderDto;
import com.circuitcommerce.project2.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  public Optional<User> findByUsername(String username);

}
