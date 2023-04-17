package entities;

import lombok.*;

import java.security.Timestamp;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class User {
    private Integer id;
    private String name;
    private String email;
    private String password;
    private Double balance;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public User(String email) {
        this.email = email;
    }

    public User(int id, String name, String email, String password, double balance, java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt) {
    }

    public User(String name, String email, String password, Double balance) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.balance = balance;
    }
}
