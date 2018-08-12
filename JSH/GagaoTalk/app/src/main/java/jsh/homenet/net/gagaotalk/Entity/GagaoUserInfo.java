package jsh.homenet.net.gagaotalk.Entity;

/**
 * 사용자 클래스
 */
public class GagaoUserInfo {

    /**
     * 고유 식별자 (파이어베이스 Uid)
     */
    private String uniqueIdentifier;

    /**
     * 아이디
     */
    private String identity;

    /**
     * 아이디
     */
    private String password;

    /**
     * 이름
     */
    private String name;

    /**
     * 핸드폰 번호
     */
    private String phone;

    /**
     * 이메일
     */
    private String email;

    /**
     * 상태 메시지
     */
    private String message;

    public GagaoUserInfo()
    {

    }
    public GagaoUserInfo(String uniqueIdentifier)
    {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
