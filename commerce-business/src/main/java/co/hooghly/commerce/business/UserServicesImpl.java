package co.hooghly.commerce.business;

import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.GroupType;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service("userDetailsService")
public class UserServicesImpl implements WebUserServices {

	private static final String DEFAULT_INITIAL_PASSWORD = "password";

	@Autowired
	private UserService userService;

	@Autowired
	private MerchantStoreService merchantStoreService;

	@Autowired
	@Qualifier("passwordEncoder")
	private PasswordEncoder passwordEncoder;

	@Autowired
	protected PermissionService permissionService;

	@Autowired
	protected GroupService groupService;

	public final static String ROLE_PREFIX = "ROLE_";// Spring Security 4

	public UserDetails loadUserByUsername(String userName) {

		co.hooghly.commerce.domain.User user = null;
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		user = userService.getByUserName(userName);

		if (user == null) {
			return null;
		}

		GrantedAuthority role = new SimpleGrantedAuthority(ROLE_PREFIX + "AUTH");// required
																					// to
																					// login
		authorities.add(role);

		List<Integer> groupsId = new ArrayList<Integer>();
		List<Group> groups = user.getGroups();
		for (Group group : groups) {

			groupsId.add(group.getId());

		}

		List<Permission> permissions = permissionService.getPermissions(groupsId);
		for (Permission permission : permissions) {
			GrantedAuthority auth = new SimpleGrantedAuthority(ROLE_PREFIX + permission.getPermissionName());
			authorities.add(auth);
		}

		User secUser = new User(userName, user.getAdminPassword(), user.isActive(), true, true, true, authorities);
		return secUser;
	}

	public void createDefaultAdmin() throws Exception {

		// TODO create all groups and permissions

		MerchantStore store = merchantStoreService.getMerchantStore(MerchantStore.DEFAULT_STORE);

		String password = passwordEncoder.encode(DEFAULT_INITIAL_PASSWORD);

		List<Group> groups = groupService.listGroup(GroupType.ADMIN);

		// creation of the super admin admin:password)
		co.hooghly.commerce.domain.User user = new co.hooghly.commerce.domain.User("admin", password,
				"admin@hooghly.co");
		user.setFirstName("Administrator");
		user.setLastName("User");

		for (Group group : groups) {
			if (group.getGroupName().equals("SUPERADMIN") || group.getGroupName().equals("ADMIN")) {
				user.getGroups().add(group);
			}
		}

		user.setMerchantStore(store);
		userService.create(user);

	}

}
