/**
 * 
 */
package edu.asu.ser335.jfm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.jfm.main.CommonConstants;
import org.jfm.main.Role;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Defines the roles of each user. Note that this is a singleton class. The
 * constructor is private and the only way to get an instance of this class is
 * through the getRoleMapping() method.
 * 
 * Other public methods are getDisplayRoles() and getPrivilegesForRole(String role)
 * which are used for displaying the roles in the UI and getting the privileges
 * for a given role respectively.
 * 
 * 
 * This class also loads the roles from the authorization.json file. Which defines
 * the role privileges matrices.
 * 
 * @author kevinagary
 *
 */
public final class RolesSingleton {
	private static Hashtable<String, String[]> rolePrivilegesMapping = new Hashtable<String, String[]>();
	private static RolesSingleton theRoles = null;

	/*
	 * This should be used if there are no roles already
	 * defined
	 */
	private RolesSingleton() {
		RolesSingleton.loadRoles();
	}

	/*
	 * User of this class should call this method to get the
	 * singleton instance of the RolesSingleton class. This
	 * will call default constructor if theRoles have not yet
	 * been defined.
	 * 
	 * A Sufficient condition for theRoles to be defined is
	 * that the authorization.json file has been loaded. i.e.,
	 * theRoles is not null.
	 */
	public static final RolesSingleton getRoleMapping() {
		if (theRoles == null) {
			theRoles = new RolesSingleton();
		}
		return theRoles;
	}

	/**
	 * getDisplayRoles
	 * @return List of roles to display in the UI
	 */
	public final String[] getDisplayRoles() {
		/*
		 * get the list of keys from the rolePrivilegesMapping. These are the
		 * roles.
		 */
		ArrayList<String> displayRoles = new ArrayList<String>();
		Enumeration<String> e = rolePrivilegesMapping.keys();
		String role = null;

		/*
		 * Print all of the roles to the console
		 */
		while (e.hasMoreElements()) {
			role = e.nextElement();
			System.out.println("Adding rike: " + role);
			displayRoles.add(role);
		}
		return displayRoles.toArray(new String[] {});
	}
	
	public final String[] getPrivilegesForRole(String role) {
		String[] rval = rolePrivilegesMapping.get(role);
		if (rval != null) {
			rval = rval.clone();
		}
		return rval;
	}
	
	// load authorization.json and create rolePrivilegesMapping.
	private static void loadRoles() {
		List<Role> userRole;

		try {
			/**
			 * Uses the jackson library to read the json file and map it to the
			 * Role class
			 */
			ObjectMapper mapper = new ObjectMapper();
			InputStream inputStream = new FileInputStream(new File(CommonConstants.AUTHORIZATION_FILE));
			TypeReference<List<Role>> typeReference = new TypeReference<List<Role>>() {};
			userRole = mapper.readValue(inputStream, typeReference);
			for (Role u : userRole) {
				rolePrivilegesMapping.put(u.getRole(), u.getPrivileges());
			}

			/*
			 * Debug print that the roles and privileges were loaded
			 */
			System.out.println("User roles and privileges loaded !!");
			System.out.println("rolePrivilegesMapping: " + RolesSingleton.rolePrivilegesMapping);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
