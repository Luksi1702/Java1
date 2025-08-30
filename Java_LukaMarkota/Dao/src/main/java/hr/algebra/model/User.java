/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.model;

import hr.algebra.utilities.PasswordUtils.HashSalt;
import static hr.algebra.utilities.PasswordUtils.hashPassword;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author lukam
 */
public class User {

    private int id;
    private String username;
    private String pwdHash;
    private String pwdSalt;
    private boolean isAdmin;
    private int personID;

    public User(String username, String password) throws NoSuchAlgorithmException {
        this.username = username;
        HashSalt result = hashPassword(password);
        this.pwdHash = result.hash;
        this.pwdSalt = result.salt;
        this.isAdmin = false;
    }

    public User(String username, String pwdHash, String pwdSalt) {
        this.username = username;
        this.pwdHash = pwdHash;
        this.pwdSalt = pwdSalt;
    }

    public User(int id, boolean isAdmin) {
        this.id = id;
        this.isAdmin = isAdmin;
    }

    
    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwdHash() {
        return pwdHash;
    }

    public void setPwdHash(String pwdHash) {
        this.pwdHash = pwdHash;
    }

    public String getPwdSalt() {
        return pwdSalt;
    }

    public void setPwdSalt(String pwdSalt) {
        this.pwdSalt = pwdSalt;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int PersonID) {
        this.personID = PersonID;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + id + ", username=" + username + ", isAdmin=" + isAdmin + '}';
    }

}
