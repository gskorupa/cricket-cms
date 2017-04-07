/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cricketmsf.microsite.cms;

import java.util.HashMap;
import java.util.List;
import org.cricketmsf.Adapter;
import org.cricketmsf.Event;
import org.cricketmsf.out.OutboundAdapter;
import org.cricketmsf.out.db.KeyValueDBException;
import org.cricketmsf.out.db.KeyValueDBIface;
import org.cricketmsf.out.file.FileReaderAdapterIface;
import org.cricketmsf.out.log.LoggerAdapterIface;

/**
 *
 * @author greg
 */
public class Cms extends OutboundAdapter implements Adapter, CmsIface {

    public static int NOT_INITIALIZED = 0;
    public static int FAILED = 1;
    public static int OK = 3;

    KeyValueDBIface database = null;
    FileReaderAdapterIface fileReader = null;
    LoggerAdapterIface logger = null;
    int status = NOT_INITIALIZED;

    /**
     * We want to use configured adapters so we must provide these adapters
     */
    @Override
    public void initialize(Object databaseAdapter, Object fileReaderAdapter, Object loggerAdapter) throws CmsException {
        if (databaseAdapter instanceof KeyValueDBIface) {
            database = (KeyValueDBIface) databaseAdapter;
        } else {
            status = FAILED;
            throw new CmsException(CmsException.UNSUPPORTED_DB_ADAPTER);
        }
        if (fileReaderAdapter instanceof FileReaderAdapterIface) {
            fileReader = (FileReaderAdapterIface) fileReaderAdapter;
        } else {
            status = FAILED;
            throw new CmsException(CmsException.UNSUPPORTED_FILE_ADAPTER);
        }
        if (loggerAdapter instanceof LoggerAdapterIface) {
            logger = (LoggerAdapterIface) fileReaderAdapter;
        } else {
            status = FAILED;
            throw new CmsException(CmsException.UNSUPPORTED_LOGGER_ADAPTER);
        }
        // create tables
        try {
            database.addTable("cms-pub-pl", 1000, true);
        } catch (KeyValueDBException e) {
            logger.log(Event.logFine(this.getClass().getSimpleName(), e.getMessage()));
        }
        try {
            database.addTable("cms-wip-pl", 1000, true);
        } catch (KeyValueDBException e) {
            logger.log(Event.logFine(this.getClass().getSimpleName(), e.getMessage()));
        }
        try {
            database.addTable("cms-pub-en", 1000, true);
        } catch (KeyValueDBException e) {
            logger.log(Event.logFine(this.getClass().getSimpleName(), e.getMessage()));
        }
        try {
            database.addTable("cms-wip-en", 1000, true);
        } catch (KeyValueDBException e) {
            logger.log(Event.logFine(this.getClass().getSimpleName(), e.getMessage()));
        }
        try { // each document uid has list of comments
            database.addTable("cms-comments", 2000, true);
        } catch (KeyValueDBException e) {
            logger.log(Event.logFine(this.getClass().getSimpleName(), e.getMessage()));
        }
        try { // each unique tag has list of documents uids
            database.addTable("cms-tags", 100, true);
        } catch (KeyValueDBException e) {
            logger.log(Event.logFine(this.getClass().getSimpleName(), e.getMessage()));
        }
        status = OK;
    }

    @Override
    public Document getDocument(String uid, String language) throws CmsException {
        Document doc = null;
        if(!(language.equals("pl")||language.equals("en"))){
            throw new CmsException(CmsException.UNSUPPORTED_LANGUAGE);
        }
        try{
            return (Document)database.get("cms_pub_"+language, uid);
        }catch(KeyValueDBException e){
        }
        try{
            return (Document)database.get("cms_wip_"+language, uid);
        }catch(KeyValueDBException e){
        }
        return doc;
    }

    @Override
    public void addDocument(Document doc) throws CmsException {
        if(!(doc.language.equals("pl")||doc.language.equals("en"))){
            throw new CmsException(CmsException.UNSUPPORTED_LANGUAGE);
        }
        String tableName = "cms_"+(doc.published?"pub_":"wip_")+doc.language;
        try{
            database.put(tableName, doc.uid, doc);
        }catch(KeyValueDBException e){
            throw new CmsException(CmsException.DATABASE_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void updateDocument(Document doc) throws CmsException {
        //TODO: when status changes from wip to published then doc should be removed from wip table
        if(!(doc.language.equals("pl")||doc.language.equals("en"))){
            throw new CmsException(CmsException.UNSUPPORTED_LANGUAGE);
        }
        String tableName = "cms_"+(doc.published?"pub_":"wip_")+doc.language;
        try{
            database.put(tableName, doc.uid, doc);
        }catch(KeyValueDBException e){
            throw new CmsException(CmsException.DATABASE_EXCEPTION, e.getMessage());
        }
    }

    @Override
    public void removeDocument(String uid, String language) throws CmsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Document> findByPath(String path, String language) throws CmsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Document findByName(String path, String language) throws CmsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Document findByUrl(String path, String name, String language) throws CmsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Comment> getComments(String uid) throws CmsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addComment(String documentUid, Comment comment) throws CmsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void acceptComment(String documentUid, String commentUid) throws CmsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeComment(String documentUid, String commentUid) throws CmsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void destroy() {
        if (database != null) {
            database.stop();
        }
    }

    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        // no specific config is required

    }

}
