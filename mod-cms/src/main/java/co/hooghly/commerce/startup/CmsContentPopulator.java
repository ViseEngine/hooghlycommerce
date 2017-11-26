package co.hooghly.commerce.startup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.domain.CmsContent;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.repository.CmsContentRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CmsContentPopulator {

	@Autowired
	private CmsContentRepository cmsStaticContentRepository;

	@Value("classpath:*.zip")
	private Resource[] resources;

	public void load(MerchantStore store) {
		log.info("Loading default CMS contents");
		for (Resource r : resources) {
			unzipAndLoad(r, store);
		}
	}

	private void unzipAndLoad(Resource r, MerchantStore store) {
		// Open the file
		try (ZipFile file = new ZipFile(r.getFile())) {
			String theme = r.getFile().getName().replaceAll(".zip", "");
			log.info("file - {}", r.getFile().getName());
			// Get file entries
			Enumeration<? extends ZipEntry> entries = file.entries();

			// Iterate over entries
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				log.info("Entry - {}", entry.getName());
				if (!entry.isDirectory() && !StringUtils.containsAny(entry.getName(), "less", "scss")) {
					InputStream is = file.getInputStream(entry);
					log.info("Code - {}", entry.getName());
					File f = new File(entry.getName());
					CmsContent content = new CmsContent();

					content.setCode(f.getName());

					if (StringUtils.endsWithAny(f.getName(), "html",  "css", "js")) {
						content.setTemplateContent(new String(IOUtils.toByteArray(is)));
					} else {
						content.setContent(IOUtils.toByteArray(is));
					}

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
			log.warn("Error loading CMS content - " + e.getMessage());
			throw new RuntimeException(e);
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
			log.warn("Failed to load - {}, error : {}", content.getOriginalFileName(), e.getMessage());
		}
	}

}
