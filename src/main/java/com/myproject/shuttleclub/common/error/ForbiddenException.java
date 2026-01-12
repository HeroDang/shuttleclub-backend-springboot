package com.myproject.shuttleclub.common.error;

import com.myproject.shuttleclub.common.error.DomainException;
import com.myproject.shuttleclub.common.error.ErrorCodes;

public class ForbiddenException extends DomainException {
	  public ForbiddenException(String message) {
	    super(ErrorCodes.FORBIDDEN, message);
	  }
}
