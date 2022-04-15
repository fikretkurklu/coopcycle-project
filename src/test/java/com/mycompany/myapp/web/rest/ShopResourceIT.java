package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.Shop;
import com.mycompany.myapp.repository.ShopRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
 * Integration tests for the {@link ShopResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShopResourceIT {

    private static final String DEFAULT_RATING = "AAAAAAAAAA";
    private static final String UPDATED_RATING = "BBBBBBBBBB";

    private static final Boolean DEFAULT_OPEN = false;
    private static final Boolean UPDATED_OPEN = true;

    private static final Integer DEFAULT_AVERAGE_DELIVERY_TIME = 1;
    private static final Integer UPDATED_AVERAGE_DELIVERY_TIME = 2;

    private static final ZonedDateTime DEFAULT_CLOSING_HOUR = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CLOSING_HOUR = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_OPENING_HOUR = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_OPENING_HOUR = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_TAGS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/shops";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShopMockMvc;

    private Shop shop;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shop createEntity(EntityManager em) {
        Shop shop = new Shop()
            .rating(DEFAULT_RATING)
            .open(DEFAULT_OPEN)
            .averageDeliveryTime(DEFAULT_AVERAGE_DELIVERY_TIME)
            .closingHour(DEFAULT_CLOSING_HOUR)
            .openingHour(DEFAULT_OPENING_HOUR)
            .tags(DEFAULT_TAGS);
        return shop;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Shop createUpdatedEntity(EntityManager em) {
        Shop shop = new Shop()
            .rating(UPDATED_RATING)
            .open(UPDATED_OPEN)
            .averageDeliveryTime(UPDATED_AVERAGE_DELIVERY_TIME)
            .closingHour(UPDATED_CLOSING_HOUR)
            .openingHour(UPDATED_OPENING_HOUR)
            .tags(UPDATED_TAGS);
        return shop;
    }

    @BeforeEach
    public void initTest() {
        shop = createEntity(em);
    }

    @Test
    @Transactional
    void createShop() throws Exception {
        int databaseSizeBeforeCreate = shopRepository.findAll().size();
        // Create the Shop
        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shop)))
            .andExpect(status().isCreated());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeCreate + 1);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testShop.getOpen()).isEqualTo(DEFAULT_OPEN);
        assertThat(testShop.getAverageDeliveryTime()).isEqualTo(DEFAULT_AVERAGE_DELIVERY_TIME);
        assertThat(testShop.getClosingHour()).isEqualTo(DEFAULT_CLOSING_HOUR);
        assertThat(testShop.getOpeningHour()).isEqualTo(DEFAULT_OPENING_HOUR);
        assertThat(testShop.getTags()).isEqualTo(DEFAULT_TAGS);
    }

    @Test
    @Transactional
    void createShopWithExistingId() throws Exception {
        // Create the Shop with an existing ID
        shop.setId(1L);

        int databaseSizeBeforeCreate = shopRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShopMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shop)))
            .andExpect(status().isBadRequest());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllShops() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        // Get all the shopList
        restShopMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shop.getId().intValue())))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN.booleanValue())))
            .andExpect(jsonPath("$.[*].averageDeliveryTime").value(hasItem(DEFAULT_AVERAGE_DELIVERY_TIME)))
            .andExpect(jsonPath("$.[*].closingHour").value(hasItem(sameInstant(DEFAULT_CLOSING_HOUR))))
            .andExpect(jsonPath("$.[*].openingHour").value(hasItem(sameInstant(DEFAULT_OPENING_HOUR))))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)));
    }

    @Test
    @Transactional
    void getShop() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        // Get the shop
        restShopMockMvc
            .perform(get(ENTITY_API_URL_ID, shop.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shop.getId().intValue()))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.open").value(DEFAULT_OPEN.booleanValue()))
            .andExpect(jsonPath("$.averageDeliveryTime").value(DEFAULT_AVERAGE_DELIVERY_TIME))
            .andExpect(jsonPath("$.closingHour").value(sameInstant(DEFAULT_CLOSING_HOUR)))
            .andExpect(jsonPath("$.openingHour").value(sameInstant(DEFAULT_OPENING_HOUR)))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS));
    }

    @Test
    @Transactional
    void getNonExistingShop() throws Exception {
        // Get the shop
        restShopMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewShop() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        int databaseSizeBeforeUpdate = shopRepository.findAll().size();

        // Update the shop
        Shop updatedShop = shopRepository.findById(shop.getId()).get();
        // Disconnect from session so that the updates on updatedShop are not directly saved in db
        em.detach(updatedShop);
        updatedShop
            .rating(UPDATED_RATING)
            .open(UPDATED_OPEN)
            .averageDeliveryTime(UPDATED_AVERAGE_DELIVERY_TIME)
            .closingHour(UPDATED_CLOSING_HOUR)
            .openingHour(UPDATED_OPENING_HOUR)
            .tags(UPDATED_TAGS);

        restShopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedShop.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedShop))
            )
            .andExpect(status().isOk());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testShop.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testShop.getAverageDeliveryTime()).isEqualTo(UPDATED_AVERAGE_DELIVERY_TIME);
        assertThat(testShop.getClosingHour()).isEqualTo(UPDATED_CLOSING_HOUR);
        assertThat(testShop.getOpeningHour()).isEqualTo(UPDATED_OPENING_HOUR);
        assertThat(testShop.getTags()).isEqualTo(UPDATED_TAGS);
    }

    @Test
    @Transactional
    void putNonExistingShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shop.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shop))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shop))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shop)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShopWithPatch() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        int databaseSizeBeforeUpdate = shopRepository.findAll().size();

        // Update the shop using partial update
        Shop partialUpdatedShop = new Shop();
        partialUpdatedShop.setId(shop.getId());

        partialUpdatedShop.averageDeliveryTime(UPDATED_AVERAGE_DELIVERY_TIME).closingHour(UPDATED_CLOSING_HOUR).tags(UPDATED_TAGS);

        restShopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShop.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShop))
            )
            .andExpect(status().isOk());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testShop.getOpen()).isEqualTo(DEFAULT_OPEN);
        assertThat(testShop.getAverageDeliveryTime()).isEqualTo(UPDATED_AVERAGE_DELIVERY_TIME);
        assertThat(testShop.getClosingHour()).isEqualTo(UPDATED_CLOSING_HOUR);
        assertThat(testShop.getOpeningHour()).isEqualTo(DEFAULT_OPENING_HOUR);
        assertThat(testShop.getTags()).isEqualTo(UPDATED_TAGS);
    }

    @Test
    @Transactional
    void fullUpdateShopWithPatch() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        int databaseSizeBeforeUpdate = shopRepository.findAll().size();

        // Update the shop using partial update
        Shop partialUpdatedShop = new Shop();
        partialUpdatedShop.setId(shop.getId());

        partialUpdatedShop
            .rating(UPDATED_RATING)
            .open(UPDATED_OPEN)
            .averageDeliveryTime(UPDATED_AVERAGE_DELIVERY_TIME)
            .closingHour(UPDATED_CLOSING_HOUR)
            .openingHour(UPDATED_OPENING_HOUR)
            .tags(UPDATED_TAGS);

        restShopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShop.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShop))
            )
            .andExpect(status().isOk());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
        Shop testShop = shopList.get(shopList.size() - 1);
        assertThat(testShop.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testShop.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testShop.getAverageDeliveryTime()).isEqualTo(UPDATED_AVERAGE_DELIVERY_TIME);
        assertThat(testShop.getClosingHour()).isEqualTo(UPDATED_CLOSING_HOUR);
        assertThat(testShop.getOpeningHour()).isEqualTo(UPDATED_OPENING_HOUR);
        assertThat(testShop.getTags()).isEqualTo(UPDATED_TAGS);
    }

    @Test
    @Transactional
    void patchNonExistingShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shop.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shop))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shop))
            )
            .andExpect(status().isBadRequest());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShop() throws Exception {
        int databaseSizeBeforeUpdate = shopRepository.findAll().size();
        shop.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShopMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(shop)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Shop in the database
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShop() throws Exception {
        // Initialize the database
        shopRepository.saveAndFlush(shop);

        int databaseSizeBeforeDelete = shopRepository.findAll().size();

        // Delete the shop
        restShopMockMvc
            .perform(delete(ENTITY_API_URL_ID, shop.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Shop> shopList = shopRepository.findAll();
        assertThat(shopList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
