package ro.sapientia2015.story.model;

import org.junit.Test;

import ro.sapientia2015.story.model.ToDo;
import static junit.framework.Assert.*;

/**
 * @author Kovacs Szabolcs
 */
public class ToDoTest {

    private String TITLE = "title";
    private String DESCRIPTION = "description";

    @Test
    public void buildWithMandatoryInformation() {
        ToDo built = ToDo.getBuilder(TITLE).build();
        
        assertNull(built.getId());
        assertNull(built.getCreationTime());
        assertNull(built.getDescription());
        assertNull(built.getModificationTime());
        assertEquals(TITLE, built.getTitle());
        assertEquals(0L, built.getVersion());
    }

    @Test
    public void buildWithAllInformation() {
        ToDo built = ToDo.getBuilder(TITLE)
                .description(DESCRIPTION)
                .build();

        assertNull(built.getId());
        assertNull(built.getCreationTime());
        assertEquals(DESCRIPTION, built.getDescription());
        assertNull(built.getModificationTime());
        assertEquals(TITLE, built.getTitle());
        assertEquals(0L, built.getVersion());
    }

    @Test
    public void prePersist() {
        ToDo ToDo = new ToDo();
        ToDo.prePersist();

        assertNull(ToDo.getId());
        assertNotNull(ToDo.getCreationTime());
        assertNull(ToDo.getDescription());
        assertNotNull(ToDo.getModificationTime());
        assertNull(ToDo.getTitle());
        assertEquals(0L, ToDo.getVersion());
        assertEquals(ToDo.getCreationTime(), ToDo.getModificationTime());
    }

    @Test
    public void preUpdate() {
        ToDo ToDo = new ToDo();
        ToDo.prePersist();

        pause(1000);

        ToDo.preUpdate();

        assertNull(ToDo.getId());
        assertNotNull(ToDo.getCreationTime());
        assertNull(ToDo.getDescription());
        assertNotNull(ToDo.getModificationTime());
        assertNull(ToDo.getTitle());
        assertEquals(0L, ToDo.getVersion());
        assertTrue(ToDo.getModificationTime().isAfter(ToDo.getCreationTime()));
    }
    
    private void pause(long timeInMillis) {
        try {
            Thread.currentThread().sleep(timeInMillis);
        }
        catch (InterruptedException e) {
            //Do Nothing
        }
    }
}
