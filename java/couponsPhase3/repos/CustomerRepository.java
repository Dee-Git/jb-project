package couponsPhase3.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import couponsPhase3.tables.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

	Customer findByEmailAndPassword(String email, String password);

	Customer findByEmail(String email);

	boolean existsByEmail(String email);
}
