package by.devtools.payment.repository;

import by.devtools.payment.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Integer> {

    Optional<Balance> findByCustomerId(int userId);
}
