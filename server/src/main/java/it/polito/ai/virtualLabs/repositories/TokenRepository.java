package it.polito.ai.virtualLabs.repositories;

import it.polito.ai.virtualLabs.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    List<Token> findAllByExpiryDateBefore(Timestamp t);
    List<Token> findAllByTeamId(Long teamId);

    @Transactional
    @Modifying
    @Query("delete  FROM Token  t  WHERE t.teamId=:teamId  ")
    void deleteFromTokenDBexpired(Long teamId);


}
