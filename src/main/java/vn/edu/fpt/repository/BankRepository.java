package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {
}
