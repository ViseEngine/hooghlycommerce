package co.hooghly.commerce.startup;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.MessageResourceService;
import co.hooghly.commerce.domain.CmsContent;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.MessageResource;
import co.hooghly.commerce.repository.CmsContentRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CmsContentPopulator {

	@Autowired
	private CmsContentRepository cmsStaticContentRepository;
	@Autowired
	private MessageResourceService messageResourceService;

	@Value("classpath:*.zip")
	private Resource[] resources;

	@Value("classpath:messages/messages_*.properties")
	private Resource[] propResources;
	
	MimetypesFileTypeMap map = new MimetypesFileTypeMap();

	public void load(MerchantStore store) {
		log.debug("Loading default CMS contents");
		for (Resource r : resources) {
			log.debug("Loading from ZIP - {}", r.getFilename());

			if (r.exists())
				readZipContents(r, store);
		}

		populateMessageResources();
	}

	protected void readZipContents(Resource r, MerchantStore store) {
		try (BufferedInputStream bis = new BufferedInputStream(r.getInputStream())) {

			try (ZipInputStream zis = new ZipInputStream(bis)) {
				ZipEntry ze;

				while ((ze = zis.getNextEntry()) != null) {
					saveZipContent(ze, zis, store);
				}
			}
		} catch (Exception e) {
			log.warn("Error loading CMS content ", e.getCause().getMessage());
		}
	}

	private void saveZipContent(ZipEntry entry, ZipInputStream zis, MerchantStore store) {
		try {
			if (!entry.isDirectory() && !StringUtils.containsAny(entry.getName(), "less", "scss")) {

				CmsContent content = new CmsContent();

				int lastIndx = StringUtils.lastIndexOf(entry.getName(), "/");
				String code = StringUtils.substring(entry.getName(), lastIndx + 1);

				content.setCode(code);
				content.setContent(IOUtils.toByteArray(zis));

				content.setMerchantStore(store);
				content.setOriginalFileName(entry.getName());
				content.setFileSize(entry.getSize());
				content.setLastModified(entry.getTime());
				content.setFolder(detectFolder(entry.getName()));
				content.setFileType(detectFileType(entry));
				content.setTheme(store.getTheme());

				save(content);
			}
		} catch (Exception e) {
			log.warn("Error reading - " + e.getCause().getMessage());
		}
	}

	private String detectFolder(String fileName) {
		String folder = "Undetermined";
		if (StringUtils.contains(fileName, "js")) {
			folder = "js";
		} else if (StringUtils.contains(fileName, "css")) {
			folder = "css";
		} else if (StringUtils.contains(fileName, "fragment")) {
			folder = "fragments";
		} else if (StringUtils.contains(fileName, "layout")) {
			folder = "layouts";
		} else if (StringUtils.contains(fileName, "common")) {
			folder = "common";
		} else if (StringUtils.containsAny(fileName, "images", "img", "image", "gif", "jpg", "jpeg", "png")) {
			folder = "images";
		}

		return folder;
	}


	private String detectFileType(ZipEntry ze) {
		String mimeType = map.getContentType(ze.getName());
		log.debug(ze.getName()+" type: "+ mimeType);
		return mimeType;
	}

	private void save(CmsContent content) {
		try {
			cmsStaticContentRepository.save(content);
		} catch (Exception e) {
			
			log.warn("Failed to load - {}, error : {}", content.getOriginalFileName(), e.getMessage());
		}
	}

	private void populateMessageResources() {
		log.info("Loading message resources.");
		for (Resource r : propResources) {

			int indexStart = StringUtils.indexOf(r.getFilename(), "_") + 1;
			int endIndex = StringUtils.indexOf(r.getFilename(), ".");
			String locale = StringUtils.substring(r.getFilename(), indexStart, endIndex);

			Properties prop = new Properties();
			try {
				prop.load(new InputStreamReader(r.getInputStream(), Charset.forName("UTF-8")));

				List<MessageResource> messageResourceList = new ArrayList<>();
				Enumeration<?> e = prop.propertyNames();
				while (e.hasMoreElements()) {
					String key = (String) e.nextElement();
					String value = prop.getProperty(key);

					int idxStart = StringUtils.indexOf(key, ".") + 1;
					int idxEnd = StringUtils.lastIndexOf(key, ".");

					MessageResource mr = new MessageResource();
					mr.setDomain(StringUtils.capitalize(StringUtils.substring(key, idxStart, idxEnd)));
					mr.setLocale(locale);
					mr.setMessageKey(key);
					mr.setMessageText(value);

					messageResourceList.add(mr);
				}

				messageResourceService.save(messageResourceList);

			} catch (Exception e) {
				log.info("Error processing properties file - {}", r.getFilename());
				log.warn("Error - ", e.getCause().getMessage());
			}
		}

	}

}
