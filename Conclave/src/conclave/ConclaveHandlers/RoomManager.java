/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import conclave.db.Room;
import conclave.model.ConferenceRoom;
import model.ConnectionsLog;
import conclave.model.TextRoom;
import conclaveinterfaces.IConclaveRoom;
import java.rmi.AlreadyBoundException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import util.Encryptor;

/**
 * This class can be likened to the AcccountManager, in that it persists objects
 * to a database, and validates room passwords using the hash method. This
 * manager is also responsible for maintaining a list of all the rooms and their
 * information, passing roomlistings to users. Users own this object as it
 * allows them to always be aware of any room connection they may be able to
 * connect to.
 *
 * @author BradleyW
 */
public class RoomManager {

    private ConnectionsLog roomConnections;
    private SecurityHandler sm;
    private HashMap<String, IConclaveRoom> hostedRooms;
    private final ArrayList<String> supportedRoomtypes = new ArrayList<>();
    private ArrayList<String> mutedUsers = new ArrayList<>();
    private static final Logger log = Logger.getLogger(RoomManager.class.getName());
    private static RoomManager instance;
    EntityManagerFactory emf;

    /**
     * Supported room types are initilized, for demonstration purposes, in the
     * constructor. New rooms that the manager could support must be added here
     * if any new rooms are developed.
     */
    private RoomManager() {
        roomConnections = new ConnectionsLog();
        sm = new SecurityHandler();
        supportedRoomtypes.add("ConferenceRoom");
        supportedRoomtypes.add("TextRoom");
        hostedRooms = new HashMap<>();
        emf = Persistence.createEntityManagerFactory("ConclavePU");
    }

    /**
     * As we want one unique instance of this object, we must employ a
     * singleton pattern.
     *
     * @return
     */
    public static RoomManager getInstance() {
        if (instance == null) {
            instance = new RoomManager();
        }
        return instance;
    }

    /**
     * This mounts an open room onto the server, that does not need a password
     * to enter. This type of room is not persisted to the database.
     *
     * @param roomname
     * @param roomType
     * @return
     * @throws RemoteException
     */
    public boolean mountOpenRoom(String roomname, int roomType) throws RemoteException {
        boolean ok = false;
        try {
            IConclaveRoom room = null;
            switch (String.valueOf(roomType)) {
                case "1":
                    room = new TextRoom(roomname);
                    break;
                case "2":
                    room = new ConferenceRoom(roomname);
                    break;
            }
            if (room != null && !isARoom(roomname)) {
                room.openRoom();
                hostedRooms.put(roomname, room);
                roomConnections.addConnection(roomname, room.getInfo());
                Registry reg = LocateRegistry.getRegistry(9807);
                reg.bind(room.getRoomName(), room);
                log.log(Level.INFO, "An open room {0} has been added to the registry", roomname);
                ok = true;
            }
        } catch (AlreadyBoundException e) {

        }
        return ok;
    }

    /**
     * Creates a room and persists it to the database, this room is not added to
     * the registry.
     *
     * @param roomname
     * @param password
     * @param roomType
     * @return
     * @throws RemoteException
     */
    public boolean createRoom(String roomname, String password, int roomType) throws RemoteException {
        boolean success = false;
        Room newPersistenceRoom = new Room();
        byte[] salt = new byte[16];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
        }
        try {
            if (!isARoom(roomname)) {
                String hashedPassword = Encryptor.hashPassword(password, salt);
                newPersistenceRoom.setHashedpassword(hashedPassword);
                newPersistenceRoom.setRoomname(roomname);
                newPersistenceRoom.setSalt(salt);
                newPersistenceRoom.setRoomtype(String.valueOf(roomType));
                EntityManager em = emf.createEntityManager();
                EntityTransaction et = em.getTransaction();
                et.begin();
                em.persist(newPersistenceRoom);
                et.commit();
                em.close();
                log.log(Level.INFO, "A new room {0} has been persisted to the DB", roomname);
                success = true;
            }
        } catch (RemoteException e) {
        }
        return success;
    }

    /**
     * This will load a room into the registry, (By roomname) from the
     * persistence database, and allow it to be accessed by users.
     *
     * @param roomname
     * @return
     * @throws RemoteException
     */
    public boolean loadRoom(String roomname) throws RemoteException {
        boolean success = false;
        try {
            if (isARoom(roomname)) {
                IConclaveRoom room = getConclaveRoom(roomname);
                Registry reg = LocateRegistry.getRegistry(9807);
                reg.bind(room.getRoomName(), room);
                room.openRoom();
                roomConnections.addConnection(roomname, room.getInfo());
                hostedRooms.put(roomname, room);
                success = true;
                log.log(Level.INFO, "A persisted room: {0} has been loaded to the registry", roomname);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }
        return success;
    }

    /**
     * this will list all the rooms on the registry in the form of a returned String.
     * This is useful for bug fixing.
     * @return 
     */
    public String listAllRooms() {
        String returnString = "";
        try {
            Registry reg = LocateRegistry.getRegistry(9807);
            String[] rooms = reg.list();
            for (String room : rooms) {
                returnString+= room;
            }
        } catch (RemoteException e) {

        }
        return returnString;
    }

    /**This will return a Connectionlog of all active rooms, this enables users to
     * get an up-to date log of all rooms when they join the server, or leave a room.
     * Updates to already connected users are done elsewhere.
     * 
     * @return
     * @throws RemoteException 
     */
    public ConnectionsLog returnRooms() throws RemoteException {
        return roomConnections;
    }

    /**
     * Returns a room based on a roomname, from the persistence database.
     * It will also assign a specific room implementation to the object it passes.
     * When implementing a new room, care must be made and the new type added to
     * the switch.
     * 
     * @param roomname
     * @return
     * @throws RemoteException 
     */
    public IConclaveRoom getConclaveRoom(String roomname) throws RemoteException {
        IConclaveRoom returnRoom = null;
        if (hostedRooms.get(roomname) != null) {
            returnRoom = hostedRooms.get(roomname);
        } else {
            EntityManager manager = emf.createEntityManager();
            Query query = manager.createNamedQuery("Room.findAll");
            List<Room> rooms = query.getResultList();
            for (Room room : rooms) {
                String tmpRoomName = room.getRoomname();
                if (tmpRoomName.equals(roomname)) {
                    String roomType = room.getRoomtype();
                    switch (roomType) {
                        case "1":
                            returnRoom = new TextRoom(roomname);
                            break;
                        case "2":
                            returnRoom = new ConferenceRoom(roomname);
                            break;
                    }
                }
            }
        }
        return returnRoom;
    }

    /**
     * Returns a boolean based on if the username is in the RoomManager muted users list
     * @param username
     * @return
     * @throws RemoteException 
     */
    public boolean isMuted(String username) throws RemoteException {
        boolean mutedStatus = false;
        if (mutedUsers.contains(username)) {
            mutedStatus = true;
        }
        return mutedStatus;
    }

    /** Kicks a user from a room. The banned flag is depreciated, but
     * may still be implemented.
     * 
     * @param username
     * @param banned
     * @throws RemoteException 
     */
    public void kickUser(String username, boolean banned) throws RemoteException {
        for (IConclaveRoom croom : hostedRooms.values()) {
            if (croom.hasUser(username)) {
                croom.kickUser(username);
            }
        }
    }

    /**Unmutes a user by a username from the mutedUser array.
     * 
     * @param username
     * @throws RemoteException 
     */
    public void uncensorUser(String username) throws RemoteException {
        if (mutedUsers.contains(username))
        {
            mutedUsers.remove(username);
            for (IConclaveRoom croom : hostedRooms.values()) {
                croom.uncensorUser(username);
            }
        }
    }

    /**Mutes a user by a username
     * 
     * @param username
     * @throws RemoteException 
     */
    public void censorUser(String username) throws RemoteException {
        mutedUsers.add(username);
        for (IConclaveRoom croom : hostedRooms.values()) {
            croom.addCensoredUser(username);
        }
    }

    /**
     * If the room exists, returns true.
     * 
     * @param roomname
     * @return
     * @throws RemoteException 
     */
    public boolean isARoom(String roomname) throws RemoteException {
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNamedQuery("Room.findAll");
        List<Room> rooms = query.getResultList();
        for (Room room : rooms) {
            String tmpRoomName = room.getRoomname();
            if (tmpRoomName.equals(roomname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a room from the Connections Log.
     * 
     * @param roomname
     * @return
     * @throws RemoteException 
     */
    public boolean deleteRoom(String roomname) throws RemoteException {
        boolean successful = false;
        //JPA code
            roomConnections.removeConnection(roomname);
        return successful;
    }

    /**
     * Stops a room, and removes it from the connections log aswell as from the
     * registry.
     * 
     * @param roomname
     * @throws RemoteException 
     */
    public void stopRoom(String roomname) throws RemoteException {
        if (isARoom(roomname) && hostedRooms.containsKey(roomname)) {
            IConclaveRoom room = getConclaveRoom(roomname);
            room.stopRoom();
            roomConnections.removeConnection(roomname);
            hostedRooms.remove(roomname);
            log.log(Level.INFO, "Room: {0} has been stopped", roomname);
        }
    }

    /**
     * Returns true if the specified roomname has a password.
     * @param roomname
     * @return
     * @throws RemoteException 
     */
    public boolean hasPassword(String roomname) throws RemoteException {
        boolean has = false;
        if (isARoom(roomname)) {
            Room room = getRoom(roomname);
            if (room.getHashedpassword() != null) {
                has = true;
            }
        }
        return has;
    }

    /**
     * Validates a room bassed on a roomname and a password. This password
     * is hashed and salted against the roomEntry password. Returns true
     * if a correct password, and false for bad password.
     * 
     * @param roomname
     * @param password
     * @return
     * @throws RemoteException 
     */
    public boolean valdiateRoom(String roomname, String password) throws RemoteException {
        boolean validated = false;
        if (isARoom(roomname) && hasPassword(roomname)) {
            Room room = getRoom(roomname);
            byte[] salt = room.getSalt();
            String enteredHashedPassword = Encryptor.hashPassword(password, salt);
            if (room.getHashedpassword().equals(enteredHashedPassword)) {
                validated = true;
            } else {
            }
        }
        return validated;
    }

    /**
     * Gets a room from the persistence database based on a roomname.
     * 
     * @param roomname
     * @return
     * @throws RemoteException 
     */
    public Room getRoom(String roomname) throws RemoteException {
        Room returnedRoom = null;
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNamedQuery("Room.findByRoomname");
        query.setParameter("roomname", roomname);
        List<Room> rooms = query.getResultList();
        for (Room room : rooms) {
            String tmpRoomName = room.getRoomname();
            if (tmpRoomName.equals(roomname)) {
                returnedRoom = room;
            }

        }
        manager.close();
        return returnedRoom;
    }
    
    /**
     * Returns a collection of all the roomnames.
     * @return 
     */
    public List<String> getAllRoomNames()
    {
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNamedQuery("Room.findAll");
        List<Room> rooms = query.getResultList();
        List<String> returnCollection = new ArrayList();
        for (Room room : rooms) {
            returnCollection.add(room.getRoomname());
        }
        return returnCollection;
    }

    /**
     * Sets a room open flag to true, it will also update the connections log.
     * 
     * @param roomname
     * @throws RemoteException 
     */
    public void openRoom(String roomname) throws RemoteException {
        IConclaveRoom room = hostedRooms.get(roomname);
        room.openRoom();
        roomConnections.removeConnection(roomname);
        roomConnections.addConnection(roomname, room.getInfo());
        log.log(Level.INFO, "Room: {0} has been opened", roomname);
    }

    /**
     * Closes a room based on a roomname. This room will not let any new users connect to it, and is used to
     * prevent interruptions to a meeting.
     * 
     * @param roomname
     * @throws RemoteException 
     */
    public void closeRoom(String roomname) throws RemoteException {
        IConclaveRoom room = hostedRooms.get(roomname);
        room.closeRoom();
        roomConnections.removeConnection(roomname);
        roomConnections.addConnection(roomname, room.getInfo());
        log.log(Level.INFO, "Room: {0} has been closed", roomname);
    }

    /**
     * Returns a collection of all the roomnames, this is used to generally
     * find out what rooms are available to try and join.
     * 
     * @return
     * @throws RemoteException 
     */
    public List<String> getAllLoadedRoomnames() throws RemoteException {
        return new ArrayList(hostedRooms.keySet());
    }
    

    /**
     * Finds out all the room types that this manager is capable of supporting.
     * @return
     * @throws RemoteException 
     */
    public List<String> getAllSupportedRoomTypes() throws RemoteException {
        return supportedRoomtypes;
    }

    public int roomsAmount(){
        return hostedRooms.size();
    }

    public void stopRooms(){
        for (IConclaveRoom room : hostedRooms.values())
        {
            try {
                UnicastRemoteObject.unexportObject(room, true);
            } catch (NoSuchObjectException ex) {
                Logger.getLogger(RoomManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
