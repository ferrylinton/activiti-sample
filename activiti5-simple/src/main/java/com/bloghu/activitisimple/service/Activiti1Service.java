package com.bloghu.activitisimple.service;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("activiti1Service")
public class Activiti1Service {
	
	@Autowired
	private RuntimeService runtimeSerice;

	public void startActiviti1(DelegateExecution execution) {
	    System.out.println("Start Activiti 1 ...");
	    ExecutionEntity executionEntity = (ExecutionEntity) execution;
	    System.out.println("executionEntity.getSuperExecutionId() : " + executionEntity.getSuperExecutionId());
	    runtimeSerice.setVariable(executionEntity.getSuperExecutionId(), "service_order_id", "service_order_001xxxxxx");
	}

}
