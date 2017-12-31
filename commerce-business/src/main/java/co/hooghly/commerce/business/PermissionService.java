package co.hooghly.commerce.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.Permission;
import co.hooghly.commerce.domain.PermissionCriteria;
import co.hooghly.commerce.domain.PermissionList;
import co.hooghly.commerce.repository.PermissionRepository;

@Service("permissionService")
public class PermissionService extends SalesManagerEntityServiceImpl<Integer, Permission> {

	private PermissionRepository permissionRepository;

	public PermissionService(PermissionRepository permissionRepository) {
		super(permissionRepository);
		this.permissionRepository = permissionRepository;

	}

	public List<Permission> getByName() {
		// TODO Auto-generated method stub
		return null;
	}

	public Permission getById(Integer permissionId) {
		return permissionRepository.findOne(permissionId);

	}

	public void deletePermission(Permission permission) throws ServiceException {
		permission = this.getById(permission.getId());// Prevents detached
														// entity error
		permission.setGroups(null);

		this.delete(permission);
	}

	@SuppressWarnings("unchecked")

	public List<Permission> getPermissions(List<Integer> groupIds) throws ServiceException {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set ids = new HashSet(groupIds);
		return permissionRepository.findByGroups(ids);
	}

	public PermissionList listByCriteria(PermissionCriteria criteria) throws ServiceException {
		return permissionRepository.listByCriteria(criteria);
	}

	public void removePermission(Permission permission, Group group) throws ServiceException {
		permission = this.getById(permission.getId());// Prevents detached
														// entity error

		permission.getGroups().remove(group);

	}

	public List<Permission> listPermission() throws ServiceException {
		return permissionRepository.findAll();
	}

}
