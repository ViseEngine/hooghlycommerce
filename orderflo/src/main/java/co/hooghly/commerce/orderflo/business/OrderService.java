package co.hooghly.commerce.orderflo.business;

import org.camunda.bpm.engine.RuntimeService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OrderService {

	@Autowired
	private RuntimeService runtimeService;

	
	public void save() {
		log.info ("Running my test process");
		runtimeService.startProcessInstanceByKey("my-process");
	}
}
