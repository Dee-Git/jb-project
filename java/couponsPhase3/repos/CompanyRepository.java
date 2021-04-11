package couponsPhase3.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import couponsPhase3.tables.Company;

public interface CompanyRepository extends JpaRepository<Company, Integer> {

	Company findByEmailAndPassword(String email, String password);

	Company findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByName(String name);
}
