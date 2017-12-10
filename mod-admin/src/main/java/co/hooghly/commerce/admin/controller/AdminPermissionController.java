package co.hooghly.commerce.admin.controller;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.GroupService;
import co.hooghly.commerce.business.PermissionService;
import co.hooghly.commerce.domain.Permission;

import co.hooghly.commerce.web.ui.AjaxResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class AdminPermissionController {

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected GroupService groupService;

	@Autowired
	CountryService countryService;

	

	@RequestMapping(value = "/admin/permissions/permissions.html", method = RequestMethod.GET)
	public String displayPermissions(Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		return "admin-user-permissions";

	}

	@SuppressWarnings("unchecked")

	@RequestMapping(value = "/admin/permissions/paging.html", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pagePermissions(@RequestParam("name") String permissionName, HttpServletRequest request,
			HttpServletResponse response) {
		
		AjaxResponse resp = new AjaxResponse();

		try {

			List<Permission> permissions = null;
			permissions = permissionService.listPermission();

			for (Permission permission : permissions) {

				@SuppressWarnings("rawtypes")
				Map entry = new HashMap();
				entry.put("permissionId", permission.getId());
				entry.put("name", permission.getPermissionName());
				resp.addDataEntry(entry);

			}

			resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			log.error("Error while paging permissions", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}

		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
	}

	

}
