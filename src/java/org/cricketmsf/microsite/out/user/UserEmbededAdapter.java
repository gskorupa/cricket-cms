/*
 * Copyright 2017 Grzegorz Skorupa <g.skorupa at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cricketmsf.microsite.out.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.cricketmsf.Adapter;
import org.cricketmsf.Event;
import org.cricketmsf.Kernel;
import org.cricketmsf.microsite.user.User;
import org.cricketmsf.out.OutboundAdapter;
import org.cricketmsf.out.db.KeyValueDBException;
import org.cricketmsf.out.db.KeyValueDBIface;

/**
 *
 * @author greg
 */
public class UserEmbededAdapter extends OutboundAdapter implements Adapter, UserAdapterIface {

    private KeyValueDBIface database = null;
    private String helperAdapterName = null;
    private boolean initialized = false;

    @Override
    public void init(String helperName) throws UserException {
        try {
            database = (KeyValueDBIface) Kernel.getInstance().getAdaptersMap().get(helperName);
            initialized = true;
        } catch (Exception e) {
            throw new UserException(UserException.HELPER_NOT_AVAILABLE, "helper adapter not available");
        }
    }

    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        helperAdapterName = properties.get("helper-name");
        Kernel.getInstance().getLogger().print("\thelper-name: " + helperAdapterName);
        try {
            init(helperAdapterName);
        } catch (UserException e) {
            Kernel.handle(Event.logSevere(this.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public User get(String uid) throws UserException {
        User user;
        try {
            user = (User) database.get("users", uid);
            return user;
        } catch (KeyValueDBException | ClassCastException e) {
            throw new UserException(UserException.HELPER_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public Map getAll() throws UserException {
        HashMap<String, User> map;
        try {
            map = (HashMap<String, User>) database.getAll("users");
            return map;
        } catch (KeyValueDBException | ClassCastException e) {
            throw new UserException(UserException.HELPER_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public User register(User user) throws UserException {
        User newUser = user;
        Random r = new Random(System.currentTimeMillis());
        newUser.setUid(newUser.getEmail());
        newUser.setConfirmString("" + r.nextLong());
        try {
            database.put("users", user.getUid(), user);
            return get(user.getUid());
        } catch (KeyValueDBException e) {
            throw new UserException(UserException.HELPER_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void modify(User user) throws UserException {
        try {
            database.put("users", user.getUid(), user);
        } catch (KeyValueDBException e) {
            throw new UserException(UserException.HELPER_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void confirmRegistration(String uid) throws UserException {
        User user = get(uid);
        try {
            user.setConfirmed(true);
            database.put("users", user.getUid(), user);
        } catch (KeyValueDBException e) {
            throw new UserException(UserException.HELPER_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void remove(String uid) throws UserException {
        User user = get(uid);
        try {
            Random r = new Random(System.currentTimeMillis());
            user.setConfirmString("" + r.nextLong());
            user.setUnregisterRequested(true);
            database.put("users", user.getUid(), user);
        } catch (KeyValueDBException e) {
            throw new UserException(UserException.HELPER_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void confirmRemove(String uid) throws UserException {
        try {
            database.remove("users", uid);
        } catch (KeyValueDBException e) {
            throw new UserException(UserException.HELPER_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public boolean checkPassword(String uid, String password) throws UserException {
        try {
            User user = (User) database.get("users", uid);
            return user.checkPassword(password);
        } catch (KeyValueDBException e) {
            throw new UserException(UserException.UNKNOWN_USER, e.getMessage());
        }
    }

}
