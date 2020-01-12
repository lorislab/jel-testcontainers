package org.lorislab.jel.testcontainers.docker.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TestPropertyLoader {

    static final Map<String, TestPropertyCreator> BUILDER = new HashMap<>();

    static {
        BUILDER.put("port", RefPortProperty::createTestProperty);
        BUILDER.put("host", RefHostProperty::createTestProperty);
        BUILDER.put("url", RefUrlProperty::createTestProperty);
        BUILDER.put("env", TestEnvProperty::createTestProperty);
        BUILDER.put("prop", TestPropProperty::createTestProperty);
    }
    static final String PATTERN = "\\$\\{(.*?)}";
    static final Pattern REF_PATTERN = Pattern.compile(PATTERN);

    private TestPropertyLoader() {
    }

    public static TestProperty createTestProperty(String key, String value) {
        List<String> matches = find(value);
        if (matches.isEmpty()) {
            return TestValueProperty.createTestProperty(key,value);
        }
        if (matches.size() == 1) {
            return createTestPropertyInstance(key, matches.get(0));
        }
        List<TestProperty> testProperties = matches.stream()
                .map(v -> createTestPropertyInstance("group", v))
                .collect(Collectors.toList());
        return TestGroupProperty.createTestProperty(key, replaceAll(value), testProperties);
    }

    private static TestProperty createTestPropertyInstance(String key, String value) {
        String[] data = value.split(":", 3);
        TestPropertyCreator builder = BUILDER.get(data[0]);
        if (builder != null) {
            return builder.createTestProperty(key, data);
        }
        throw new IllegalStateException("Not supported type " + data[0] + " for key: " + key);
    }

    private static String replaceAll(String value) {
        Matcher m = REF_PATTERN.matcher(value);
     StringBuffer sb = new StringBuffer();
     int index = 0;
     while (m.find()) {
         m.appendReplacement(sb, "{" + index + "}");
         index++;
      }
      m.appendTail(sb);
     return sb.toString();
    }

    private static List<String> find(String value) {
        Matcher m = REF_PATTERN.matcher(value);
        List<String> result = new ArrayList<>();
        while (m.find()) {
            result.add(m.group(1));
        }
        return result;
    }

}
