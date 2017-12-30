package co.hooghly.commerce.startup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.hooghly.commerce.business.GroupService;

import co.hooghly.commerce.business.PermissionService;
import co.hooghly.commerce.business.WebUserServices;
import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.GroupType;

import co.hooghly.commerce.domain.Permission;
import co.hooghly.commerce.domain.admin.Permissions;
import co.hooghly.commerce.domain.admin.ShopPermission;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(8)
public class UserGroupPermissionPopulator extends AbstractDataPopulator {
	
	public UserGroupPermissionPopulator() {
		super("USERGROUPPERMISSION");
	}

	
	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private ObjectMapper jacksonObjectMapper;

	

	@Autowired
	protected GroupService groupService;

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	private WebUserServices userDetailsService;

	@Override
	public void runInternal(String... args) throws Exception {
		log.info("8.Populating User, Group, Permissions.");
		//populate();
	}

	public void populate() throws Exception {
		File permissionXML = resourceLoader.getResource("classpath:/permission/permission.json").getFile();
		StreamSource xmlSource = new StreamSource(permissionXML);
		// Permissions permissions= (Permissions)
		// jaxb2Marshaller.unmarshal(xmlSource);

		Permissions permissions = jacksonObjectMapper.readValue(permissionXML, Permissions.class);

		

		// security groups and permissions

		Map<String, Group> groupMap = new HashMap<String, Group>();
		if (CollectionUtils.isNotEmpty(permissions.getShopPermission())) {

			for (ShopPermission shopPermission : permissions.getShopPermission()) {

				Permission permission = new Permission(shopPermission.getType());

				for (String groupName : shopPermission.getShopGroup().getName()) {
					if (groupMap.get(groupName) == null) {
						Group group = new Group(groupName);
						group.setGroupType(GroupType.ADMIN);
						groupService.create(group);
						groupMap.put(groupName, group);
						permission.getGroups().add(group);
					} else {
						permission.getGroups().add(groupMap.get(groupName));
					}
					permissionService.create(permission);
				}

			}
		}

		userDetailsService.createDefaultAdmin();
	}
}
