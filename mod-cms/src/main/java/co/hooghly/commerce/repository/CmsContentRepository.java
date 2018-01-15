package co.hooghly.commerce.repository;

import java.util.List;
import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.hooghly.commerce.domain.CmsContent;

@Repository
public interface CmsContentRepository extends JpaRepository<CmsContent, Long>{
	List<CmsContent> findByFolder(String folder);
	Optional<CmsContent> findByCode(String code);
	Optional<CmsContent> findByMerchantStoreIdAndAndCode(Long id, String code);
}
