package com.pulmuone.OnlineIFServer.util;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;

public final class StringUtil {
	
	private static final String[] FILE_SIZE_UNIT = new String[] {"B", "kB", "MB", "GB", "TB"};
	
	public static String toString(Object value, String defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		return value.toString();
	}
	
	public static String toString(Object value) {
		return StringUtil.toString(value, null);
	}
	
	public static boolean isEmpty(String value) {
		return (value == null || value.length() == 0);
	}
	
	public static String nvl(String value, String defaultValue) {
		if (StringUtil.isNotEmpty(value)) {
			return value;
		}
		return defaultValue;
	}
	
	public static boolean isNotEmpty(String value) {
		return !StringUtil.isEmpty(value);
	}

	public static boolean isUndefined(String value) {
		return (value == null || value.length() == 0 || "undefined".equals(value) || "null".equals(value));
	}
	
	public static boolean isDefined(String value) {
		return !StringUtil.isUndefined(value);
	}
	
	public static String trim(String value) {
		if (value == null) {
			return null;
		}
		return value.trim();
	}
	
	public static String lpad(String value, int len, String pad) {
		if (value == null) {
			value = "";
		}
		return StringUtils.leftPad(value, len, pad);
	}
	
	public static String rpad(String value, int len, String pad) {
		if (value == null) {
			value = "";
		}
		return StringUtils.rightPad(value, len, pad);
	}
	
	public static String camelLower(String name) {
    	return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
    }
	
	public static String camelUpper(String name) {
    	return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }
	
	public static boolean equals(String str1, String str2) {
		return StringUtils.equals(str1, str2);
	}
	
	/**
	 * 메시지 Format
	 * 
	 * @param msg - "{0}는 {1}자리입니다."
	 * @param params - new Object[]{"ID", 8}
	 * @return
	 */
	public static String msgFormat(String msg, Object ... params) {
		String formatMsg = null;
		if (StringUtil.isNotEmpty(msg)) {
			if (params != null && params.length > 0) {
				formatMsg = MessageFormat.format(msg, params);
			} else {
				formatMsg = msg;
			}
		}
		return formatMsg;
	}	
	
	public static String formatAmount(String value, String pattern) {
		String format = null;
		if (StringUtil.isNotEmpty(value)) {
			Double number = Double.parseDouble(value);
			DecimalFormat formatter = new DecimalFormat(pattern);
			format = formatter.format(number);
		}
		return format;
	}
	
	public static String formatAmount(Object value, String pattern) {
		return StringUtil.formatAmount(StringUtil.toString(value), pattern);
	}
	
	public static String formatAmount(String value) {
		return StringUtil.formatAmount(value, "#,###");
	}
	
	public static String formatAmount(Object value) {
		return StringUtil.formatAmount(StringUtil.toString(value));
	}
	
	public static String formatFileSize(long fileSize) {
		if (fileSize <= 0) {
			return "0";
		}
		int digitGroups = (int) (Math.log10(fileSize) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(fileSize / Math.pow(1024, digitGroups)) + " " + StringUtil.FILE_SIZE_UNIT[digitGroups];
	}
		
	/**
	 * 첫번째 문자 소문자로 변환
	 * 
	 * @param str
	 * @return
	 */
	public static String decapitalize(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		return Character.toLowerCase(str.charAt(0)) + (str.length() > 1 ? str.substring(1) : "");
	}
	
	/**
	 * String 을 구분자로 구분하여 배열로 변환
	 * 
	 * @param str
	 * @param seperator
	 * @return
	 */
	public static List<String> splitList(String str, String seperator) {
		if (StringUtil.isEmpty(str)) {
			return null;
		}
		List<String> list = Lists.newArrayList();
		String[] arrStr = str.split(seperator);
		for (int i = 0, s = arrStr.length; i < s; i++) {
			list.add(arrStr[i]);
		}
		return list;
	}
	
	/**
	 * String 을 콤마(,)로 구분하여 배열로 변환
	 * 
	 * @param str
	 * @return
	 */
	public static List<String> splitList(String str) {
		return StringUtil.splitList(str, ",");
	}
	
	
	/**
	 * UUID
	 * 
	 * @return
	 */
	public static String getUuid() {
		return UUID.randomUUID().toString();
	}
	
}
