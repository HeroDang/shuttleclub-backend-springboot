package com.myproject.shuttleclub.common.error;

public final class ErrorCodes {
	  private ErrorCodes() {}

	  // Not found
	  public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
	  public static final String CLUB_NOT_FOUND = "CLUB_NOT_FOUND";
	  public static final String MEMBERSHIP_NOT_FOUND = "MEMBERSHIP_NOT_FOUND";

	  // Business rules
	  public static final String FORBIDDEN = "FORBIDDEN";
	  public static final String MEMBERSHIP_ALREADY_EXISTS = "MEMBERSHIP_ALREADY_EXISTS";
	  public static final String MEMBERSHIP_NOT_INVITED = "MEMBERSHIP_NOT_INVITED";
	  public static final String INVALID_MEMBERSHIP_STATE = "INVALID_MEMBERSHIP_STATE";
	}
