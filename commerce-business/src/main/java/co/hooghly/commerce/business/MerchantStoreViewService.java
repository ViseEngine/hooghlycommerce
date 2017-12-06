package co.hooghly.commerce.business;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.MerchantStoreView;

import co.hooghly.commerce.repository.MerchantStoreViewRepository;

@Service
public class MerchantStoreViewService extends SalesManagerEntityServiceImpl<Integer, MerchantStoreView> {

	private MerchantStoreViewRepository merchantStoreViewRepository;

	public MerchantStoreViewService(JpaRepository<MerchantStoreView, Integer> repository) {
		super(repository);
		this.merchantStoreViewRepository = (MerchantStoreViewRepository)repository;
	}
	
	public Optional<MerchantStoreView> findByCode(String code){
		return merchantStoreViewRepository.findByCode(code);
	}
}
