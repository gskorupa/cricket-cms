/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cricketmsf.microsite.user;

import java.util.HashSet;

/**
 *
 * @author greg
 */
public class User {

    public static final int USER = 0;
    public static final int OWNER = 1;
    private int type = USER;
    private String uid;
    private String secretHash;
    private String email;
    private String role;
    private boolean confirmed;
    private boolean unregisterRequested;
    private String confirmString;
    private String password;

    public User() {
        confirmed = false;
        unregisterRequested = false;
    }

    /**
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return the confirmed
     */
    public boolean isConfirmed() {
        return confirmed;
    }

    /**
     * @param confirmed the confirmed to set
     */
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    /**
     * @return the waitingForUnregister
     */
    public boolean isUnregisterRequested() {
        return unregisterRequested;
    }

    /**
     * @param unregisterRequested the waitingForUnregister to set
     */
    public void setUnregisterRequested(boolean unregisterRequested) {
        this.unregisterRequested = unregisterRequested;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the secretHash
     */
    public String getSecretHash() {
        return secretHash;
    }

    /**
     * @param secretHash the secretHash to set
     */
    public void setSecretHash(String secretHash) {
        this.secretHash = secretHash;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
            newUser.setType(User.OWNER);
     * @return the confirmString
     */
    public String getConfirmString() {
        return confirmString;
    }

    /**
     * @param confirmString the confirmString to set
     */
    public void setConfirmString(String confirmString) {
        this.confirmString = confirmString;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = HashMaker.md5Java(password);
    }
    
    public boolean checkPassword(String passToCheck){
        return getPassword()!=null && getPassword().equals(HashMaker.md5Java(passToCheck));
    }

}
