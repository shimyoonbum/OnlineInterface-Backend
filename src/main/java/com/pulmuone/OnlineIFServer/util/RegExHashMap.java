package com.pulmuone.OnlineIFServer.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
/**
 * This class is an extended version of Java HashMap
 * and includes pattern-value lists which are used to
 * evaluate regular expression values. If given item
 * is a regular expression, it is saved in regexp lists.
 * If requested item matches with a regular expression,
 * its value is get from regexp lists.
 *
 * @author cb
 *
 * @param <K> : Key of the map item.
 * @param <V> : Value of the map item.
 */
public class RegExHashMap<K, V> extends HashMap<K, V> {
    // list of regular expression patterns
    private ArrayList<Pattern> regExPatterns = new ArrayList<Pattern>();
    // list of regular expression values which match patterns
    private ArrayList<V> regExValues = new ArrayList<V>();
    /**
     * Compile regular expression and add it to the regexp list as key.
     */
    @Override
    public V put(K key, V value) {
       
        regExPatterns.add(Pattern.compile(key.toString()));
        regExValues.add(value);
        return value;
    }
    /**
     * If requested value matches with a regular expression,
     * returns it from regexp lists.
     */
    @Override
    public V get(Object key) {
        CharSequence cs = new String(key.toString());
       
        for (int i = 0; i < regExPatterns.size(); i++) {
            if (regExPatterns.get(i).matcher(cs).matches()) {
               
                return regExValues.get(i);
            }
        }
        return super.get(key);
    }

    @Override
    public String toString() {
    	StringBuffer sb = new StringBuffer();
        
        for (int i = 0; i < regExPatterns.size(); i++) {
            sb.append(regExPatterns.get(i).toString() + ":" + regExValues.get(i).toString()+"\n");
        }
    	
    	return sb.toString();
    }
    
    /**
    public static void main(String[] args) {
    	RegExHashMap map = new RegExHashMap();
    	map.put("\\/user\\/?", "user1");
    	map.put("\\/user\\/[^\\/]*\\/?", "user2");
    	map.put("\\/user\\/[^\\/]*\\/[^\\/]*\\/?", "user3");
    	map.put("\\/user\\/[^\\/]*\\/id\\/[^\\/]*\\/?", "user4");

        System.out.println("map : " + map.toString());
        System.out.println("find /user : " + map.get("/user"));
        System.out.println("find /user/ : " + map.get("/user/"));
        System.out.println("find /user/1 : " + map.get("/user/1"));
        System.out.println("find /user/1/ : " + map.get("/user/1/"));
        System.out.println("find /user/1/2 : " + map.get("/user/1/2"));
        System.out.println("find /user/1/2/ : " + map.get("/user/1/2/"));
        System.out.println("find /user/1/id/2 : " + map.get("/user/1/id/2"));
        System.out.println("find /user/1/id/2/ : " + map.get("/user/1/id/2/"));
        
    	Map map2 = new HashMap();
    	map2.put("\\/user\\/?", "user1");
    	map2.put("\\/user\\/[^\\/]*\\/?", "user2");
    	map2.put("\\/user\\/[^\\/]*\\/[^\\/]*\\/?", "user3");
    	map2.put("\\/user\\/[^\\/]*\\/id\\/[^\\/]*\\/?", "user4");
    	
    	map2.forEach((key, value)
    		    -> System.out.println("key: " + key + ", value: " + value));

        System.out.println("find /user : " + map2.get("/user"));
        System.out.println("find /user/ : " + map2.get("/user/"));
        System.out.println("find /user/1 : " + map2.get("/user/1"));
        System.out.println("find /user/1/ : " + map2.get("/user/1/"));
        System.out.println("find /user/1/2 : " + map2.get("/user/1/2"));
        System.out.println("find /user/1/2/ : " + map2.get("/user/1/2/"));
        System.out.println("find /user/1/id/2 : " + map2.get("/user/1/id/2"));
        System.out.println("find /user/1/id/2/ : " + map2.get("/user/1/id/2/"));
    }
    **/
}
