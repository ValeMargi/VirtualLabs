package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.PasswordResetToken;
import it.polito.ai.virtualLabs.entities.UserDAO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.stream.Stream;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);

    @Transactional
    @Modifying
    @Query("delete from PasswordResetToken t where t.expiryDate <=:now")
    void deleteAllExpiredSince(Date now);
    @Transactional
    @Modifying
    void deleteByToken(String token);

}