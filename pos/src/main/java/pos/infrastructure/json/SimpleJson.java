package pos.infrastructure.json;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SimpleJson {

    private SimpleJson() {
    }

    public static String write(Object value) {
        StringBuilder sb = new StringBuilder();
        writeValue(value, sb, 0);
        sb.append("\n");
        return sb.toString();
    }

    private static void writeValue(Object value, StringBuilder sb, int indent) {
        if (value == null) {
            sb.append("null");
            return;
        }
        if (value instanceof String s) {
            writeString(s, sb);
            return;
        }
        if (value instanceof Double d) {
            sb.append(d == Math.floor(d) && !Double.isInfinite(d)
                    ? Long.toString((long) d.doubleValue()) : d.toString());
            return;
        }
        if (value instanceof Number || value instanceof Boolean) {
            sb.append(value);
            return;
        }
        String pad = "  ".repeat(indent);
        String childPad = "  ".repeat(indent + 1);
        if (value instanceof Map<?, ?> map) {
            if (map.isEmpty()) {
                sb.append("{}");
                return;
            }
            sb.append("{\n");
            var iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                sb.append(childPad);
                writeString(String.valueOf(entry.getKey()), sb);
                sb.append(": ");
                writeValue(entry.getValue(), sb, indent + 1);
                if (iterator.hasNext()) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append(pad).append("}");
            return;
        }
        if (value instanceof List<?> list) {
            if (list.isEmpty()) {
                sb.append("[]");
                return;
            }
            sb.append("[\n");
            for (int i = 0; i < list.size(); i++) {
                sb.append(childPad);
                writeValue(list.get(i), sb, indent + 1);
                if (i < list.size() - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }
            sb.append(pad).append("]");
            return;
        }
        throw new IllegalArgumentException("Неподдерживаемый тип: " + value.getClass());
    }

    private static void writeString(String s, StringBuilder sb) {
        sb.append('"');
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> sb.append(c);
            }
        }
        sb.append('"');
    }

    public static Object parse(String text) {
        Parser parser = new Parser(text);
        parser.skipWhitespace();
        Object value = parser.parseValue();
        parser.skipWhitespace();
        if (!parser.isEnd()) {
            throw new IllegalArgumentException("Лишние символы после JSON");
        }
        return value;
    }

    private static final class Parser {

        private final String text;
        private int pos;

        Parser(String text) {
            this.text = text;
        }

        boolean isEnd() {
            return pos >= text.length();
        }

        void skipWhitespace() {
            while (!isEnd() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
        }

        Object parseValue() {
            skipWhitespace();
            if (isEnd()) {
                throw new IllegalArgumentException("Неожиданный конец JSON");
            }
            char c = text.charAt(pos);
            return switch (c) {
                case '{' -> parseObject();
                case '[' -> parseArray();
                case '"' -> parseString();
                case 't' -> parseLiteral("true", Boolean.TRUE);
                case 'f' -> parseLiteral("false", Boolean.FALSE);
                case 'n' -> parseLiteral("null", null);
                default -> parseNumber();
            };
        }

        private Map<String, Object> parseObject() {
            expect('{');
            Map<String, Object> map = new LinkedHashMap<>();
            skipWhitespace();
            if (tryConsume('}')) {
                return map;
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                map.put(key, parseValue());
                skipWhitespace();
                if (tryConsume('}')) {
                    return map;
                }
                expect(',');
            }
        }

        private List<Object> parseArray() {
            expect('[');
            List<Object> list = new ArrayList<>();
            skipWhitespace();
            if (tryConsume(']')) {
                return list;
            }
            while (true) {
                list.add(parseValue());
                skipWhitespace();
                if (tryConsume(']')) {
                    return list;
                }
                expect(',');
            }
        }

        private String parseString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (!isEnd()) {
                char c = text.charAt(pos++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c == '\\') {
                    if (isEnd()) {
                        break;
                    }
                    char escaped = text.charAt(pos++);
                    switch (escaped) {
                        case '"' -> sb.append('"');
                        case '\\' -> sb.append('\\');
                        case '/' -> sb.append('/');
                        case 'n' -> sb.append('\n');
                        case 'r' -> sb.append('\r');
                        case 't' -> sb.append('\t');
                        default -> sb.append(escaped);
                    }
                } else {
                    sb.append(c);
                }
            }
            throw new IllegalArgumentException("Незакрытая строка в JSON");
        }

        private Object parseLiteral(String literal, Object value) {
            if (!text.startsWith(literal, pos)) {
                throw new IllegalArgumentException("Ожидалось '" + literal + "'");
            }
            pos += literal.length();
            return value;
        }

        private Object parseNumber() {
            int start = pos;
            while (!isEnd() && "-+0123456789.eE".indexOf(text.charAt(pos)) >= 0) {
                pos++;
            }
            if (start == pos) {
                throw new IllegalArgumentException("Ожидалось значение JSON");
            }
            try {
                return Double.parseDouble(text.substring(start, pos));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Некорректное число в JSON");
            }
        }

        private void expect(char c) {
            if (isEnd() || text.charAt(pos) != c) {
                throw new IllegalArgumentException("Ожидался символ '" + c + "'");
            }
            pos++;
        }

        private boolean tryConsume(char c) {
            if (!isEnd() && text.charAt(pos) == c) {
                pos++;
                return true;
            }
            return false;
        }
    }
}
