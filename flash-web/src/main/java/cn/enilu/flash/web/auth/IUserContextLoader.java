package cn.enilu.flash.web.auth;

public interface IUserContextLoader {
    
    UserContext load(String id);
    
}
