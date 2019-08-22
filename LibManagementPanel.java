

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;
import javax.swing.table.TableModel;


public class LibManagementPanel extends JPanel
{
  
	JComboBox<String>cBoxFilter, cBoxhidden;
	TextField hiddenTextField;
	JRadioButton viewBorrowerRadioBtn, viewBookRadioBtn;
	JTable table;
	private static DefaultComboBoxModel<String> modelForHiddenCboBox;
	public LibManagementPanel()
  {
  	super();
  	this.setLayout(new BorderLayout());
  	
  	//build filter panel
   JPanel filterPanel=new JPanel();
   filterPanel.setLayout(new GridLayout(2,2,5,5));
   viewBorrowerRadioBtn =new JRadioButton("View All Borrowers",true);
   viewBookRadioBtn =new JRadioButton("View Books By:",false);
   ButtonGroup btnGroup =new ButtonGroup();
   btnGroup.add(viewBookRadioBtn);
   btnGroup.add(viewBorrowerRadioBtn); 
   filterPanel.add(viewBookRadioBtn);
   filterPanel.add(viewBorrowerRadioBtn);
     
   //build combo box filter 
   String[] cBoxFilterStrings = {"All","Out On Loan","Subject","Author"};
   cBoxFilter = new JComboBox<String>(cBoxFilterStrings);  
 	 cBoxFilter.setSize(100, 100);
 	 cBoxFilter.setEnabled(false);
 	 cBoxFilter.setEditable(false);
 	 filterPanel.add(cBoxFilter);
  	 
 	 //build hidden combo-box for author/subject
 	 cBoxhidden = new JComboBox<String>();
   filterPanel.add(cBoxhidden);
   cBoxhidden.setVisible(false);  
   
 	 //build display Books/Borrowers area
   //construct the JTable using the passed in TableModel parameter
 		table = new JTable();
 		
 		//add table to a JScrollPane
 		JScrollPane scrollPane = new JScrollPane(table);
 	 
 	 //build the Display button 
  	JButton displayBtn=new JButton("Display");
  	
  	this.add(filterPanel,BorderLayout.NORTH);
  	this.add(scrollPane,BorderLayout.CENTER);
  	this.add(displayBtn,BorderLayout.SOUTH);
  	
  	//Register listener for radio button
  	viewBorrowerRadioBtn.addItemListener(new JRadioButtonListener("borrower"));
  	viewBookRadioBtn.addItemListener(new JRadioButtonListener("book"));
  	//Register listener for filter combo box
  	cBoxFilter.addActionListener(new FilterComboBoxListener());
  	//Register listener for Display button
  	displayBtn.addActionListener(new DisplayBtnListener());
  	
  	//set visibility
  	this.setVisible(true);
  }
	
	//Handle Display button Event
	private class DisplayBtnListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if(viewBorrowerRadioBtn.isSelected()) // view all the borrowers
			{
				String allBorrowerQuery = "SELECT Last_name,First_name,Borrower_Email FROM Borrower";
				table.setModel(LibManagementPanel.getTableData(allBorrowerQuery));
			}
			else if(viewBookRadioBtn.isSelected() && cBoxFilter.getSelectedItem().toString().equals("All"))//view all books
			{
				String allBookQuery = "SELECT Title,ISBN,Edition_number,Subject FROM Book";
				table.setModel(LibManagementPanel.getTableData(allBookQuery));
			}
			else if(viewBookRadioBtn.isSelected() && cBoxFilter.getSelectedItem().toString().equals("Out On Loan")) // view books on loan
			{
				String bookOnLoanQuery = "SELECT CONCAT(br.Last_Name,', ', br.First_Name) AS 'Borrower FullName', b.Title FROM Book b INNER JOIN"
						                  + " Book_Loan bl ON b.BookID = bl.Book_BookID INNER JOIN Borrower br"
						                  + " ON bl.Borrower_Borrower_ID = br.Borrower_ID WHERE Available = 0";
				table.setModel(LibManagementPanel.getTableData(bookOnLoanQuery));
			}
			else if(viewBookRadioBtn.isSelected() && cBoxFilter.getSelectedItem().toString().equals("Subject"))// view books by subjects
			{
				String selectedSubject = cBoxhidden.getSelectedItem().toString();
				String bookBySubjectQuery = "SELECT Title,ISBN,Edition_number FROM Book WHERE Subject ='" + selectedSubject + "'";
				table.setModel(LibManagementPanel.getTableData(bookBySubjectQuery));
			}
			else if(viewBookRadioBtn.isSelected() && cBoxFilter.getSelectedItem().toString().equals("Author"))//view books by author
			{
				String[] selectedAuthor = cBoxhidden.getSelectedItem().toString().split(",");
				String bookByAuthorQuery = "SELECT b.Title,b.ISBN,b.Edition_number FROM Book b INNER JOIN Book_Author ba ON b.BookID = ba.Book_BookID"
						                       +" INNER JOIN Author a ON ba.Author_AuthorID = a.AuthorID WHERE a.Last_name ='" + selectedAuthor[0] + 
						                       "' AND a.First_name='" + selectedAuthor[1] + "'";
				table.setModel(LibManagementPanel.getTableData(bookByAuthorQuery));
			}
		}
		
	}
	//handle the Filter combo box  listener
	private class FilterComboBoxListener implements ActionListener{
		@Override
	  
		public void actionPerformed(ActionEvent e)
		{
			JComboBox<?> comboBox=(JComboBox<?>)e.getSource();
			
			if(comboBox.getSelectedItem().toString().equals("Subject"))//select subject
			{				
				cBoxhidden.removeAllItems();
				modelForHiddenCboBox = LibManagementPanel.getComboBoxData("subject");//show hidden combo box that allows user to select subject
				for(int i=0; i<modelForHiddenCboBox.getSize();i++) 
				{
					cBoxhidden.addItem(modelForHiddenCboBox.getElementAt(i));
				}
				cBoxhidden.setVisible(true);
			}
			else if(comboBox.getSelectedItem().toString().equals("Author"))//select author
			{
				cBoxhidden.removeAllItems();
				modelForHiddenCboBox = LibManagementPanel.getComboBoxData("author");//show hidden combo box that allows user to select author
				for(int i=0; i<modelForHiddenCboBox.getSize();i++) 
				{
					cBoxhidden.addItem(modelForHiddenCboBox.getElementAt(i));
				}
				cBoxhidden.setVisible(true);
				
			}
			else {
				cBoxhidden.setVisible(false);
			}
			
		}
	}
  private class JRadioButtonListener implements ItemListener
  {
    String type;
  	JRadioButtonListener(String s)
  	{
  		this.type=s;
  	}
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			if(type.equals("book"))
			{
				cBoxFilter.setEnabled(true);
				if(cBoxFilter.getSelectedItem().toString().equals("Subject") || cBoxFilter.getSelectedItem().toString().equals("Author")) 
				{
				 //hiddenTextField.setVisible(true);
				 cBoxhidden.setVisible(true);
				}
				else {
					//hiddenTextField.setVisible(false);
					cBoxhidden.setVisible(false);
				}
			}
			else {
				cBoxFilter.setEnabled(false);
				//hiddenTextField.setVisible(false);
				cBoxhidden.setVisible(false);
			}
			
		}
  	
  }

  public static TableModel getTableData(String query)
  {
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRslt = null;
		TableModel model = null;
	try
	{
		myConn = DriverManager.getConnection(
														"jdbc:mysql://localhost:3306/INFO3136_Books?useSSL=false&allowPublicKeyRetrieval=true", 
										        "root","password");		
		myStmt = myConn.createStatement();
		myRslt = myStmt.executeQuery(query);
		model = DbUtils.resultSetToTableModel(myRslt);	
	}//end try
	catch(SQLException ex)
	{
		System.out.println("SQL Exception, message is: " + ex.getMessage());
	}
	catch(Exception ex)
	{
		System.out.println("Some other Exception, message is: " + ex.getMessage());
	}
	finally
	{
		try
		{
			if(myRslt != null)
			  myRslt.close();
			if(myStmt != null)
			  myStmt.close();
			if(myConn != null)
			  myConn.close();	
		}//end try
		catch(SQLException ex)
		{
			System.out.println("SQLException INSIDE finally block: "+ ex.getMessage());
			ex.printStackTrace();
		}
	}//end finally
	return model;
  }
  //get default data for combo box
  public static DefaultComboBoxModel<String> getComboBoxData(String comboBoxName)
	{
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRslt = null;
		DefaultComboBoxModel<String> model =null;
		try
		{
			myConn = DriverManager.getConnection(
						         "jdbc:mysql://localhost:3306/INFO3136_Books?useSSL=false&allowPublicKeyRetrieval=true", 
						                       "root","password");		
			myStmt = myConn.createStatement();
			if(comboBoxName.equals("author"))
			{
				myRslt = myStmt.executeQuery("SELECT * FROM Author");
			}
			else if(comboBoxName.equals("subject"))
			{
				myRslt = myStmt.executeQuery("SELECT * FROM Book");
			}
		
			model=BuildComboBoxUtility.resultSetToDefaultComboBoxModel(myRslt,comboBoxName);
		}//end try
		catch(SQLException ex)
		{
			System.out.println("SQL Exception, message is: " + ex.getMessage());
		}
		catch(Exception ex)
		{
			System.out.println("Some other Exception, message is: " + ex.getMessage());
		}
		finally
		{
			try
			{
				if(myRslt != null)
				  myRslt.close();
				if(myStmt != null)
					myStmt.close();
				if(myConn != null)
				  myConn.close();	
			}//end try
			catch(SQLException ex)
			{
				System.out.println("SQLException INSIDE finally block: "+ ex.getMessage());
				ex.printStackTrace();
			}
		}//end finally
		return model;
	}//end method
}
//end class