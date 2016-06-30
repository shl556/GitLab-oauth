package com.shl.sparkRest;


public class Error {
    private String errorMessage;
    
    private String errorCode;

    
    public Error(){
    	
    }
    
    public Error(String errorMessage,String errorCode){
    	this.errorCode=errorCode;
    	this.errorMessage=errorMessage;
    }
    
    
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
    
	@Override
	public String toString() {
		return "Error Code: "+errorCode+"  /Error Message: "+errorMessage;
	}
	
	
}
