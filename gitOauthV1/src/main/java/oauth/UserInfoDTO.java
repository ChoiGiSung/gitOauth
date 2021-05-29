package oauth;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserInfoDTO {

    //DB의 기본키가 아닌 리소스서버의 고유 id
    protected Long id;

    public UserInfoDTO() {
    }

    public UserInfoDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
