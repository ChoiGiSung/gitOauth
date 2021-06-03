package oauth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.nio.charset.Charset;

public class RestTemplateExceptionHandler implements ResponseErrorHandler  {

    private static final String UNKNOWN_MESSAGE = "알 수 없는 상태 코드";
    private static final String CLIENT_MESSAGE = "클라이언트 에러";
    private static final String SERVER_MESSAGE = "서버 에러";

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        int rawStatusCode = response.getRawStatusCode();
        HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
                                //정의된 코드가 있음                //정의된 코드가 없음
        return (statusCode != null ? hasError(statusCode) : hasError(rawStatusCode));
    }

    // status가 있지만 400, 500 번대 에러가 아님 false
    protected boolean hasError(HttpStatus statusCode){
        return statusCode.isError();
    }

    // 400,500 대 이면 참, 다른 번지수이면 거짓 -> 다른 번지 수는 에러가 아니다.
    protected boolean hasError(int unknownStatusCode){
        HttpStatus.Series series = HttpStatus.Series.resolve(unknownStatusCode);
        return (series == HttpStatus.Series.CLIENT_ERROR || series == HttpStatus.Series.SERVER_ERROR);
    }

    //예외 핸들 부분
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if(statusCode == null){
            byte[] body = getResponseBody(response);

            throw new UnknownHttpStatusCodeException(UNKNOWN_MESSAGE,
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

        switch (statusCode.series()){
            case CLIENT_ERROR:
                throw HttpClientErrorException.create(CLIENT_MESSAGE,statusCode,statusText,headers,body,charset);
            case SERVER_ERROR:
                throw HttpServerErrorException.create(SERVER_MESSAGE,statusCode,statusText,headers,body,charset);
            default: //null은 아니지만 400,500 상태 코드가 아닌 경우
                throw new UnknownHttpStatusCodeException(UNKNOWN_MESSAGE,statusCode.value(),statusText,headers,body,charset);
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
