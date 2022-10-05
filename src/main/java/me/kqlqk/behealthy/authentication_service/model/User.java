package me.kqlqk.behealthy.authentication_service.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, insertable = false, updatable = false)
    private long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "refresh_token_id", referencedColumnName = "id")
    @EqualsAndHashCode.Exclude
    private RefreshToken refreshToken;

    public User(String name, String email, String password, RefreshToken refreshToken) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.refreshToken = refreshToken;
    }
}
