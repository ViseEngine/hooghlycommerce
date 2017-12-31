package co.hooghly.commerce.admin.controller;

import co.hooghly.commerce.business.GroupService;
import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.GroupType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;


@Controller
public class AdminSecurityController {
	
	@Autowired
	GroupService groupService;

	@RequestMapping(value="/admin/user/permissions.html", method=RequestMethod.GET)
	public String displayPermissions(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		return "admin-user-permissions";
		
		
	}
	
	
	@RequestMapping(value="/admin/user/groups.html", method=RequestMethod.GET)
	public String displayGroups(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		List<Group> groups = groupService.listGroup(GroupType.ADMIN);
		
		model.addAttribute("groups", groups);
		
		return "admin-user-groups";
		
		
	}
	
	

}
