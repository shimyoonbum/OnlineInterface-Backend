package com.pulmuone.OnlineIFServer.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.type.Alias;

/*import org.apache.commons.collections4.map.ListOrderedMap;*/

@Alias("cmap")
public class CamelListMap extends HashMap {
	public static CamelListMap toCamelListMap(Map<String,Object> map) {
		CamelListMap cmap = new CamelListMap();
		for(String key: map.keySet())
			cmap.put(key, map.get(key));
		return cmap;
	}
	
	private String toProperCase(String s, boolean isCapital) {

		String rtnValue = "";

		if (isCapital) {
			rtnValue = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
		} else {
			rtnValue = s.toLowerCase();
		}
		return rtnValue;
	}

	private String toCamelCase(String s) {
		String[] parts = s.split("_");
		StringBuilder camelCaseString = new StringBuilder();

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			camelCaseString.append(toProperCase(part, (i != 0 ? true : false)));
		}

		return camelCaseString.toString();
	}

	public static String toCamelCaseString(String s) {
		String[] parts = s.split("_");
		StringBuilder camelCaseString = new StringBuilder();

		for (int i = 0; i < parts.length; i++) {
			String part = parts[i];
			camelCaseString.append(toProperCaseString(part, (i != 0 ? true : false)));
		}

		return camelCaseString.toString();
	}

	private static String toProperCaseString(String s, boolean isCapital) {

		String rtnValue = "";

		if (isCapital) {
			rtnValue = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
		} else {
			rtnValue = s.toLowerCase();
		}
		return rtnValue;
	}

	@Override
	public Object put(Object key, Object value) {
		return super.put(toCamelCase((String) key), value);
		//return super.put(toCamelCase((String) key), value.toString());

	}

}
