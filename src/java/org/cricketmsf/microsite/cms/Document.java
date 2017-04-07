/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cricketmsf.microsite.cms;

/**
 *
 * @author greg
 */
public class Document {
    public static int PAGE = 1;
    public static int ARTICLE = 2;
    public static int CODE = 3;
    public static int FILE = 4;
    
    String uid;
    int type;
    String name;
    String path;
    String title;
    String summary;
    String content;
    String[] tags;
    String language;
    boolean commentable;
    boolean published;
    DocumentMetadata metadata;
}
