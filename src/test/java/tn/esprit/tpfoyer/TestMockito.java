package tn.esprit.tpfoyer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import tn.esprit.tpfoyer.entity.Foyer;
import tn.esprit.tpfoyer.repository.FoyerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.tpfoyer.service.FoyerServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

 class TestMockito {

    @Mock
    private FoyerRepository foyerRepository;

    @InjectMocks
    private FoyerServiceImpl foyerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRetrieveAllFoyers() {
        // Arrange
        Foyer foyer1 = new Foyer(); // Create Foyer objects and set properties if needed
        Foyer foyer2 = new Foyer();
        when(foyerRepository.findAll()).thenReturn(Arrays.asList(foyer1, foyer2));

        // Act
        List<Foyer> foyers = foyerService.retrieveAllFoyers();

        // Assert
        assertNotNull(foyers);
        assertEquals(2, foyers.size());
        verify(foyerRepository, times(1)).findAll();
    }

    @Test
    void
    testRetrieveFoyer() {
        // Arrange
        Long foyerId = 1L;
        Foyer foyer = new Foyer();
        when(foyerRepository.findById(foyerId)).thenReturn(Optional.of(foyer));

        // Act
        Foyer retrievedFoyer = foyerService.retrieveFoyer(foyerId);

        // Assert
        assertNotNull(retrievedFoyer);
        verify(foyerRepository, times(1)).findById(foyerId);
    }

    @Test
    void testAddFoyer() {
        // Arrange
        Foyer foyer = new Foyer();
        when(foyerRepository.save(foyer)).thenReturn(foyer);

        // Act
        Foyer addedFoyer = foyerService.addFoyer(foyer);

        // Assert
        assertNotNull(addedFoyer);
        verify(foyerRepository, times(1)).save(foyer);
    }

    @Test
    void testModifyFoyer() {
        // Arrange
        Foyer foyer = new Foyer();
        when(foyerRepository.save(foyer)).thenReturn(foyer);

        // Act
        Foyer modifiedFoyer = foyerService.modifyFoyer(foyer);

        // Assert
        assertNotNull(modifiedFoyer);
        verify(foyerRepository, times(1)).save(foyer);
    }

    @Test
    void testRemoveFoyer() {
        // Arrange
        Long foyerId = 1L;

        // Act
        foyerService.removeFoyer(foyerId);

        // Assert
        verify(foyerRepository, times(1)).deleteById(foyerId);
    }
}
