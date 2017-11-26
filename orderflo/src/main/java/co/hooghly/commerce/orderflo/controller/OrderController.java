package co.hooghly.commerce.orderflo.controller;

import co.hooghly.commerce.orderflo.business.OrderService;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/orders")
@Slf4j
class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@PostMapping("/{orderType}")
	public String submitOrder(@PathVariable String orderType, @RequestBody Map order){
		log.info( "Job submitted - type {} , {}" , orderType, order);
		orderService.save();
		
		return "OK";
		
	}
}
