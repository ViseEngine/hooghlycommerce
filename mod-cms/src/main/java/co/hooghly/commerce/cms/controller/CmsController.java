package co.hooghly.commerce.cms.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import co.hooghly.commerce.business.CmsContentService;
import co.hooghly.commerce.domain.CmsContent;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class CmsController {

	@Autowired
	private CmsContentService cmsContentService;
	
	@GetMapping("/cms/css/{merchantId}/{file:.+}")
	public ResponseEntity<?> getCss(@PathVariable Integer merchantId, @PathVariable String file,HttpServletResponse response) {
		log.debug("css request received - {}, {}  ", merchantId, file);

		// check file extension and redirect if required as css files can be
		// embeded font, image requests
		if (StringUtils.endsWithAny(file, "woff", "woff2")) {
			// redirect for font
			log.debug("font request from internal css, redirecting - {}", file);
			return findFont(merchantId,file);
			
		} else if (StringUtils.endsWithAny(file, "png", "jpg", "jpeg","gif")) {
			
			log.debug("font request from internal css, redirecting - {}", file);
			return findImage(merchantId , file);
		}

		return findCss(merchantId, file, response);
	}
	
	@GetMapping({ "/cms/css/images/{file:.+}" ,  "/cms/css/fonts/{file:.+}"})
	public ResponseEntity<byte[]> getCssExtras(@PathVariable String file,HttpServletResponse response) {
		log.debug("css exrtra request received - {}  ",  file);
		ResponseEntity<byte[]> responseEntity = null;
		Optional<CmsContent> content = cmsContentService.findByCode(file);
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		if (content.isPresent()) {
			byte[] media = content.get().getContent();

			responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
		}
		return responseEntity;

		
	}
	
	private ResponseEntity<String> findCss(Integer merchantId, String file,HttpServletResponse response) {
		response.setBufferSize(1024 * 1024); // 1MB buffer
		response.setContentType("text/stylesheet");

		ResponseEntity<String> responseEntity = null;
		Optional<CmsContent> content = cmsContentService.findByMerchantStoreIdAndAndCode(merchantId,file);
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		if (content.isPresent()) {
			String css = new String(content.get().getContent());

			responseEntity = new ResponseEntity<>(css, headers, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
		}
		return responseEntity;
	}
	
	
	public ResponseEntity<byte[]> findFont(Integer merchantId, String file) {

		
		ResponseEntity<byte[]> responseEntity = null;
		Optional<CmsContent> content = cmsContentService.findByMerchantStoreIdAndAndCode(merchantId,file);
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		if (content.isPresent()) {
			byte[] media = content.get().getContent();

			responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
		}
		return responseEntity;
	}

	@GetMapping(value = "/cms/js/{merchantId}/{file:.+}")
	public ResponseEntity<String> getJs(@PathVariable Integer merchantId, @PathVariable String file,
			HttpServletResponse response) {
		log.debug("js request received - {}, {}  ", merchantId, file);

		response.setBufferSize(2048 * 1024); // 1MB buffer
		response.setContentType("text/javascript");

		ResponseEntity<String> responseEntity = null;
		Optional<CmsContent> content = cmsContentService.findByMerchantStoreIdAndAndCode(merchantId,file);
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		if (content.isPresent()) {
			String js = new String(content.get().getContent());

			responseEntity = new ResponseEntity<>(js, headers, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
		}
		return responseEntity;

	}

	@GetMapping("/cms/images/{merchantId}/{file:.+}")
	public ResponseEntity<byte[]> getImage(@PathVariable Integer merchantId, @PathVariable String file) {
		return findImage(merchantId,file);
	}
	
	private ResponseEntity<byte[]> findImage(Integer merchantId,  String file) {

		log.debug("image request received - {}, {}  ", merchantId, file);
		ResponseEntity<byte[]> responseEntity = null;
		Optional<CmsContent> content = cmsContentService.findByMerchantStoreIdAndAndCode(merchantId,file);
		HttpHeaders headers = new HttpHeaders();
		headers.setCacheControl(CacheControl.noCache().getHeaderValue());
		if (content.isPresent()) {
			byte[] media = content.get().getContent();

			responseEntity = new ResponseEntity<>(media, headers, HttpStatus.OK);
		} else {
			responseEntity = new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
		}
		return responseEntity;
	}
}
