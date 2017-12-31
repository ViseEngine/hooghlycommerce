package co.hooghly.commerce.business;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.GroupType;
import co.hooghly.commerce.repository.GroupRepository;

@Service
public class GroupService extends SalesManagerEntityServiceImpl<Integer, Group> {

	GroupRepository groupRepository;

	public GroupService(GroupRepository groupRepository) {
		super(groupRepository);
		this.groupRepository = groupRepository;

	}

	public List<Group> listGroup(GroupType groupType) throws ServiceException {

		return groupRepository.findByType(groupType);

	}

	public List<Group> listGroupByIds(Set<Integer> ids) {

		return groupRepository.findByIds(ids);

	}

}
