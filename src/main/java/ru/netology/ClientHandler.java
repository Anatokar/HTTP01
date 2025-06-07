package ru.netology;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Set<String> validPaths;

    public ClientHandler(Socket socket, Set<String> validPaths) {
        this.socket = socket;
        this.validPaths = validPaths;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            final var requestLine = in.readLine();
            if (requestLine == null || requestLine.isBlank()) return;

            final var parts = requestLine.split(" ");
            if (parts.length != 3) return;

            final var path = parts[1];
            if (!validPaths.contains(path)) {
                writeResponse(out, "404 Not Found", "text/plain", new byte[0]);
                return;
            }

            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);

            if (path.equals("/classic.html")) {
                final var template = Files.readString(filePath);
                final var content = template.replace("{time}", LocalDateTime.now().toString()).getBytes();
                writeResponse(out, "200 OK", mimeType, content);
            } else {
                final var content = Files.readAllBytes(filePath);
                writeResponse(out, "200 OK", mimeType, content);
            }

        } catch (IOException e) {
            System.err.println("Client error: " + e.getMessage());
        }
    }

    private void writeResponse(BufferedOutputStream out, String status, String contentType, byte[] content) throws IOException {
        out.write((
                "HTTP/1.1 " + status + "\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Content-Length: " + content.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n").getBytes());
        out.write(content);
        out.flush();
    }
}