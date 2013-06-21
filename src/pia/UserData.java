package pia;

public class UserData {
    
    private String username;
    private String password;
    private boolean isAnonymous;
    
    public UserData(){
        this(null);
    }
    
    public UserData(String username){
        this(username, null);
    }
    
    public UserData(String username, String password){
        this(username, password, false);
    }
    
    public UserData(String username, String password, boolean isAnonymous){
        this.username = username;
        this.password = password;
        this.isAnonymous = isAnonymous;
    }
    
    public String getUsername(){
        return username;
    }
    
    public void setUsername(String username){
        this.username = username;
    }
    
    public String getPassword(){
        return this.password;
    }
    
    public boolean isAnonymous(){
        return isAnonymous;
    }
    
}
