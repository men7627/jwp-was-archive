package http.supoort;

import http.controller.NotFoundException;
import http.model.HttpMethod;
import http.model.HttpRequest;
import http.model.RequestLine;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestParserTest {

    @Test
    void 올바른_입력_파싱_확인() {
        InputStream in = new ByteArrayInputStream("GET /index.html HTTP/1.1\r\nHost: localhost:8080/".getBytes());
        String requestMessage = "GET /index.html HTTP/1.1";
        RequestLine requestLine = new RequestLine(requestMessage);
        HttpRequest httpRequest = new HttpRequest(requestLine, null, null);
        HttpRequest parsedRequest = HttpRequestParser.parse(in);
        assertThat(parsedRequest.getRequestLine().getHttpMethod()).isEqualTo(httpRequest.getRequestLine().getHttpMethod());
        assertThat(parsedRequest.getRequestLine().getHttpUri()).isEqualTo(httpRequest.getRequestLine().getHttpUri());
    }

    @Test
    void 올바른_입력_파라미터_존재_파싱_확인() {
        InputStream in = new ByteArrayInputStream("GET /index.html?name=coogi&age=25 HTTP/1.1\r\nHost: localhost:8080/".getBytes());
        String requestMessage = "GET /index.html HTTP/1.1";
        RequestLine requestLine = new RequestLine(requestMessage);
        HttpRequest httpRequest = new HttpRequest(requestLine, null, null);
        HttpRequest parsedRequest = HttpRequestParser.parse(in);

        assertThat(parsedRequest.getRequestLine().getHttpMethod()).isEqualTo(httpRequest.getRequestLine().getHttpMethod());
        assertThat(parsedRequest.getRequestLine().getHttpUri()).isEqualTo(httpRequest.getRequestLine().getHttpUri());
        assertThat(parsedRequest.getParameter("name")).isEqualTo("coogi");
        assertThat(parsedRequest.getParameter("age")).isEqualTo("25");
    }

    @Test
    void 올바른_HTTP_요청_아닌_경우1() {
        InputStream in = new ByteArrayInputStream("GET /index.html?name=coogi&age=25HTTP/1.1\r\nHost: localhost:8080/".getBytes());
        assertThatThrownBy(() -> HttpRequestParser.parse(in)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void 올바른_HTTP_요청_아닌_경우2() {
        InputStream in = new ByteArrayInputStream("GET/index.html?name=coogi&age=25 HTTP/1.1\r\nHost: localhost:8080/".getBytes());
        assertThatThrownBy(() -> HttpRequestParser.parse(in)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void 올바른_HTTP_요청_아닌_경우3() {
        InputStream in = new ByteArrayInputStream("".getBytes());
        assertThatThrownBy(() -> HttpRequestParser.parse(in)).isInstanceOf(NotFoundException.class);
    }

    @Test
    public void request_POST2() {
        InputStream in = new ByteArrayInputStream(("POST /user/create?id=1 HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "Content-Length: 46\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n" +
                "Accept: */*\r\n" +
                "\r\n" +
                "userId=javajigi&password=password&name=JaeSung").getBytes());
        HttpRequest request = HttpRequestParser.parse(in);

        assertThat(request.getRequestLine().getHttpMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.getRequestLine().getHttpUri().getResourceLocation()).isEqualTo("/user/create");
        assertThat(request.getHeaders().getHeader("Connection")).isEqualTo("keep-alive");
        assertThat(request.getParameter("id")).isEqualTo("1");
        assertThat(request.getParameter("userId")).isEqualTo("javajigi");
    }
}