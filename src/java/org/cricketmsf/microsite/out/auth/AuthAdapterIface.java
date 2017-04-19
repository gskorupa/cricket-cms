/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cricketmsf.microsite.out.auth;

/**
 *
 * @author greg
 */
public interface AuthAdapterIface {
    public void init(String helperName, String helperName2) throws AuthException;
    public Token login(String login, String password) throws AuthException;
    public void userAuthorize(String userId, String role) throws AuthException;
    public void cmsAuthorize(String docId, String role) throws AuthException;
    //public void appAuthorize(String id, String role) throws AuthException;
    public Token createToken(String userID) throws AuthException;
    public boolean checkToken(String tokenID) throws AuthException;
}
