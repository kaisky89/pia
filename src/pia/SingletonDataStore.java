/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pia;

/**
 *
 * @author kaisky89
 */
public class SingletonDataStore {
    private static SingletonDataStore instance = new SingletonDataStore();
    
    private SingletonDataStore(){
    }
    
    public static SingletonDataStore getInstance(){
        return instance;
    }
    
    
    
    private UserData user;
    private String serverAdress = "localhost";

    public String getJID(){
        return user.getUsername() + "@" + getServerAdress();
    }
    
    public String getJIDex(){
        return user.getUsername() + "@" + getServerAdress() + "/Smack";
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
    
}
