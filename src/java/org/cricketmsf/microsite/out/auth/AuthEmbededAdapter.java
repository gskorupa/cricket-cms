/*
 * Copyright 2017 Grzegorz Skorupa <g.skorupa at gmail.com>.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.cricketmsf.microsite.out.auth;

import java.util.HashMap;
import org.cricketmsf.Adapter;
import org.cricketmsf.Event;
import org.cricketmsf.Kernel;
import org.cricketmsf.microsite.out.user.UserAdapterIface;
import org.cricketmsf.microsite.out.user.UserException;
import org.cricketmsf.microsite.user.User;
import org.cricketmsf.out.OutboundAdapter;
import org.cricketmsf.out.db.KeyValueDBException;
import org.cricketmsf.out.db.KeyValueDBIface;

/**
 *
 * @author greg
 */
public class AuthEmbededAdapter extends OutboundAdapter implements Adapter, AuthAdapterIface {

    private String helperAdapterName;
    private String helperAdapterName2;
    private KeyValueDBIface database = null;
    private UserAdapterIface userAdapter = null;
    private boolean initialized = false;

    @Override
    public void init(String helperName, String helperName2) throws AuthException {
        try {
            database = (KeyValueDBIface) Kernel.getInstance().getAdaptersMap().get(helperName);
            userAdapter = (UserAdapterIface) Kernel.getInstance().getAdaptersMap().get(helperName2);
            initialized = true;
        } catch (Exception e) {
            throw new AuthException(UserException.HELPER_NOT_AVAILABLE, "helper adapter not available");
        }
    }

    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        helperAdapterName = properties.get("helper-name");
        Kernel.getInstance().getLogger().print("\thelper-name: " + helperAdapterName);
        helperAdapterName2 = properties.get("helper-name-2");
        Kernel.getInstance().getLogger().print("\thelper-name-2: " + helperAdapterName2);
        try {
            init(helperAdapterName, helperAdapterName2);
        } catch (AuthException e) {
            e.printStackTrace();
            Kernel.handle(Event.logSevere(this.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public Token login(String userID, String password) throws AuthException {
        try {
            User user = userAdapter.get(userID);
            if (user.checkPassword(password)) {
                return createToken(userID);
            } else {
                return null;
            }
        } catch (UserException e) {
            throw new AuthException(AuthException.ACCESS_DENIED, e.getMessage());
        }
    }

    @Override
    public void userAuthorize(String userId, String role) throws AuthException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cmsAuthorize(String docId, String role) throws AuthException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Token createToken(String userID) throws AuthException {
        Token t = new Token(userID,1000*60);
        try {
            database.put("tokens", t.getTid(), t);
        } catch (KeyValueDBException ex) {
            throw new AuthException(AuthException.HELPER_EXCEPTION, ex.getMessage());
        }
        return t;
    }

    @Override
    public boolean checkToken(String tokenID) throws AuthException {
        try {
            Token t = null;
            t= (Token) database.get("tokens", tokenID);
            return t.isValid();
        } catch (KeyValueDBException ex) {
            throw new AuthException(AuthException.HELPER_EXCEPTION, ex.getMessage());
        }
    }

}
