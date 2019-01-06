package ro.sapientia2015.story.controller;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ro.sapientia2015.story.dto.StoryDTO;
import ro.sapientia2015.story.dto.ToDoDTO;
import ro.sapientia2015.story.exception.NotFoundException;
import ro.sapientia2015.story.model.Story;
import ro.sapientia2015.story.model.ToDo;
import ro.sapientia2015.story.service.StoryService;
import ro.sapientia2015.story.service.ToDoService;

import javax.annotation.Resource;
import javax.validation.Valid;

import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * @author Kovacs Szabolcs
 */
@Controller
@SessionAttributes("todos")
public class ToDoController {

    protected static final String FEEDBACK_MESSAGE_KEY_ADDED = "feedback.message.todo.added";
    protected static final String FEEDBACK_MESSAGE_KEY_UPDATED = "feedback.message.todo.updated";
    protected static final String FEEDBACK_MESSAGE_KEY_DELETED = "feedback.message.todo.deleted";

    protected static final String FLASH_MESSAGE_KEY_ERROR = "errorMessage";
    protected static final String FLASH_MESSAGE_KEY_FEEDBACK = "feedbackMessage";

    protected static final String MODEL_ATTRIBUTE = "todo";
    protected static final String MODEL_ATTRIBUTE_LIST = "todos";
    protected static final String MODEL_ID = "id";

    protected static final String PARAMETER_ID = "id";

    protected static final String REQUEST_MAPPING_LIST = "/todos/list";
    protected static final String REQUEST_MAPPING_VIEW = "/todo/{id}";

    protected static final String VIEW_ADD = "todo/add";
    protected static final String VIEW_LIST = "todo/list";
    protected static final String VIEW_UPDATE = "todo/update";
    protected static final String VIEW_VIEW = "todo/view";

    @Resource
    private ToDoService toDoService;
    
    @Resource
    private StoryService storyService;

   
    
    @Resource
    private MessageSource messageSource;
    
    @RequestMapping(value = REQUEST_MAPPING_VIEW, method = RequestMethod.GET)
    public String findById(@PathVariable("id") Long id, Model model) throws NotFoundException {
        ToDo found = toDoService.findById(id);
        model.addAttribute(MODEL_ATTRIBUTE, found);
        return VIEW_VIEW;
    }
    
    
    @RequestMapping(value = "/todo/add/{id}", method = RequestMethod.GET)
    public String showAddForm(@PathVariable("id") Long id,Model model) throws NotFoundException {
    	
    	storyService.findById(id);
    	ToDoDTO formObject = new ToDoDTO();
        model.addAttribute(MODEL_ATTRIBUTE, formObject);
        model.addAttribute(MODEL_ID, id);

        return VIEW_ADD;
    }
    
    @RequestMapping(value = "/todo/add/{id}", method = RequestMethod.POST)
    public String add(@PathVariable("id") Long id,@Valid @ModelAttribute(MODEL_ATTRIBUTE) ToDoDTO dto, BindingResult result, RedirectAttributes attributes) throws NotFoundException {
        if (result.hasErrors()) {	
            return VIEW_ADD;
        }
        Story added=storyService.addToDo(id, dto);
        
        
        
        addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_ADDED, dto.getTitle());
        attributes.addAttribute(PARAMETER_ID, added.getId());

        return createRedirectViewPath("/story/"+added.getId());
    }
    
    @RequestMapping(value = "/todo/update/{id}", method = RequestMethod.GET)
    public String showUpdateForm(@PathVariable("id") Long id, Model model) throws NotFoundException {
        ToDo updated = toDoService.findById(id);
        ToDoDTO formObject = constructFormObjectForUpdateForm(updated);
        model.addAttribute(MODEL_ATTRIBUTE, formObject);

        return VIEW_UPDATE;
    }

    @RequestMapping(value = "/todo/update", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute(MODEL_ATTRIBUTE) ToDoDTO dto, BindingResult result, RedirectAttributes attributes) throws NotFoundException {
        if (result.hasErrors()) {
            return VIEW_UPDATE;
        }

        ToDo updated = toDoService.update(dto);
        addFeedbackMessage(attributes, FEEDBACK_MESSAGE_KEY_UPDATED, updated.getTitle());
        attributes.addAttribute(PARAMETER_ID, updated.getId());

        return createRedirectViewPath(REQUEST_MAPPING_VIEW);
    }
    private ToDoDTO constructFormObjectForUpdateForm(ToDo updated) {
    	ToDoDTO dto = new ToDoDTO();

        dto.setId(updated.getId());
        dto.setDescription(updated.getDescription());
        dto.setTitle(updated.getTitle());

        return dto;
    }
/*
    @RequestMapping(value = "/todos/random", method = RequestMethod.GET)
    public String random(Model model) {
    	
        Random rand = new Random();

        int n = rand.nextInt(50) + 1;
        
        ToDoDTO temp=new ToDoDTO();
        temp.setId(new Long(n));
        temp.setDescription("teszt"+n);
        temp.setTitle("title"+n);
        temp.setPriority(10);
      
		temp.setStory(storyService.findAll().iterator().next());
	
        
        toDoService.add(temp);

        return VIEW_LIST;
    }
    



    @RequestMapping(value = REQUEST_MAPPING_LIST, method = RequestMethod.GET)
    public String findAll(Model model) {
    	
        List<ToDo> models = toDoService.findAll();
        
		for (ToDo temp : models) {
			System.out.println(temp);
		}
		System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\\\");
		for (Story temp : storyService.findAll()) {
			System.out.println(temp);
		}
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        models.add( ToDo.getBuilder("teszt").description("teszt2").id(new Long(1)).build());
        model.addAttribute(MODEL_ATTRIBUTE_LIST, models);
        return VIEW_LIST;
    }
    
*/
    








    private void addFeedbackMessage(RedirectAttributes attributes, String messageCode, Object... messageParameters) {
        String localizedFeedbackMessage = getMessage(messageCode, messageParameters);
        attributes.addFlashAttribute(FLASH_MESSAGE_KEY_FEEDBACK, localizedFeedbackMessage);
    }

    private String getMessage(String messageCode, Object... messageParameters) {
        Locale current = LocaleContextHolder.getLocale();
        return messageSource.getMessage(messageCode, messageParameters, current);
    }


    private String createRedirectViewPath(String requestMapping) {
        StringBuilder redirectViewPath = new StringBuilder();
        redirectViewPath.append("redirect:");
        redirectViewPath.append(requestMapping);
        return redirectViewPath.toString();
    }
}
