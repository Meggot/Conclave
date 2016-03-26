/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conclave.rmiPolicy;
import java.net.URL;
import java.rmi.RMISecurityManager;

/**
 *
 * @author dhb
 */
public class RMISecurityPolicyLoader {

    private static boolean loaded = false;
    public static void LoadDefaultPolicy()
    {
        LoadPolicy("RMISecurity.policy");
    }
    
    public static void LoadPolicy(String policy) {
        if (loaded) {    return;     }
        loaded = true;
        ClassLoader cl = RMISecurityPolicyLoader.class.getClassLoader();
        URL url = cl.getResource(policy) ;
        if (url == null) {
            System.out.println("Policy not found null (Have you included it in the jar file? (Don't forget to clean and build))");
        } else {
            System.out.println("Policy found");
        }
        System.out.println(url.toString());
        System.setProperty("java.security.policy", url.toString());

        if (System.getSecurityManager() == null) {
            System.out.println("Creating security policy");
            System.setSecurityManager(new RMISecurityManager());
        }

    }

    private RMISecurityPolicyLoader() {
    }
}
