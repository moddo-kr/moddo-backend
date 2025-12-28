package com.dnd.moddo.common.converter;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
	@Override
	public String convertToDatabaseColumn(List<String> attribute) {
		return attribute == null ? null : String.join(",", attribute);
	}

	@Override
	public List<String> convertToEntityAttribute(String dbData) {
		return dbData == null || dbData.isEmpty() ? null : Arrays.asList(dbData.split(","));
	}
}
