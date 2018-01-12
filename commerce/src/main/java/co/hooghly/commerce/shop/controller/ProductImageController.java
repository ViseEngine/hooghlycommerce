package co.hooghly.commerce.shop.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import co.hooghly.commerce.business.ProductImageService;
import co.hooghly.commerce.domain.ProductImage;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/shop/product/image")
@ConditionalOnProperty(prefix="shop.controller.ProductImageController", name="enabled")
public class ProductImageController {

	@Autowired
	private ProductImageService productImageService;

	
}
