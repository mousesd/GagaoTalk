package jsh.homenet.net.gagaotalk.Entity;

/**
 * 사용자 클래스
 */
public class UserInfo {

    /**
     * 아이디
     */
    private String identity;

    /**
     * 패스워드
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

    public UserInfo(String identity)
    {
        this.identity = identity;
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
}
