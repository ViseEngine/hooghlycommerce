package co.hooghly.commerce.business;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.GroupType;
import co.hooghly.commerce.repository.GroupRepository;


@Service
public class GroupService extends
		SalesManagerEntityServiceImpl<Integer, Group>  {

	GroupRepository groupRepository;


	
	public GroupService(GroupRepository groupRepository) {
		super(groupRepository);
		this.groupRepository = groupRepository;

	}


	
	public List<Group> listGroup(GroupType groupType) throws ServiceException {
		try {
			return groupRepository.findByType(groupType);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	public List<Group> listGroupByIds(Set<Integer> ids) throws ServiceException {
		try {
			return groupRepository.findByIds(ids);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}


}
