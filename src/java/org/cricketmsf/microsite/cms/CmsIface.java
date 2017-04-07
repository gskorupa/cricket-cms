/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cricketmsf.microsite.cms;

import java.util.List;

/**
 *
 * @author greg
 */
public interface CmsIface {
    public void initialize(Object databaseAdapter, Object fileReaderAdapter, Object loggerAdapter) throws CmsException;
    public void destroy() throws CmsException;
    
    public Document getDocument(String uid, String language) throws CmsException;
    public void addDocument(Document doc) throws CmsException;
    public void updateDocument(Document doc) throws CmsException;
    public void removeDocument(String uid, String language) throws CmsException;
    public List<Document> findByPath(String path, String language) throws CmsException;
    public Document findByName(String path, String language) throws CmsException;
    public Document findByUrl(String path, String name, String language) throws CmsException;
    
    public List<Comment> getComments(String uid) throws CmsException;
    public void addComment(String documentUid, Comment comment) throws CmsException;
    public void acceptComment(String documentUid, String commentUid) throws CmsException;
    public void removeComment(String documentUid, String commentUid) throws CmsException;
}
