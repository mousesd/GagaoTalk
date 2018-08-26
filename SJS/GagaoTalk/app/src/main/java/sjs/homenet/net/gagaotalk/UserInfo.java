package sjs.homenet.net.gagaotalk;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserInfo {

    // 파이어베이스 Key
    public String fareKey;

    // 이름
    public String name;

    // 이메일, 아이디
    public String email;

    //핸드폰번호
    public String phoneNum;

    // 유저정보
    public UserInfo() {

    }

    /**
     * 생성자
     * @param fareKey  fareKey
     * @param name 이름
     * @param email 이메일
     * @param phoneNum 연락처
     */
    public UserInfo(String fareKey, String name, String email, String phoneNum) {
        this.fareKey = fareKey;
        this.name = name;
        this.email = email;
        this.phoneNum = phoneNum;
    }
}
