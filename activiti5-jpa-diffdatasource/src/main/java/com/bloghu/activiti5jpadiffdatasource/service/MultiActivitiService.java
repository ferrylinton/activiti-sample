package com.bloghu.activiti5jpadiffdatasource.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.springframework.stereotype.Service;

@Service("multiActivitiService")
public class MultiActivitiService {

	public void startMultiActiviti(DelegateExecution execution) {
	    System.out.println("Start Multi Activiti ...");
	    ExecutionEntity executionEntity = (ExecutionEntity) execution;
	    System.out.println("executionEntity.getId() : " + executionEntity.getId());
	}
	
	public void finishMultiActiviti(DelegateExecution execution) {
	    System.out.println("Finish Multi Activiti ...");
	    ExecutionEntity executionEntity = (ExecutionEntity) execution;
	    System.out.println("executionEntity.getId() : " + executionEntity.getId());
	}
	
}
