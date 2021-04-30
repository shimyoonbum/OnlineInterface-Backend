package com.pulmuone.OnlineIFServer.util;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class CUtil {

	public static long getAsLong(Object object) {
		if (object instanceof BigInteger) {
			return ((BigInteger) object).longValue();
		} else if (object instanceof Long) {
			return (long) object;
		} else if (object instanceof Integer) {
			return (long) object;
		} else if (object instanceof String) {
			return Long.parseLong((String) object);
		}

		return -1;
	}

	public static String convertListOfObjectArrayToJsonString(List<Object[]> list) {
		List<String> listNew= new ArrayList<String>();
		for(Object[] oa : list)
			listNew.add(convertObjectArrayToJsonString(oa));
		return listNew.toString();
	}

	public static String convertObjectArrayToJsonString(Object[] oa) {
		StringBuffer sb = new StringBuffer();
		boolean needComma= false;
		sb.append("[");
		for(Object o : oa) {
			if(needComma)
				sb.append(",");
			sb.append(o == null? "null": o.toString());
			needComma= true;
		}
		sb.append("]");
		return sb.toString();
	}

	public static String convertListToJsonString(List<Map> list) {
		List<String> listNew= new ArrayList<String>();
		for(Map map : list)
			listNew.add(convertMapToJsonString(map));
		return listNew.toString();
	}

	public static String convertMapToJsonString(Map<String, Object> map) {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
        for (Object key : map.keySet()) {
        	Object value = map.get(key);
        	
        	if(sb.length() > 1)
        		sb.append(",");
        	
        	if(value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Float || value instanceof Boolean)
            	sb.append("\"" + key +"\":"+ value + "");
        	else
        		sb.append("\"" + key +"\":\""+ value + "\"");
        }
		sb.append("}");
		return sb.toString();
	}
	
	public static String nullString(String str,String def){
		if(str == null || str.length() == 0 || str == "null")return def;
		return str;
	}	

	public static <T> T castClass(Object classData, Class<T> classType){		
		return new Gson().fromJson(new Gson().toJson(classData), classType);
	}	
}
