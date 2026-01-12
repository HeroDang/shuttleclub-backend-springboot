package com.myproject.shuttleclub.common.error;

import com.myproject.shuttleclub.common.error.DomainException;

public class NotFoundException extends DomainException {
	  public NotFoundException(String code, String message) {
	    super(code, message);
	  }
	}
