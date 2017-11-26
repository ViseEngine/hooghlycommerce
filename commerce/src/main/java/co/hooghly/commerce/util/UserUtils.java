package co.hooghly.commerce.util;

import java.util.List;

import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.User;

public class UserUtils {
	
	public static boolean userInGroup(User user,String groupName) {
		
		
		
		List<Group> logedInUserGroups = user.getGroups();
		for(Group group : logedInUserGroups) {
			if(group.getGroupName().equals(groupName)) {
				return true;
			}
		}
		
		return false;
		
	}

}
