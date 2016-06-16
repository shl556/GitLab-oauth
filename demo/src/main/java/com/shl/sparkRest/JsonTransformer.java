package com.shl.sparkRest;


import com.fasterxml.jackson.databind.ObjectMapper;

import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {
	@Override
	public String render(Object model) throws Exception {
		ObjectMapper mapper=new ObjectMapper();
		String json=mapper.writeValueAsString(model);
		return json;
	}
    
	
}
