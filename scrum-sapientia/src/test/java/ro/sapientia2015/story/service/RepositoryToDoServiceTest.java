package ro.sapientia2015.story.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import ro.sapientia2015.story.ToDoTestUtil;
import ro.sapientia2015.story.dto.ToDoDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.ToDo;
import ro.sapientia2015.story.repository.ToDoRepository;
import ro.sapientia2015.story.service.RepositoryToDoService;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * @author Kiss Tibor
 */
public class RepositoryToDoServiceTest {

    private RepositoryToDoService service;

    private ToDoRepository repositoryMock;

    @Before
    public void setUp() {
        service = new RepositoryToDoService();

        repositoryMock = mock(ToDoRepository.class);
        ReflectionTestUtils.setField(service, "repository", repositoryMock);
    }

    @Test
    public void add() {
        ToDoDTO dto = ToDoTestUtil.createFormObject(null, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);

        service.add(dto);

        ArgumentCaptor<ToDo> ToDoArgument = ArgumentCaptor.forClass(ToDo.class);
        verify(repositoryMock, times(1)).save(ToDoArgument.capture());
        verifyNoMoreInteractions(repositoryMock);

        ToDo model = ToDoArgument.getValue();

        assertNull(model.getId());
        assertEquals(dto.getDescription(), model.getDescription());
        assertEquals(dto.getTitle(), model.getTitle());
    }

    @Test
    public void deleteById() throws NotFoundException {
        ToDo model = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(repositoryMock.findOne(ToDoTestUtil.ID)).thenReturn(model);

        ToDo actual = service.deleteById(ToDoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(ToDoTestUtil.ID);
        verify(repositoryMock, times(1)).delete(model);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(model, actual);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdWhenIsNotFound() throws NotFoundException {
        when(repositoryMock.findOne(ToDoTestUtil.ID)).thenReturn(null);

        service.deleteById(ToDoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(ToDoTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void findAll() {
        List<ToDo> models = new ArrayList<ToDo>();
        when(repositoryMock.findAll()).thenReturn(models);

        List<ToDo> actual = service.findAll();

        verify(repositoryMock, times(1)).findAll();
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(models, actual);
    }

    @Test
    public void findById() throws NotFoundException {
        ToDo model = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(repositoryMock.findOne(ToDoTestUtil.ID)).thenReturn(model);

        ToDo actual = service.findById(ToDoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(ToDoTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(model, actual);
    }

    @Test(expected = NotFoundException.class)
    public void findByIdWhenIsNotFound() throws NotFoundException {
        when(repositoryMock.findOne(ToDoTestUtil.ID)).thenReturn(null);

        service.findById(ToDoTestUtil.ID);

        verify(repositoryMock, times(1)).findOne(ToDoTestUtil.ID);
        verifyNoMoreInteractions(repositoryMock);
    }

    @Test
    public void update() throws NotFoundException {
        ToDoDTO dto = ToDoTestUtil.createFormObject(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION_UPDATED, ToDoTestUtil.TITLE_UPDATED);
        ToDo model = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(repositoryMock.findOne(dto.getId())).thenReturn(model);

        ToDo actual = service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);

        assertEquals(dto.getId(), actual.getId());
        assertEquals(dto.getDescription(), actual.getDescription());
        assertEquals(dto.getTitle(), actual.getTitle());
    }

    @Test(expected = NotFoundException.class)
    public void updateWhenIsNotFound() throws NotFoundException {
        ToDoDTO dto = ToDoTestUtil.createFormObject(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION_UPDATED, ToDoTestUtil.TITLE_UPDATED);
        when(repositoryMock.findOne(dto.getId())).thenReturn(null);

        service.update(dto);

        verify(repositoryMock, times(1)).findOne(dto.getId());
        verifyNoMoreInteractions(repositoryMock);
    }
}
