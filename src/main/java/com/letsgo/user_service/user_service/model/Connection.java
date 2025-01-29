package com.letsgo.user_service.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Connection {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Many connection entities can be linked to one user.

    // The user who is following
    @ManyToOne
    @JoinColumn(name = "follower_id", referencedColumnName = "id", nullable = false)
    private User follower;

    // The user being followed
    @ManyToOne
    @JoinColumn(name = "following_id", referencedColumnName = "id", nullable = false)
    private User following;

    // Timestamp when the connection was made
    @UpdateTimestamp
    @Column(name = "connection_timestamp", nullable = false)
    private LocalDateTime connectionTimestamp;
}
