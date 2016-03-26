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
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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

/**
 *
 * @author BradleyW
 */
public class RoomManager {

    private ConnectionsLog roomConnections;
    private SecurityManager sm;
    private HashMap<String, IConclaveRoom> hostedRooms;
    private final ArrayList<String> supportedRoomtypes = new ArrayList<>();
    private ArrayList<String> mutedUsers = new ArrayList<>();
    private static final Logger log = Logger.getLogger( RoomManager.class.getName() );
    private static RoomManager instance;
    EntityManagerFactory emf;

    private RoomManager() {
        roomConnections = new ConnectionsLog();
        sm = SecurityManager.getInstance();
        supportedRoomtypes.add("ConferenceRoom");
        supportedRoomtypes.add("TextRoom");
        hostedRooms = new HashMap<>();
        emf = Persistence.createEntityManagerFactory("ConclavePU");
    }

    public static RoomManager getInstance() {
        if (instance == null) {
            instance = new RoomManager();
        }
        return instance;
    }

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
            if (room != null) {
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
                String hashedPassword = sm.hashPassword(password, salt);
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
                emf.close();
                log.log(Level.INFO, "A new room {0} has been persisted to the DB", roomname);
                success = true;
            }
        } catch (RemoteException e) {

        }
        return success;
    }

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
    
    public void listAllRooms() {
        try {
            Registry reg = LocateRegistry.getRegistry(9807);
            String[] rooms = reg.list();
            for (String room : rooms) {
                System.out.println(room);
            }
        } catch (RemoteException e) {

        }
    }

    public ConnectionsLog returnRooms() throws RemoteException {
        return roomConnections;
    }


    public IConclaveRoom getConclaveRoom(String roomname) throws RemoteException {
        IConclaveRoom returnRoom = null;
        if (hostedRooms.get(roomname) != null) {
            returnRoom = hostedRooms.get(roomname);
        } else {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConclavePU");
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
    

    public boolean isMuted(String username) throws RemoteException
    {
        boolean mutedStatus = false;
        if (mutedUsers.contains(username))
        {
            mutedStatus = true;
        }
        return mutedStatus;
    }


    public void kickUser(String username, boolean banned) throws RemoteException {
        for (IConclaveRoom croom : hostedRooms.values())
        {
            if (croom.hasUser(username))
            {
                croom.kickUser(username);
            }
        }
    }
    

    public void uncensorUser(String username) throws RemoteException {
        mutedUsers.remove(username);
        for (IConclaveRoom croom : hostedRooms.values())
        {
            croom.uncensorUser(username);
        }
    }
    

    public void censorUser(String username) throws RemoteException {
        mutedUsers.add(username);
        for (IConclaveRoom croom : hostedRooms.values())
        {
            croom.addCensoredUser(username);
        }
    }


    public boolean isARoom(String roomname) throws RemoteException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConclavePU");
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


    public boolean deleteRoom(String roomname) throws RemoteException {
        boolean successful = false;
        //JPA code
        roomConnections.removeConnection(roomname);
        return successful;
    }


    public void stopRoom(String roomname) throws RemoteException {
        if (isARoom(roomname) && hostedRooms.containsKey(roomname)) {
            IConclaveRoom room = getConclaveRoom(roomname);
            room.stopRoom();
            roomConnections.removeConnection(roomname);
            hostedRooms.remove(roomname);
            log.log(Level.INFO, "Room: {0} has been stopped", roomname);
        }
    }


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


    public boolean valdiateRoom(String roomname, String password) throws RemoteException {
        boolean validated = false;
        if (isARoom(roomname) && hasPassword(roomname)) {
            Room room = getRoom(roomname);
            byte[] salt = room.getSalt();
            String enteredHashedPassword = sm.hashPassword(password, salt);
            if (room.getHashedpassword().equals(enteredHashedPassword)) {
                validated = true;
            }
        }
        return validated;
    }


    public Room getRoom(String roomname) throws RemoteException {
        Room returnedRoom = null;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("ConclavePU");
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNamedQuery("Room.findAll");
        List<Room> rooms = query.getResultList();
        for (Room room : rooms) {
            String tmpRoomName = room.getRoomname();
            if (tmpRoomName.equals(roomname)) {
                returnedRoom = room;
            }

        }
        return returnedRoom;
    }


    public void openRoom(String roomname) throws RemoteException {
        IConclaveRoom room = hostedRooms.get(roomname);
        room.openRoom();
        roomConnections.removeConnection(roomname);
        roomConnections.addConnection(roomname, room.getInfo());
        log.log(Level.INFO, "Room: {0} has been opened", roomname);
    }


    public void closeRoom(String roomname) throws RemoteException {
        IConclaveRoom room = hostedRooms.get(roomname);
        room.closeRoom();
        roomConnections.removeConnection(roomname);
        roomConnections.addConnection(roomname, room.getInfo());
        log.log(Level.INFO, "Room: {0} has been closed", roomname);
    }
    
    
    

    public List<String> getAllRoomnames() throws RemoteException {
        return new ArrayList(hostedRooms.keySet());
    }
    

    public List<String> getAllSupportedRoomTypes() throws RemoteException {
        return supportedRoomtypes;
    }
    
}
