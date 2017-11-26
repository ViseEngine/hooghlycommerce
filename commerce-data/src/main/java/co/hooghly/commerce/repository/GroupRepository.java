package co.hooghly.commerce.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.GroupType;

public interface GroupRepository extends JpaRepository<Group, Integer> {


	Group findById(Long id);
	
	@Query("select distinct g from Group as g join fetch g.permissions perms order by g.id")
	List<Group> findAll();
	
	@Query("select distinct g from Group as g join fetch g.permissions perms where perms.id in (?1) ")
	List<Group> findByPermissions(Set<Integer> permissionIds);
	
	@Query("select distinct g from Group as g join fetch g.permissions perms where g.id in (?1) ")
	List<Group> findByIds(Set<Integer> groupIds);
	
	@Query("select distinct g from Group as g join fetch g.permissions perms where g.groupType = ?1")
	List<Group> findByType(GroupType type);
}
