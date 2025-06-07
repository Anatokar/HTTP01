package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final String protocol;
    private final Map<String, String> headers = new HashMap<>();
    private final String body;

    public Request(BufferedReader reader) throws IOException {
        // читаем request line
        var requestLine = reader.readLine();
        var parts = requestLine.split(" ");
        method = parts[0];
        path = parts[1];
        protocol = parts[2];

        // читаем заголовки
        String line;
        while ((line = reader.readLine()) != null && !line.isBlank()) {
            var colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                var key = line.substring(0, colonIndex).trim();
                var value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }

        // читаем тело (если есть)
        var contentLength = headers.getOrDefault("Content-Length", "0");
        var length = Integer.parseInt(contentLength);
        if (length > 0) {
            char[] buf = new char[length];
            reader.read(buf, 0, length);
            body = new String(buf);
        } else {
            body = null;
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}