package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;

/**
 * A Shop.
 */
@Entity
@Table(name = "shop")
public class Shop implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "rating")
    private String rating;

    @Column(name = "open")
    private Boolean open;

    @Column(name = "average_delivery_time")
    private Integer averageDeliveryTime;

    @Column(name = "closing_hour")
    private ZonedDateTime closingHour;

    @Column(name = "opening_hour")
    private ZonedDateTime openingHour;

    @Column(name = "tags")
    private String tags;

    @OneToOne
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(mappedBy = "shop")
    @JsonIgnoreProperties(value = { "deliveryMan", "shop", "client" }, allowSetters = true)
    private Set<Order> deliveries = new HashSet<>();

    @OneToMany(mappedBy = "shop")
    @JsonIgnoreProperties(value = { "shop", "client" }, allowSetters = true)
    private Set<Payment> payments = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "shops" }, allowSetters = true)
    private Cooperative cooperative;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Shop id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRating() {
        return this.rating;
    }

    public Shop rating(String rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Boolean getOpen() {
        return this.open;
    }

    public Shop open(Boolean open) {
        this.setOpen(open);
        return this;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public Integer getAverageDeliveryTime() {
        return this.averageDeliveryTime;
    }

    public Shop averageDeliveryTime(Integer averageDeliveryTime) {
        this.setAverageDeliveryTime(averageDeliveryTime);
        return this;
    }

    public void setAverageDeliveryTime(Integer averageDeliveryTime) {
        this.averageDeliveryTime = averageDeliveryTime;
    }

    public ZonedDateTime getClosingHour() {
        return this.closingHour;
    }

    public Shop closingHour(ZonedDateTime closingHour) {
        this.setClosingHour(closingHour);
        return this;
    }

    public void setClosingHour(ZonedDateTime closingHour) {
        this.closingHour = closingHour;
    }

    public ZonedDateTime getOpeningHour() {
        return this.openingHour;
    }

    public Shop openingHour(ZonedDateTime openingHour) {
        this.setOpeningHour(openingHour);
        return this;
    }

    public void setOpeningHour(ZonedDateTime openingHour) {
        this.openingHour = openingHour;
    }

    public String getTags() {
        return this.tags;
    }

    public Shop tags(String tags) {
        this.setTags(tags);
        return this;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Shop user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Order> getDeliveries() {
        return this.deliveries;
    }

    public void setDeliveries(Set<Order> orders) {
        if (this.deliveries != null) {
            this.deliveries.forEach(i -> i.setShop(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setShop(this));
        }
        this.deliveries = orders;
    }

    public Shop deliveries(Set<Order> orders) {
        this.setDeliveries(orders);
        return this;
    }

    public Shop addDelivery(Order order) {
        this.deliveries.add(order);
        order.setShop(this);
        return this;
    }

    public Shop removeDelivery(Order order) {
        this.deliveries.remove(order);
        order.setShop(null);
        return this;
    }

    public Set<Payment> getPayments() {
        return this.payments;
    }

    public void setPayments(Set<Payment> payments) {
        if (this.payments != null) {
            this.payments.forEach(i -> i.setShop(null));
        }
        if (payments != null) {
            payments.forEach(i -> i.setShop(this));
        }
        this.payments = payments;
    }

    public Shop payments(Set<Payment> payments) {
        this.setPayments(payments);
        return this;
    }

    public Shop addPayment(Payment payment) {
        this.payments.add(payment);
        payment.setShop(this);
        return this;
    }

    public Shop removePayment(Payment payment) {
        this.payments.remove(payment);
        payment.setShop(null);
        return this;
    }

    public Cooperative getCooperative() {
        return this.cooperative;
    }

    public void setCooperative(Cooperative cooperative) {
        this.cooperative = cooperative;
    }

    public Shop cooperative(Cooperative cooperative) {
        this.setCooperative(cooperative);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Shop)) {
            return false;
        }
        return id != null && id.equals(((Shop) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Shop{" +
            "id=" + getId() +
            ", rating='" + getRating() + "'" +
            ", open='" + getOpen() + "'" +
            ", averageDeliveryTime=" + getAverageDeliveryTime() +
            ", closingHour='" + getClosingHour() + "'" +
            ", openingHour='" + getOpeningHour() + "'" +
            ", tags='" + getTags() + "'" +
            "}";
    }
}
