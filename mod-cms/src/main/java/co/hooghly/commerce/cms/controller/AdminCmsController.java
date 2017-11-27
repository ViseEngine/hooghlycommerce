package co.hooghly.commerce.cms.controller;

import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import co.hooghly.commerce.business.CmsContentService;
import co.hooghly.commerce.domain.CmsContent;


import co.hooghly.commerce.domain.MerchantStore;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin/secure/cms")
@Slf4j
public class AdminCmsController {
	
	@Autowired
	private CmsContentService cmsContentService;
	
	//@Autowired
	//private MerchantStoreService merchantStoreService;
	
	@GetMapping("/page/{folder}")
	public String listPages(@PathVariable("folder") String folder, Model model) {
		log.info("Request received to load - folder - {}", folder);
		
		model.addAttribute("pages", cmsContentService.findByFolder(folder));
		
		
		return "admin/cmspages";
	}
	
	@GetMapping("/page/{pageType}/new")
	public String newLayoutPage(@PathVariable("folder") String folder, Model model) {
		log.info("Request received to load -folder - {}", folder);
		
		model.addAttribute("pages", cmsContentService.findByFolder(folder));
		
		
		return "admin/cmsnew";
	}
	
	@PostMapping(value = "/page/{folder}/save")
	public String handleFileUpload(MultipartHttpServletRequest request, @PathVariable("folder") String folder,
			@RequestParam("file") MultipartFile file, @RequestParam Map<String, String> allRequestParams, MerchantStore merchantStore) {

		//For now just add it to merchant store default 
		//Later in the UI we can have a drop down
		
		CmsContent content = BeanUtils.instantiate(CmsContent.class);
		content.setFolder(folder);
		
		//MerchantStore merchantStore = merchantStoreService.getMerchantStore(MerchantStore.DEFAULT_STORE);
		content.setMerchantStore(merchantStore);
		log.info("All request params - {}", allRequestParams);

		BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(content);
		beanWrapper.setPropertyValues(new MutablePropertyValues(allRequestParams), true, true);

		if (!file.isEmpty()) {
			try {
				log.info("Received file - {}", file.getOriginalFilename());
				
				
				content.setOriginalFileName(file.getOriginalFilename());
				content.setTemplateContent(new String(file.getBytes()));
				content.setFileSize(file.getSize());
				content.setContentType(file.getContentType());
				
				//cmsPageService.save(page);

				
			} catch (Exception e) {
				log.error("Error in restore - {}", e);
				
			}
		} else {
			log.error("Empty file");
		}

		return "admin/cmsnew";
	}
}
