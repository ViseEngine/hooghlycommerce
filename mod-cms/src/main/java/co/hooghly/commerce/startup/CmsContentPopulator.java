package co.hooghly.commerce.startup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
					saveZipContent(ze,zis,store);
				}
			}
		} catch (Exception e) {
			log.error("Error loading CMS content ", e);
		}
	}

	private void saveZipContent(ZipEntry entry,ZipInputStream zis, MerchantStore store) {
		try{
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
			//content.setFileType(detectFileType(f));
			content.setTheme("zap");
			
			log.info("Saving cms content");
			save(content);
		}
		}
		catch(Exception e){
			log.info("Error reading");
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

	/**
	 * Identify file type of file with provided path and name using JDK 7's
	 * Files.probeContentType(Path).
	 *
	 * @param fileName
	 *            Name of file whose type is desired.
	 * @return String representing identified type of file with provided name.
	 */
	private String detectFileType(final File file) {
		String fileType = "Undetermined";

		try {
			fileType = Files.probeContentType(file.toPath());
		} catch (IOException ioException) {
			log.warn("ERROR: Unable to determine file type for " + file.getName() + " due to exception " + ioException);
		}
		return fileType;
	}

	private void save(CmsContent content) {
		try {
			cmsStaticContentRepository.save(content);
		} catch (Exception e) {
			log.error("Error", e);
			log.warn("Failed to load - {}, error : {}", content.getOriginalFileName(), e.getMessage());
		}
	}

	private void populateMessageResources() {
		log.info("=== Loading message resource ====");
		for (Resource r : propResources) {
			log.info("Properties files - {}", r.getFilename());
			int indexStart = StringUtils.indexOf(r.getFilename(), "_") + 1;
			int endIndex = StringUtils.indexOf(r.getFilename(), ".");
			String locale = StringUtils.substring(r.getFilename(), indexStart, endIndex);
			log.info("Locale - {}", locale);
			Properties prop = new Properties();
			try {
				prop.load(new InputStreamReader(r.getInputStream(), Charset.forName("UTF-8")));

				List<MessageResource> messageResourceList = new ArrayList<>();
				Enumeration<?> e = prop.propertyNames();
				while (e.hasMoreElements()) {
					String key = (String) e.nextElement();
					String value = prop.getProperty(key);
					log.info("Key : " + key + ", Value : " + value);

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
				log.error("Error", e);
			}
		}

	}

}
