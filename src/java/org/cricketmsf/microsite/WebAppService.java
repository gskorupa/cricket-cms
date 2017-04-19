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
package org.cricketmsf.microsite;

import java.util.Base64;
import org.cricketmsf.Event;
import org.cricketmsf.Kernel;
import org.cricketmsf.RequestObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.cricketmsf.annotation.EventHook;
import org.cricketmsf.annotation.HttpAdapterHook;
import org.cricketmsf.in.http.EchoHttpAdapterIface;
import org.cricketmsf.in.http.HtmlGenAdapterIface;
import org.cricketmsf.in.http.HttpAdapter;
import org.cricketmsf.in.http.ParameterMapResult;
import org.cricketmsf.in.http.StandardResult;
import org.cricketmsf.in.scheduler.SchedulerIface;
import org.cricketmsf.microsite.out.auth.Token;
import org.cricketmsf.microsite.cms.CmsIface;
import org.cricketmsf.microsite.out.auth.AuthAdapterIface;
import org.cricketmsf.microsite.out.auth.AuthException;
import org.cricketmsf.microsite.out.iot.ThingsDataIface;
import org.cricketmsf.microsite.out.queue.QueueAdapterIface;
import org.cricketmsf.microsite.out.user.UserAdapterIface;
import org.cricketmsf.microsite.out.user.UserException;
import org.cricketmsf.microsite.user.User;
import org.cricketmsf.microsite.user.UserEvent;
import org.cricketmsf.out.db.KeyValueDB;
import org.cricketmsf.out.db.KeyValueDBException;
import org.cricketmsf.out.db.KeyValueDBIface;
import org.cricketmsf.out.file.FileReaderAdapterIface;
import org.cricketmsf.out.log.LoggerAdapterIface;

/**
 * EchoService
 *
 * @author greg
 */
public class WebAppService extends Kernel {

    // adapterClasses
    LoggerAdapterIface logAdapter = null;
    EchoHttpAdapterIface httpAdapter = null;
    KeyValueDBIface database = null;
    SchedulerIface scheduler = null;
    HtmlGenAdapterIface htmlAdapter = null;
    FileReaderAdapterIface fileReader = null;

    // optional
    // we don't need to register input adapters:
    // UserApi, AuthApi and other input adapter if we not need to acces them directly from the service
    //CM module
    KeyValueDBIface cmsDatabase = null;
    FileReaderAdapterIface cmsFileReader = null;
    CmsIface cms = null;
    //user module
    KeyValueDBIface userDatabase = null; //TODO
    UserAdapterIface userAdapter = null;
    //auth module
    AuthAdapterIface authAdapter = null;
    //event broker client
    QueueAdapterIface queueAdapter = null;
    KeyValueDBIface queueDB = null;
    // IoT
    ThingsDataIface thingsAdapter = null;
    KeyValueDBIface thingsDB = null;

    @Override
    public void getAdapters() {
        // standard Cricket adapters
        logAdapter = (LoggerAdapterIface) getRegistered("logger");
        //httpAdapter = (EchoHttpAdapterIface) getRegistered("echo");
        database = (KeyValueDBIface) getRegistered("database");
        scheduler = (SchedulerIface) getRegistered("scheduler");
        htmlAdapter = (HtmlGenAdapterIface) getRegistered("WwwService");
        fileReader = (FileReaderAdapterIface) getRegistered("FileReader");
        //cms
        cmsFileReader = (FileReaderAdapterIface) getRegistered("CmsFileReader");
        cmsDatabase = (KeyValueDBIface) getRegistered("cmsDatabase");
        //user
        userAdapter = (UserAdapterIface) getRegistered("userAdapter");
        //auth
        authAdapter = (AuthAdapterIface) getRegistered("authAdapter");
        //queue
        queueDB = (KeyValueDBIface) getRegistered("queueDB");
        queueAdapter = (QueueAdapterIface) getRegistered("queueAdapter");
        //IoT
        thingsAdapter = (ThingsDataIface) getRegistered("iotAdapter");
        thingsDB = (KeyValueDB) getRegistered("iotDB");
    }

    @Override
    public void runInitTasks() {
        try {
            database.addTable("webcache", 200, false);
        } catch (KeyValueDBException e) {
            handle(Event.logInfo(getClass().getSimpleName(), e.getMessage()));
        }
        try {
            database.addTable("counters", 1, false);
        } catch (KeyValueDBException e) {
            handle(Event.logInfo(getClass().getSimpleName(), e.getMessage()));
        }
        // USERS
        try {
            database.addTable("users", 10, true);
            String initialAdminEmail = (String) getProperties().getOrDefault("initial-admin-email", "");
            String initialAdminPassword = (String) getProperties().getOrDefault("initial-admin-password", "");
            if (initialAdminEmail.isEmpty() || initialAdminPassword.isEmpty()) {
                handle(Event.logSevere(this.getClass().getSimpleName(), "initial-admin-email or initial-admin-secret properties not set. Stop the server now!"));
            }
            User newUser = new User();
            //String newUid = HashMaker.md5Java(initialAdminSecret + "@" + initialAdminEmail);
            newUser.setUid(initialAdminEmail);
            //String newSecret = HashMaker.md5Java(initialAdminEmail + "@" + initialAdminSecret);
            //newUser.setSecretHash(newSecret);
            newUser.setEmail(initialAdminEmail);
            newUser.setType(User.OWNER);
            newUser.setRole("admin");
            newUser.setPassword(initialAdminPassword);
            Random r = new Random(System.currentTimeMillis());
            newUser.setConfirmString("" + r.nextLong());
            // no confirmation necessary for initial admin account
            newUser.setConfirmed(true);
            database.put("users", newUser.getUid(), newUser);
        } catch (KeyValueDBException e) {
            handle(Event.logInfo(getClass().getSimpleName(), e.getMessage()));
        }
        // AUTH / IDM
        try {
            database.addTable("tokens", 100, false);
        } catch (KeyValueDBException e) {
            handle(Event.logInfo(getClass().getSimpleName(), e.getMessage()));
        }
        // CMS
        /*
        try{
           cms.initialize(cmsDatabase, cmsFileReader, logAdapter);
        }catch(CmsException e){
           //TODO: 
        }*/

    }

    @Override
    public void runFinalTasks() {
        /*
        // CLI adapter doesn't start automaticaly as other inbound adapters
        if (cli != null) {
            cli.start();
        }
         */
    }

    @Override
    public void runOnce() {
        super.runOnce();
        handleEvent(Event.logInfo("Service.runOnce()", "executed"));
    }

    /**
     * Process requests from simple web server implementation given by
     * HtmlGenAdapter access web web resources
     *
     * @param event
     * @return ParameterMapResult with the file content as a byte array
     */
    @HttpAdapterHook(adapterName = "WwwService", requestMethod = "GET")
    public Object doGet(Event event) {
        //TODO: to nie jest optymalne rozwiązanie
        handle(Event.logFinest(this.getClass().getSimpleName(), event.getRequest().uri));
        ParameterMapResult result;
        result = (ParameterMapResult) fileReader
                .getFile(event.getRequest(), htmlAdapter.useCache() ? database : null, "webcache");
        if (result.getCode() == HttpAdapter.SC_NOT_FOUND) {
            if (event.getRequest().pathExt.endsWith(".html")) {
                //TODO: configurable index file params
                RequestObject request = processRequest(event.getRequest(), ".html", "index_pl.html");
                result = (ParameterMapResult) fileReader
                        .getFile(request, htmlAdapter.useCache() ? database : null, "webcache");
            }
        }
        // TODO: caching policy 
        result.setMaxAge(120);
        return result;
    }

    /**
     * Modify request pathExt basic on adapter configuration for CMS/Website
     * systems
     *
     * @param originalRequest
     * @param indexFileExt
     * @param indexFileName
     * @return
     */
    private RequestObject processRequest(RequestObject originalRequest, String indexFileExt, String indexFileName) {
        RequestObject request = originalRequest;
        String[] pathElements = request.uri.split("/");
        if (pathElements.length == 0) {
            return request;
        }
        StringBuilder sb = new StringBuilder();
        if (pathElements[pathElements.length - 1].endsWith(indexFileExt)) {
            if (!pathElements[pathElements.length - 1].equals(indexFileName)) {
                for (int i = 0; i < pathElements.length - 1; i++) {
                    sb.append(pathElements[i]).append("/");
                }
                request.pathExt = sb.toString();
            }
        }
        return request;
    }

    @HttpAdapterHook(adapterName = "UserService", requestMethod = "GET")
    public Object userGet(Event event) {
        RequestObject request = event.getRequest();
        //handle(Event.logFinest(this.getClass().getSimpleName(), request.pathExt));
        String uid = request.pathExt;
        StandardResult result = new StandardResult();
        try {
            if (uid.isEmpty()) {
                Map m = userAdapter.getAll();
                result.setData(m);
            } else {
                User u = (User) userAdapter.get(uid);
                result.setData(u);
            }
        } catch (UserException e) {
            result.setCode(HttpAdapter.SC_NOT_FOUND);
        }
        return result;
    }

    @HttpAdapterHook(adapterName = "UserService", requestMethod = "POST")
    public Object userAdd(Event event) {
        RequestObject request = event.getRequest();
        //handle(Event.logFinest(this.getClass().getSimpleName(), request.pathExt));
        StandardResult result = new StandardResult();
        try {
            User newUser = new User();
            newUser.setEmail(event.getRequestParameter("email"));
            newUser.setType(User.USER);
            newUser.setRole("");
            newUser.setPassword(event.getRequestParameter("password"));
            newUser = userAdapter.register(newUser);
            if (((String) getProperties().getOrDefault("user-confirm", "false")).equalsIgnoreCase("true")) {
                result.setCode(HttpAdapter.SC_ACCEPTED);
                //fire event to send "need confirmation" email
                handle(new UserEvent(UserEvent.USER_REGISTERED, newUser.getUid()));
            } else {
                userAdapter.confirmRegistration(newUser.getUid());
                result.setCode(HttpAdapter.SC_CREATED);
                //fire event to send "welcome" email
                handle(new UserEvent(UserEvent.USER_REG_CONFIRMED, newUser.getUid()));
            }
            result.setData(newUser.getUid());
        } catch (UserException e) {
            result.setCode(HttpAdapter.SC_BAD_REQUEST);
        }
        return result;
    }

    @HttpAdapterHook(adapterName = "UserService", requestMethod = "PUT")
    public Object userUpdate(Event event) {
        RequestObject request = event.getRequest();
        String uid = request.pathExt;
        StandardResult result = new StandardResult();
        if (uid == null) {
            result.setCode(HttpAdapter.SC_BAD_REQUEST);
            return result;
        }
        try {
            //User user = (User) database.get("users", uid);
            User user = userAdapter.get(uid);
            String email = event.getRequestParameter("email");
            String type = event.getRequestParameter("type");
            String role = event.getRequestParameter("role");
            String password = event.getRequestParameter("password");
            if (email != null) {
                user.setEmail(email);
            }
            //if (type != null) {
            //    user.setType(type);
            //}
            if (role != null) {
                user.setRole(role);
            }
            if (password != null) {
                user.setPassword(password);
            }
            userAdapter.modify(user);
            //fire event
            handle(new UserEvent(UserEvent.USER_UPDATED, user.getUid()));
            result.setCode(HttpAdapter.SC_OK);
            result.setData(user);
        } catch (UserException e) {
            result.setCode(HttpAdapter.SC_BAD_REQUEST);
        }
        return result;
    }

    /**
     * Set user as waiting for removal
     *
     * @param event
     * @return
     */
    @HttpAdapterHook(adapterName = "UserService", requestMethod = "DELETE")
    public Object userDelete(Event event) {
        RequestObject request = event.getRequest();
        String uid = request.pathExt;
        StandardResult result = new StandardResult();
        if (uid == null) {
            result.setCode(HttpAdapter.SC_BAD_REQUEST);
            return result;
        }
        try {
            userAdapter.remove(uid);
            if (((String) getProperties().getOrDefault("user-confirm", "false")).equalsIgnoreCase("true")) {
                // fire event
                result.setCode(HttpAdapter.SC_ACCEPTED);
                handle(new UserEvent(UserEvent.USER_DEL_SHEDULED, uid));
            } else {
                userAdapter.confirmRemove(uid);
                // fire event
                handle(new UserEvent(UserEvent.USER_DELETED, uid));
                result.setCode(HttpAdapter.SC_OK);
            }
            result.setData(uid);
        } catch (UserException e) {
            result.setCode(HttpAdapter.SC_BAD_REQUEST);
        }
        return result;
    }

    @HttpAdapterHook(adapterName = "AuthService", requestMethod = "POST")
    public Object authLogin(Event event) {
        StandardResult result = new StandardResult();
        result.setCode(HttpAdapter.SC_FORBIDDEN);
        result.setData("authorization required");

        String authData = event.getRequest().headers.getFirst("Authentication");
        handle(Event.logFine("apiLogin", "authData=" + authData));
        try {
            String[] s = authData.split(" ");
            if (s[0].equalsIgnoreCase("Basic")) {
                String authPair = new String(Base64.getDecoder().decode(s[1]));
                while (authPair.endsWith("\r") || authPair.endsWith("\n")) {
                    authPair = authPair.substring(0, authPair.length() - 1);
                }
                s = authPair.split(":");
                handle(Event.logFine("apiLogin", "authPair=" + authPair));
                Token token = authAdapter.login(s[0], s[1]);
                if (token != null) {
                    result.setData(token.getToken());
                    result.setCode(HttpAdapter.SC_OK);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            handle(Event.logInfo(this.getClass().getSimpleName(), e.getMessage()));
        }
        return result;
    }

    @HttpAdapterHook(adapterName = "AuthService", requestMethod = "DELETE")
    public Object authLogout(Event event) {
        RequestObject request = event.getRequest();
        String token = request.pathExt;
        StandardResult result = new StandardResult();
        //TODO: invalidate token and remove from the token database
        return result;
    }

    @HttpAdapterHook(adapterName = "AuthService", requestMethod = "GET")
    public Object authCheck(Event event) {
        RequestObject request = event.getRequest();
        String tokenValue = request.pathExt;
        StandardResult result = new StandardResult();
        result.setCode(HttpAdapter.SC_FORBIDDEN);
        try {
            if(authAdapter.checkToken(tokenValue)){
                result.setCode(HttpAdapter.SC_OK);
            }
        } catch (AuthException ex) {
            Kernel.handle(Event.logFine(this.getClass().getSimpleName(), ex.getMessage()));
        }
        return result;
    }


    @HttpAdapterHook(adapterName = "echo", requestMethod = "*")
    public Object doGetEcho(Event requestEvent) {
        return sendEcho(requestEvent.getRequest());
    }

    @EventHook(eventCategory = Event.CATEGORY_LOG)
    public void logEvent(Event event) {
        logAdapter.log(event);
    }

    @EventHook(eventCategory = Event.CATEGORY_HTTP_LOG)
    public void logHttpEvent(Event event) {
        logAdapter.log(event);
    }

    @EventHook(eventCategory = UserEvent.CATEGORY_USER)
    public void processUserEvent(Event event) {
        if (event.getTimePoint() != null) {
            scheduler.handleEvent(event);
            return;
        }
        //TODO: all events should be send to the relevant queue
        switch(event.getType()) {
            case UserEvent.USER_DEL_SHEDULED:   //TODO: send confirmation email
            case UserEvent.USER_DELETED:        //TODO: update user
            case UserEvent.USER_REGISTERED:     //TODO: send confirmation email
            case UserEvent.USER_REG_CONFIRMED:  //TODO: update user
            case UserEvent.USER_UPDATED:
            default:
                handleEvent(Event.logInfo(this.getClass().getSimpleName(), "Event recived: "+event.getType()));
                System.out.println(event.toString());
                break;
        }
    }
    
    @EventHook(eventCategory = "*")
    public void processEvent(Event event) {
        if (event.getTimePoint() != null) {
            scheduler.handleEvent(event);
        } else {
            handleEvent(Event.logInfo("Don't know how to handle category " + event.getCategory(), event.getPayload().toString()));
        }
    }

    public Object sendEcho(RequestObject request) {
        StandardResult r = new StandardResult();
        r.setCode(HttpAdapter.SC_OK);
        try {
            if (!httpAdapter.isSilent()) {
                // with echo counter
                Long counter;
                counter = (Long) database.get("counters", "counter", new Long(0));
                counter++;
                database.put("counters", "counter", counter);
                HashMap<String, Object> data = new HashMap<>(request.parameters);
                data.put("service.uuid", getUuid().toString());
                data.put("request.method", request.method);
                data.put("request.pathExt", request.pathExt);
                data.put("echo.counter", database.get("counters", "counter"));
                if (data.containsKey("error")) {
                    int errCode = HttpAdapter.SC_INTERNAL_SERVER_ERROR;
                    try {
                        errCode = Integer.parseInt((String) data.get("error"));
                    } catch (Exception e) {
                    }
                    r.setCode(errCode);
                    data.put("error", "error forced by request");
                }
                r.setData(data);
            }
        } catch (Exception e) {
            handle(Event.logSevere(this.getClass().getSimpleName(), e.getMessage()));
            r.setCode(500);
        }
        return r;
    }
}
