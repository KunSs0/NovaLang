package com.novalang.runtime.interpreter.stdlib;

import com.novalang.runtime.*;
import com.novalang.runtime.interpreter.NovaNativeFunction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * nova.text 模块的编译模式运行时实现。
 *
 * <p>Regex 构造函数返回 NovaMap，方法通过 NovaDynamic 分派。</p>
 */
public final class StdlibRegexCompiled {

    private StdlibRegexCompiled() {}

    public static Object Regex(Object pattern) {
        return RegexWithFlags(pattern, 0);
    }

    public static Object RegexWithFlags(Object pattern, Object flags) {
        String patStr = str(pattern);
        int flagVal = flags instanceof Number ? ((Number) flags).intValue() : 0;
        Pattern compiled = Pattern.compile(patStr, flagVal);
        Map<String, Integer> namedGroupIndices = extractNamedGroups(patStr);
        return createRegexMap(compiled, namedGroupIndices);
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

        regex.put(NovaString.of("matches"), NovaNativeFunction.create("matches", (input) ->
                NovaBoolean.of(pattern.matcher(input.asString()).matches())));

        regex.put(NovaString.of("containsMatchIn"), NovaNativeFunction.create("containsMatchIn", (input) ->
                NovaBoolean.of(pattern.matcher(input.asString()).find())));

        regex.put(NovaString.of("find"), NovaNativeFunction.create("find", (input) -> {
            Matcher m = pattern.matcher(input.asString());
            if (!m.find()) return null;
            return createMatchResult(m, namedGroupIndices);
        }));

        regex.put(NovaString.of("findAll"), NovaNativeFunction.create("findAll", (input) -> {
            Matcher m = pattern.matcher(input.asString());
            NovaList results = new NovaList();
            while (m.find()) results.add(createMatchResult(m, namedGroupIndices));
            return results;
        }));

        regex.put(NovaString.of("replace"), NovaNativeFunction.create("replace", (input, replacement) ->
                NovaString.of(pattern.matcher(input.asString()).replaceAll(replacement.asString()))));

        regex.put(NovaString.of("replaceFirst"), NovaNativeFunction.create("replaceFirst", (input, replacement) ->
                NovaString.of(pattern.matcher(input.asString()).replaceFirst(replacement.asString()))));

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

                if (i + 1 < len && pattern.charAt(i + 1) == '?') {
                    continue;
                }

                groupCount++;
            }
        }

        return result;
    }

    private static boolean isEscapedAt(String s, int pos) {
        int slashes = 0;
        while (pos - 1 >= 0 && s.charAt(pos - 1) == '\\') {
            slashes++;
            pos--;
        }
        return slashes % 2 != 0;
    }

    private static String str(Object o) {
        if (o instanceof String) return (String) o;
        return String.valueOf(o);
    }
}
