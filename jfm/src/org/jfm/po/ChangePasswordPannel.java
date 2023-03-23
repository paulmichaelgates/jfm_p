/**
 * 
 */
package org.jfm.po;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.jfm.main.CommonConstants;
import org.jfm.main.LoginPannel;
import org.jfm.main.Salt;
import org.jfm.main.User;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.asu.ser335.jfm.RolesSingleton;
import edu.asu.ser335.jfm.SaltsSingleton;
import io.whitfin.siphash.SipHasher;
import edu.asu.ser335.jfm.UsersSingleton;


/**
 * @author Nikhil Hiremath
 *
 */
public class ChangePasswordPannel extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JLabel labelUsername = new JLabel("Enter username: ");
	private JLabel labelPassword = new JLabel("Enter password: ");
	private JLabel labelRole = new JLabel("Enter Role: ");
	private JLabel message;
	private JTextField textUsername = new JTextField(20);
	private JPasswordField fieldPassword = new JPasswordField(20);
	private JButton buttonChangePassword = new JButton("Submit");
	private JPanel newPanel;
	private JComboBox<String> roleList;
	private List<User> users;
	private List<Salt> salts;

	public ChangePasswordPannel() {
		// create a new panel with GridBagLayout manager
		newPanel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(10, 10, 10, 10);

		// add components to the panel
		// UserName
		constraints.gridx = 0;
		constraints.gridy = 0;
		newPanel.add(labelUsername, constraints);

		constraints.gridx = 1;
		newPanel.add(textUsername, constraints);

		constraints.gridx = 0;
		constraints.gridy = 1;

		// Password
		newPanel.add(labelPassword, constraints);

		constraints.gridx = 1;
		newPanel.add(fieldPassword, constraints);

		constraints.gridx = 0;
		constraints.gridy = 2;

		// Role
		newPanel.add(labelRole, constraints);
		constraints.gridx = 1;

		// drop down
		roleList = new JComboBox<String>(RolesSingleton.getRoleMapping().getDisplayRoles());

		// add to the parent container (e.g. a JFrame):
		newPanel.add(roleList, constraints);

		// System.out.println("Selected role: " + role);

		constraints.gridx = 0;
		constraints.gridy = 3;

		message = new JLabel();
		newPanel.add(message, constraints);
		constraints.gridx = 1;

		constraints.gridwidth = 3;
		constraints.anchor = GridBagConstraints.CENTER;
		newPanel.add(buttonChangePassword, constraints);

		// Adding the listeners to components..

		buttonChangePassword.addActionListener(this);

		// set border for the panel
		newPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Admin Panel"));

		// add the panel to this frame
		add(newPanel);

		pack();
		setLocationRelativeTo(null);
	}
	
	/**
	 * Attempts to update the password for the user
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// just for fun check that the underlying text fields have
		// been initialized
		if( textUsername == null || fieldPassword == null || roleList == null )
			{
			JOptionPane.showMessageDialog(this, "Error initializing text fields", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
			}

		String userName = textUsername.getText();
		// String userName = (String) roleList.getSelectedItem();
		String password = String.valueOf(fieldPassword.getPassword());
		
		String role = (String) roleList.getSelectedItem();
		
		// check if the username and password fields are empty
		if (userName.trim().isEmpty() || password.trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Username or Password cannot be empty", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// check if the user exists 
		try
			{
			if ( !( UsersSingleton.getUserRoleMapping().containsKey( userName ) ) )
				{
				// show a nice error message to the user
				JOptionPane.showMessageDialog(this, "User already exists", "Error",
						JOptionPane.ERROR_MESSAGE);

				return;
				}
			// ensure that the selected role matches what is in the user role mapping
			// from the user singleton
			if( !UsersSingleton.getUserRoleMapping().get( userName ).equals( role ) )
				{
				// show a nice error message to the user
				JOptionPane.showMessageDialog(this, "Role does not match user", "Error",
						JOptionPane.ERROR_MESSAGE);

				return;
				}
			}
		catch( Exception ex )
			{
			JOptionPane.showMessageDialog(this, "User does not exist", "Error",
					JOptionPane.ERROR_MESSAGE);
			}

		// if we made it here then the user exists, go ahead and generate a new password
		// for the user
		try
			{
			String salted_password = SaltsSingleton.createSaltedPassword( userName, password );

			// debug print the salted password
			System.out.println( "Salted password: " + salted_password );
			
			UsersSingleton.updatePassword( salted_password, userName );
			}
		catch( Exception ex )
			{
			ex.printStackTrace();

			// give a nice error message to the user
			JOptionPane.showMessageDialog(this, "Error updating password", "Error",
					JOptionPane.ERROR_MESSAGE);
			}

		
		
	}

}
