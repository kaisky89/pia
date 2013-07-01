package pia;

import java.util.Date;

final public class SingletonDataStore {
    final private static SingletonDataStore instance = new SingletonDataStore();
    
    private SingletonDataStore(){
    }
    
    public static SingletonDataStore getInstance(){
        return instance;
    }
    
    
    private String resource = null;
    private UserData user;
    private String serverAdress = "liquidbox.de";
    //private String serverAdress = "localhost";

    public String getJID(){
        return user.getUsername() + "@" + getServerAdress();
    }
    
    public String getJIDex(){
        return user.getUsername() + "@" + getServerAdress() + "/" + getResource();
    }
    
    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public void setServerAdress(String serverAdress) {
        this.serverAdress = serverAdress;
    }

    public String getServerAdress() {
        return serverAdress;
    }

    public String getResource() {
        if (resource == null) {
            Date date = new Date();
            resource = String.valueOf(date.getTime());
        }
        return resource;
    }

}
