package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Cooperative.
 */
@Entity
@Table(name = "cooperative")
public class Cooperative implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "number_of_shop")
    private Integer numberOfShop;

    @OneToMany(mappedBy = "cooperative")
    @JsonIgnoreProperties(value = { "user", "deliveries", "payments", "cooperative" }, allowSetters = true)
    private Set<Shop> shops = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cooperative id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return this.city;
    }

    public Cooperative city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getNumberOfShop() {
        return this.numberOfShop;
    }

    public Cooperative numberOfShop(Integer numberOfShop) {
        this.setNumberOfShop(numberOfShop);
        return this;
    }

    public void setNumberOfShop(Integer numberOfShop) {
        this.numberOfShop = numberOfShop;
    }

    public Set<Shop> getShops() {
        return this.shops;
    }

    public void setShops(Set<Shop> shops) {
        if (this.shops != null) {
            this.shops.forEach(i -> i.setCooperative(null));
        }
        if (shops != null) {
            shops.forEach(i -> i.setCooperative(this));
        }
        this.shops = shops;
    }

    public Cooperative shops(Set<Shop> shops) {
        this.setShops(shops);
        return this;
    }

    public Cooperative addShop(Shop shop) {
        this.shops.add(shop);
        shop.setCooperative(this);
        return this;
    }

    public Cooperative removeShop(Shop shop) {
        this.shops.remove(shop);
        shop.setCooperative(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cooperative)) {
            return false;
        }
        return id != null && id.equals(((Cooperative) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cooperative{" +
            "id=" + getId() +
            ", city='" + getCity() + "'" +
            ", numberOfShop=" + getNumberOfShop() +
            "}";
    }
}
