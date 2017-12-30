package co.hooghly.commerce.startup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
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
import java.time.LocalDate;
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

	private final static Long MILLS_IN_DAY = 86400000L;

	public void load(MerchantStore store) {
		log.debug("Loading default CMS contents");
		for (Resource r : resources) {
			log.info("Loading from ZIP - {}", r.getFilename());
			log.info("Loading from ZIP exists - {}", r.exists());
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

					System.out.format("Directory : %s File: %s Size: %d Last Modified %s %n", ze.isDirectory(), ze.getName(), ze.getSize(),
							LocalDate.ofEpochDay(ze.getTime() / MILLS_IN_DAY));
				}
			}
		} catch (Exception e) {
			log.error("Error loading CMS content ", e);
		}
	}

	private void unzipAndLoad(Resource r, MerchantStore store) {
		// Open the file
		try (ZipFile file = new ZipFile(r.getFile())) {
			String theme = r.getFilename().replace(".zip", "");

			// Get file entries
			Enumeration<? extends ZipEntry> entries = file.entries();

			// Iterate over entries
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				log.info("Entry - {}", entry.getName());
				if (!entry.isDirectory() && !StringUtils.containsAny(entry.getName(), "less", "scss")) {
					InputStream is = file.getInputStream(entry);
					log.debug("Code - {}", entry.getName());
					File f = new File(entry.getName());
					CmsContent content = new CmsContent();

					content.setCode(f.getName());
					content.setContent(IOUtils.toByteArray(is));

					content.setMerchantStore(store);
					content.setOriginalFileName(entry.getName());
					content.setFileSize(entry.getSize());
					content.setLastModified(entry.getTime());
					content.setFolder(detectFolder(entry.getName()));
					content.setFileType(detectFileType(f));
					content.setTheme(theme);
					save(content);
				}

			}
		} catch (IOException e) {
			log.error("Error loading CMS content ", e);
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
