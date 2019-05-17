package com._4point.aem.docservices.rest_services.it_tests.forms;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

public class ByteArrayString {
	private final byte[] data;
	private static CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();

	public ByteArrayString(byte[] target, int size) {
		super();
		int resultSize = size < target.length ? size : target.length;
		data = new byte[resultSize];
		for (int i = 0; i < data.length; i++) {
			data[i] = target[i];
		}
	}

	@Override
	public String toString() {
		String asciiString = "";
		for (byte b : data) {
			if (!asciiString.isEmpty()) {
				asciiString += ", "; 
			}
			if (asciiEncoder.canEncode((char)b)) {
				asciiString += Character.toString((char)b);
			} else {
				asciiString += toHex(b);
			}
		}
		return "ByteArrayString [data=" + Arrays.toString(data) + ", ascii=[" + asciiString + "]]";
	}
	
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	private static String toHex(byte b) {
		char[] hexChars = new char[2];
		int v = b & 0xFF;
		hexChars[0] = hexArray[v >>> 4];
		hexChars[1] = hexArray[v & 0x0F];
		return "0x" + new String(hexChars);
	}

	private static boolean isPureAscii(String v) {
		return asciiEncoder.canEncode(v);
		// or "ISO-8859-1" for ISO Latin 1
		// or StandardCharsets.US_ASCII with JDK1.7+
	}
	
	public static String toString(byte[] target, int size) {
		return new ByteArrayString(target, size).toString();
	}
}
