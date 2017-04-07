/*
 * Copyright 2015 Grzegorz Skorupa <g.skorupa at gmail.com>.
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

import org.cricketmsf.Event;
import org.cricketmsf.Kernel;
import org.cricketmsf.RequestObject;
import java.util.HashMap;
import java.util.Random;
import org.cricketmsf.annotation.EventHook;
import org.cricketmsf.annotation.HttpAdapterHook;
import org.cricketmsf.in.http.EchoHttpAdapterIface;
import org.cricketmsf.in.http.HtmlGenAdapterIface;
import org.cricketmsf.in.http.HttpAdapter;
import org.cricketmsf.in.http.ParameterMapResult;
import org.cricketmsf.in.http.StandardResult;
import org.cricketmsf.in.scheduler.SchedulerIface;
import org.cricketmsf.microsite.auth.Token;
import org.cricketmsf.microsite.cms.CmsException;
import org.cricketmsf.microsite.cms.CmsIface;
import org.cricketmsf.microsite.user.HashMaker;
import org.cricketmsf.microsite.user.User;
import org.cricketmsf.out.db.KeyValueDBException;
import org.cricketmsf.out.db.KeyValueDBIface;
import org.cricketmsf.out.file.FileReaderAdapterIface;
import org.cricketmsf.out.log.LoggerAdapterIface;

/**
 * EchoService
 *
 * @author greg
 */
public class Service extends Kernel {

    // adapterClasses
    LoggerAdapterIface logAdapter = null;
    EchoHttpAdapterIface httpAdapter = null;
    KeyValueDBIface database = null;
    SchedulerIface scheduler = null;
    HtmlGenAdapterIface htmlAdapter = null;
    FileReaderAdapterIface fileReader = null;
    // optional
    // we don't need to register input adapters:
    // UserApi
    KeyValueDBIface cmsDatabase = null;
    FileReaderAdapterIface cmsFileReader = null;
    CmsIface cms = null;

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
        cmsDatabase = (KeyValueDBIface) getRegistered("database");
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
            //TODO: configurable admin account
            String newUid = HashMaker.md5Java("cricketrulez" + "@" + "g.skorupa@gmail.com");
            String newSecret = HashMaker.md5Java("g.skorupa@gmail.com" + "@" + "cricketrulez");
            User newUser = new User();
            newUser.setUid(newUid);
            newUser.setSecretHash(newSecret);
            newUser.setEmail("g.skorupa@gmail.com");
            newUser.setType(User.OWNER);
            newUser.setRole("admin");
            newUser.setPassword("test123");
            Random r = new Random(System.currentTimeMillis());
            newUser.setConfirmString("" + r.nextLong());
            // TODO: change when confirmation method is ready
            newUser.setConfirmed(true);
            database.put("users", newUid, newUser);
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
            User u = (User) database.get("users", uid);
            result.setData(u);
        } catch (KeyValueDBException e) {
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
            String newUid = HashMaker.md5Java(event.getRequestParameter("secret") + "@" + event.getRequestParameter("email"));
            String newSecret = HashMaker.md5Java(event.getRequestParameter("secret") + "@" + newUid);
            User newUser = new User();
            newUser.setUid(newUid);
            newUser.setSecretHash(newSecret);
            newUser.setEmail(event.getRequestParameter("email"));
            newUser.setType(User.USER);
            newUser.setRole("");
            newUser.setPassword(event.getRequestParameter("password"));
            Random r = new Random(System.currentTimeMillis());
            newUser.setConfirmString("" + r.nextLong());
            // TODO: change when confirmation method is ready
            if (((String) getProperties().getOrDefault("user-confirm", "false")).equalsIgnoreCase("true")) {
                newUser.setConfirmed(false);
                result.setCode(HttpAdapter.SC_ACCEPTED);
            } else {
                newUser.setConfirmed(true);
                result.setCode(HttpAdapter.SC_CREATED);
            }
            database.put("users", newUid, newUser);
            result.setData(newUser.getUid());
        } catch (KeyValueDBException e) {
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
            User user = (User) database.get("users", uid);
            String email = event.getRequestParameter("email");
            String type = event.getRequestParameter("type");
            String role = event.getRequestParameter("role");
            String password = event.getRequestParameter("password");
            if (email != null) {
                user.setEmail(event.getRequestParameter("email"));
            }
            if (type != null) {
                user.setType(User.USER);
            }
            if (role != null) {
                user.setRole("");
            }
            if (password != null) {
                user.setPassword(event.getRequestParameter("password"));
            }
            database.put("users", uid, user);
            result.setCode(HttpAdapter.SC_OK);
            result.setData(user);
        } catch (KeyValueDBException e) {
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
            User user = (User) database.get("users", uid);
            user.setUnregisterRequested(true);
            Random r = new Random(System.currentTimeMillis());
            user.setConfirmString("" + r.nextLong());
            //TODO: send confirmation link or remove user without confirmation - depends on config
            if (((String) getProperties().getOrDefault("user-confirm", "false")).equalsIgnoreCase("true")) {
                database.put("users", uid, user);
                result.setCode(HttpAdapter.SC_ACCEPTED);
            } else {
                database.remove("users", uid);
                result.setCode(HttpAdapter.SC_OK);
            }
            result.setData(user.getUid());
        } catch (KeyValueDBException e) {
            result.setCode(HttpAdapter.SC_BAD_REQUEST);
        }
        return result;
    }

    public Token tokenGet(String tokenId) {
        try {
            Token t = (Token) database.get("tokens", tokenId);
            return t;
        } catch (KeyValueDBException e) {
            handle(Event.logInfo(getClass().getSimpleName(), e.getMessage()));
            return null;
        }
    }

    public boolean tokenRemove(String tokenId) {
        try {
            database.remove("tokens", tokenId);
            return true;
        } catch (KeyValueDBException e) {
            handle(Event.logFinest(getClass().getSimpleName(), e.getMessage()));
            return false;
        }
    }

    public void tokenAdd(Token token) {
        try {
            database.put("tokens", token.getTid(), token);
        } catch (KeyValueDBException e) {
            handle(Event.logWarning(getClass().getSimpleName(), e.getMessage()));
        }
    }

    /*
    public void cleanTokens(long timeout) {
        try {
            storage.cleanTokens(timeout);
        } catch (StorageError e) {
            handle(Event.logWarning(getClass().getSimpleName(), e.getMessage()));
        }
    }
     */
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
