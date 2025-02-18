package com.letsgo.user_service.user_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getFollower() {
        return follower;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowing() {
        return following;
    }

    public void setFollowing(User following) {
        this.following = following;
    }

    public LocalDateTime getConnectionTimestamp() {
        return connectionTimestamp;
    }

    public void setConnectionTimestamp(LocalDateTime connectionTimestamp) {
        this.connectionTimestamp = connectionTimestamp;
    }

    public Connection(UUID id, User follower, User following, LocalDateTime connectionTimestamp) {
        this.id = id;
        this.follower = follower;
        this.following = following;
        this.connectionTimestamp = connectionTimestamp;
    }
    public Connection() {
        // No-args constructor needed by frameworks like Hibernate
    }

}
