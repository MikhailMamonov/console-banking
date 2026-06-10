package com.example.banking;

import org.springframework.stereotype.Component;

@Component
public class GreetingService {
	public String getGreeting() {
		return "Hello from Spring Core Context!";
	}
}