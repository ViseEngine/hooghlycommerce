package co.hooghly.commerce.spring.extension;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import com.google.common.collect.Sets;

import co.hooghly.commerce.business.CmsContentService;
import co.hooghly.commerce.domain.CmsContent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CmsDbBackedHtmlTemplateResolver extends StringTemplateResolver {
	

	@Autowired
	private CmsContentService cmsContentService;

	public CmsDbBackedHtmlTemplateResolver() {
		setResolvablePatterns(Sets.newHashSet("**/*.cms"));
		setOrder(2);
		setTemplateMode(TemplateMode.HTML);
		setCacheable(true);
		setCacheTTLMs(DEFAULT_CACHE_TTL_MS);
	}

	@Override
	protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate,
			String template, Map<String, Object> templateResolutionAttributes) {
		log.info("Finding DB backed HTML template - {} - attr - {}", template, templateResolutionAttributes);
		String htmlTemplate = StringUtils.replace(template, ".cms", ".html");
		String htmlTemplateParts [] = htmlTemplate.split("/");
		
		
		Optional<CmsContent> content = cmsContentService.findByMerchantStoreIdAndAndCode(Integer.parseInt(htmlTemplateParts[0]) , htmlTemplateParts[1]);
		ITemplateResource resource = null;
		if (content.isPresent()) {
			log.info("Resource found");
			try{
			resource = super.computeTemplateResource(configuration, ownerTemplate,content.get().getTemplateContent(),
					templateResolutionAttributes);
			}
			catch(Exception e) {
				log.warn("Error resolving CMS resource - {}", e.getMessage());
			}
		}
		
		log.info("Final resource state - {}", resource);
		return resource;

	}

	
	
	
}
