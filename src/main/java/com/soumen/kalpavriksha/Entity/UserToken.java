package com.soumen.kalpavriksha.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_tokens")
public class UserToken
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String registrationToken;

    @Column(name = "register_token_created_at")
    private LocalDateTime registerTokenCreatedAt;

    @Column(name = "register_token_expires_at")
    private LocalDateTime registerTokenExpiresAt;

    private String resetToken;

    @Column(name = "reset_token_created_at")
    private LocalDateTime resetTokenCreatedAt;

    @Column(name = "reset_token_expires_at")
    private LocalDateTime resetTokenExpiresAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
