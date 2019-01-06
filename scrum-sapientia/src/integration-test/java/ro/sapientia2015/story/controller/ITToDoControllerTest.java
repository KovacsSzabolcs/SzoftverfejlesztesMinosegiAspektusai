
package ro.sapientia2015.story.controller;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.server.MockMvc;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ro.sapientia2015.common.controller.ErrorController;
import ro.sapientia2015.config.ExampleApplicationContext;
import ro.sapientia2015.context.WebContextLoader;
import ro.sapientia2015.story.StoryTestUtil;
import ro.sapientia2015.story.ToDoTestUtil;
import ro.sapientia2015.story.controller.StoryController;
import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.dto.ToDoDTO;
import ro.sapientia2015.story.model.Story;

import javax.annotation.Resource;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.view;

/**
 * This test uses the annotation based application context configuration.
 * @author Kovacs Szabolcs
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = WebContextLoader.class, classes = {ExampleApplicationContext.class})
//@ContextConfiguration(loader = WebContextLoader.class, locations = {"classpath:exampleApplicationContext.xml"})
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class })
@DatabaseSetup("storyData.xml")
public class ITToDoControllerTest {

    private static final String FORM_FIELD_DESCRIPTION = "description";
    private static final String FORM_FIELD_ID = "id";
    private static final String FORM_FIELD_TITLE = "title";

    @Resource
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webApplicationContextSetup(webApplicationContext)
                .build();
    }

    
    @Test
    @ExpectedDatabase(assertionMode=DatabaseAssertionMode.NON_STRICT,value= "storyData-delete-expected.xml")
    public void deleteById() throws Exception {
        String expectedRedirectViewPath = StoryTestUtil.createRedirectViewPath(StoryController.REQUEST_MAPPING_LIST);
        mockMvc.perform(get("/story/delete/{id}", 2L))
                .andExpect(status().isOk())
                .andExpect(view().name(expectedRedirectViewPath))
                .andExpect(flash().attribute(StoryController.FLASH_MESSAGE_KEY_FEEDBACK, is("Story entry: Bar was deleted.")));
    }
    @Test
    @ExpectedDatabase(value="toDoData-update-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void update() throws Exception {
        String expectedRedirectViewPath =ToDoTestUtil.createRedirectViewPath(ToDoController.REQUEST_MAPPING_VIEW);

        mockMvc.perform(post("/todo/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(FORM_FIELD_DESCRIPTION, "gud")
                .param(FORM_FIELD_ID, "1")
                .param(FORM_FIELD_TITLE, "git")
                .sessionAttr(ToDoController.MODEL_ATTRIBUTE, new ToDoDTO())
        )
                .andExpect(status().isOk())
                .andExpect(view().name(expectedRedirectViewPath))
                .andExpect(model().attribute(ToDoController.PARAMETER_ID, is("1")))
                .andExpect(flash().attribute(ToDoController.FLASH_MESSAGE_KEY_FEEDBACK, is("To do entry: git was updated.")));
    }
    
    @Test
    @ExpectedDatabase(assertionMode=DatabaseAssertionMode.NON_STRICT,value= "storyData.xml")
    public void updateWhenIsNotFound() throws Exception {
        mockMvc.perform(post("/todo/update")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(FORM_FIELD_DESCRIPTION, "description2")
                .param(FORM_FIELD_ID, "5")
                .param(FORM_FIELD_TITLE, "title2")
                .sessionAttr(ToDoController.MODEL_ATTRIBUTE, new ToDoDTO())
        )
                .andExpect(status().isNotFound())
                .andExpect(view().name(ErrorController.VIEW_NOT_FOUND))
                .andExpect(forwardedUrl("/WEB-INF/jsp/error/404.jsp"));
    }

    @Test
    @ExpectedDatabase(value="toDoData-add-expected.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void add() throws Exception {
        String expectedRedirectViewPath = ToDoTestUtil.createRedirectViewPath("/story/1");

        mockMvc.perform(post("/todo/add/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param(FORM_FIELD_DESCRIPTION, "description")
                .param(FORM_FIELD_TITLE, "title")
                .sessionAttr(ToDoController.MODEL_ATTRIBUTE, new ToDoDTO())
        )
                .andExpect(status().isOk())
                .andExpect(view().name(expectedRedirectViewPath))
                .andExpect(model().attribute(ToDoController.PARAMETER_ID, is("1")))
                .andExpect(flash().attribute(StoryController.FLASH_MESSAGE_KEY_FEEDBACK, is("To do entry: title was added.")));
    }
    
    @Test
    @ExpectedDatabase(assertionMode=DatabaseAssertionMode.NON_STRICT,value= "storyData.xml")
    public void findById() throws Exception {
        mockMvc.perform(get("/todo/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name(ToDoController.VIEW_VIEW))
                .andExpect(forwardedUrl("/WEB-INF/jsp/todo/view.jsp"))
                .andExpect(model().attribute(ToDoController.MODEL_ATTRIBUTE, hasProperty("id", is(1L))))
                .andExpect(model().attribute(ToDoController.MODEL_ATTRIBUTE, hasProperty("description", is("rekt"))))
                .andExpect(model().attribute(ToDoController.MODEL_ATTRIBUTE, hasProperty("title", is("get"))));
        		//.andExpect(model().attribute(ToDoController.MODEL_ATTRIBUTE, hasProperty("story", is("ro.sapientia2015.story.model.Story@50bc3219[id=1,creationTime=2012-10-21T14:13:28.000+03:00,description=Lorem ipsum,modificationTime=2012-10-21T14:13:28.000+03:00,title=Foo,version=0,todos=[ro.sapientia2015.story.model.ToDo@78b03788[id=1,creationTime=2012-10-21T14:13:28.000+03:00,description=rekt,modificationTime=2012-10-21T14:13:28.000+03:00,title=get,version=0,story=ro.sapientia2015.story.model.Story@50bc3219]]]"))));
    }
    

}

