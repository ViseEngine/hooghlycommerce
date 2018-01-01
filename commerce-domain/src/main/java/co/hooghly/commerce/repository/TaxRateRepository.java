package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.TaxRate;

public interface TaxRateRepository extends JpaRepository<TaxRate, Long> {

	@Query("select t from TaxRate t join fetch t.taxClass join fetch t.merchantStore tm join fetch t.country left join fetch t.zone left join fetch t.descriptions left join t.parent where tm.id=?1 order by t.taxPriority asc")
	List<TaxRate> findByStore(Long id);
	
	@Query("select t from TaxRate t join fetch t.taxClass join fetch t.merchantStore tm join fetch t.country left join fetch t.zone left join fetch t.descriptions td left join t.parent where tm.id=?1 and td.language.id=?2 order by t.taxPriority asc")
	List<TaxRate> findByStoreAndLanguage(Long id, Integer languageId);
	
	@Query("select t from TaxRate t join fetch t.taxClass join fetch t.merchantStore tm join fetch t.country left join fetch t.zone left join fetch t.descriptions td left join t.parent where tm.id=?1 and t.code=?2")
	TaxRate findByStoreAndCode(Long id, String code);
	
	@Query("select t from TaxRate t join fetch t.taxClass join fetch t.merchantStore tm join fetch t.country left join fetch t.zone left join fetch t.descriptions td left join t.parent where t.id=?1")
	TaxRate findOne(Long id);
	
	@Query("select t from TaxRate t join fetch t.taxClass join fetch t.merchantStore tm join fetch t.country tc left join fetch t.zone tz left join fetch t.descriptions td left join t.parent where tm.id=?1 AND (tz.id=?2 OR tz IS NULL) and tc.id=?3 and td.language.id=?4 order by t.taxPriority asc")
	List<TaxRate> findByMerchantAndZoneAndCountryAndLanguage(Long id, Long zoneId, Long countryId, Integer languageId);
	
	@Query("select t from TaxRate t join fetch t.taxClass join fetch t.merchantStore tm join fetch t.country tc left join fetch t.zone tz left join fetch t.descriptions td left join t.parent where tm.id=?1 AND t.stateProvince=?2 and tc.id=?3 and td.language.id=?4 order by t.taxPriority asc")
	List<TaxRate> findByMerchantAndProvinceAndCountryAndLanguage(Long id, String province, Long countryId, Integer languageId);
	
	
}
