package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A DeliveryMan.
 */
@Entity
@Table(name = "delivery_man")
public class DeliveryMan implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "rides")
    private Integer rides;

    @Column(name = "earned")
    private Integer earned;

    @Column(name = "rating")
    private String rating;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "deliveryMan")
    @JsonIgnoreProperties(value = { "deliveryMan", "shop", "client" }, allowSetters = true)
    private Set<Order> deliveries = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DeliveryMan id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVehicleType() {
        return this.vehicleType;
    }

    public DeliveryMan vehicleType(String vehicleType) {
        this.setVehicleType(vehicleType);
        return this;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Integer getRides() {
        return this.rides;
    }

    public DeliveryMan rides(Integer rides) {
        this.setRides(rides);
        return this;
    }

    public void setRides(Integer rides) {
        this.rides = rides;
    }

    public Integer getEarned() {
        return this.earned;
    }

    public DeliveryMan earned(Integer earned) {
        this.setEarned(earned);
        return this;
    }

    public void setEarned(Integer earned) {
        this.earned = earned;
    }

    public String getRating() {
        return this.rating;
    }

    public DeliveryMan rating(String rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DeliveryMan user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Order> getDeliveries() {
        return this.deliveries;
    }

    public void setDeliveries(Set<Order> orders) {
        if (this.deliveries != null) {
            this.deliveries.forEach(i -> i.setDeliveryMan(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setDeliveryMan(this));
        }
        this.deliveries = orders;
    }

    public DeliveryMan deliveries(Set<Order> orders) {
        this.setDeliveries(orders);
        return this;
    }

    public DeliveryMan addDelivery(Order order) {
        this.deliveries.add(order);
        order.setDeliveryMan(this);
        return this;
    }

    public DeliveryMan removeDelivery(Order order) {
        this.deliveries.remove(order);
        order.setDeliveryMan(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryMan)) {
            return false;
        }
        return id != null && id.equals(((DeliveryMan) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DeliveryMan{" +
            "id=" + getId() +
            ", vehicleType='" + getVehicleType() + "'" +
            ", rides=" + getRides() +
            ", earned=" + getEarned() +
            ", rating='" + getRating() + "'" +
            "}";
    }
}
