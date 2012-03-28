package edu.harvard.mcz.imagecapture.data;

// Generated Feb 5, 2009 5:22:31 PM by Hibernate Tools 3.2.2.GA

/**
 * Users, modified from generated class.
 */
public class Users implements java.io.Serializable {

	private static final long serialVersionUID = -5258394845638721810L;

    /**
     * All rights, including editing/creating users (equivalent to DBA)
     */
    public static final String ROLE_ADMINISTRATOR = "Administrator";

	/*
	 * Specialist able to mark records as specialist reviewed, plus able to
	 * create/edit/promote/demote users.
	 *
	 */
	public static final String ROLE_CHIEF_EDITOR = "Chief Editor";

	/**
	 * Specialist able to mark records as specialist reviewed.
	 */
	public static final String ROLE_EDITOR = "Editor";

    /**
     * All of data entry rights, plus quality control and preprocessing.
     */

    public static final String ROLE_FULL = "Full access";

    /**
     * Search/Browse/Edit specimen records only.
     */
    public static final String ROLE_DATAENTRY = "Data entry";
	
	public static final String[] ROLES = { ROLE_ADMINISTRATOR, ROLE_CHIEF_EDITOR, ROLE_EDITOR, ROLE_FULL, ROLE_DATAENTRY };
	
	private Integer userid;
	private String username;
	private String fullname;
	private String description;
	private String role;
	private String hash;
	
	/** A text description suitable for error messages that describes
	 * the rules implemented in testProposedPassword()
	 * 
	 * @see Users.testProposedPassword()
	 */
	public static String PASSWORD_RULES_MESSAGE = "A password must be at least 11 characters long and contain at least one number, at least one lowercase letter, and at least one upper case letter";
	
	/**
	 * Test a password against password complexity rules for the user password.  The 
	 * rules implemented here should match the rules described in PASSWORD_RULES_MESSAGE
	 * 
	 * @param proposal a string that has been proposed as a new password for user.
	 * @return true if password matches rules, false if password does not.
	 * @see Users.PASSWORD_RULES_MESSAGE;
	 */
	public static boolean testProposedPassword(String proposal, String username) {
		boolean result = false;
		if (proposal.length() > 10) { 
			if (proposal.matches(".*[0-9].*")) { 
			   if (proposal.matches(".*[A-Z].*")) { 
			       if (proposal.matches(".*[a-z].*")) { 
			    	   if (!proposal.equals(username)) { 
			               result = true;
			    	   }
			       }
			   }
			}
		}
		return result;
	}

	public Users() {
	}

	public Users(String username, String fullname, String role) {
		this.username = username;
		this.fullname = fullname;
		this.role = role;
	}

	public Users(String username, String fullname, String description,
			String role) {
		this.username = username;
		this.fullname = fullname;
		this.description = description;
		this.role = role;
	}

	public Integer getUserid() {
		return this.userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullname() {
		return this.fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
	/**
	 * Test whether a user has rights under a role.  Use UsersLifeCycle.isUserAdministrator() or
	 * UsersLifeCycle.isUserChiefEditor() to test if a particular user   
	 * has administrative rights instead of this method, as it validates the rights against
	 * an underlying Users database record, while this method only validates against an instance of 
	 * the Users class.  This isUserRole() test returns true if the user is a member of a more 
	 * privileged class of users, e.g. isUserRole(Users.ROLE_DATAENTRY) returns true for a user who
	 * is in role ROLE_FULL.
	 * 
	 * @see edu.harvard.mcz.imagecapture.data.UsersLifeCycle#isUserAdministrator
	 * @param aUserRole the role to test, one of ROLE_DATAENTRY, ROLE_FULL, or ROLE_ADMINISTRATOR.
	 * @return true if this User has rights under aUserRole.
	 */
	public boolean isUserRole(String aUserRole) { 
        boolean result = false;
        if (this.role.equals(aUserRole)) {
            // If equals, then user has this role.
            result = true;
        } else {
            // Check more inclusive roles.
            if (this.role.equals(Users.ROLE_ADMINISTRATOR)) {
                // Administrator can do anything.
                result = true;
            }
            if (this.role.equals(Users.ROLE_CHIEF_EDITOR) 
            		&& (aUserRole.equals(Users.ROLE_DATAENTRY) 
            				|| aUserRole.equals(Users.ROLE_FULL) 
            				|| aUserRole.equals(Users.ROLE_CHIEF_EDITOR) 
            				|| aUserRole.equals(Users.ROLE_EDITOR) 
            				)
            		) {
                // Role chief editor includes roles full access, editor and data entry.
                result = true;
            }
            if (this.role.equals(Users.ROLE_EDITOR) 
            		&& (aUserRole.equals(Users.ROLE_DATAENTRY) 
            				|| aUserRole.equals(Users.ROLE_FULL) 
            				|| aUserRole.equals(Users.ROLE_EDITOR)
            				)
            		) {
                // Role editor  includes roles full and data entry.
                result = true;
            }
            if (this.role.equals(Users.ROLE_FULL) && aUserRole.equals(Users.ROLE_DATAENTRY)) {
                // Role full includes role data entry.
                result = true;
            }
        }
        return result;
	}

}
