package co.hooghly.commerce.domain.admin;

import java.util.List;

import co.hooghly.commerce.domain.MerchantConfiguration;



public class ConfigListWrapper
{
	private List<MerchantConfiguration> merchantConfigs;

	public List<MerchantConfiguration> getMerchantConfigs()
	{
		return merchantConfigs;
	}

	public void setMerchantConfigs(List<MerchantConfiguration> merchantConfigs)
	{
		this.merchantConfigs = merchantConfigs;
	}

}
