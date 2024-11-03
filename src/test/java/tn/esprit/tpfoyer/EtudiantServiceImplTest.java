package tn.esprit.tpfoyer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tn.esprit.tpfoyer.entity.Etudiant;
import tn.esprit.tpfoyer.repository.EtudiantRepository;
import tn.esprit.tpfoyer.service.EtudiantServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EtudiantServiceImplTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EtudiantServiceImpl etudiantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void retrieveAllEtudiants() {
        // Arrange
        Etudiant etudiant1 = new Etudiant();
        Etudiant etudiant2 = new Etudiant();
        when(etudiantRepository.findAll()).thenReturn(Arrays.asList(etudiant1, etudiant2));

        // Act
        List<Etudiant> etudiants = etudiantService.retrieveAllEtudiants();

        // Assert
        assertEquals(2, etudiants.size(), "The number of retrieved students should be 2.");
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    void retrieveEtudiant() {
        // Arrange
        Long etudiantId = 1L;
        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(etudiantId);
        when(etudiantRepository.findById(etudiantId)).thenReturn(Optional.of(etudiant));

        // Act
        Etudiant foundEtudiant = etudiantService.retrieveEtudiant(etudiantId);

        // Assert
        assertNotNull(foundEtudiant, "The retrieved student should not be null.");
        assertEquals(etudiantId, foundEtudiant.getIdEtudiant(), "The student ID should match.");
        verify(etudiantRepository, times(1)).findById(etudiantId);
    }

    @Test
    void addEtudiant() {
        // Arrange
        Etudiant etudiant = new Etudiant();
        when(etudiantRepository.save(etudiant)).thenReturn(etudiant);

        // Act
        Etudiant savedEtudiant = etudiantService.addEtudiant(etudiant);

        // Assert
        assertNotNull(savedEtudiant, "The saved student should not be null.");
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void modifyEtudiant() {
        // Arrange
        Etudiant etudiant = new Etudiant();
        etudiant.setIdEtudiant(1L);
        when(etudiantRepository.save(etudiant)).thenReturn(etudiant);

        // Act
        Etudiant updatedEtudiant = etudiantService.modifyEtudiant(etudiant);

        // Assert
        assertNotNull(updatedEtudiant, "The updated student should not be null.");
        assertEquals(1L, updatedEtudiant.getIdEtudiant(), "The student ID should match.");
        verify(etudiantRepository, times(1)).save(etudiant);
    }

    @Test
    void removeEtudiant() {
        // Arrange
        Long etudiantId = 1L;
        doNothing().when(etudiantRepository).deleteById(etudiantId);

        // Act
        etudiantService.removeEtudiant(etudiantId);

        // Assert
        verify(etudiantRepository, times(1)).deleteById(etudiantId);
    }

    @Test
    void recupererEtudiantParCin() {
        // Arrange
        long cin = 123456;
        Etudiant etudiant = new Etudiant();
        etudiant.setCinEtudiant(cin);
        when(etudiantRepository.findEtudiantByCinEtudiant(cin)).thenReturn(etudiant);

        // Act
        Etudiant foundEtudiant = etudiantService.recupererEtudiantParCin(cin);

        // Assert
        assertNotNull(foundEtudiant, "The student with the given CIN should not be null.");
        assertEquals(cin, foundEtudiant.getCinEtudiant(), "The CIN should match.");
        verify(etudiantRepository, times(1)).findEtudiantByCinEtudiant(cin);
    }
}
