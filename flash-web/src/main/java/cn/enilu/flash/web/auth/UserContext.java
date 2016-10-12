package cn.enilu.flash.web.auth;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户上下文
 *
 * @author enilu(eniluzt@qq.com)
 */
public class UserContext {
    /**
     * session 中用户id属性名
     */
    public static final String USER_ID_SESSION_ATTRIBUTE = "uid";
    /**
     * context中 用户属性名
     */
    public static final String CONTEXT_ATTRIBUTE = "userContext";
    public static final String USER_ATTRIBUTE = UserContext.class.getName() + ".user";

    private String id;
    private Object user;
    private Set<String> permissions = new HashSet<>();
    
    public Long getLongId() {
        if (id == null) {
            return null;
        }
        
        return Long.valueOf(id);
    }
    
    public Integer getIntId() {
        if (id == null) {
            return null;
        }

        return Integer.valueOf(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUser(Object user) {
        this.user = user;
    }

    /**
     * 获取当前用户
     * @param <T>
     * @return
     */
    public <T> T getUser() {
        return (T) user;
    }

    /**
     * 获取当前用户权限列表
     * @return
     */
    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    /**
     * 判断用户是否包含指定权限
     * @param permission 权限标识
     * @return
     */
    public boolean hasPermission(String permission) {
        return this.permissions.contains(permission);
    }
}
