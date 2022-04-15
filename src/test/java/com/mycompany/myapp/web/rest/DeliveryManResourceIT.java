package com.mycompany.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.DeliveryMan;
import com.mycompany.myapp.repository.DeliveryManRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link DeliveryManResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DeliveryManResourceIT {

    private static final String DEFAULT_VEHICLE_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_VEHICLE_TYPE = "BBBBBBBBBB";

    private static final Integer DEFAULT_RIDES = 1;
    private static final Integer UPDATED_RIDES = 2;

    private static final Integer DEFAULT_EARNED = 1;
    private static final Integer UPDATED_EARNED = 2;

    private static final String DEFAULT_RATING = "AAAAAAAAAA";
    private static final String UPDATED_RATING = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/delivery-men";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DeliveryManRepository deliveryManRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDeliveryManMockMvc;

    private DeliveryMan deliveryMan;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeliveryMan createEntity(EntityManager em) {
        DeliveryMan deliveryMan = new DeliveryMan()
            .vehicleType(DEFAULT_VEHICLE_TYPE)
            .rides(DEFAULT_RIDES)
            .earned(DEFAULT_EARNED)
            .rating(DEFAULT_RATING);
        return deliveryMan;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DeliveryMan createUpdatedEntity(EntityManager em) {
        DeliveryMan deliveryMan = new DeliveryMan()
            .vehicleType(UPDATED_VEHICLE_TYPE)
            .rides(UPDATED_RIDES)
            .earned(UPDATED_EARNED)
            .rating(UPDATED_RATING);
        return deliveryMan;
    }

    @BeforeEach
    public void initTest() {
        deliveryMan = createEntity(em);
    }

    @Test
    @Transactional
    void createDeliveryMan() throws Exception {
        int databaseSizeBeforeCreate = deliveryManRepository.findAll().size();
        // Create the DeliveryMan
        restDeliveryManMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deliveryMan)))
            .andExpect(status().isCreated());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeCreate + 1);
        DeliveryMan testDeliveryMan = deliveryManList.get(deliveryManList.size() - 1);
        assertThat(testDeliveryMan.getVehicleType()).isEqualTo(DEFAULT_VEHICLE_TYPE);
        assertThat(testDeliveryMan.getRides()).isEqualTo(DEFAULT_RIDES);
        assertThat(testDeliveryMan.getEarned()).isEqualTo(DEFAULT_EARNED);
        assertThat(testDeliveryMan.getRating()).isEqualTo(DEFAULT_RATING);
    }

    @Test
    @Transactional
    void createDeliveryManWithExistingId() throws Exception {
        // Create the DeliveryMan with an existing ID
        deliveryMan.setId(1L);

        int databaseSizeBeforeCreate = deliveryManRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDeliveryManMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deliveryMan)))
            .andExpect(status().isBadRequest());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllDeliveryMen() throws Exception {
        // Initialize the database
        deliveryManRepository.saveAndFlush(deliveryMan);

        // Get all the deliveryManList
        restDeliveryManMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deliveryMan.getId().intValue())))
            .andExpect(jsonPath("$.[*].vehicleType").value(hasItem(DEFAULT_VEHICLE_TYPE)))
            .andExpect(jsonPath("$.[*].rides").value(hasItem(DEFAULT_RIDES)))
            .andExpect(jsonPath("$.[*].earned").value(hasItem(DEFAULT_EARNED)))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)));
    }

    @Test
    @Transactional
    void getDeliveryMan() throws Exception {
        // Initialize the database
        deliveryManRepository.saveAndFlush(deliveryMan);

        // Get the deliveryMan
        restDeliveryManMockMvc
            .perform(get(ENTITY_API_URL_ID, deliveryMan.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(deliveryMan.getId().intValue()))
            .andExpect(jsonPath("$.vehicleType").value(DEFAULT_VEHICLE_TYPE))
            .andExpect(jsonPath("$.rides").value(DEFAULT_RIDES))
            .andExpect(jsonPath("$.earned").value(DEFAULT_EARNED))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING));
    }

    @Test
    @Transactional
    void getNonExistingDeliveryMan() throws Exception {
        // Get the deliveryMan
        restDeliveryManMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDeliveryMan() throws Exception {
        // Initialize the database
        deliveryManRepository.saveAndFlush(deliveryMan);

        int databaseSizeBeforeUpdate = deliveryManRepository.findAll().size();

        // Update the deliveryMan
        DeliveryMan updatedDeliveryMan = deliveryManRepository.findById(deliveryMan.getId()).get();
        // Disconnect from session so that the updates on updatedDeliveryMan are not directly saved in db
        em.detach(updatedDeliveryMan);
        updatedDeliveryMan.vehicleType(UPDATED_VEHICLE_TYPE).rides(UPDATED_RIDES).earned(UPDATED_EARNED).rating(UPDATED_RATING);

        restDeliveryManMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedDeliveryMan.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedDeliveryMan))
            )
            .andExpect(status().isOk());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeUpdate);
        DeliveryMan testDeliveryMan = deliveryManList.get(deliveryManList.size() - 1);
        assertThat(testDeliveryMan.getVehicleType()).isEqualTo(UPDATED_VEHICLE_TYPE);
        assertThat(testDeliveryMan.getRides()).isEqualTo(UPDATED_RIDES);
        assertThat(testDeliveryMan.getEarned()).isEqualTo(UPDATED_EARNED);
        assertThat(testDeliveryMan.getRating()).isEqualTo(UPDATED_RATING);
    }

    @Test
    @Transactional
    void putNonExistingDeliveryMan() throws Exception {
        int databaseSizeBeforeUpdate = deliveryManRepository.findAll().size();
        deliveryMan.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryManMockMvc
            .perform(
                put(ENTITY_API_URL_ID, deliveryMan.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deliveryMan))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDeliveryMan() throws Exception {
        int databaseSizeBeforeUpdate = deliveryManRepository.findAll().size();
        deliveryMan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryManMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(deliveryMan))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDeliveryMan() throws Exception {
        int databaseSizeBeforeUpdate = deliveryManRepository.findAll().size();
        deliveryMan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryManMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(deliveryMan)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDeliveryManWithPatch() throws Exception {
        // Initialize the database
        deliveryManRepository.saveAndFlush(deliveryMan);

        int databaseSizeBeforeUpdate = deliveryManRepository.findAll().size();

        // Update the deliveryMan using partial update
        DeliveryMan partialUpdatedDeliveryMan = new DeliveryMan();
        partialUpdatedDeliveryMan.setId(deliveryMan.getId());

        partialUpdatedDeliveryMan.rides(UPDATED_RIDES).earned(UPDATED_EARNED).rating(UPDATED_RATING);

        restDeliveryManMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeliveryMan.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDeliveryMan))
            )
            .andExpect(status().isOk());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeUpdate);
        DeliveryMan testDeliveryMan = deliveryManList.get(deliveryManList.size() - 1);
        assertThat(testDeliveryMan.getVehicleType()).isEqualTo(DEFAULT_VEHICLE_TYPE);
        assertThat(testDeliveryMan.getRides()).isEqualTo(UPDATED_RIDES);
        assertThat(testDeliveryMan.getEarned()).isEqualTo(UPDATED_EARNED);
        assertThat(testDeliveryMan.getRating()).isEqualTo(UPDATED_RATING);
    }

    @Test
    @Transactional
    void fullUpdateDeliveryManWithPatch() throws Exception {
        // Initialize the database
        deliveryManRepository.saveAndFlush(deliveryMan);

        int databaseSizeBeforeUpdate = deliveryManRepository.findAll().size();

        // Update the deliveryMan using partial update
        DeliveryMan partialUpdatedDeliveryMan = new DeliveryMan();
        partialUpdatedDeliveryMan.setId(deliveryMan.getId());

        partialUpdatedDeliveryMan.vehicleType(UPDATED_VEHICLE_TYPE).rides(UPDATED_RIDES).earned(UPDATED_EARNED).rating(UPDATED_RATING);

        restDeliveryManMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDeliveryMan.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDeliveryMan))
            )
            .andExpect(status().isOk());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeUpdate);
        DeliveryMan testDeliveryMan = deliveryManList.get(deliveryManList.size() - 1);
        assertThat(testDeliveryMan.getVehicleType()).isEqualTo(UPDATED_VEHICLE_TYPE);
        assertThat(testDeliveryMan.getRides()).isEqualTo(UPDATED_RIDES);
        assertThat(testDeliveryMan.getEarned()).isEqualTo(UPDATED_EARNED);
        assertThat(testDeliveryMan.getRating()).isEqualTo(UPDATED_RATING);
    }

    @Test
    @Transactional
    void patchNonExistingDeliveryMan() throws Exception {
        int databaseSizeBeforeUpdate = deliveryManRepository.findAll().size();
        deliveryMan.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDeliveryManMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, deliveryMan.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deliveryMan))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDeliveryMan() throws Exception {
        int databaseSizeBeforeUpdate = deliveryManRepository.findAll().size();
        deliveryMan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryManMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(deliveryMan))
            )
            .andExpect(status().isBadRequest());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDeliveryMan() throws Exception {
        int databaseSizeBeforeUpdate = deliveryManRepository.findAll().size();
        deliveryMan.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDeliveryManMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(deliveryMan))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DeliveryMan in the database
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDeliveryMan() throws Exception {
        // Initialize the database
        deliveryManRepository.saveAndFlush(deliveryMan);

        int databaseSizeBeforeDelete = deliveryManRepository.findAll().size();

        // Delete the deliveryMan
        restDeliveryManMockMvc
            .perform(delete(ENTITY_API_URL_ID, deliveryMan.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DeliveryMan> deliveryManList = deliveryManRepository.findAll();
        assertThat(deliveryManList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
