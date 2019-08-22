
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.*;

public class RegisterPanelController extends JPanel
{

	static JPanel authorMorePanel=new JPanel();
	static Integer index = 1;
	static List<JLabel> listNumAuthors = new ArrayList<JLabel>();
	static List<JLabel> listAuthorFNameLabels = new ArrayList<JLabel>();;
	static List<JLabel> listAuthorLNameLabels = new ArrayList<JLabel>();;
	static List<JTextField> listAuthorFNameField = new ArrayList<JTextField>();;
	static List<JTextField> listAuthorLNameField = new ArrayList<JTextField>();

	private UserModel userModel = new UserModel();
	private BookModel bookModel = new BookModel();
	private ActionListener listener = new ButtonListener();
	// Listener related components
	private JTextField borrowerFNameTextField=new JTextField(20);
	private JTextField borrowerLNameTextField=new JTextField(20);
	private JTextField emailTextField=new JTextField(20);
	private JTextField titleTextField=new JTextField(20);
	private JTextField ISBNTextField=new JTextField(3);
	private JTextField editionTextField=new JTextField(20);
	private JTextField subjectTextField=new JTextField(20);
	private JTextField authorFNameField=new JTextField(20);
	private JTextField authorLNameField=new JTextField(20);

	private JButton addBorrowerBtn=new JButton("Register Borrower");
	private JButton addMoreAuthorBtn=new JButton("Click here to add more authors");
	private JButton addBookBtn=new JButton("Register Book");



	RegisterBookView viewer = null;


	public RegisterPanelController() throws HeadlessException
	{
		super();
		this.setLayout(new BorderLayout());

		// Create Adding Borrower Panel
		JPanel borrowerAddPanel=new JPanel();
		borrowerAddPanel.setLayout(new BorderLayout(0,20));

		JPanel borrowerInfoPanel=new JPanel();
		borrowerInfoPanel.setLayout(new GridLayout(3,2,10,10));
		JLabel firstNameLabel=new JLabel("First Name:");
		JLabel lastNameLabel=new JLabel("Last Name:");
		JLabel emailLabel=new JLabel("Email:");
		JLabel emptyLabel=new JLabel("");

		borrowerInfoPanel.add(firstNameLabel);
		borrowerInfoPanel.add(borrowerFNameTextField);
		borrowerInfoPanel.add(lastNameLabel);
		borrowerInfoPanel.add(borrowerLNameTextField);
		borrowerInfoPanel.add(emailLabel);
		borrowerInfoPanel.add(emailTextField);

		borrowerAddPanel.add(borrowerInfoPanel,BorderLayout.CENTER);
		borrowerAddPanel.add(addBorrowerBtn,BorderLayout.SOUTH);
		addBorrowerBtn.addActionListener(listener);

		this.add(borrowerAddPanel,BorderLayout.NORTH);

		// Create Adding Book Panel
		JPanel bookAddPanel=new JPanel();
		bookAddPanel.setLayout(new BorderLayout(0,0));

		JPanel bookInfoPanel=new JPanel();
		bookInfoPanel.setLayout(new GridLayout(7,2,10,10));
		JLabel titleLabel=new JLabel("Title:");
		JLabel ISBNLabel=new JLabel("ISBN:");
		JLabel editionLabel=new JLabel("Edition Number:");
		JLabel subjectLabel=new JLabel("Subject:");
		JLabel authorLabel=new JLabel("Author 1");
		JLabel authorFName=new JLabel("First Name:");
		JLabel authorLName=new JLabel("Last Name");

		bookInfoPanel.add(titleLabel);
		bookInfoPanel.add(titleTextField);
		bookInfoPanel.add(ISBNLabel);
		bookInfoPanel.add(ISBNTextField);
		bookInfoPanel.add(editionLabel);
		bookInfoPanel.add(editionTextField);
		bookInfoPanel.add(subjectLabel);
		bookInfoPanel.add(subjectTextField);
		bookInfoPanel.add(authorLabel);
		bookInfoPanel.add(emptyLabel);
		bookInfoPanel.add(authorFName);
		bookInfoPanel.add(authorFNameField);
		bookInfoPanel.add(authorLName);
		bookInfoPanel.add(authorLNameField);

		bookAddPanel.add(bookInfoPanel,BorderLayout.NORTH);

		// Create Co-Author Panel
		authorMorePanel.setLayout(new GridBagLayout());
		bookAddPanel.add(authorMorePanel,BorderLayout.CENTER);

		// Create Adding Book Button Panel
		JPanel BookAddButtonPanel = new JPanel();
		BookAddButtonPanel.setLayout(new GridLayout(1,2));
		BookAddButtonPanel.add(addMoreAuthorBtn);
		addMoreAuthorBtn.addActionListener(listener);
		BookAddButtonPanel.add(addBookBtn);
		addBookBtn.addActionListener(listener);

		this.add(BookAddButtonPanel,BorderLayout.SOUTH);
		//add panel to a JScrollPane
		JScrollPane scrollPane = new JScrollPane(bookAddPanel);
		//add scrollPane to frame
		this.add(scrollPane);
		//set Visibility
		this.setVisible(true);
	}

	class ButtonListener implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{

			// When Clicking the button to add more co-author
			if(e.getActionCommand().equals("Click here to add more authors"))
			{

				authorMorePanel.removeAll();

				// Create label and text field
				JTextField authorFNameJTextField = new JTextField();
				authorFNameJTextField.setSize(100, 200);
				JTextField authorlNameJTextField = new JTextField();
				authorlNameJTextField.setSize(100, 200);
				listAuthorFNameField.add(authorFNameJTextField);
				listAuthorFNameLabels.add(new JLabel("First Name:"));
				listAuthorLNameField.add(authorlNameJTextField);
				listAuthorLNameLabels.add(new JLabel("Last Name:"));
				listNumAuthors.add(new JLabel("Author" + (index + 1)));

				// Create constraints
				GridBagConstraints NumAuthorsLabelConstraints = new GridBagConstraints();
				GridBagConstraints FNameFieldConstraints = new GridBagConstraints();
				GridBagConstraints LNameFieldConstraints = new GridBagConstraints();
				GridBagConstraints FNameLabelConstraints = new GridBagConstraints();
				GridBagConstraints LNameLabelConstraints = new GridBagConstraints();

				int currentGridY = -1;

				// Add labels and text fields
				for(int i = 0; i < index; i++)
				{
					++currentGridY;
					NumAuthorsLabelConstraints.gridx = 0;
					NumAuthorsLabelConstraints.gridy = currentGridY;
					NumAuthorsLabelConstraints.insets = new Insets(10, 0, 5, 20);

					++currentGridY;
					// Text field constraints
					FNameFieldConstraints.gridx = 1;
					FNameFieldConstraints.fill = GridBagConstraints.BOTH;
					FNameFieldConstraints.weightx = 0.5;
					FNameFieldConstraints.insets = new Insets(5, 170, 5, 0);
					FNameFieldConstraints.gridy = currentGridY;

					// Label constraints
					FNameLabelConstraints.gridx = 0;
					FNameLabelConstraints.gridy = currentGridY;
					FNameLabelConstraints.insets = new Insets(5, 0, 5, 0);

					++currentGridY;

					// Text field constraints
					LNameFieldConstraints.gridx = 1;
					LNameFieldConstraints.fill = GridBagConstraints.BOTH;
					LNameFieldConstraints.weightx = 0.5;
					LNameFieldConstraints.insets = new Insets(5, 170, 5, 0);
					LNameFieldConstraints.gridy = currentGridY;

					// Label constraints
					LNameLabelConstraints.gridx = 0;
					LNameLabelConstraints.gridy = currentGridY;
					LNameLabelConstraints.insets = new Insets(5, 0, 5, 0);

					// Add them to panel
					authorMorePanel.add(listNumAuthors.get(i), NumAuthorsLabelConstraints);
					authorMorePanel.add(listAuthorFNameLabels.get(i), FNameLabelConstraints);
					authorMorePanel.add(listAuthorFNameField.get(i), FNameFieldConstraints);
					authorMorePanel.add(listAuthorLNameLabels.get(i), LNameLabelConstraints);
					authorMorePanel.add(listAuthorLNameField.get(i), LNameFieldConstraints);

				}

				int tempGridY = currentGridY + 1;
				// Align components top-to-bottom
				GridBagConstraints c = new GridBagConstraints();
				c.gridx = 0;
				c.gridy = tempGridY;
				c.weighty = 1;
				authorMorePanel.add(new JLabel(), c);

				// Increment indexer
				index++;
				authorMorePanel.updateUI();
			}
			// When Clicking the button to add a new user
			else if(e.getActionCommand().equals("Register Borrower"))
			{

				JTextField[] textFields = { borrowerFNameTextField, borrowerLNameTextField, emailTextField};
				boolean isInputValid = true;

				// To indicate which field is blanked
				for (int i = 0; i < textFields.length; i++)
				{
					JTextField jTextField = textFields[i];
					String textValue = jTextField.getText().trim();

					if (textValue.length() == 0)
					{
						jTextField.setBackground(Color.YELLOW);
						isInputValid = false;
					}
				}

				boolean validFName = false, validLName = false, validEmail = false;

				if(isInputValid)
				{
					for (int i = 0; i < textFields.length; i++) {
						JTextField jTextField = textFields[i];
						jTextField.setBackground(Color.WHITE);

					}

					if(isValidName(borrowerFNameTextField.getText()))
					{
						borrowerFNameTextField.setBackground(Color.WHITE);
						validFName = true;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "First Name should not be over 45 characters!");
						clearInvalidTextFiled(borrowerFNameTextField);
					}

					if(isValidName(borrowerLNameTextField.getText()))
					{
						borrowerLNameTextField.setBackground(Color.WHITE);
						validLName = true;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Last Name should not be over 45 characters!");
						clearInvalidTextFiled(borrowerLNameTextField);
					}

					if(isValidEmailAddress(emailTextField.getText()))
					{
						emailTextField.setBackground(Color.WHITE);
						validEmail = true;

					}
					else
					{
						clearInvalidTextFiled(emailTextField);
					}

					if(validEmail && validFName && validLName)
					{
						// Add the data to the model
						try
						{
							userModel.addUserElement(borrowerFNameTextField.getText(), borrowerLNameTextField.getText(), emailTextField.getText());
							JOptionPane.showMessageDialog(null, "Successfully registered!");

							// Reset
							borrowerFNameTextField.setText("");
							borrowerLNameTextField.setText("");
							emailTextField.setText("");
						}
						catch (SQLException ex)
						{
							System.out.println(ex.getMessage() );
						}
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "All fields are required!");
				}

			}
			// When Clicking the button to add a new Book
			else if(e.getActionCommand().equals("Register Book"))
			{
				JTextField[] textFields = { titleTextField, ISBNTextField, editionTextField, subjectTextField, authorFNameField, authorLNameField};
				boolean isInputValid = true;

				// To indicate which field is blanked
				for (int i = 0; i < textFields.length; i++)
				{
					JTextField jTextField = textFields[i];
					String textValue = jTextField.getText().trim();

					if (textValue.length() == 0)
					{
						jTextField.setBackground(Color.YELLOW);
						isInputValid = false;
					}
				}

				boolean validBookTitle = false, validISBN = false, validEdition = false, validSubject = false, validFName = false, validLName = false, validAddedFName = false, validAddedLName = false, validMoreAuthorFiled = true;

				if(isInputValid)
				{
					for (int i = 0; i < textFields.length; i++) {
						JTextField jTextField = textFields[i];
						jTextField.setBackground(Color.WHITE);
					}

					if(isValidBookTitle(titleTextField.getText()))
					{
						titleTextField.setBackground(Color.WHITE);
						validBookTitle = true;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Title should not be over 255 characters!");
						clearInvalidTextFiled(titleTextField);
					}

					if(isValidISBN(ISBNTextField.getText()))
					{
						ISBNTextField.setBackground(Color.WHITE);
						validISBN = true;
					}
					else
					{
						clearInvalidTextFiled(ISBNTextField);
					}

					if(isValidEditionNum(editionTextField.getText()))
					{
						editionTextField.setBackground(Color.WHITE);
						validEdition = true;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Edition number should be integer, maximum 3 characters!");
						clearInvalidTextFiled(editionTextField);
					}

					if(isValidBookSubject(subjectTextField.getText()))
					{
						subjectTextField.setBackground(Color.WHITE);
						validSubject = true;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Title should not be over 45 characters!");
						clearInvalidTextFiled(subjectTextField);
					}

					if(isValidName(authorFNameField.getText()))
					{
						authorFNameField.setBackground(Color.WHITE);
						validFName = true;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "First Name should not be over 45 characters!");
						clearInvalidTextFiled(authorFNameField);
					}

					if(isValidName(authorLNameField.getText()))
					{
						authorLNameField.setBackground(Color.WHITE);
						validLName = true;
					}
					else
					{
						JOptionPane.showMessageDialog(null, "Last Name should not be over 45 characters!");
						clearInvalidTextFiled(authorLNameField);
					}

					// Validation for co-author's text field
					if(listAuthorFNameField.size() !=0 || listAuthorLNameField.size() != 0)
					{
						validMoreAuthorFiled = false;

						boolean isInputListValid = true;

						for (int i = 0; i < index-1; i++) {


							JTextField jFNTextField = listAuthorFNameField.get(i);
							jFNTextField.setBackground(Color.WHITE);
							JTextField jLNTextField = listAuthorLNameField.get(i);
							jLNTextField.setBackground(Color.WHITE);

							String textFNValue = jFNTextField.getText().trim();
							String textLNValue = jLNTextField.getText().trim();

							if (textFNValue.length() == 0)
							{
								clearInvalidTextFiled(jFNTextField);
								isInputListValid = false;
							}

							if (textLNValue.length() == 0)
							{
								clearInvalidTextFiled(jLNTextField);
								isInputListValid = false;
							}

						}

						if(isInputListValid)
						{

							for (int i = 0; i < index-1; i++) {
								JTextField jFNTextField = listAuthorFNameField.get(i);
								jFNTextField.setBackground(Color.WHITE);
								JTextField jLNTextField = listAuthorLNameField.get(i);
								jLNTextField.setBackground(Color.WHITE);

								if(isValidName(listAuthorFNameField.get(i).getText()))
								{
									listAuthorFNameField.get(i).setBackground(Color.WHITE);
									validAddedFName = true;
								}
								else
								{
									JOptionPane.showMessageDialog(null, "First Name should not be over 45 characters!");
									clearInvalidTextFiled(listAuthorFNameField.get(i));
									validAddedFName = false;
								}

								if(isValidName(listAuthorLNameField.get(i).getText()))
								{
									listAuthorLNameField.get(i).setBackground(Color.WHITE);
									validAddedLName = true;
								}
								else
								{
									JOptionPane.showMessageDialog(null, "Last Name should not be over 45 characters!");
									clearInvalidTextFiled(listAuthorLNameField.get(i));
									validAddedLName = false;
								}

							}

							if(validAddedFName && validAddedLName)
								validMoreAuthorFiled = true;
						}
						else
						{
							JOptionPane.showMessageDialog(null, "More Author's name should be filled!");
						}

					}


					// If all information is correct
					if(validBookTitle && validISBN && validEdition && validSubject && validFName && validLName && validMoreAuthorFiled)
					{

						HashMap<String, String> authorList  = new HashMap<String, String>();

						// Add the first author to the list
						authorList.put(authorFNameField.getText(), authorLNameField.getText());

						// If there are more authors, add them to the same list
						if(listAuthorFNameField.size() != 0 && listAuthorLNameField.size() != 0) {
							for (int i = 0; i < index-1; i++) {

								String fName = listAuthorFNameField.get(i).getText();
								String lName = listAuthorLNameField.get(i).getText();
								authorList.put(fName, lName);
							}
						}

						// Add book information to the model
						try
						{
							bookModel.addBookElement(titleTextField.getText(), ISBNTextField.getText(), editionTextField.getText(), subjectTextField.getText(), authorList);


							viewer = new RegisterBookView(bookModel);


							// reset
							titleTextField.setText("");
							ISBNTextField.setText("");
							editionTextField.setText("");
							subjectTextField.setText("");
							authorFNameField.setText("");
							authorLNameField.setText("");


							authorMorePanel.removeAll();
							listNumAuthors = new ArrayList<JLabel>();
							listAuthorFNameLabels = new ArrayList<JLabel>();
							listAuthorLNameLabels = new ArrayList<JLabel>();
							listAuthorFNameField = new ArrayList<JTextField>();
							listAuthorLNameField = new ArrayList<JTextField>();

							index = 1;

						}
						catch (SQLException ex)
						{
							System.out.println(ex.getMessage() );
						}
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null, "All fields are required!");
				}
			}

		}


		public boolean isValidEmailAddress(String email)
		{

			String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
			java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
			java.util.regex.Matcher m = p.matcher(email);

			try {

				if(userModel.compareNewUserToDB(email))
				{
					JOptionPane.showMessageDialog(null, "Email you typed is already registered! Please use other email address!");
					return false;
				}

			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}


			if(m.matches() && email.length() <= 45)
				return true;
			else
			{
				JOptionPane.showMessageDialog(null, "Email's form is wrong!");
				return false;
			}

		}

		public boolean isValidBookTitle(String title)
		{
			if (title.length() <= 255)
				return true;
			else
				return false;
		}

		public boolean isValidISBN(String isbn)
		{
			// Must be a 13 digit ISBN
			try
			{

				if ( isbn.length() != 13)
				{
					JOptionPane.showMessageDialog(null, "ISBN should be 13 lengths");
					return false;
				}

				long isbnInt = Long.parseLong(isbn);

			}
			catch (NumberFormatException nfe)
			{
				JOptionPane.showMessageDialog(null, "Wrong format ISBN, only put numeric input");
				System.err.println("NumberFormat Exception: " + nfe.getMessage());
				return false;
			}

			// Must be unique, compare a new ISBN to one in the DB
			try {

				if(bookModel.compareNewISBNToDB(isbn))
				{
					JOptionPane.showMessageDialog(null, "ISBN you typed is already exist!");
					return false;
				}

			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}

			return true;

		}

		public boolean isValidEditionNum(String editNum)
		{
			boolean validNum = false;
			int tempEditNum;

			if(editNum.length() <= 3)
			{
				try
				{
					tempEditNum = Integer.parseInt(editNum);
					validNum = true;

				}
				catch (NumberFormatException nfe)
				{
					System.err.println("NumberFormat Exception: " + nfe.getMessage());
				}
			}

			return validNum;
		}

		public boolean isValidBookSubject(String subject)
		{
			if (subject.length() <= 45)
				return true;
			else
				return false;
		}

		public boolean isValidName(String name)
		{
			if(name.length() <= 45)
				return true;
			else
				return false;
		}

		public void clearInvalidTextFiled(JTextField fld)
		{
			fld.requestFocus();
			fld.setText("");
			fld.setBackground(Color.YELLOW);
		}

	}

}
//end class