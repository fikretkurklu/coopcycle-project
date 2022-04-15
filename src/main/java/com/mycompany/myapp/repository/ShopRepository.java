package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.Shop;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Shop entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {}
