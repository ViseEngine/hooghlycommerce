package co.hooghly.commerce.business;


import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.MerchantLog;
import co.hooghly.commerce.repository.MerchantLogRepository;

@Service
public class MerchantLogService extends
		SalesManagerEntityServiceImpl<Long, MerchantLog>  {
	
	
	private MerchantLogRepository merchantLogRepository;
	
	
	public MerchantLogService(
			MerchantLogRepository merchantLogRepository) {
			super(merchantLogRepository);
			this.merchantLogRepository = merchantLogRepository;
	}


	




}
