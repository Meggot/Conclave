/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.ConclaveHandlers;

import conclave.db.Account;
import java.net.ConnectException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import org.eclipse.persistence.config.QueryHints;
import util.Encryptor;

/**
 * This class will manage Account persistence and also validate passwords
 *
 * @author BradleyW
 */
public class AccountManager {

    private SecurityHandler sm;
    private static AccountManager instance;
    EntityManagerFactory emf;

    private AccountManager() {
        emf = Persistence.createEntityManagerFactory("ConclavePU");
    }
    
    public static AccountManager getInstance()
    {
        if (instance==null)
        {
            instance = new AccountManager();
        }
        return instance;
    }
    /**
     * Returns an Account object based on a userid
     *
     * @param userid
     * @return Account
     * @throws ConnectException
     */
    public Account getAccount(int userid) throws ConnectException {
        EntityManager manager = emf.createEntityManager();
        manager.setProperty(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.USE);
        Query query = manager.createNamedQuery("Account.findByUserid");
        query.setParameter("userid", userid);
        List<Account> accounts = query.getResultList();
        Account potAccount = accounts.get(0);
        if (potAccount != null) {
            return potAccount;
        }
        manager.close();
        return null;
    }

    /**
     * Adds an account using a username and a password. It will hash this
     * password and store it into the persistence database
     *
     * @param username
     * @param PTpassword
     * @throws ConnectException
     */
    public void addAccount(String username, String PTpassword) throws ConnectException {
        byte[] salt = new byte[16];
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.nextBytes(salt);
        } catch (NoSuchAlgorithmException e) {
        }
        if (!isAUser(username)) {
            String hashedPassword = Encryptor.hashPassword(PTpassword, salt);
            Account newAccount = new Account();
            newAccount.setHashedpassword(hashedPassword);
            newAccount.setUsername(username);
            newAccount.setSalt(salt);
            EntityManager em = emf.createEntityManager();
            em.setProperty(QueryHints.CACHE_STORE_MODE, CacheRetrieveMode.BYPASS);
            EntityTransaction et = em.getTransaction();
            et.begin();
            em.persist(newAccount);
            et.commit();
            em.close();
        }
    }

    /**
     * Gets a user object by using a username, this is a much more general
     * search of the database and allows a user to be found without knowing the
     * userid. As username uniquness is enforced, this is a perfectly acceptable
     * way to search the database
     *
     * @param username
     * @return Account
     * @throws ConnectException
     */
    /**
     *
     * @param username
     * @return
     * @throws ConnectException
     */
    public Account getUserByName(String username) throws ConnectException {
        Account returnedAccount = null;
        if (isAUser(username)) {
            EntityManager em = emf.createEntityManager();
            em.setProperty(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.USE);
            Query query = em.createNamedQuery("Account.findByUsername");
            query.setParameter("username", username);
            returnedAccount = (Account) query.getSingleResult();
            em.close();
        }
        return returnedAccount;
    }

    /**
     * Verifies a username and password, this is used to verify login details
     * before a ServerController initiates a UserInterface initilization and
     * adds it to the RMI Registry.
     *
     * @param iusername
     * @param ptPassword
     * @return
     * @throws ConnectException
     */
    public boolean verifyUser(String iusername, String ptPassword) throws ConnectException {
        boolean verified = false;
        Account returnedAccount = getUserByName(iusername);
        if (returnedAccount.getUsername().equals(iusername)) {
            String hashandSalt = returnedAccount.getHashedpassword();
            byte[] salt = returnedAccount.getSalt();
            String enteredHashedPassword = Encryptor.hashPassword(ptPassword, salt);
            if (enteredHashedPassword.equals(hashandSalt)) {
                verified = true;
                //System.out.println("User " + returnedAccount.getUsername() + " has logged in.");
            }
        }
        return verified;
    }

    /**
     * Verifies if a username exists in the database.
     *
     * @param username
     * @return
     * @throws ConnectException
     */
    public boolean isAUser(String username) throws ConnectException {
        EntityManager em = emf.createEntityManager();
        em.setProperty(QueryHints.CACHE_RETRIEVE_MODE, CacheRetrieveMode.USE);
        Query query = em.createNamedQuery("Account.findByUsername");
        query.setParameter("username", username);
        List<Account> accounts = query.getResultList();
        for (Account account : accounts) {
            String tempUsrName = account.getUsername();
            if (tempUsrName.equals(username)) {
                return true;
            }
        }
        em.close();
        return false;
    }

    /**
     * Lists all the users on a database, this method isn't used in Conclave,
     * but during development allowed testing and provides a primitive java
     * interface.
     *
     * @return
     * @throws ConnectException
     */
    public String listAllUsers() throws ConnectException {
        String allUsers = "";
        EntityManager manager = emf.createEntityManager();
        Query query = manager.createNamedQuery("Account.findAll");
        List<Account> accounts = query.getResultList();
        for (Account account : accounts) {
            allUsers = allUsers + account.toString();
        }
        manager.close();
        return allUsers;
    }
}
