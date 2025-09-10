package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
  Optional<RefreshToken> findByToken(String refreshToken);

  @Transactional
  @Modifying
  @Query(
          "DELETE FROM RefreshToken t WHERE t.token = ?1"
  )
  void deleteByToken(String refreshToken);
}