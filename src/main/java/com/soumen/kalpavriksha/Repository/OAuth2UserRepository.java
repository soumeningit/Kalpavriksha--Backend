package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.OAuth2User;
import com.soumen.kalpavriksha.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuth2UserRepository extends JpaRepository<OAuth2User, Integer>
{
  boolean existsByUser(User user);
}