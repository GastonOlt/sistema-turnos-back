package com.gaston.sistema.turno.sistematunos_back.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gaston.sistema.turno.sistematunos_back.dto.ShopDTO;
import com.gaston.sistema.turno.sistematunos_back.entities.Owner;
import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopRepository;
import com.gaston.sistema.turno.sistematunos_back.validation.ResourceNotFoundException;

/**
 * Tests for ShopServiceImpl — Sprint 4 improvements.
 *
 * Key behaviors tested:
 * - getShopById: throws ResourceNotFoundException (HTTP 404) instead of IllegalArgumentException (HTTP 400)
 * - getByOwner: throws ResourceNotFoundException (HTTP 404)
 * - getShopDTOById / getByOwnerDTO: return DTOs instead of entities
 * - DIP: service depends on OwnerService interface (verified by @Mock)
 */
@ExtendWith(MockitoExtension.class)
class ShopServiceImplTest {

    @Mock private ShopRepository shopRepository;
    @Mock private OwnerService ownerService; // DIP: interface, not OwnerServiceImpl

    @InjectMocks
    private ShopServiceImpl shopService;

    private Shop testShop;

    @BeforeEach
    void setUp() {
        Owner owner = new Owner();
        owner.setId(1L);
        owner.setName("Carlos");

        testShop = new Shop();
        testShop.setId(1L);
        testShop.setName("Test Shop");
        testShop.setAddress("123 Main St");
        testShop.setOwner(owner);
    }

    // ========================================================================
    // getShopById — ResourceNotFoundException (Sprint 4)
    // ========================================================================

    @Nested
    @DisplayName("getShopById")
    class GetShopByIdTests {

        @Test
        @DisplayName("should return shop when found")
        void shouldReturnShop() {
            when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));

            Shop result = shopService.getShopById(1L);

            assertEquals("Test Shop", result.getName());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException (404) when shop not found")
        void shouldThrowResourceNotFound() {
            when(shopRepository.findById(999L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> shopService.getShopById(999L));

            assertTrue(ex.getMessage().contains("Shop"));
            assertTrue(ex.getMessage().contains("999"));
        }
    }

    // ========================================================================
    // getShopDTOById — returns DTO (Sprint 4)
    // ========================================================================

    @Nested
    @DisplayName("getShopDTOById")
    class GetShopDTOByIdTests {

        @Test
        @DisplayName("should return ShopDTO instead of entity")
        void shouldReturnDTO() {
            when(shopRepository.findById(1L)).thenReturn(Optional.of(testShop));

            ShopDTO result = shopService.getShopDTOById(1L);

            assertNotNull(result);
            assertEquals("Test Shop", result.getName());
            assertInstanceOf(ShopDTO.class, result);
        }
    }

    // ========================================================================
    // getByOwner — ResourceNotFoundException (Sprint 4)
    // ========================================================================

    @Nested
    @DisplayName("getByOwner")
    class GetByOwnerTests {

        @Test
        @DisplayName("should return shop when found by owner")
        void shouldReturnShopByOwner() {
            when(shopRepository.findByOwnerId(1L)).thenReturn(Optional.of(testShop));

            Shop result = shopService.getByOwner(1L);

            assertEquals("Test Shop", result.getName());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException (404) when no shop for owner")
        void shouldThrowResourceNotFoundForOwner() {
            when(shopRepository.findByOwnerId(999L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> shopService.getByOwner(999L));
        }
    }

    // ========================================================================
    // getByOwnerDTO — returns DTO (Sprint 4)
    // ========================================================================

    @Nested
    @DisplayName("getByOwnerDTO")
    class GetByOwnerDTOTests {

        @Test
        @DisplayName("should return ShopDTO instead of entity")
        void shouldReturnDTO() {
            when(shopRepository.findByOwnerId(1L)).thenReturn(Optional.of(testShop));

            ShopDTO result = shopService.getByOwnerDTO(1L);

            assertNotNull(result);
            assertInstanceOf(ShopDTO.class, result);
        }
    }

    // ========================================================================
    // createShop — owner lookup
    // ========================================================================

    @Nested
    @DisplayName("createShop")
    class CreateShopTests {

        @Test
        @DisplayName("should create shop and link to owner")
        void shouldCreateShop() {
            Owner owner = new Owner();
            owner.setId(1L);
            owner.setName("Carlos");

            when(ownerService.findById(1L)).thenReturn(Optional.of(owner));
            when(shopRepository.save(any(Shop.class))).thenReturn(testShop);

            ShopDTO result = shopService.createShop(testShop, 1L);

            assertNotNull(result);
            verify(shopRepository).save(testShop);
            assertEquals(owner, testShop.getOwner());
        }

        @Test
        @DisplayName("should throw when owner not found")
        void shouldThrowWhenOwnerNotFound() {
            when(ownerService.findById(999L)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> shopService.createShop(testShop, 999L));
        }
    }
}
