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
package org.cricketmsf.microsite.auth;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import org.cricketmsf.Event;
import org.cricketmsf.Kernel;
import org.cricketmsf.SecurityFilterResult;
import org.cricketmsf.microsite.out.auth.AuthAdapterIface;
import org.cricketmsf.microsite.out.auth.AuthException;

/**
 * This is default filter used to check required request conditions. Does
 * nothing. Could be used as a starting point to implement required filter.
 *
 * @author Grzegorz Skorupa <g.skorupa at gmail.com>
 */
public class SecurityFilter extends Filter {

    private int errorCode = 200;
    private String errorMessage = "";

    private String[] restrictedPost = null;
    private String[] restrictedPut = null;
    private String[] restrictedGet = null;
    private String[] restrictedDelete = null;

    private boolean authRequired = false;

    @Override
    public String description() {
        return "Default security filter";
    }

    private void initialize() {
        ArrayList<String> aPost = new ArrayList<>();
        ArrayList<String> aPut = new ArrayList<>();
        ArrayList<String> aGet = new ArrayList<>();
        ArrayList<String> aDelete = new ArrayList<>();

        String restr = (String) Kernel.getInstance().getProperties().getOrDefault("restricted-resources", "");
        if (!restr.isEmpty()) {
            String r[] = restr.split(" ");
            String tmpPath;
            String tmpMethod;
            for (String r1 : r) {
                String[] r2 = r1.split("\\@");
                tmpMethod = r2[0];
                tmpPath = r2[1];
                switch (tmpMethod) {
                    case "POST":
                        aPost.add(tmpPath);
                        authRequired = true;
                        break;
                    case "PUT":
                        aPut.add(tmpPath);
                        authRequired = true;
                        break;
                    case "GET":
                        aGet.add(tmpPath);
                        authRequired = true;
                        break;
                    case "DELETE":
                        aDelete.add(tmpPath);
                        authRequired = true;
                        break;
                }
            }
            if (aPost.size() > 0) {
                restrictedPost = new String[aPost.size()];
                restrictedPost = aPost.toArray(restrictedPost);
            } else {
                restrictedPost = new String[0];
            }
            if (aPut.size() > 0) {
                restrictedPut = new String[aPut.size()];
                restrictedPut = aPost.toArray(restrictedPut);
            } else {
                restrictedPut = new String[0];
            }
            if (aGet.size() > 0) {
                restrictedGet = new String[aGet.size()];
                restrictedGet = aGet.toArray(restrictedGet);
            } else {
                restrictedGet = new String[0];
            }
            if (aDelete.size() > 0) {
                restrictedDelete = new String[aDelete.size()];
                restrictedDelete = aDelete.toArray(restrictedDelete);
            } else {
                restrictedDelete = new String[0];
            }
        }
        /*
            for (String restrictedPost1 : restrictedPost) {
                System.out.println(restrictedPost1);
            }
         */
    }

    private boolean isRestrictedPath(String method, String path) {
        if (restrictedPost == null) {
            initialize();
        }
        if (authRequired) {
            switch (method) {
                case "GET":
                    if (restrictedGet != null) {
                        for (String restrictedGet1 : restrictedGet) {
                            if (path.startsWith(restrictedGet1)) {
                                return true;
                            }
                        }
                    }
                    break;
                case "POST":
                    if (restrictedPost != null) {
                        for (String restrictedPost1 : restrictedPost) {
                            if (path.startsWith(restrictedPost1)) {
                                return true;
                            }
                        }
                    }
                    break;
                case "PUT":
                    if (restrictedPut != null) {
                        for (String restrictedPut1 : restrictedPut) {
                            if (path.startsWith(restrictedPut1)) {
                                return true;
                            }
                        }
                    }
                    break;
                case "DELETE":
                    if (restrictedDelete != null) {
                        for (String restrictedDelete1 : restrictedDelete) {
                            if (path.startsWith(restrictedDelete1)) {
                                return true;
                            }
                        }
                    }
                    break;
            }
        }
        return false;
    }

    /**
     * Does request analysis
     *
     * @param exchange request object
     * @return
     */
    public SecurityFilterResult checkRequest(HttpExchange exchange) {
        // if we found problems analysing exchange object
        String path = exchange.getRequestURI().getPath();

        boolean authorizationNotRequired = true;
        try {
            authorizationNotRequired = !isRestrictedPath(exchange.getRequestMethod(), path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //boolean authorized = true;
        //if (authorizationRequired) {
        //    authorized = isTokenValid(exchange.getRequestHeaders().getFirst("Authorization"));
        //}
        SecurityFilterResult result = new SecurityFilterResult();
        if (!authorizationNotRequired && isTokenInvalid(exchange.getRequestHeaders().getFirst("Authorization"))) {
            result.code = 403; // FORBIDDEN
            result.message = "request blocket by security filter\r\n";
            Kernel.handle(Event.logInfo(this.getClass().getSimpleName(), "not authorized " + path));
        } else {
            result.code = 200;
            result.message = "";
        }
        return result;
    }

    private boolean isTokenInvalid(String token) {
        //ask dedicated adapter
        AuthAdapterIface authAdapter = (AuthAdapterIface) Kernel.getInstance().getAdaptersMap().getOrDefault("authAdapter", null);
        if (authAdapter != null) {
            try {
                return !authAdapter.checkToken(token);
            } catch (AuthException e) {
                e.printStackTrace();
                return true;
            }

        }
        return true;
    }

    @Override
    public void doFilter(HttpExchange exchange, Chain chain)
            throws IOException {
        SecurityFilterResult result = checkRequest(exchange);
        if (result.code != 200) {
            exchange.sendResponseHeaders(result.code, result.message.length());
            exchange.getResponseBody().write(result.message.getBytes());
            exchange.getResponseBody().close();
        } else {
            chain.doFilter(exchange);
        }
    }

}
