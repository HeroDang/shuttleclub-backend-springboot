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
	  
	  public static final String AUTH_REQUIRED = "AUTH_REQUIRED";
	  public static final String AUTH_INVALID = "AUTH_INVALID";
	  public static final String SESSION_NOT_FOUND = "SESSION_NOT_FOUND";
	  public static final String SESSION_START_TIME_INVALID = "SESSION_START_TIME_INVALID";
	  public static final String SESSION_CLOSED = "SESSION_CLOSED";
	  public static final String CLUB_MEMBERSHIP_REQUIRED = "CLUB_MEMBERSHIP_REQUIRED";
	  public static final String INVALID_TIME_RANGE = "INVALID_TIME_RANGE";
	  public static final String CLUB_ADMIN_REQUIRED = "CLUB_ADMIN_REQUIRED";
	}
