package com.gaston.sistema.turno.sistematunos_back.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gaston.sistema.turno.sistematunos_back.entities.Shop;
import com.gaston.sistema.turno.sistematunos_back.repositories.ShopRepository;

@ExtendWith(MockitoExtension.class)
class ShopServiceImplTest {

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private ShopServiceImpl shopService;

    @Test
    void getShopById_Success() {
        // Arrange
        Long shopId = 1L;
        Shop shop = new Shop();
        shop.setId(shopId);
        shop.setName("Test Shop");
        when(shopRepository.findById(shopId)).thenReturn(Optional.of(shop));

        // Act
        Shop result = shopService.getShopById(shopId);

        // Assert
        assertThat(result.getName()).isEqualTo("Test Shop");
        verify(shopRepository).findById(shopId);
    }

    @Test
    void getShopById_NotFound() {
        // Arrange
        Long shopId = 99L;
        when(shopRepository.findById(shopId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            shopService.getShopById(shopId);
        });

        assertThat(exception.getMessage()).isEqualTo("Local no encontrado con ese id 99");
    }
}
