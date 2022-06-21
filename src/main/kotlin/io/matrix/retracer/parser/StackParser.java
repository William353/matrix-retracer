package io.matrix.retracer.parser;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StackParser {
    /**
     * Stack中，方法间的分割符
     */
    private static final String STACK_SPLIT = " ";

    /**
     * 解析后，每个方法的分隔符
     */
    private static final String STACK_RETRACED_SPLIT = " ";

    private static final String TEXT_PARSE_ERROR = "parse error";

    public String parseStack(String stack, Map<Integer, String> map) {
        if (stack == null || stack.isEmpty()) {
            return "";
        }
        try {
            StringBuilder sb = new StringBuilder();
            String[] lines = stack.split(STACK_SPLIT);
            for (String line : lines) {
                if (line.isEmpty()) {
                    continue;
                }
                String[] args = line.split(",");
                Integer methodId = Integer.parseInt(args[1]);
                if (!map.containsKey(methodId)) {
                    System.out.print("error!!!");
                    continue;
                }
                args[1] = map.get(methodId);
                sb.append(args[0]).append(",")
                        .append(args[1]).append(",")
                        .append(args[2]).append(",")
                        .append(args[3].trim()).append("ms");
                sb.append(STACK_RETRACED_SPLIT);
            }
            return sb.toString();

        } catch (Exception e) {
            return TEXT_PARSE_ERROR;
        }
    }

    public String parseStackKey(String stackKey, Map<Integer, String> map) {
        if (stackKey == null || stackKey.isEmpty()) {
            return "";
        }
        try {
            String[] keys = stackKey.split("|");
            StringBuilder sb = new StringBuilder();
            for (String key : keys) {
                if (key.isEmpty()) {
                    continue;
                }
                int methodId = Integer.parseInt(key);
                sb.append(map.get(methodId)).append("|");
            }
            return sb.toString();
        } catch (Exception e) {
            return TEXT_PARSE_ERROR;
        }
    }
}
