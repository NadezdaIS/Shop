package entities;

import lombok.*;

import java.security.Timestamp;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class Order {
    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;

    private Timestamp purchaseDate;

    public Order(int id, int userId, int productId, int quantity, java.sql.Timestamp purchaseDate) {
    }

    public Order(Integer userId, Integer productId, Integer quantity) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
    }
}
