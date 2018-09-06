package jbk.homenet.net.gagaotalk.Class;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserInfo {

    //region == [ Fields ] ==

    /**
     * uid
     */
    public String uid;

    /**
     * 이름
     */
    public String name;

    /**
     * 이메일
     */
    public String email;

    /**
     * 상태 메세지
     */
    public String stateMsg;

    /**
     * 연락처
     */
    public String phoneNum;

    /**
     * 이미지 입력 여부
     */
    public Boolean hasImage;

    //endregion == [ Fields ] ==

    //region == [ Constructors ] ==

    /**
     * 생성자
     */
    public UserInfo(){

    }

    /**
     * 생성자
     * @param uid  uId
     * @param name 이름
     * @param email 이메일
     * @param stateMsg 상태메세지
     * @param phoneNum 연락처
     */
    public UserInfo(String uid, String name, String email, String stateMsg, String phoneNum) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.stateMsg = stateMsg;
        this.phoneNum = phoneNum;
    }
    //endregion == [ Constructors ] ==

    //region == [ Override Methods ] ==
    //endregion == [ Override Methods ] ==

    //region == [ Methods ] ==
    //endregion == [ Methods ] ==


}
