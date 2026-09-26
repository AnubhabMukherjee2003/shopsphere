package com.example.shopsphere.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopsphere.model.User;
import java.util.Optional;

public interface userRepo extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
}
