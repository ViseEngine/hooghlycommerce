package co.hooghly.commerce.repository;

import co.hooghly.commerce.domain.PermissionCriteria;
import co.hooghly.commerce.domain.PermissionList;




public interface PermissionRepositoryCustom {

	PermissionList listByCriteria(PermissionCriteria criteria);


}
