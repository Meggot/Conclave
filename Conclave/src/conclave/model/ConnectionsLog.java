/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author BradleyW
 */
public class ConnectionsLog implements Serializable{
    
    private HashMap<String, ConnectionEntry> connections;
    
    public ConnectionsLog()
    {
        connections = new HashMap<>();
    }
    
    public String getConnectionsDisplay()
    {
        String returnString = "";
        int indexBuilder = 0;
        for (ConnectionEntry connect : connections.values())
        {
            returnString = returnString + 
                    indexBuilder + " " + connect.getName() +
                    "\n";
            indexBuilder++;
        }
        return returnString;
    }
    
    public ConnectionEntry getConnection(String connectionName)
    {
        return connections.get(connectionName);
    }
    
    public LinkedList<ConnectionEntry> getAllConnections()
    {
        return new LinkedList(connections.values());
    }
    
    public void removeConnection(String connectionName)
    {
        connections.remove(connectionName);
    }
    
    public void addConnection(String name, String desc)
    {
        ConnectionEntry newConnection = new ConnectionEntry(name, desc);
        connections.put(name, newConnection);
    }
    
    public ConnectionEntry getConnection(int connectionIndex)
    {
        int index = 0;
        for (ConnectionEntry conn : connections.values())
        {
            if (index==connectionIndex)
            {
                return conn;
            }
        }
        return null;
    }
    
    public String getConnectionName(int connectionIndex)
    {
        ConnectionEntry entry = getConnection(connectionIndex);
        return entry.getName();
    }
    
    public String getConnectionDetails(int connectionIndex)
    {
        ConnectionEntry entry = getConnection(connectionIndex);
        return entry.getDesc();
    }
    
    public int getConnectionsSize()
    {
        return connections.size();
    }

}
