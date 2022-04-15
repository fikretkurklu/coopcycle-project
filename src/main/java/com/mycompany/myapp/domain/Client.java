package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Client.
 */
@Entity
@Table(name = "client")
public class Client implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "balance")
    private Integer balance;

    @Column(name = "order_count")
    private Integer orderCount;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "client")
    @JsonIgnoreProperties(value = { "deliveryMan", "shop", "client" }, allowSetters = true)
    private Set<Order> deliveries = new HashSet<>();

    @OneToMany(mappedBy = "client")
    @JsonIgnoreProperties(value = { "shop", "client" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Client id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getBalance() {
        return this.balance;
    }

    public Client balance(Integer balance) {
        this.setBalance(balance);
        return this;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getOrderCount() {
        return this.orderCount;
    }

    public Client orderCount(Integer orderCount) {
        this.setOrderCount(orderCount);
        return this;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Client user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Order> getDeliveries() {
        return this.deliveries;
    }

    public void setDeliveries(Set<Order> orders) {
        if (this.deliveries != null) {
            this.deliveries.forEach(i -> i.setClient(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setClient(this));
        }
        this.deliveries = orders;
    }

    public Client deliveries(Set<Order> orders) {
        this.setDeliveries(orders);
        return this;
    }

    public Client addDelivery(Order order) {
        this.deliveries.add(order);
        order.setClient(this);
        return this;
    }

    public Client removeDelivery(Order order) {
        this.deliveries.remove(order);
        order.setClient(null);
        return this;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setClient(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setClient(this));
        }
        this.payments = payments;
    }

    public Client payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public Client addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setClient(this);
        return this;
    }

    public Client removePayment(Payment payment) {
        this.payments.remove(payment);
        payment.setClient(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Client)) {
            return false;
        }
        return id != null && id.equals(((Client) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Client{" +
            "id=" + getId() +
            ", balance=" + getBalance() +
            ", orderCount=" + getOrderCount() +
            "}";
    }
}
