package com.trading.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.trading.entities.User;
import java.util.List;


public interface UserRepo extends JpaRepository<User, Long> {

    User findByEmail(String email);
}