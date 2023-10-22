package by.devtools.domain;

import java.util.Objects;

public class OrderDto {

    private Integer id;
    private Integer productCount;
    private Integer customerId;
    private Integer productId;
    private Double totalPrice;
    private String orderStatus;
    private String paymentStatus;
    private String inventoryStatus;

    public OrderDto() {
    }

    public OrderDto(Integer id, Integer productCount, Integer customerId, Integer productId, Double totalPrice, String orderStatus, String paymentStatus, String inventoryStatus) {
        this.id = id;
        this.productCount = productCount;
        this.customerId = customerId;
        this.productId = productId;
        this.totalPrice = totalPrice;
        this.orderStatus = orderStatus;
        this.paymentStatus = paymentStatus;
        this.inventoryStatus = inventoryStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductCount() {
        return productCount;
    }

    public void setProductCount(Integer productCount) {
        this.productCount = productCount;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getInventoryStatus() {
        return inventoryStatus;
    }

    public void setInventoryStatus(String inventoryStatus) {
        this.inventoryStatus = inventoryStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDto orderDto = (OrderDto) o;
        return Objects.equals(id, orderDto.id) &&
                Objects.equals(productCount, orderDto.productCount) &&
                Objects.equals(customerId, orderDto.customerId) &&
                Objects.equals(productId, orderDto.productId) &&
                Objects.equals(totalPrice, orderDto.totalPrice) &&
                Objects.equals(orderStatus, orderDto.orderStatus) &&
                Objects.equals(paymentStatus, orderDto.paymentStatus) &&
                Objects.equals(inventoryStatus, orderDto.inventoryStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, productCount, customerId, productId, totalPrice, orderStatus, paymentStatus, inventoryStatus);
    }
}
