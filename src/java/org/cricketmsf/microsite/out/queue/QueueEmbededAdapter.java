/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cricketmsf.microsite.out.queue;

import java.util.HashMap;
import java.util.List;
import org.cricketmsf.Adapter;
import org.cricketmsf.Event;
import org.cricketmsf.Kernel;
import org.cricketmsf.microsite.out.user.UserException;
import org.cricketmsf.out.OutboundAdapter;
import org.cricketmsf.out.db.KeyValueDBException;
import org.cricketmsf.out.db.KeyValueDBIface;

/**
 *
 * @author greg
 */
public class QueueEmbededAdapter extends OutboundAdapter implements Adapter, QueueAdapterIface {

    private String helperAdapterName;
    private KeyValueDBIface database = null;
    private boolean initialized = false;
    private int maxQueueSize = 1000;
    private boolean defaultPersistency = false;
    
    //TODO: configured max queue size
    //TODO: configured queue persistency 
    //TODO: removing paths/queues ?

    @Override
    public void loadProperties(HashMap<String, String> properties, String adapterName) {
        helperAdapterName = properties.get("helper-name");
        Kernel.getInstance().getLogger().print("\thelper-name: " + helperAdapterName);
        try {
            init(helperAdapterName);
        } catch (QueueException e) {
            e.printStackTrace();
            Kernel.handle(Event.logSevere(this.getClass().getSimpleName(), e.getMessage()));
        }
    }

    @Override
    public void init(String helperName) throws QueueException {
        try {
            database = (KeyValueDBIface) Kernel.getInstance().getAdaptersMap().get(helperName);
            initialized = true;
        } catch (Exception e) {
            throw new QueueException(UserException.HELPER_NOT_AVAILABLE, "helper adapter not available");
        }
    }

    @Override
    public List<Event> get(String path) throws QueueException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void send(String path, Event event) throws QueueException {
        // we can store event object without serializing
        // for other implementations an event serialization/deserialization shuld be implemented
        try {
            database.put(path, "" + event.getId(), event);
        } catch (KeyValueDBException e) {
            if (e.getCode() == KeyValueDBException.TABLE_NOT_EXISTS) {
                try {
                    database.addTable(path, maxQueueSize, defaultPersistency);
                    database.put(path, "" + event.getId(), event);
                } catch (KeyValueDBException e2) {
                    throw new QueueException(QueueException.UNKNOWN, e.getMessage());
                }
            }
        }
    }

}
