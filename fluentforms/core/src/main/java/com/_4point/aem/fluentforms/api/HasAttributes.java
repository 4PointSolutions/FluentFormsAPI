package com._4point.aem.fluentforms.api;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BiFunction;

/**
 * This interface contains a series of "helper" functions that pertain to attributes.  These functions improve on type safety
 * and reduce boilerplate in client code.
 */
public interface HasAttributes {
	
	public static final String ATTRIBUTE_PAGE_COUNT = "com._4point.aem.fluentforms.api.PAGE_COUNT";

	Object getAttribute(String name);

	HasAttributes setAttribute(String name, Object val);

	// TODO: Should make this private but that requires Java 11
	static final BiFunction<String, String, ? extends NoSuchElementException> exceptionSupplier = (name, type)->new NoSuchElementException("No such attribute found (" + name + ") of type '" + type + "'.");

	default Optional<Boolean> getOptionalAttributeAsBoolean(String name){
		Object attributeValue = this.getAttribute(name);
		return attributeValue instanceof Boolean ? Optional.of((Boolean) attributeValue) : Optional.empty();
	}

	default Boolean getMandatoryAttributeAsBoolean(String name) {
		return getOptionalAttributeAsBoolean(name).orElseThrow(()->HasAttributes.exceptionSupplier.apply(name, "Boolean"));
	}

	default HasAttributes  setAttributeAsBoolean(String name, Boolean val) {
		this.setAttribute(name, val);
		return this;
	}


	default Optional<Byte> getOptionalAttributeAsByte(String name){
		Object attributeValue = this.getAttribute(name);
		return attributeValue instanceof Byte ? Optional.of((Byte) attributeValue) : Optional.empty();
	}

	default Byte getMandatoryAttributeAsByte(String name) {
		return getOptionalAttributeAsByte(name).orElseThrow(()->HasAttributes.exceptionSupplier.apply(name, "Byte"));
	}

	default HasAttributes  setAttributeAsByte(String name, Byte val) {
		this.setAttribute(name, val);
		return this;
	}


	default Optional<Character> getOptionalAttributeAsCharacter(String name){
		Object attributeValue = this.getAttribute(name);
		return attributeValue instanceof Character ? Optional.of((Character) attributeValue) : Optional.empty();
	}

	default Character getMandatoryAttributeAsCharacter(String name) {
		return getOptionalAttributeAsCharacter(name).orElseThrow(()->HasAttributes.exceptionSupplier.apply(name, "Character"));
	}

	default HasAttributes setAttributeAsCharacter(String name, Character val) {
		this.setAttribute(name, val);
		return this;
	}

	default Optional<Float> getOptionalAttributeAsFloat(String name) {
		Object attributeValue = this.getAttribute(name);
		return attributeValue instanceof Float ? Optional.of((Float) attributeValue) : Optional.empty();
	}

	default Float getMandatoryAttributeAsFloat(String name) {
		return getOptionalAttributeAsFloat(name).orElseThrow(()->HasAttributes.exceptionSupplier.apply(name, "Float"));
	}
	
	default HasAttributes setAttributeAsFloat(String name, Float val) {
		this.setAttribute(name, val);
		return this;
	}

	default Optional<Integer> getOptionalAttributeAsInteger(String name) {
		Object attributeValue = this.getAttribute(name);
		return attributeValue instanceof Integer ? Optional.of((Integer) attributeValue) : Optional.empty();
	}

	default Integer getMandatoryAttributeAsInteger(String name) {
		return getOptionalAttributeAsInteger(name).orElseThrow(()->HasAttributes.exceptionSupplier.apply(name, "Integer"));
	}
	
	default HasAttributes setAttributeAsInteger(String name, Integer val) {
		this.setAttribute(name, val);
		return this;
	}

	default Optional<Long> getOptionalAttributeAsLong(String name) {
		Object attributeValue = this.getAttribute(name);
		return attributeValue instanceof Long ? Optional.of((Long) attributeValue) : Optional.empty();
	}

	default Long getMandatoryAttributeAsLong(String name) {
		return getOptionalAttributeAsLong(name).orElseThrow(()->HasAttributes.exceptionSupplier.apply(name, "Long"));
	}
	
	default HasAttributes setAttributeAsLong(String name, Long val) {
		this.setAttribute(name, val);
		return this;
	}

	default Optional<Short> getOptionalAttributeAsShort(String name) {
		Object attributeValue = this.getAttribute(name);
		return attributeValue instanceof Short ? Optional.of((Short) attributeValue) : Optional.empty();
	}

	default Short getMandatoryAttributeAsShort(String name) {
		return getOptionalAttributeAsShort(name).orElseThrow(()->HasAttributes.exceptionSupplier.apply(name, "Short"));
	}

	default HasAttributes setAttributeAsShort(String name, Short val) {
		this.setAttribute(name, val);
		return this;
	}

	default Optional<String> getOptionalAttributeAsString(String name) {
		Object attributeValue = this.getAttribute(name);
		return attributeValue instanceof String ? Optional.of((String) attributeValue) : Optional.empty();
	}

	default String getMandatoryAttributeAsString(String name) {
		return getOptionalAttributeAsString(name).orElseThrow(()->HasAttributes.exceptionSupplier.apply(name, "String"));
	}

	default HasAttributes setAttributeAsString(String name, String val) {
		this.setAttribute(name, val);
		return this;
	}
}
