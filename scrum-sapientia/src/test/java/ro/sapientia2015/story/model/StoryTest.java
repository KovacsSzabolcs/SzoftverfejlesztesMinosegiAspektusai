package ro.sapientia2015.story.model;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ro.sapientia2015.story.ToDoTestUtil;
import ro.sapientia2015.story.dto.ToDoDTO;
import ro.sapientia2015.story.model.Story;
import static junit.framework.Assert.*;

/**
 * @author Kiss Tibor
 */
public class StoryTest {

    private String TITLE = "title";
    private String DESCRIPTION = "description";
    private Long TODO_ID=(long) 1;

    @Test
    public void buildWithMandatoryInformation() {
        Story built = Story.getBuilder(TITLE).build();

        assertNull(built.getId());
        assertNull(built.getCreationTime());
        assertNull(built.getDescription());
        assertNull(built.getModificationTime());
        assertEquals(TITLE, built.getTitle());
        assertEquals(0L, built.getVersion());
        
        //SZABI
        assertNotNull(built.getTodos());
    }

    @Test
    public void buildWithAllInformation() {
        Story built = Story.getBuilder(TITLE)
                .description(DESCRIPTION)
                .build();

        assertNull(built.getId());
        assertNull(built.getCreationTime());
        assertEquals(DESCRIPTION, built.getDescription());
        assertNull(built.getModificationTime());
        assertEquals(TITLE, built.getTitle());
        assertEquals(0L, built.getVersion());
        
        //SZABI
        assertNotNull(built.getTodos());
    }

    @Test
    public void prePersist() {
        Story story = new Story();
        story.prePersist();

        assertNull(story.getId());
        assertNotNull(story.getCreationTime());
        assertNull(story.getDescription());
        assertNotNull(story.getModificationTime());
        assertNull(story.getTitle());
        assertEquals(0L, story.getVersion());
        assertEquals(story.getCreationTime(), story.getModificationTime());
    }

    @Test
    public void preUpdate() {
        Story story = new Story();
        story.prePersist();

        pause(1000);

        story.preUpdate();

        assertNull(story.getId());
        assertNotNull(story.getCreationTime());
        assertNull(story.getDescription());
        assertNotNull(story.getModificationTime());
        assertNull(story.getTitle());
        assertEquals(0L, story.getVersion());
        assertTrue(story.getModificationTime().isAfter(story.getCreationTime()));
    }
    
    //SZABI
    @Test
    public void addToDo() {
        Story story = new Story();
        story.prePersist();
        
      
        assertNotNull(story.getTodos());
        assertEquals(0,story.getTodos().size());
        assertEquals(true,story.getTodos().isEmpty());
        
        ToDo todo=ToDoTestUtil.createModel(TODO_ID, DESCRIPTION, TITLE);
        story.addToDo(todo);
        
        assertEquals(1,story.getTodos().size());
        assertEquals(false,story.getTodos().isEmpty());
        assertEquals(true,story.getTodos().contains(todo));
        
        
    }
    
    //SZABI
    @Test
    public void deleteToDo() {
        Story story = new Story();
        story.prePersist();
        
      
        assertNotNull(story.getTodos());
        assertEquals(0,story.getTodos().size());
        assertEquals(true,story.getTodos().isEmpty());
        
        ToDo todo=ToDoTestUtil.createModel(TODO_ID, DESCRIPTION, TITLE);
        story.addToDo(todo);
        
        assertEquals(false,story.getTodos().isEmpty());
        assertEquals(1,story.getTodos().size());
        assertEquals(true,story.getTodos().contains(todo));
        
        ToDo todo2=ToDoTestUtil.createModel(TODO_ID, DESCRIPTION, TITLE);
        story.addToDo(todo2);
        
        assertEquals(2,story.getTodos().size());
        assertEquals(false,story.getTodos().isEmpty());
        assertEquals(true,story.getTodos().contains(todo2));
        
        story.deleteToDo(todo2);
        
        assertEquals(false,story.getTodos().isEmpty());
        assertEquals(1,story.getTodos().size());
        assertEquals(true,story.getTodos().contains(todo));
        assertEquals(false,story.getTodos().contains(todo2));
        
        
        
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
