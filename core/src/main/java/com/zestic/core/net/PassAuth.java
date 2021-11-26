package com.zestic.core.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/*
 * 账号密码形式的{@link Authenticator} 实现。
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.5.3
 */
public class PassAuth extends Authenticator {

    private final PasswordAuthentication auth;

    /*
     * 构造
     *
     * @param user 用户名
     * @param pass 密码
     */
    public PassAuth(String user, char[] pass) {
        auth = new PasswordAuthentication(user, pass);
    }

    /*
     * 创建账号密码形式的{@link Authenticator} 实现。
     *
     * @param user 用户名
     * @param pass 密码
     * @return PassAuth
     */
    public static PassAuth of(String user, char[] pass) {
        return new PassAuth(user, pass);
    }

    @Override protected PasswordAuthentication getPasswordAuthentication() {
        return auth;
    }
}
