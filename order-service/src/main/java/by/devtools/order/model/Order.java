package by.devtools.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "product_count")
    private Integer productCount;

    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "total_price")
    private Double totalPrice;

    @Column(name = "payment_status")
    private String paymentStatus;

    @Column(name = "inventory_status")
    private String inventoryStatus;

    @Column(name = "order_status")
    private String orderStatus;
}
