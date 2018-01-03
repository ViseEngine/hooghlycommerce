package co.hooghly.commerce.business;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.CmsContent;
import co.hooghly.commerce.repository.CmsContentRepository;
import co.hooghly.commerce.startup.CmsContentPopulator;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CmsContentService {

	@Autowired
	private CmsContentRepository cmsContentRepository;
	
	@Autowired
	private CmsContentPopulator cmsContentPopulator;

	@Transactional(readOnly = true)
	public List<CmsContent> findByFolder(String folder) {
		log.debug("Retrieving pages for folder - {}", folder);
		return cmsContentRepository.findByFolder(folder);
	}

	@Transactional(readOnly = true)
	@Cacheable("content-code")
	public Optional<CmsContent> findByCode(String code) {
		return cmsContentRepository.findByCode(code);
	}
	
	@Transactional(readOnly = true)
	@Cacheable("content-merchantstore-code")
	public Optional<CmsContent> findByMerchantStoreIdAndAndCode(Long storeId, String code){
		log.debug("Finding with merchant store id - {} and code - {}", storeId, code);
		return cmsContentRepository.findByMerchantStoreIdAndAndCode(storeId, code);
	}
	
	public void load(MerchantStore store) {
		cmsContentPopulator.load(store);
	}
}
