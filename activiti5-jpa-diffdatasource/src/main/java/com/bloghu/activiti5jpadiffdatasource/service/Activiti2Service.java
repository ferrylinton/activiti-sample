package com.bloghu.activiti5jpadiffdatasource.service;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bloghu.activiti5jpadiffdatasource.tesseract.domain.City;
import com.bloghu.activiti5jpadiffdatasource.tesseract.service.CityService;

@Service("activiti2Service")
public class Activiti2Service {

	@Autowired
	private RuntimeService runtimeSerice;
	
	@Autowired
	private CityService cityService;
	
	public void startActiviti2(DelegateExecution execution) {
	    System.out.println("Start Activiti 2 ...");
	    ExecutionEntity executionEntity = (ExecutionEntity) execution;
	    executionEntity.setVariable("val", true);
	    System.out.println("executionEntity.getSuperExecutionId() : " + executionEntity.getSuperExecutionId());
	    System.out.println("service_order_id : " + runtimeSerice.getVariable(executionEntity.getSuperExecutionId(), "service_order_id"));
	    System.out.println("xxx : " + runtimeSerice.getVariable(executionEntity.getSuperExecutionId(), "xxx"));
	
	    City city = cityService.getCity("Bath", "UK");
	    
	    System.out.println("###### city : " + city.getName());
	}
	
	public void getConfirmation(DelegateExecution execution) {
	    System.out.println("Get confirmation ...");
	    ExecutionEntity executionEntity = (ExecutionEntity) execution;
	    System.out.println("cofirmation : " + executionEntity.getVariable("confirmation"));
	}
	
}
