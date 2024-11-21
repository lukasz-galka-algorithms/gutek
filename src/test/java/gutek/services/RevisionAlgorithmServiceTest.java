package gutek.services;

import gutek.entities.algorithms.RevisionAlgorithm;
import gutek.entities.cards.CardBase;
import gutek.repositories.RevisionAlgorithmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RevisionAlgorithmServiceTest {

    @Mock
    private TranslationService translationService;

    @Mock
    private RevisionAlgorithmRepository revisionAlgorithmRepository;

    private RevisionAlgorithmService revisionAlgorithmService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        revisionAlgorithmService = new RevisionAlgorithmService(translationService, revisionAlgorithmRepository);
    }

    @Test
    void testGetAlgorithmNames_WithValidAlgorithms_UsingReflection() throws Exception {
        // Arrange
        Set<Class<? extends RevisionAlgorithm<? extends CardBase>>> mockClasses = Set.of(MockRevisionAlgorithm.class);

        Field algorithmClassesField = RevisionAlgorithmService.class.getDeclaredField("algorithmClasses");
        algorithmClassesField.setAccessible(true);
        algorithmClassesField.set(revisionAlgorithmService, mockClasses);

        // Act
        Set<String> algorithmNames = revisionAlgorithmService.getAlgorithmNames();

        // Assert
        assertEquals(Set.of("MockAlgorithm"), algorithmNames);
    }

    @Test
    void testCreateAlgorithmInstance_ValidAlgorithm() throws Exception {
        // Arrange
        Set<Class<? extends RevisionAlgorithm<? extends CardBase>>> mockClasses = Set.of(MockRevisionAlgorithm.class);

        Field algorithmClassesField = RevisionAlgorithmService.class.getDeclaredField("algorithmClasses");
        algorithmClassesField.setAccessible(true);
        algorithmClassesField.set(revisionAlgorithmService, mockClasses);

        // Act
        RevisionAlgorithm<?> result = revisionAlgorithmService.createAlgorithmInstance("MockAlgorithm");

        // Assert
        assertNotNull(result);
        assertEquals("MockAlgorithm", result.getAlgorithmName());
    }

    @Test
    void testCreateAlgorithmInstance_InvalidAlgorithm() throws Exception{
        // Arrange
        Set<Class<? extends RevisionAlgorithm<? extends CardBase>>> mockClasses = Set.of(MockRevisionAlgorithm.class);

        Field algorithmClassesField = RevisionAlgorithmService.class.getDeclaredField("algorithmClasses");
        algorithmClassesField.setAccessible(true);
        algorithmClassesField.set(revisionAlgorithmService, mockClasses);

        // Act
        RevisionAlgorithm<?> result = revisionAlgorithmService.createAlgorithmInstance("NonExistingAlgorithm");

        // Assert
        assertNull(result);
    }

    @Test
    void testSaveAlgorithm() {
        // Arrange
        RevisionAlgorithm mockAlgorithm = mock(RevisionAlgorithm.class);
        when(revisionAlgorithmRepository.save(mockAlgorithm)).thenReturn(mockAlgorithm);

        // Act
        RevisionAlgorithm<?> savedAlgorithm = revisionAlgorithmService.saveAlgorithm(mockAlgorithm);

        // Assert
        assertNotNull(savedAlgorithm);
        verify(revisionAlgorithmRepository, times(1)).save(mockAlgorithm);
    }
}