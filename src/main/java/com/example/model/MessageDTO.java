package com.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class MessageDTO {
	private String name;
	private String message;
	private String fileName;

}