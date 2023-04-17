package entities;

import lombok.*;

import java.security.Timestamp;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer id;
    private String name;
    private int quantity;
    private Double price;
    private Double sellingPrice;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Product(int id, String name, int quantity, double price, double sellingPrice, java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt) {
    }
}
