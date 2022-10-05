package me.kqlqk.behealthy.authentication_service.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "refresh_tokens")
@Data
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, insertable = false, updatable = false)
    private long id;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "expires", nullable = false)
    private long expires;
}
