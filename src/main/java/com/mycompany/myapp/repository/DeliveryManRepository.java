package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.DeliveryMan;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the DeliveryMan entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DeliveryManRepository extends JpaRepository<DeliveryMan, Long> {}
