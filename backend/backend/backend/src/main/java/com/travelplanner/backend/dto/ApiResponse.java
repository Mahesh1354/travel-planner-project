package com.travelplanner.backend.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Map<String, String> errors;
	public static ApiResponse success(String string, UserResponse userResponse) {
		// TODO Auto-generated method stub
		return null;
	}
	public static ApiResponse error(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	public static ApiResponse success(String string, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}
	public static ApiResponse success(String string, AuthResponse authResponse) {
		// TODO Auto-generated method stub
		return null;
	}
	public static Object success(String string, TripResponse tripResponse) {
		// TODO Auto-generated method stub
		return null;
	}
}
