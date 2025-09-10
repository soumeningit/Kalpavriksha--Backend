package com.soumen.kalpavriksha.Repository;

import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Integer>
{
    Optional<UserProfile> findByUser(User user);
}