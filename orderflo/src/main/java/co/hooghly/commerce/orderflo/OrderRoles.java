package co.hooghly.commerce.orderflo;

import org.camunda.bpm.engine.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OrderRoles implements CommandLineRunner{
	
	@Autowired
	private RuntimeService runtimeService;

	public void run(String... args) {
		log.info ("Running Roles retrival process");
		System.out.println("...Rajiv Before Application started....");
		log.info( "...Rajiv Before Application started...." );
		runtimeService.startProcessInstanceByKey("my-process");
		}
	
}
