package com.novalang.runtime;

import com.novalang.runtime.interpreter.NovaNativeFunction;

import java.lang.invoke.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式字面量 re"..." 的运行时支持。
 *
 * <p>提供与 {@code nova.text.Regex()} 构造函数相同的 NovaMap 包装接口，
 * 使 re"..." 字面量返回的对象与 Regex() 构造的 API 兼容。</p>
 */
public final class RegexLiteral {

    private RegexLiteral() {}

    /**
     * 编译正则表达式模式并返回 NovaMap 包装。
     * 由 MIR 降级层通过 INVOKE_DYNAMIC 调用。
     */
    public static NovaMap compile(String pattern) {
        Pattern compiled = Pattern.compile(pattern);
        Map<String, Integer> namedGroupIndices = extractNamedGroups(pattern);
        return createRegexMap(compiled, namedGroupIndices);
    }

    /**
     * INVOKE_DYNAMIC bootstrap 方法。
     * 通过 MethodHandles.Lookup 运行时解析 compile 方法，100% relocate 安全。
     */
    public static CallSite bootstrapRegexCompile(MethodHandles.Lookup lookup,
                                                  String name, MethodType type)
            throws NoSuchMethodException, IllegalAccessException {
        MethodHandle target = lookup.findStatic(RegexLiteral.class, "compile",
                MethodType.methodType(NovaMap.class, String.class));
        return new ConstantCallSite(target.asType(type));
    }

    private static NovaMap createRegexMap(Pattern pattern,
                                           Map<String, Integer> namedGroupIndices) {
        NovaMap regex = new NovaMap();
        regex.put(NovaString.of("pattern"), NovaString.of(pattern.pattern()));

        // —— 命名捕获组：pattern 级别查询 API ——
        NovaList groupNames = new NovaList();
        NovaMap nameToIndex = new NovaMap();
        for (Map.Entry<String, Integer> e : namedGroupIndices.entrySet()) {
            groupNames.add(NovaString.of(e.getKey()));
            nameToIndex.put(NovaString.of(e.getKey()), NovaInt.of(e.getValue()));
        }
        regex.put(NovaString.of("groupNames"), groupNames);
        regex.put(NovaString.of("namedGroupIndices"), nameToIndex);

        regex.put(NovaString.of("matches"), NovaNativeFunction.create("matches",
                (input) -> NovaBoolean.of(pattern.matcher(input.asString()).matches())));

        regex.put(NovaString.of("containsMatchIn"), NovaNativeFunction.create("containsMatchIn",
                (input) -> NovaBoolean.of(pattern.matcher(input.asString()).find())));

        regex.put(NovaString.of("find"), NovaNativeFunction.create("find", (input) -> {
            Matcher m = pattern.matcher(input.asString());
            if (!m.find()) return NovaNull.NULL;
            return createMatchResult(m, namedGroupIndices);
        }));

        regex.put(NovaString.of("findAll"), NovaNativeFunction.create("findAll", (input) -> {
            Matcher m = pattern.matcher(input.asString());
            NovaList results = new NovaList();
            while (m.find()) results.add(createMatchResult(m, namedGroupIndices));
            return results;
        }));

        regex.put(NovaString.of("replace"), NovaNativeFunction.create("replace",
                (input, replacement) -> NovaString.of(
                        pattern.matcher(input.asString()).replaceAll(replacement.asString()))));

        regex.put(NovaString.of("replaceFirst"), NovaNativeFunction.create("replaceFirst",
                (input, replacement) -> NovaString.of(
                        pattern.matcher(input.asString()).replaceFirst(replacement.asString()))));

        regex.put(NovaString.of("split"), NovaNativeFunction.create("split", (input) -> {
            String[] parts = pattern.split(input.asString());
            NovaList result = new NovaList();
            for (String part : parts) result.add(NovaString.of(part));
            return result;
        }));

        return regex;
    }

    private static NovaMap createMatchResult(Matcher m,
                                             Map<String, Integer> namedGroupIndices) {
        NovaMap result = new NovaMap();
        result.put(NovaString.of("value"), NovaString.of(m.group()));
        result.put(NovaString.of("start"), NovaInt.of(m.start()));
        result.put(NovaString.of("end"), NovaInt.of(m.end()));

        NovaList groups = new NovaList();
        for (int i = 0; i <= m.groupCount(); i++) {
            String g = m.group(i);
            groups.add(g != null ? NovaString.of(g) : NovaNull.NULL);
        }
        result.put(NovaString.of("groups"), groups);

        NovaList groupValues = new NovaList();
        for (int i = 0; i <= m.groupCount(); i++) {
            String g = m.group(i);
            groupValues.add(g != null ? NovaString.of(g) : NovaString.of(""));
        }
        result.put(NovaString.of("groupValues"), groupValues);

        // —— 命名捕获组 ——
        NovaMap namedGroups = new NovaMap();
        for (Map.Entry<String, Integer> e : namedGroupIndices.entrySet()) {
            String g = m.group(e.getValue());
            namedGroups.put(NovaString.of(e.getKey()),
                    g != null ? NovaString.of(g) : NovaNull.NULL);
        }
        result.put(NovaString.of("namedGroups"), namedGroups);

        return result;
    }

    // ================================================================
    // 命名捕获组提取
    // ================================================================

    /**
     * 从正则表达式模式字符串中提取命名捕获组 (?:&lt;name&gt;...) 的名称和索引。
     *
     * @return name → 1-based group index 的映射（保持声明顺序）
     */
    private static Map<String, Integer> extractNamedGroups(String pattern) {
        Map<String, Integer> result = new LinkedHashMap<>();
        int len = pattern.length();
        int groupCount = 0;
        boolean inCharClass = false;

        for (int i = 0; i < len; i++) {
            char c = pattern.charAt(i);

            if (c == '[' && !isEscapedAt(pattern, i)) {
                inCharClass = true;
            } else if (c == ']' && !isEscapedAt(pattern, i)) {
                inCharClass = false;
            }

            if (inCharClass) continue;
            if (isEscapedAt(pattern, i)) continue;

            if (c == '(') {
                // 检测命名捕获组 (?<name>...
                if (i + 3 < len
                        && pattern.charAt(i + 1) == '?'
                        && pattern.charAt(i + 2) == '<') {
                    char afterBracket = pattern.charAt(i + 3);
                    if (afterBracket != '=' && afterBracket != '!') {
                        int nameStart = i + 3;
                        int nameEnd = pattern.indexOf('>', nameStart);
                        if (nameEnd > nameStart) {
                            String name = pattern.substring(nameStart, nameEnd);
                            groupCount++;
                            if (!result.containsKey(name)) {
                                result.put(name, groupCount);
                            }
                            i = nameEnd;
                            continue;
                        }
                    }
                }

                // 跳过非捕获组 (?:, (?=, (?!, (?<=, (?<!, (?>, (?flags 等)
                if (i + 1 < len && pattern.charAt(i + 1) == '?') {
                    continue;
                }

                // 普通捕获组
                groupCount++;
            }
        }

        return result;
    }

    /**
     * 判断字符串中指定位置是否被反斜杠转义。
     */
    private static boolean isEscapedAt(String s, int pos) {
        int slashes = 0;
        while (pos - 1 >= 0 && s.charAt(pos - 1) == '\\') {
            slashes++;
            pos--;
        }
        return slashes % 2 != 0;
    }
}
