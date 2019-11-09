package com.service.managevotingsessionsservice.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum DecisionType {
	
	YES("yes"), NO("no");
	
	public String decisionValue;
	
}
