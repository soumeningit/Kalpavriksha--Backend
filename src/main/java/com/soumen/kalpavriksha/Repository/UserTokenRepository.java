package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Entity.UserToken;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserToken, Integer>
{
    @Query("SELECT ut FROM UserToken ut " +
            "WHERE ut.registrationToken = :token " +
            "AND ut.registerTokenExpiresAt > CURRENT_TIMESTAMP " +
            "ORDER BY ut.id DESC")
    List<UserToken> findValidTokens(@Param("token") String token, Pageable pageable);

    Optional<UserToken> findByUser(User user);

    @Query("SELECT ut FROM UserToken ut " +
            "WHERE ut.resetToken = ?1 " +
            "AND ut.resetTokenExpiresAt > CURRENT_TIMESTAMP " +
            "ORDER BY ut.id DESC")
    List<UserToken> findUserByToken(@Param("token") String token, Pageable pageable);
}