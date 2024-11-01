package tn.esprit.tpfoyer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.tpfoyer.entity.Foyer;
import tn.esprit.tpfoyer.entity.Universite;
import tn.esprit.tpfoyer.repository.UniversiteRepository;
import tn.esprit.tpfoyer.service.UniversiteServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UniversiteServiceImplTest {
    @Mock
    private UniversiteRepository universiteRepository;

    @InjectMocks
    private UniversiteServiceImpl universiteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    Foyer foyer = new Foyer();
    @Test
    void testRetrieveAllUniversites() {

        Universite u1 = new Universite(1L,"Universit","", foyer );
        Universite u2 = new Universite(2L, "Universite 2","",foyer);
        List<Universite> universites = Arrays.asList(u1, u2);

        when(universiteRepository.findAll()).thenReturn(universites);

        List<Universite> result = universiteService.retrieveAllUniversites();
        assertEquals(2, result.size());
        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveUniversite() {
        Universite universite = new Universite(1L, "Universite 1","",foyer);

        when(universiteRepository.findById(1L)).thenReturn(Optional.of(universite));

        Universite result = universiteService.retrieveUniversite(1L);
        assertNotNull(result);
        assertEquals("Universite 1", result.getNomUniversite());
        verify(universiteRepository, times(1)).findById(1L);
    }

    @Test
    void testAddUniversite() {
        Universite universite = new Universite(1L, "Universite 1","",foyer);

        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite result = universiteService.addUniversite(universite);
        assertNotNull(result);
        assertEquals("Universite 1", result.getNomUniversite());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testModifyUniversite() {
        Universite universite = new Universite(1L, "Updated Universite","",foyer);

        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite result = universiteService.modifyUniversite(universite);
        assertNotNull(result);
        assertEquals("Updated Universite", result.getNomUniversite());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testRemoveUniversite() {
        Long universiteId = 1L;

        doNothing().when(universiteRepository).deleteById(universiteId);

        universiteService.removeUniversite(universiteId);
        verify(universiteRepository, times(1)).deleteById(universiteId);
    }
}
