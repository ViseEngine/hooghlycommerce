package co.hooghly.commerce.admin.controller;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.GroupService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.PermissionService;
import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.GroupType;
import co.hooghly.commerce.domain.admin.Menu;
import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.web.ui.AjaxPageableResponse;
import co.hooghly.commerce.web.ui.AjaxResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class AdminGroupsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(AdminGroupsController.class);

	@Inject
	LanguageService languageService;

	@Inject
	protected GroupService groupService;
	
	@Inject
	PermissionService permissionService;

	@Inject
	CountryService countryService;

	@Inject
	LabelUtils messages;



	
	@RequestMapping(value = "/admin/groups/editGroup.html", method = RequestMethod.GET)
	public String displayGroup(@RequestParam("id") Integer groupId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// display menu
		setMenu(model, request);

		Group group = groupService.getById(groupId);

		model.addAttribute("group", group);

		return "admin-user-group";
	}



	
	@RequestMapping(value = "/admin/groups/groups.html", method = RequestMethod.GET)
	public String displayGroups(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		setMenu(model, request);
		List<Group> groups = groupService.listGroup(GroupType.ADMIN);
		model.addAttribute("groups", groups);

		return "admin-user-groups";
	}

	
	
	@RequestMapping(value = "/admin/groups/paging.html", method = RequestMethod.POST)
	public @ResponseBody
	ResponseEntity<String> pageGroups(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {

		AjaxResponse resp = new AjaxResponse();
		try {

				List<Group> groups = groupService.list();

				for(Group group : groups) {
					Map entry = new HashMap();
					entry.put("groupId", group.getId());
					entry.put("name", group.getGroupName());

					StringBuilder key = new StringBuilder().append("security.group.description.").append(group.getGroupName());
					try {
					
						String message =  messages.getMessage(key.toString(), locale);
						entry.put("description",message);
					
					} catch(Exception noLabelException) {
						LOGGER.error("No label found for key [" + key.toString() + "]");
					}
					
					
					

					resp.addDataEntry(entry);
				}

			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			LOGGER.error("Error while paging permissions", e);
			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	

	private void setMenu(Model model, HttpServletRequest request)
			 {

		// display menu
		Map<String, String> activeMenus = new HashMap<String, String>();
		activeMenus.put("profile", "profile");
		activeMenus.put("security", "security");

		@SuppressWarnings("unchecked")
		Map<String, Menu> menus = (Map<String, Menu>) request
				.getAttribute("MENUMAP");

		Menu currentMenu = (Menu) menus.get("profile");
		model.addAttribute("currentMenu", currentMenu);
		model.addAttribute("activeMenus", activeMenus);
		//

	}

}
