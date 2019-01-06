package ro.sapientia2015.story.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.MessageSource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.support.BindingAwareModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import ro.sapientia2015.story.StoryTestUtil;
import ro.sapientia2015.story.ToDoTestUtil;
import ro.sapientia2015.story.config.UnitTestContext;
import ro.sapientia2015.story.controller.ToDoController;
import ro.sapientia2015.story.dto.ToDoDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.model.ToDo;
import ro.sapientia2015.story.service.StoryService;
import ro.sapientia2015.story.service.ToDoService;
import ro.sapientia2015.story.service.ToDoService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Kiss Tibor
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UnitTestContext.class})
public class ToDoControllerTest {

    private static final String FEEDBACK_MESSAGE = "feedbackMessage";
    private static final String FIELD_DESCRIPTION = "description";
    private static final String FIELD_TITLE = "title";
    private static final Long FIELD_ID= 1L;
    
    
    private ToDoController controller;

    private MessageSource messageSourceMock;

    private StoryService storyServiceMock;

    private ToDoService toDoServiceMock;
    
    @Resource
    private Validator validator;

    @Before
    public void setUp() {
        controller = new ToDoController();

        messageSourceMock = mock(MessageSource.class);
        ReflectionTestUtils.setField(controller, "messageSource", messageSourceMock);
        

        storyServiceMock = mock(StoryService.class);
        ReflectionTestUtils.setField(controller, "storyService", storyServiceMock);
        
        toDoServiceMock = mock(ToDoService.class);
        ReflectionTestUtils.setField(controller, "toDoService", toDoServiceMock);
        
    }

    @Test
    public void findById() throws NotFoundException {
    	
        BindingAwareModelMap model = new BindingAwareModelMap();

        ToDo found = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(toDoServiceMock.findById(ToDoTestUtil.ID)).thenReturn(found);

        String view = controller.findById(ToDoTestUtil.ID, model);

        verify(toDoServiceMock, times(1)).findById(ToDoTestUtil.ID);
        verifyNoMoreInteractions(toDoServiceMock);
        verifyZeroInteractions(messageSourceMock);

        assertEquals(ToDoController.VIEW_VIEW, view);
        assertEquals(found, model.asMap().get(ToDoController.MODEL_ATTRIBUTE));
        
        
    }
    @Test
    public void showAddToDoForm() throws NotFoundException {
        BindingAwareModelMap model = new BindingAwareModelMap();

        Story found = StoryTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(storyServiceMock.findById(ToDoTestUtil.ID)).thenReturn(found);
  
        String view = controller.showAddForm(ToDoTestUtil.ID,model);

        verify(storyServiceMock, times(1)).findById(ToDoTestUtil.ID);
        verifyNoMoreInteractions(storyServiceMock);
        verifyZeroInteractions(messageSourceMock, toDoServiceMock);
        assertEquals(ToDoController.VIEW_ADD, view);

        ToDoDTO formObject = (ToDoDTO) model.asMap().get(ToDoController.MODEL_ATTRIBUTE);
        Long storyID=(Long) model.asMap().get(ToDoController.MODEL_ID);
        
        assertEquals(storyID,ToDoTestUtil.ID);
        assertNull(formObject.getId());
        assertNull(formObject.getDescription());
        assertNull(formObject.getTitle());
    }
    @Test
    public void add()throws NotFoundException {
    	ToDoDTO todoDTO=ToDoTestUtil.createFormObject(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
    	ToDo todo=ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
    	Story story=StoryTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
    	
    	when(storyServiceMock.addToDo(StoryTestUtil.ID,todoDTO)).thenReturn(story);
    	
///
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/todo/add/"+story.getId());
        BindingResult result = bindAndValidate(mockRequest, todoDTO);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        initMessageSourceForFeedbackMessage(ToDoController.FEEDBACK_MESSAGE_KEY_ADDED);

        String view = controller.add(story.getId(),todoDTO, result, attributes);

        verify(storyServiceMock, times(1)).addToDo(StoryTestUtil.ID,todoDTO);
        verifyNoMoreInteractions(storyServiceMock);
        verifyNoMoreInteractions(toDoServiceMock);
        String expectedView = ToDoTestUtil.createRedirectViewPath("/story/"+story.getId());
        assertEquals(expectedView, view);

        assertEquals(Long.valueOf((String) attributes.get(ToDoController.PARAMETER_ID)), todoDTO.getId());

        assertFeedbackMessage(attributes, ToDoController.FEEDBACK_MESSAGE_KEY_ADDED);
    	
    	
    }
/*
    @Test
    public void add() {
        ToDoDTO formObject = ToDoTestUtil.createFormObject(null, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);

        ToDo model = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(serviceMock.add(formObject)).thenReturn(model);
        
        //SZABI
        verifyNoMoreInteractions(toDoServiceMock);
        //
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/ToDo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        initMessageSourceForFeedbackMessage(ToDoController.FEEDBACK_MESSAGE_KEY_ADDED);

        String view = controller.add(formObject, result, attributes);

        verify(serviceMock, times(1)).add(formObject);
        verifyNoMoreInteractions(serviceMock);

        String expectedView = ToDoTestUtil.createRedirectViewPath(ToDoController.REQUEST_MAPPING_VIEW);
        assertEquals(expectedView, view);

        assertEquals(Long.valueOf((String) attributes.get(ToDoController.PARAMETER_ID)), model.getId());

        assertFeedbackMessage(attributes, ToDoController.FEEDBACK_MESSAGE_KEY_ADDED);
    }

    @Test
    public void addEmptyToDo() {
        ToDoDTO formObject = ToDoTestUtil.createFormObject(null, "", "");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/ToDo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.add(formObject, result, attributes);

        verifyZeroInteractions(serviceMock, messageSourceMock);

        assertEquals(ToDoController.VIEW_ADD, view);
        assertFieldErrors(result, FIELD_TITLE);
    }

    @Test
    public void addWithTooLongDescriptionAndTitle() {
        String description = ToDoTestUtil.createStringWithLength(ToDo.MAX_LENGTH_DESCRIPTION + 1);
        String title = ToDoTestUtil.createStringWithLength(ToDo.MAX_LENGTH_TITLE + 1);

        ToDoDTO formObject = ToDoTestUtil.createFormObject(null, description, title);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/ToDo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.add(formObject, result, attributes);

        verifyZeroInteractions(serviceMock, messageSourceMock);

        assertEquals(ToDoController.VIEW_ADD, view);
        assertFieldErrors(result, FIELD_DESCRIPTION, FIELD_TITLE);
    }

    @Test
    public void deleteById() throws NotFoundException {
        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        ToDo model = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(serviceMock.deleteById(ToDoTestUtil.ID)).thenReturn(model);

        initMessageSourceForFeedbackMessage(ToDoController.FEEDBACK_MESSAGE_KEY_DELETED);

        String view = controller.deleteById(ToDoTestUtil.ID, attributes);

        verify(serviceMock, times(1)).deleteById(ToDoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);

        assertFeedbackMessage(attributes, ToDoController.FEEDBACK_MESSAGE_KEY_DELETED);

        String expectedView = ToDoTestUtil.createRedirectViewPath(ToDoController.REQUEST_MAPPING_LIST);
        assertEquals(expectedView, view);
    }

    @Test(expected = NotFoundException.class)
    public void deleteByIdWhenIsNotFound() throws NotFoundException {
        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        when(serviceMock.deleteById(ToDoTestUtil.ID)).thenThrow(new NotFoundException(""));

        controller.deleteById(ToDoTestUtil.ID, attributes);

        verify(serviceMock, times(1)).deleteById(ToDoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);
    }

    @Test
    public void findAll() {
        BindingAwareModelMap model = new BindingAwareModelMap();

        List<ToDo> models = new ArrayList<ToDo>();
        when(serviceMock.findAll()).thenReturn(models);

        String view = controller.findAll(model);

        verify(serviceMock, times(1)).findAll();
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);

        assertEquals(ToDoController.VIEW_LIST, view);
        assertEquals(models, model.asMap().get(ToDoController.MODEL_ATTRIBUTE_LIST));
    }

    @Test
    public void findById() throws NotFoundException {
        BindingAwareModelMap model = new BindingAwareModelMap();

        ToDo found = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(serviceMock.findById(ToDoTestUtil.ID)).thenReturn(found);

        String view = controller.findById(ToDoTestUtil.ID, model);

        verify(serviceMock, times(1)).findById(ToDoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);

        assertEquals(ToDoController.VIEW_VIEW, view);
        assertEquals(found, model.asMap().get(ToDoController.MODEL_ATTRIBUTE));
    }

    @Test(expected = NotFoundException.class)
    public void findByIdWhenIsNotFound() throws NotFoundException {
        BindingAwareModelMap model = new BindingAwareModelMap();

        when(serviceMock.findById(ToDoTestUtil.ID)).thenThrow(new NotFoundException(""));

        controller.findById(ToDoTestUtil.ID, model);

        verify(serviceMock, times(1)).findById(ToDoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);
    }

    @Test
    public void showUpdateToDoForm() throws NotFoundException {
        BindingAwareModelMap model = new BindingAwareModelMap();

        ToDo updated = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION, ToDoTestUtil.TITLE);
        when(serviceMock.findById(ToDoTestUtil.ID)).thenReturn(updated);

        String view = controller.showUpdateForm(ToDoTestUtil.ID, model);

        verify(serviceMock, times(1)).findById(ToDoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);

        assertEquals(ToDoController.VIEW_UPDATE, view);

        ToDoDTO formObject = (ToDoDTO) model.asMap().get(ToDoController.MODEL_ATTRIBUTE);

        assertEquals(updated.getId(), formObject.getId());
        assertEquals(updated.getDescription(), formObject.getDescription());
        assertEquals(updated.getTitle(), formObject.getTitle());
    }

    @Test(expected = NotFoundException.class)
    public void showUpdateToDoFormWhenIsNotFound() throws NotFoundException {
        BindingAwareModelMap model = new BindingAwareModelMap();

        when(serviceMock.findById(ToDoTestUtil.ID)).thenThrow(new NotFoundException(""));

        controller.showUpdateForm(ToDoTestUtil.ID, model);

        verify(serviceMock, times(1)).findById(ToDoTestUtil.ID);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);
    }

    @Test
    public void update() throws NotFoundException {
        ToDoDTO formObject = ToDoTestUtil.createFormObject(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION_UPDATED, ToDoTestUtil.TITLE_UPDATED);

        ToDo model = ToDoTestUtil.createModel(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION_UPDATED, ToDoTestUtil.TITLE_UPDATED);
        when(serviceMock.update(formObject)).thenReturn(model);

        //SZABI
        verifyNoMoreInteractions(toDoServiceMock);
        //
        
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/ToDo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        initMessageSourceForFeedbackMessage(ToDoController.FEEDBACK_MESSAGE_KEY_UPDATED);

        String view = controller.update(formObject, result, attributes);

        verify(serviceMock, times(1)).update(formObject);
        verifyNoMoreInteractions(serviceMock);

        String expectedView = ToDoTestUtil.createRedirectViewPath(ToDoController.REQUEST_MAPPING_VIEW);
        assertEquals(expectedView, view);

        assertEquals(Long.valueOf((String) attributes.get(ToDoController.PARAMETER_ID)), model.getId());

        assertFeedbackMessage(attributes, ToDoController.FEEDBACK_MESSAGE_KEY_UPDATED);
    }

    @Test
    public void updateEmpty() throws NotFoundException {
        ToDoDTO formObject = ToDoTestUtil.createFormObject(ToDoTestUtil.ID, "", "");

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/ToDo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.update(formObject, result, attributes);

        verifyZeroInteractions(messageSourceMock, serviceMock);

        assertEquals(ToDoController.VIEW_UPDATE, view);
        assertFieldErrors(result, FIELD_TITLE);
    }

    @Test
    public void updateWhenDescriptionAndTitleAreTooLong() throws NotFoundException {
        String description = ToDoTestUtil.createStringWithLength(ToDo.MAX_LENGTH_DESCRIPTION + 1);
        String title = ToDoTestUtil.createStringWithLength(ToDo.MAX_LENGTH_TITLE + 1);

        ToDoDTO formObject = ToDoTestUtil.createFormObject(ToDoTestUtil.ID, description, title);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/ToDo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        String view = controller.update(formObject, result, attributes);

        verifyZeroInteractions(messageSourceMock, serviceMock);

        assertEquals(ToDoController.VIEW_UPDATE, view);
        assertFieldErrors(result, FIELD_DESCRIPTION, FIELD_TITLE);
    }

    @Test(expected = NotFoundException.class)
    public void updateWhenIsNotFound() throws NotFoundException {
        ToDoDTO formObject = ToDoTestUtil.createFormObject(ToDoTestUtil.ID, ToDoTestUtil.DESCRIPTION_UPDATED, ToDoTestUtil.TITLE_UPDATED);

        when(serviceMock.update(formObject)).thenThrow(new NotFoundException(""));

        MockHttpServletRequest mockRequest = new MockHttpServletRequest("POST", "/ToDo/add");
        BindingResult result = bindAndValidate(mockRequest, formObject);

        RedirectAttributesModelMap attributes = new RedirectAttributesModelMap();

        controller.update(formObject, result, attributes);

        verify(serviceMock, times(1)).update(formObject);
        verifyNoMoreInteractions(serviceMock);
        verifyZeroInteractions(messageSourceMock);
    }
*/
    private void assertFeedbackMessage(RedirectAttributes attributes, String messageCode) {
        assertFlashMessages(attributes, messageCode, ToDoController.FLASH_MESSAGE_KEY_FEEDBACK);
    }

    private void assertFieldErrors(BindingResult result, String... fieldNames) {
        assertEquals(fieldNames.length, result.getFieldErrorCount());
        for (String fieldName : fieldNames) {
            assertNotNull(result.getFieldError(fieldName));
        }
    }

    private void assertFlashMessages(RedirectAttributes attributes, String messageCode, String flashMessageParameterName) {
        Map<String, ?> flashMessages = attributes.getFlashAttributes();
        Object message = flashMessages.get(flashMessageParameterName);

        assertNotNull(message);
        flashMessages.remove(message);
        assertTrue(flashMessages.isEmpty());

        verify(messageSourceMock, times(1)).getMessage(eq(messageCode), any(Object[].class), any(Locale.class));
        verifyNoMoreInteractions(messageSourceMock);
    }

    private BindingResult bindAndValidate(HttpServletRequest request, Object formObject) {
        WebDataBinder binder = new WebDataBinder(formObject);
        binder.setValidator(validator);
        binder.bind(new MutablePropertyValues(request.getParameterMap()));
        binder.getValidator().validate(binder.getTarget(), binder.getBindingResult());
        return binder.getBindingResult();
    }

    private void initMessageSourceForFeedbackMessage(String feedbackMessageCode) {
        when(messageSourceMock.getMessage(eq(feedbackMessageCode), any(Object[].class), any(Locale.class))).thenReturn(FEEDBACK_MESSAGE);
    }
}
