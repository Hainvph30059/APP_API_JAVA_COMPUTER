package com.hoangduong.hoangduongcomputer.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        // Chỉ dùng addHeader với đầy đủ thuộc tính để tránh tạo duplicate cookies
        response.addHeader("Set-Cookie", String.format(
                "%s=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=None",
                name, value, maxAge
        ));
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        // Xóa cookie bằng cách set Max-Age=0
        response.addHeader("Set-Cookie", String.format(
                "%s=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=None",
                name
        ));
    }

    public static final int FOURTEEN_DAYS = 14 * 24 * 60 * 60;
    public static final int ONE_HOUR = 60 * 60;
    public static final int ONE_DAY = 24 * 60 * 60;
}
