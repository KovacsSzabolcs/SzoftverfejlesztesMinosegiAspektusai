package ro.sapientia2015.story.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import ro.sapientia2015.story.StoryTestUtil;
import ro.sapientia2015.story.ToDoTestUtil;
import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.dto.ToDoDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.model.ToDo;
import ro.sapientia2015.story.repository.StoryRepository;
import ro.sapientia2015.story.repository.ToDoRepository;
import ro.sapientia2015.story.service.RepositoryStoryService;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Kiss Tibor
 */
public class RepositoryStoryServiceTest {

    private RepositoryStoryService service;

    private StoryRepository repositoryMock;
    
    private ToDoRepository toDoRepositoryMock;

    @Before
    public void setUp() {
        service = new RepositoryStoryService();

        repositoryMock = mock(StoryRepository.class);
        ReflectionTestUtils.setField(service, "repository", repositoryMock);
        
        toDoRepositoryMock = mock(ToDoRepository.class);
        ReflectionTestUtils.setField(service, "toDoRepository", toDoRepositoryMock);
    }

    @Test
    public void add() {
        StoryDTO dto = StoryTestUtil.createFormObject(null, StoryTestUtil.DESCRIPTION, StoryTestUtil.TITLE);

        service.add(dto);

        
        //SZABI
        verifyNoMoreInteractions(toDoRepositoryMock);
        //
        ArgumentCaptor<Story> storyArgument = ArgumentCaptor.forClass(Story.class);
        verify(repositoryMock, times(1)).save(storyArgument.capture());
        verifyNoMoreInteractions(repositoryMock);

        Story model = storyArgument.getValue();

        assertNull(model.getId());
        assertEquals(dto.getDescription(), model.getDescription());
        assertEquals(dto.getTitle(), model.getTitle());
    }

    //SZABi
    @Test
    public void addToDo() throws NotFoundException {
        Story story = StoryTestUtil.createModel(StoryTestUtil.ID, StoryTestUtil.DESCRIPTION, StoryTestUtil.TITLE);
        ToDoDTO toDoDTO=ToDoTestUtil.createFormObject(null, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE); 
		when(repositoryMock.findOne(StoryTestUtil.ID)).thenReturn(story);
        Story actual = service.addToDo(story.getId(), toDoDTO);
 
        verify(repositoryMock, times(1)).findOne(StoryTestUtil.ID);
        
        verifyNoMoreInteractions(repositoryMock);
        verifyNoMoreInteractions(toDoRepositoryMock);
        
        assertEquals(story, actual);
        assertEquals(false,story.getTodos().isEmpty());
        assertEquals(1,story.getTodos().size());
        
        ToDo todo=actual.getTodos().iterator().next();
        
        assertEquals(toDoDTO.getDescription(), todo.getDescription());
        assertEquals(toDoDTO.getTitle(), todo.getTitle());
        assertEquals(toDoDTO.getId(), todo.getId());
       
    }
    @Test
    public void deleteToDo() throws NotFoundException {
        Story story = StoryTestUtil.createModel(StoryTestUtil.ID, StoryTestUtil.DESCRIPTION, StoryTestUtil.TITLE);
        ToDo todo=ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        ToDoDTO toDoDTO=ToDoTestUtil.createFormObject(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE); 
        story.addToDo(todo);
        
        
		when(repositoryMock.findOne(StoryTestUtil.ID)).thenReturn(story);
		when(toDoRepositoryMock.findOne(ToDoTestUtil.ID)).thenReturn(todo);
		
        Story actual = service.deleteToDo(story.getId(), toDoDTO.getId());
 
        
        verify(repositoryMock, times(1)).findOne(StoryTestUtil.ID);
        verify(toDoRepositoryMock, times(1)).findOne(ToDoTestUtil.ID);
        
        verifyNoMoreInteractions(repositoryMock);
        verifyNoMoreInteractions(toDoRepositoryMock);
        
        assertEquals(story, actual);
        assertEquals(true,story.getTodos().isEmpty());
        assertEquals(0,story.getTodos().size());
          
    }
    
    //
    @Test
    public void deleteById() throws NotFoundException {
        Story model = StoryTestUtil.createModel(StoryTestUtil.ID, StoryTestUtil.DESCRIPTION, StoryTestUtil.TITLE);
        when(repositoryMock.findOne(StoryTestUtil.ID)).thenReturn(model);

        Story actual = service.deleteById(StoryTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(StoryTestUtil.ID);
        verify(repositoryMock, times(1)).delete(model);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(model, actual);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdWhenIsNotFound() throws NotFoundException {
        when(repositoryMock.findOne(StoryTestUtil.ID)).thenReturn(null);

        service.deleteById(StoryTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(StoryTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void findAll() {
        List<Story> models = new ArrayList<Story>();
        when(repositoryMock.findAll()).thenReturn(models);

        List<Story> actual = service.findAll();

        verify(repositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(models, actual);
    }

    @Test
    public void findById() throws NotFoundException {
        Story model = StoryTestUtil.createModel(StoryTestUtil.ID, StoryTestUtil.DESCRIPTION, StoryTestUtil.TITLE);
        when(repositoryMock.findOne(StoryTestUtil.ID)).thenReturn(model);

        Story actual = service.findById(StoryTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(StoryTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(model, actual);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdWhenIsNotFound() throws NotFoundException {
        when(repositoryMock.findOne(StoryTestUtil.ID)).thenReturn(null);

        service.findById(StoryTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(StoryTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void update() throws NotFoundException {
        StoryDTO dto = StoryTestUtil.createFormObject(StoryTestUtil.ID, StoryTestUtil.DESCRIPTION_UPDATED, StoryTestUtil.TITLE_UPDATED);
        Story model = StoryTestUtil.createModel(StoryTestUtil.ID, StoryTestUtil.DESCRIPTION, StoryTestUtil.TITLE);
        when(repositoryMock.findOne(dto.getId())).thenReturn(model);

        Story actual = service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(dto.getId(), actual.getId());
        assertEquals(dto.getDescription(), actual.getDescription());
        assertEquals(dto.getTitle(), actual.getTitle());
    }

    @Test(expected = NotFoundException.class)
    public void updateWhenIsNotFound() throws NotFoundException {
        StoryDTO dto = StoryTestUtil.createFormObject(StoryTestUtil.ID, StoryTestUtil.DESCRIPTION_UPDATED, StoryTestUtil.TITLE_UPDATED);
        when(repositoryMock.findOne(dto.getId())).thenReturn(null);

        service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);
    }
}
