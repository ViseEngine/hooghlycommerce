package co.hooghly.commerce.shop.controller;


import java.util.Map;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/order")
@Slf4j
@ConditionalOnProperty(prefix="shop.controller.OrderManagementController", name="enabled")
public class OrderManagementController {
	
	@PostMapping("")
	public String submitOrder(@RequestBody Map order){
		log.info("Received order details - " + order);
		
		
		
		return UUID.randomUUID().toString();
	}
}
