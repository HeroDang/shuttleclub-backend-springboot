package com.myproject.shuttleclub.common.error;

import com.myproject.shuttleclub.common.error.DomainException;

public class ConflictException extends DomainException {
  public ConflictException(String code, String message) {
    super(code, message);
  }
}
