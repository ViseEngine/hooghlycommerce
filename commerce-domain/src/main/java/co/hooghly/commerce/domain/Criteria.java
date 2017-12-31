package co.hooghly.commerce.domain;

import lombok.Data;

@Data
public class Criteria {
	
	private int startIndex = 0;
	private int maxCount = 0;
	private String code;
	
	
	private CriteriaOrderBy orderBy = CriteriaOrderBy.DESC;
	
}