package com.get_quotes.getQuotes.Utility.ThreadUtlity;


import java.util.HashMap;
import java.util.Map;

public class ThreadLocalContext {
    private static final ThreadLocal<Map<String, Object>> threadLocalData = ThreadLocal.withInitial(HashMap::new);

    public static void set(String key, Object value) {
        threadLocalData.get().put(key, value);
    }

    public static Object get(String key) {
        return threadLocalData.get().get(key);
    }

    public static void clear() {
        threadLocalData.remove();
    }

    public static String getUserId(){
        return (String) get(ThreadLocalContextKeys.USER_ID);
    }
}