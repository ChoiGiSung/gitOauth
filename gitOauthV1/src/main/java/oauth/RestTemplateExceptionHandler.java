package oauth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RestTemplateExceptionHandler implements ResponseErrorHandler  {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        int rawStatusCode = response.getRawStatusCode();
        HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
        //정의된 번호에 없는 상태 코드일때 거짓 -> 에러가 없다
        return (statusCode != null ? hasError(statusCode) : hasError(rawStatusCode));
    }

    // 400, 500 번대 에러이다
    protected boolean hasError(HttpStatus statusCode){
        return statusCode.isError();
    }

    // 400,500 대 이면 참, 다른 번지수이면 거짓
    protected boolean hasError(int unknownStatusCode){
        HttpStatus.Series series = HttpStatus.Series.resolve(unknownStatusCode);
        return (series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if(statusCode == null){
            byte[] body = getResponseBody(response);
            String message = getErrorMessage(response.getRawStatusCode(),
                    response.getStatusText(), body, getCharset(response));

            throw new UnknownHttpStatusCodeException(message,
                    response.getRawStatusCode(), response.getStatusText(),
                    response.getHeaders(),body,getCharset(response));
        }
        handleError(response,statusCode);
    }

    private void handleError(ClientHttpResponse response, HttpStatus statusCode) throws IOException {
        String statusText = response.getStatusText();
        HttpHeaders headers = response.getHeaders();
        byte[] body = getResponseBody(response);
        Charset charset =getCharset(response);
        String message = getErrorMessage(statusCode.value(), statusText, body, charset);

        switch (statusCode.series()){
            case CLIENT_ERROR:
                throw HttpClientErrorException.create(message,statusCode,statusText,headers,body,charset);
            case SERVER_ERROR:
                throw HttpServerErrorException.create(message,statusCode,statusText,headers,body,charset);
            default:
                throw new UnknownHttpStatusCodeException(message,statusCode.value(),statusText,headers,body,charset);
        }
    }

    private String getErrorMessage(int rawStatusCode, String statusText, @Nullable byte[] responseBody, Charset charset) {
        String preface = rawStatusCode + " " + statusText + ": ";
        if(ObjectUtils.isEmpty(responseBody)){
            return preface + "[ no body ]";
        }

        if(charset == null){
            charset = StandardCharsets.UTF_8;
        }
        int maxChars = 200;

        if(responseBody.length <  maxChars *2){
            return preface + "[" + new String(responseBody,charset) +"]";
        }
        try {
            Reader reader = new InputStreamReader(new ByteArrayInputStream(responseBody), charset);
            CharBuffer buffer = CharBuffer.allocate(maxChars);
            reader.read(buffer);
            reader.close();
            buffer.flip();
            return preface + "[" + buffer.toString() + "... (" + responseBody.length + " bytes)]";
        }
        catch (IOException ex) {
            // should never happen
            throw new IllegalStateException(ex);
        }

    }

    private Charset getCharset(ClientHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        MediaType contentType = headers.getContentType();
        return (contentType != null ? contentType.getCharset() : null);

    }


    private byte[] getResponseBody(ClientHttpResponse response) {
        try {
            return FileCopyUtils.copyToByteArray(response.getBody());
        } catch (IOException e) {

        }
        return new byte[0];
    }
}
