package com.pulmuone.OnlineIFServer.util;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.ArrayUtils;

import com.pulmuone.OnlineIFServer.common.BaseException;

public class ArrayUtil {

	private final static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	public static byte[] subbyte(byte[] array, int start, int end) {
		if (array == null) {
			return null;
		}
		return ArrayUtils.subarray(array, start, end);
	}
	
	public static byte[] subbyte(String arrayStr, int start, int end, String charset) {
		byte[] subarray = null;
		if (arrayStr == null) {
			return subarray;
		}
		try {
			subarray = ArrayUtil.subbyte(arrayStr.getBytes(charset), start, end);
		} catch (UnsupportedEncodingException e) {
			throw new BaseException(e);
		}
		return subarray;
	}	
	
	public static byte[] add(byte[] array1, byte[] array2) {
		return ArrayUtils.addAll(array1, array2);
	}
	
	public static byte[] add(byte[] ... arrays) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if (arrays != null) {
			for (byte[] array : arrays) {
				if(array != null) {
					baos.write(array, 0, array.length);
				}
			}
		}
		return baos.toByteArray();
	}
	
	public static byte[] add(byte ... arrays) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		if(arrays != null) {
			for(byte array : arrays) {
				baos.write(array);
			}
		}
		return baos.toByteArray();
	}
	
	public static byte[] add(byte[] array, byte element) {
		return ArrayUtils.add(array, element);
	}
	
	public static byte[] add(byte element, byte[] array) {
		if(array == null) {
			return new byte[]{element};
		}
		byte[] newArray = new byte[1 + array.length];
		newArray[0] = element;
		System.arraycopy(array, 0, newArray, 1, array.length);
		return newArray;
	}
	
	public static byte[] lpadFix(byte[] array, int len, byte pad) {
		return ArrayUtil.padFix(array, len, pad, true);
	}
	
	public static byte[] rpadFix(byte[] array, int len, byte pad) {
		return ArrayUtil.padFix(array, len, pad, false);
	}
	
	public static byte[] padFix(byte[] array, int len, byte pad, boolean isLpad) {
		byte[] returnValue = null;
		try {
			if(array == null) {
				array = new byte[0];
			}
			int arrayLen = array.length;
			if(arrayLen == len) {
				returnValue = ArrayUtils.clone(array);
			} else if(arrayLen > len) {
				returnValue = ArrayUtil.subbyte(array, 0, len);
			} else {
				int subLen = len - arrayLen;
				byte[] addByte = new byte[subLen];
				for(int i = 0; i < subLen; i++) {
					addByte[i] = pad;
				}
				if(isLpad) {
					returnValue = ArrayUtil.add(addByte, array);
				} else {
					returnValue = ArrayUtil.add(array, addByte);
				}
			}
		} catch (Exception e) {
			throw new BaseException(e);
		}
		return returnValue;
	}
	
	public static String toHexString(byte b) {
		StringBuffer result = new StringBuffer(3);
		result.append(Integer.toString((b & 0xF0) >> 4, 16));
		result.append(Integer.toString(b & 0x0F, 16));
		return result.toString();
	}
	
	public static String toHexString(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		StringBuffer result = new StringBuffer();
		for (byte b : bytes) {
			result.append(Integer.toString((b & 0xF0) >> 4, 16));
			result.append(Integer.toString(b & 0x0F, 16));
		}
		return result.toString();
	}	
}

