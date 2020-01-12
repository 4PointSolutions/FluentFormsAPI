package com._4point.aem.fluentforms.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class HasAttributesTest {

	private static final String testAttributeName = "testAttribute";
	

	@Nested
	class AsBooleanTesting {
		final Boolean testValue = Boolean.TRUE;
		
		HasAttributes booleanStub = new HasAttributes() {
			Boolean curValue = testValue;
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				assertTrue(val instanceof Boolean);
				curValue = (Boolean)val;
			}
			
		};

		HasAttributes nonBooleanStub = new HasAttributes() {
			String curValue = "attributeValue";
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				throw new IllegalStateException("This method should never be called.");
			}
			
		};

		@Test
		void testGetOptionalAttribute() {
			Optional<Boolean> returnedValue = booleanStub.getOptionalAttributeAsBoolean(testAttributeName);
			assertTrue(returnedValue.isPresent());
			assertEquals(testValue, returnedValue.get());
		}
	
		@Test
		void testGetMandatoryAttribute() {
			assertEquals(testValue, booleanStub.getMandatoryAttributeAsBoolean(testAttributeName));
		}
		
		@Test 
		void testSetAttribute() {
			final Boolean otherTestValue = Boolean.FALSE;
			booleanStub.setAttributeAsBoolean(testAttributeName, otherTestValue);
			assertEquals(otherTestValue, booleanStub.getMandatoryAttributeAsBoolean(testAttributeName));
		}

		@Test
		void testGetOptionalAttributeInvalid() {
			Optional<Boolean> returnedValue = nonBooleanStub.getOptionalAttributeAsBoolean(testAttributeName);
			assertFalse(returnedValue.isPresent());
		}
	
		@Test
		void testGetMandatoryAttributeInvalid() {
			NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->nonBooleanStub.getMandatoryAttributeAsBoolean(testAttributeName));
			assertTrue(ex.getMessage().contains(testAttributeName));
		}
	}
	
	@Nested
	class AsByteTesting {
		final Byte testValue = Byte.valueOf((byte) 23);
		
		HasAttributes byteStub = new HasAttributes() {
			Byte curValue = testValue;
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				assertTrue(val instanceof Byte);
				curValue = (Byte)val;
			}
			
		};

		HasAttributes nonByteStub = new HasAttributes() {
			String curValue = "attributeValue";
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				throw new IllegalStateException("This method should never be called.");
			}
			
		};

		@Test
		void testGetOptionalAttribute() {
			Optional<Byte> returnedValue = byteStub.getOptionalAttributeAsByte(testAttributeName);
			assertTrue(returnedValue.isPresent());
			assertEquals(testValue, returnedValue.get());
		}
	
		@Test
		void testGetMandatoryAttribute() {
			assertEquals(testValue, byteStub.getMandatoryAttributeAsByte(testAttributeName));
		}
		
		@Test 
		void testSetAttribute() {
			final Byte otherTestValue = Byte.valueOf((byte) 15);
			byteStub.setAttributeAsByte(testAttributeName, otherTestValue);
			assertEquals(otherTestValue, byteStub.getMandatoryAttributeAsByte(testAttributeName));
		}

		@Test
		void testGetOptionalAttributeInvalid() {
			Optional<Byte> returnedValue = nonByteStub.getOptionalAttributeAsByte(testAttributeName);
			assertFalse(returnedValue.isPresent());
		}
	
		@Test
		void testGetMandatoryAttributeInvalid() {
			NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->nonByteStub.getMandatoryAttributeAsByte(testAttributeName));
			assertTrue(ex.getMessage().contains(testAttributeName));
		}
	}
	
	@Nested
	class AsCharacterTesting {
		final Character testValue = Character.valueOf((char)23);
		
		HasAttributes characterStub = new HasAttributes() {
			Character curValue = testValue;
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				assertTrue(val instanceof Character);
				curValue = (Character)val;
			}
			
		};

		HasAttributes nonCharacterStub = new HasAttributes() {
			String curValue = "attributeValue";
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				throw new IllegalStateException("This method should never be called.");
			}
			
		};

		@Test
		void testGetOptionalAttribute() {
			Optional<Character> returnedValue = characterStub.getOptionalAttributeAsCharacter(testAttributeName);
			assertTrue(returnedValue.isPresent());
			assertEquals(testValue, returnedValue.get());
		}
	
		@Test
		void testGetMandatoryAttribute() {
			assertEquals(testValue, characterStub.getMandatoryAttributeAsCharacter(testAttributeName));
		}
		
		@Test 
		void testSetAttribute() {
			final Character otherTestValue = Character.valueOf((char) 15);
			characterStub.setAttributeAsCharacter(testAttributeName, otherTestValue);
			assertEquals(otherTestValue, characterStub.getMandatoryAttributeAsCharacter(testAttributeName));
		}

		@Test
		void testGetOptionalAttributeInvalid() {
			Optional<Character> returnedValue = nonCharacterStub.getOptionalAttributeAsCharacter(testAttributeName);
			assertFalse(returnedValue.isPresent());
		}
	
		@Test
		void testGetMandatoryAttributeInvalid() {
			NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->nonCharacterStub.getMandatoryAttributeAsCharacter(testAttributeName));
			assertTrue(ex.getMessage().contains(testAttributeName));
		}
	}
	
	@Nested
	class AsFloatTesting {
		final Float testValue = Float.valueOf(23);
		
		HasAttributes floatStub = new HasAttributes() {
			Float curValue = testValue;
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				assertTrue(val instanceof Float);
				curValue = (Float)val;
			}
			
		};

		HasAttributes nonFloatStub = new HasAttributes() {
			String curValue = "attributeValue";
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				throw new IllegalStateException("This method should never be called.");
			}
			
		};

		@Test
		void testGetOptionalAttribute() {
			Optional<Float> returnedValue = floatStub.getOptionalAttributeAsFloat(testAttributeName);
			assertTrue(returnedValue.isPresent());
			assertEquals(testValue, returnedValue.get());
		}
	
		@Test
		void testGetMandatoryAttribute() {
			assertEquals(testValue, floatStub.getMandatoryAttributeAsFloat(testAttributeName));
		}
		
		@Test 
		void testSetAttribute() {
			final Float otherTestValue = Float.valueOf(15);
			floatStub.setAttributeAsFloat(testAttributeName, otherTestValue);
			assertEquals(otherTestValue, floatStub.getMandatoryAttributeAsFloat(testAttributeName));
		}

		@Test
		void testGetOptionalAttributeInvalid() {
			Optional<Float> returnedValue = nonFloatStub.getOptionalAttributeAsFloat(testAttributeName);
			assertFalse(returnedValue.isPresent());
		}
	
		@Test
		void testGetMandatoryAttributeInvalid() {
			NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->nonFloatStub.getMandatoryAttributeAsFloat(testAttributeName));
			assertTrue(ex.getMessage().contains(testAttributeName));
		}
	}
	
	@Nested
	class AsIntegerTesting {
		final Integer testValue = Integer.valueOf(23);
		
		HasAttributes integerStub = new HasAttributes() {
			Integer curValue = testValue;
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				assertTrue(val instanceof Integer);
				curValue = (Integer)val;
			}
			
		};

		HasAttributes nonIntegerStub = new HasAttributes() {
			String curValue = "attributeValue";
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				throw new IllegalStateException("This method should never be called.");
			}
			
		};

		@Test
		void testGetOptionalAttribute() {
			Optional<Integer> returnedValue = integerStub.getOptionalAttributeAsInteger(testAttributeName);
			assertTrue(returnedValue.isPresent());
			assertEquals(testValue, returnedValue.get());
		}
	
		@Test
		void testGetMandatoryAttribute() {
			assertEquals(testValue, integerStub.getMandatoryAttributeAsInteger(testAttributeName));
		}
		
		@Test 
		void testSetAttribute() {
			final Integer otherTestValue = Integer.valueOf(15);
			integerStub.setAttributeAsInteger(testAttributeName, otherTestValue);
			assertEquals(otherTestValue, integerStub.getMandatoryAttributeAsInteger(testAttributeName));
		}

		@Test
		void testGetOptionalAttributeInvalid() {
			Optional<Boolean> returnedValue = nonIntegerStub.getOptionalAttributeAsBoolean(testAttributeName);
			assertFalse(returnedValue.isPresent());
		}
	
		@Test
		void testGetMandatoryAttributeInvalid() {
			NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->nonIntegerStub.getMandatoryAttributeAsInteger(testAttributeName));
			assertTrue(ex.getMessage().contains(testAttributeName));
		}
	}
	
	@Nested
	class AsLongTesting {
		final Long testValue = Long.valueOf(23L);
		
		HasAttributes longStub = new HasAttributes() {
			Long curValue = testValue;
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				assertTrue(val instanceof Long);
				curValue = (Long)val;
			}
			
		};

		HasAttributes nonLongStub = new HasAttributes() {
			String curValue = "attributeValue";
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				throw new IllegalStateException("This method should never be called.");
			}
			
		};

		@Test
		void testGetOptionalAttribute() {
			Optional<Long> returnedValue = longStub.getOptionalAttributeAsLong(testAttributeName);
			assertTrue(returnedValue.isPresent());
			assertEquals(testValue, returnedValue.get());
		}
	
		@Test
		void testGetMandatoryAttribute() {
			assertEquals(testValue, longStub.getMandatoryAttributeAsLong(testAttributeName));
		}
		
		@Test 
		void testSetAttribute() {
			final Long otherTestValue = Long.valueOf(15L);
			longStub.setAttributeAsLong(testAttributeName, otherTestValue);
			assertEquals(otherTestValue, longStub.getMandatoryAttributeAsLong(testAttributeName));
		}

		@Test
		void testGetOptionalAttributeInvalid() {
			Optional<Long> returnedValue = nonLongStub.getOptionalAttributeAsLong(testAttributeName);
			assertFalse(returnedValue.isPresent());
		}
	
		@Test
		void testGetMandatoryAttributeInvalid() {
			NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->nonLongStub.getMandatoryAttributeAsLong(testAttributeName));
			assertTrue(ex.getMessage().contains(testAttributeName));
		}
	}
	
	@Nested
	class AsShortTesting {
		final Short testValue = Short.valueOf((short) 23);
		
		HasAttributes shortStub = new HasAttributes() {
			Short curValue = testValue;
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				assertTrue(val instanceof Short);
				curValue = (Short)val;
			}
			
		};

		HasAttributes nonShortStub = new HasAttributes() {
			String curValue = "attributeValue";
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				throw new IllegalStateException("This method should never be called.");
			}
			
		};

		@Test
		void testGetOptionalAttribute() {
			Optional<Short> returnedValue = shortStub.getOptionalAttributeAsShort(testAttributeName);
			assertTrue(returnedValue.isPresent());
			assertEquals(testValue, returnedValue.get());
		}
	
		@Test
		void testGetMandatoryAttribute() {
			assertEquals(testValue, shortStub.getMandatoryAttributeAsShort(testAttributeName));
		}
		
		@Test 
		void testSetAttribute() {
			final Short otherTestValue = Short.valueOf((short) 15);
			shortStub.setAttributeAsShort(testAttributeName, otherTestValue);
			assertEquals(otherTestValue, shortStub.getMandatoryAttributeAsShort(testAttributeName));
		}

		@Test
		void testGetOptionalAttributeInvalid() {
			Optional<Short> returnedValue = nonShortStub.getOptionalAttributeAsShort(testAttributeName);
			assertFalse(returnedValue.isPresent());
		}
	
		@Test
		void testGetMandatoryAttributeInvalid() {
			NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->nonShortStub.getMandatoryAttributeAsShort(testAttributeName));
			assertTrue(ex.getMessage().contains(testAttributeName));
		}
	}
	
	@Nested
	class AsStringTesting {
		final String testValue = "testString";
		
		HasAttributes stringStub = new HasAttributes() {
			String curValue = testValue;
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				assertTrue(val instanceof String);
				curValue = (String)val;
			}
			
		};

		HasAttributes nonStringStub = new HasAttributes() {
			Boolean curValue = Boolean.TRUE;
			
			@Override
			public Object getAttribute(String name) {
				return curValue;
			}

			@Override
			public void setAttribute(String name, Object val) {
				throw new IllegalStateException("This method should never be called.");
			}
			
		};

		@Test
		void testGetOptionalAttribute() {
			Optional<String> returnedValue = stringStub.getOptionalAttributeAsString(testAttributeName);
			assertTrue(returnedValue.isPresent());
			assertEquals(testValue, returnedValue.get());
		}
	
		@Test
		void testGetMandatoryAttribute() {
			assertEquals(testValue, stringStub.getMandatoryAttributeAsString(testAttributeName));
		}
		
		@Test 
		void testSetAttribute() {
			final String otherTestValue = "otherTestString";
			stringStub.setAttributeAsString(testAttributeName, otherTestValue);
			assertEquals(otherTestValue, stringStub.getMandatoryAttributeAsString(testAttributeName));
		}

		@Test
		void testGetOptionalAttributeInvalid() {
			Optional<String> returnedValue = nonStringStub.getOptionalAttributeAsString(testAttributeName);
			assertFalse(returnedValue.isPresent());
		}
	
		@Test
		void testGetMandatoryAttributeInvalid() {
			NoSuchElementException ex = assertThrows(NoSuchElementException.class, ()->nonStringStub.getMandatoryAttributeAsString(testAttributeName));
			assertTrue(ex.getMessage().contains(testAttributeName));
		}
	}
	
	@Nested
	class NullTesting {
		HasAttributes nullStub = new HasAttributes() {
			
			@Override
			public void setAttribute(String name, Object val) {
			}
			
			@Override
			public Object getAttribute(String name) {
				return null;
			}
		};
		@Test
		void testGetOptionalAttributes() {
			assertFalse(nullStub.getOptionalAttributeAsBoolean(testAttributeName).isPresent());
			assertFalse(nullStub.getOptionalAttributeAsByte(testAttributeName).isPresent());
			assertFalse(nullStub.getOptionalAttributeAsCharacter(testAttributeName).isPresent());
			assertFalse(nullStub.getOptionalAttributeAsFloat(testAttributeName).isPresent());
			assertFalse(nullStub.getOptionalAttributeAsInteger(testAttributeName).isPresent());
			assertFalse(nullStub.getOptionalAttributeAsLong(testAttributeName).isPresent());
			assertFalse(nullStub.getOptionalAttributeAsShort(testAttributeName).isPresent());
			assertFalse(nullStub.getOptionalAttributeAsString(testAttributeName).isPresent());
		}

		@Test
		void testGetMandatoryAttributes() {
			NoSuchElementException exBoolean = assertThrows(NoSuchElementException.class, ()->nullStub.getMandatoryAttributeAsBoolean(testAttributeName));
			assertTrue(exBoolean.getMessage().contains(testAttributeName));
			NoSuchElementException exByte = assertThrows(NoSuchElementException.class, ()->nullStub.getMandatoryAttributeAsByte(testAttributeName));
			assertTrue(exByte.getMessage().contains(testAttributeName));
			NoSuchElementException exCharacter = assertThrows(NoSuchElementException.class, ()->nullStub.getMandatoryAttributeAsCharacter(testAttributeName));
			assertTrue(exCharacter.getMessage().contains(testAttributeName));
			NoSuchElementException exFloat = assertThrows(NoSuchElementException.class, ()->nullStub.getMandatoryAttributeAsFloat(testAttributeName));
			assertTrue(exFloat.getMessage().contains(testAttributeName));
			NoSuchElementException exInteger = assertThrows(NoSuchElementException.class, ()->nullStub.getMandatoryAttributeAsInteger(testAttributeName));
			assertTrue(exInteger.getMessage().contains(testAttributeName));
			NoSuchElementException exLong = assertThrows(NoSuchElementException.class, ()->nullStub.getMandatoryAttributeAsLong(testAttributeName));
			assertTrue(exLong.getMessage().contains(testAttributeName));
			NoSuchElementException exShort = assertThrows(NoSuchElementException.class, ()->nullStub.getMandatoryAttributeAsShort(testAttributeName));
			assertTrue(exShort.getMessage().contains(testAttributeName));
			NoSuchElementException exString = assertThrows(NoSuchElementException.class, ()->nullStub.getMandatoryAttributeAsString(testAttributeName));
			assertTrue(exString.getMessage().contains(testAttributeName));
		}


	}
}
