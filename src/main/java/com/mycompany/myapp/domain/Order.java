package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;

/**
 * A Order.
 */
@Entity
@Table(name = "jhi_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "takeout_adress")
    private String takeoutAdress;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "deliveries" }, allowSetters = true)
    private DeliveryMan deliveryMan;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "deliveries", "payments", "cooperative" }, allowSetters = true)
    private Shop shop;

    @ManyToOne
    @JsonIgnoreProperties(value = { "user", "deliveries", "payments" }, allowSetters = true)
    private Client client;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Order id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeliveryAddress() {
        return this.deliveryAddress;
    }

    public Order deliveryAddress(String deliveryAddress) {
        this.setDeliveryAddress(deliveryAddress);
        return this;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getTakeoutAdress() {
        return this.takeoutAdress;
    }

    public Order takeoutAdress(String takeoutAdress) {
        this.setTakeoutAdress(takeoutAdress);
        return this;
    }

    public void setTakeoutAdress(String takeoutAdress) {
        this.takeoutAdress = takeoutAdress;
    }

    public DeliveryMan getDeliveryMan() {
        return this.deliveryMan;
    }

    public void setDeliveryMan(DeliveryMan deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public Order deliveryMan(DeliveryMan deliveryMan) {
        this.setDeliveryMan(deliveryMan);
        return this;
    }

    public Shop getShop() {
        return this.shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Order shop(Shop shop) {
        this.setShop(shop);
        return this;
    }

    public Client getClient() {
        return this.client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Order client(Client client) {
        this.setClient(client);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Order)) {
            return false;
        }
        return id != null && id.equals(((Order) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Order{" +
            "id=" + getId() +
            ", deliveryAddress='" + getDeliveryAddress() + "'" +
            ", takeoutAdress='" + getTakeoutAdress() + "'" +
            "}";
    }
}
