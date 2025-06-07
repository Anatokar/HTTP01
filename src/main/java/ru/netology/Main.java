package ru.netology;

public class Main {
  public static void main(String[] args) {
    final var server = new Server();

    server.addHandler("GET", "/messages", (request, out) -> {
      String body = "Hello from GET /messages!";
      out.write((
              "HTTP/1.1 200 OK\r\n" +
                      "Content-Type: text/plain\r\n" +
                      "Content-Length: " + body.length() + "\r\n" +
                      "Connection: close\r\n" +
                      "\r\n" +
                      body
      ).getBytes());
      out.flush();
    });

    server.addHandler("POST", "/messages", (request, out) -> {
      String body = "POST received: " + request.getBody();
      out.write((
              "HTTP/1.1 200 OK\r\n" +
                      "Content-Type: text/plain\r\n" +
                      "Content-Length: " + body.length() + "\r\n" +
                      "Connection: close\r\n" +
                      "\r\n" +
                      body
      ).getBytes());
      out.flush();
    });

    server.listen(9999);
  }
}