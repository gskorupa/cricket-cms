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

import java.util.Base64;

/**
 * Session token object
 * @author grzesk
 */
public class Token {

    private String tid;
    private String uid;
    private long timestamp;
    private long eofLife;
    private String token;

    public Token(String userID, long lifetime){
        timestamp = System.currentTimeMillis();
        eofLife = timestamp+lifetime;
        uid=userID;
        token=Base64.getEncoder().encodeToString((uid+":"+timestamp).getBytes());
    }
    
    public boolean isValid(){
        return eofLife-System.currentTimeMillis() > 0;
    }
    
    /**
     * Returns token ID
     * @return the tid
     */
    public String getTid() {
        return tid;
    }

    /**
     * Sets token IDthrow new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
     * @param tid the tid to set
     */
    public void setTid(String tid) {
        this.tid = tid;
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
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the eofLife
     */
    public long getEofLife() {
        return eofLife;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }
    
}
