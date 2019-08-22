
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Vector;
import java.util.regex.*;
import javax.swing.*;


public class BorrowReturnPanel extends JPanel
{
	JTextField dueDateTextField,borrowDateTextField,returnDateTextField,borrowPeriodTextField,ISBNTextField, commenTextField;
	JComboBox<String> cBoxBorrowPeriodBox,cBoxBorrower,cBoxBook;
	JRadioButton borrowBtn,returnBtn;
	private static DefaultComboBoxModel<String> modelBook,modelBorrower ;
	Date now = new Date();
  SimpleDateFormat sf = new SimpleDateFormat("YYYY-MM-dd"); 
	
	public BorrowReturnPanel()
  {
  	super();
  	this.setLayout(new BorderLayout());
  	
  	//build Radio Button Borrow/Return
  	JPanel radioBtnPanel =new JPanel();
  	radioBtnPanel.setLayout(new FlowLayout());
  	borrowBtn=new JRadioButton("Borrow",true);
  	returnBtn=new JRadioButton("Return",false);
  	radioBtnPanel.add(borrowBtn);
  	radioBtnPanel.add(returnBtn);
  	ButtonGroup radioBtnGroup=new ButtonGroup();
  	radioBtnGroup.add(borrowBtn);
  	radioBtnGroup.add(returnBtn);
  	this.add(radioBtnPanel,BorderLayout.NORTH);
  	
    //call the method that makes the connection, set default value for 2 combo box Borrower and Books
  	BorrowReturnPanel.makeConnection();
  	
  	// Build other fields
  	JPanel centerPanel =new JPanel();
  	centerPanel.setLayout(new GridLayout(8,2,10,30));
  	JLabel borrowerLabel =new JLabel("Borrower Name:");
  	JLabel bookLabel =new JLabel("Book:");
  	JLabel ISBNLabel =new JLabel("ISBN:");
  	JLabel borrowDateLabel =new JLabel("Borrowed Date:");
  	JLabel returnDateLabel =new JLabel("Returned Date:");
  	JLabel dateDueLabel =new JLabel("Due Date:");
  	JLabel borrowPeriodLabel =new JLabel("Borrowed Period (Weeks):");
  	JLabel commentLabel = new JLabel("Comment:");
  	
  	cBoxBorrower = new JComboBox<String>(modelBorrower);
  	cBoxBorrower.setEditable(false);
  	cBoxBorrower.setSize(100, 100);
  	cBoxBorrower.setRenderer(new MyComboBoxRenderer("Select a borrower"));
  	cBoxBorrower.setSelectedIndex(-1);
    cBoxBook = new JComboBox<String>(modelBook);
  	cBoxBook.setEditable(false);
  	cBoxBook.setSize(100, 100);
  	cBoxBook.setRenderer(new MyComboBoxRenderer("Select a book"));
  	cBoxBook.setSelectedIndex(-1);
  	
  	ISBNTextField =new JTextField();
  	borrowDateTextField =new JTextField();
  	//set current date by default for borrow Date field
    borrowDateTextField.setText(sf.format(now));
    
    returnDateTextField =new JTextField();
    String[] periods = {"1 week","2 weeks","3 weeks"};
    cBoxBorrowPeriodBox=new JComboBox<String>(periods);
    cBoxBorrowPeriodBox.setRenderer(new MyComboBoxRenderer("Select a borrowed period"));
  	cBoxBorrowPeriodBox.setSelectedIndex(-1);
  	
  	dueDateTextField=new JTextField();
  	dueDateTextField.setEditable(false);
  	ISBNTextField.setEditable(false);
  	returnDateTextField.setEditable(false); 	
  	commenTextField = new JTextField();

  	JButton saveBtn =new JButton("Save");
  	//Add components to center Panel
  	centerPanel.add(borrowerLabel);
  	centerPanel.add(cBoxBorrower);
  	centerPanel.add(bookLabel);
  	centerPanel.add(cBoxBook);
  	centerPanel.add(ISBNLabel);
  	centerPanel.add(ISBNTextField);
  	centerPanel.add(borrowDateLabel);
  	centerPanel.add(borrowDateTextField);
  	centerPanel.add(borrowPeriodLabel);
  	centerPanel.add(cBoxBorrowPeriodBox);
  	centerPanel.add(dateDueLabel);
  	centerPanel.add(dueDateTextField);
  	centerPanel.add(returnDateLabel);
  	centerPanel.add(returnDateTextField);
  	centerPanel.add(commentLabel);
  	centerPanel.add(commenTextField);
  	
  	this.add(centerPanel,BorderLayout.CENTER);
  	this.add(saveBtn,BorderLayout.SOUTH);
  	//Register Listener for Return/Borrow Radio Button
  	borrowBtn.addItemListener(new RadioButtonListener());
  	returnBtn.addItemListener(new RadioButtonListener());
  	
    //Register listener for filter combo box Borrower Name, Book and Borrowed Period
  	cBoxBorrower.addActionListener(new ComboBoxListener());
  	cBoxBorrower.setName("borrower");
  	cBoxBook.addActionListener(new ComboBoxListener());
  	cBoxBook.setName("book");
  	cBoxBorrowPeriodBox.addActionListener(new ComboBoxListener());
  	cBoxBorrowPeriodBox.setName("borrowPeriod");
  	//Register Listener for Save Button
  	saveBtn.addActionListener(new SaveButtonListener());
  	
  	//set visibility
  	this.setVisible(true);
  }
	// this allows to show the combo list by default with a title instead of a specific option in the combo list
	private class MyComboBoxRenderer extends JLabel implements ListCellRenderer
  {
      private String _title;

      public MyComboBoxRenderer(String title)
      {
          _title = title;
      }

      @Override
      public Component getListCellRendererComponent(JList list, Object value,
              int index, boolean isSelected, boolean hasFocus)
      {
          if (index == -1 && value == null) setText(_title);
          else setText(value.toString());
          return this;
      }
  }
	//this will handle combo box event listenert for both Borrower and Book
	private class ComboBoxListener implements ActionListener
	{@Override
		public void actionPerformed(ActionEvent e)
		{
			JComboBox<?> comboBox=(JComboBox<?>)e.getSource();
			if("book".equals(comboBox.getName()))
			{//get ISBN data when selecting book
				ISBNTextField.setText("");
				if(comboBox.getSelectedIndex() != -1) {
						String selectedBook = comboBox.getSelectedItem().toString();
						String iSBNQuery = "SELECT ISBN FROM Book Where Title=\"" + selectedBook +"\"";// TODO: handle the title has ' in the name
						Vector<String> isbn = BorrowReturnPanel.getData(iSBNQuery,"ISBN");
						ISBNTextField.setText(isbn.firstElement());
						//if the mode is return, then display the rest of info if the book is on loan
						if(returnBtn.isSelected())
						{
							String borrowDateQuery ="SELECT Date_out FROM Book_Loan bl Inner Join Book b On bl.Book_BookID = b.BookID Where b.Title=\"" + selectedBook +"\"";
							String dueDateQuery ="SELECT Date_due FROM Book_Loan bl Inner Join Book b On bl.Book_BookID = b.BookID Where b.Title=\"" + selectedBook +"\"";
							Vector<String> borrowDate = BorrowReturnPanel.getData(borrowDateQuery,"Date_out");
							Vector<String> dueDate = BorrowReturnPanel.getData(dueDateQuery,"Date_due");
							borrowDateTextField.setText(borrowDate.firstElement());
							dueDateTextField.setText(dueDate.firstElement());
						}
				}
			}
			else if("borrowPeriod".equals(comboBox.getName()))
			{
				//if the borrow Period has been selected, then due date will be calculated
				if(comboBox.getSelectedIndex()!= -1) {
					switch(comboBox.getSelectedItem().toString())
					{
					  case "1 week":
					  	dueDateTextField.setText(LocalDate.now().plusDays(7).toString());
					  	break;
					  case "2 weeks":
					  	dueDateTextField.setText(LocalDate.now().plusDays(14).toString());
					  	break;
					  case "3 weeks":
					  	dueDateTextField.setText(LocalDate.now().plusDays(21).toString());
					  	break; 	
					}
				}
			}
			else if("borrower".equals(comboBox.getName()) && returnBtn.isSelected())
			{
				if(comboBox.getSelectedIndex() != -1) 
				{
				//get selected BorrowerID
					String[] names =cBoxBorrower.getSelectedItem().toString().split(",");
					String selectedBorrowerQuery = "Select * From Borrower Where Last_name ='" + names[0] + "' And First_name='" + names[1] + "'";
					String selectedBorrowerID = BorrowReturnPanel.getData(selectedBorrowerQuery, "Borrower_ID").firstElement();
					String bookOnLoanQuery = "Select * from Book b Inner Join Book_Loan bl on b.BookID = bl.Book_BookID Where bl.Borrower_Borrower_ID ='" + selectedBorrowerID + "'" +
					                          "And b.Available = 0";// TODO: handle the title has ' in the name
					Vector<String> bookOnLoan = BorrowReturnPanel.getData(bookOnLoanQuery,"Title");
					cBoxBook.removeAllItems();
					for(int i=0; i<bookOnLoan.size(); i++)
					{
						cBoxBook.addItem(bookOnLoan.elementAt(i));
					}	
			  }
		  }
	  }
	}
	//This will handle events for Save Button
	private class SaveButtonListener implements ActionListener
	{@Override
		public void actionPerformed(ActionEvent e)
		{
		  boolean validInput = false;
			if(borrowBtn.isSelected())
			{
				String bookID,borrowerID,outDate,dueDate,comment;
				bookID=borrowerID=outDate=dueDate=comment="";
				
			//get selected BorrowerID
				String[] names =cBoxBorrower.getSelectedItem().toString().split(",");
				String selectedBorrowerQuery = "Select * From Borrower Where Last_name ='" + names[0] + "' And First_name='" + names[1] + "'";
				borrowerID = BorrowReturnPanel.getData(selectedBorrowerQuery, "Borrower_ID").firstElement();
			//get selected BookID
				String bookTitle = cBoxBook.getSelectedItem().toString();
				String selectedBookQuery = "Select * From Book Where Title =\"" + bookTitle + "\"";
				bookID = BorrowReturnPanel.getData(selectedBookQuery, "BookID").firstElement();
			//get comment
				comment = commenTextField.getText();
			//get Date_out and Date_due	
				outDate = borrowDateTextField.getText();
				if(!Pattern.matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))", outDate)) {
					JOptionPane.showMessageDialog(null, "Borrow Date format must be 'YYYY-MM-DD'");	
					borrowDateTextField.setText("");
					borrowDateTextField.requestFocus(true);
					validInput = false;
				}
				else
				{
					validInput = true;
				}
				dueDate = dueDateTextField.getText();
			//insert a new book loan into table
				if(validInput) 
				{
					String newBookLoanInsert = "INSERT INTO Book_Loan (Book_BookID, Borrower_Borrower_ID, Comment, Date_out, Date_due) " +
				                              " VALUES('" + bookID + "', '" + borrowerID + "', '" + comment + "', '" + outDate + "', '" + dueDate + "')";
					int insertVal = BorrowReturnPanel.insertOrUpdateData(newBookLoanInsert);
					String bookStatusUpdate = "UPDATE Book SET Available = 0 Where BookID = " + bookID;
					if(insertVal > 0) 
					{
						int updateVal = BorrowReturnPanel.insertOrUpdateData(bookStatusUpdate);
						cBoxBook.removeItemAt(cBoxBook.getSelectedIndex());
					}
					JOptionPane.showMessageDialog(null, insertVal + " book has been checked out!");			
					
					//clear all fields after check out successfully
					//cBoxBook.removeItemAt(cBoxBook.getSelectedIndex());
					cBoxBook.setSelectedIndex(-1);
					cBoxBorrower.setSelectedIndex(-1);
					ISBNTextField.setText("");
					borrowDateTextField.setText("");
					cBoxBorrowPeriodBox.setSelectedIndex(-1);
					dueDateTextField.setText("");	
					commenTextField.setText("");
				}
			}
			else if(returnBtn.isSelected())
			{
			//get selected BorrowerID
				String[] names =cBoxBorrower.getSelectedItem().toString().split(",");
				String selectedBorrowerQuery = "Select * From Borrower Where Last_name ='" + names[0] + "' And First_name='" + names[1] + "'";
				String borrowerID = BorrowReturnPanel.getData(selectedBorrowerQuery, "Borrower_ID").firstElement();	
			//get selected BookID
				String bookTitle = cBoxBook.getSelectedItem().toString();
				String selectedBookQuery = "Select * From Book Where Title =\"" + bookTitle + "\"";
				String bookID = BorrowReturnPanel.getData(selectedBookQuery, "BookID").firstElement();
			//get comment
				String comment = commenTextField.getText();
			//update the book loan record when book is returned	
				String dateReturned = returnDateTextField.getText();
				if(!Pattern.matches("([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))", dateReturned)) {
					JOptionPane.showMessageDialog(null, "Return Date format must be 'YYYY-MM-DD'");	
					returnDateTextField.setText("");
					returnDateTextField.requestFocus(true);
					validInput = false;
				}
				else
				{
					validInput = true;
				}
				if(validInput)
				{
					String bookLoanReturn ="UPDATE Book_Loan SET Date_Returned ='" + dateReturned + "', Comment ='"
							                    + comment +  "' Where Book_BookID = " + bookID + 
							                   " And Borrower_Borrower_ID = " + borrowerID;
					int updateReturnVal = BorrowReturnPanel.insertOrUpdateData(bookLoanReturn);
					String bookStatusUpdate = "UPDATE Book SET Available = 1 Where BookID = " + bookID;
					if(updateReturnVal > 0)
					{
					 int updateStatusVal = BorrowReturnPanel.insertOrUpdateData(bookStatusUpdate);
					}
					JOptionPane.showMessageDialog(null, updateReturnVal + " book has been returned!");	
				
				 //clear all fields after return successfully
					cBoxBook.setSelectedIndex(-1);
					cBoxBorrower.setSelectedIndex(-1);
					ISBNTextField.setText("");
					borrowDateTextField.setText("");
					cBoxBorrowPeriodBox.setSelectedIndex(-1);
					dueDateTextField.setText("");	
					returnDateTextField.setText("");
					commenTextField.setText("");
				}
			}
			
		}
	}
//This will handle events for Radio Button borrown or return
  private class RadioButtonListener implements ItemListener
  {	
		@Override
	  public void itemStateChanged(ItemEvent e)
	  {
	  		
	  		if(borrowBtn.isSelected())
	  	{
	  		returnDateTextField.setEnabled(false);
	  		borrowDateTextField.setEnabled(true);
				cBoxBorrowPeriodBox.setEnabled(true);
				cBoxBook.removeAllItems();
				cBoxBorrower.removeAllItems();
				returnDateTextField.setText("");
			 //reset value of Borrower/Book combo Box value to default
				BorrowReturnPanel.makeConnection();
				for(int i=0; i<modelBorrower.getSize();i++) 
				{
				cBoxBorrower.addItem(modelBorrower.getElementAt(i));
				}
				for(int i=0; i<modelBook.getSize();i++) 
				{
				cBoxBook.addItem(modelBook.getElementAt(i));
				}
				cBoxBook.setSelectedIndex(-1);
				cBoxBorrower.setSelectedIndex(-1);
				ISBNTextField.setText("");
				dueDateTextField.setText("");
				borrowDateTextField.setText(sf.format(now));
	  	}
	  	else if(returnBtn.isSelected()) 
	  	{//Return mode
				borrowDateTextField.setEnabled(false);
				cBoxBorrowPeriodBox.setEnabled(false);
				returnDateTextField.setEditable(true);
				returnDateTextField.setEnabled(true);
			
			  //reset all fields to default
				cBoxBorrower.setSelectedIndex(-1);
				cBoxBook.setSelectedIndex(-1);
				ISBNTextField.setText("");
				borrowDateTextField.setText("");
				cBoxBorrowPeriodBox.setSelectedIndex(-1);
				returnDateTextField.setText(sf.format(now));
				//check due date when retrieve this data from DB
				String dueDateOnLoanQuery = "Select * from Borrower b Inner Join Book_Loan bl on b.Borrower_ID = bl.Borrower_Borrower_ID";
				Vector<String>dueDateOnLoan = BorrowReturnPanel.getData(dueDateOnLoanQuery, "Date_returned");
				if(cBoxBook.getSelectedIndex() == -1)
				{
					dueDateTextField.setText("");
				}
				else {
					dueDateTextField.setText(dueDateOnLoan.firstElement());
				}
				//when user select the borrower name and book to see other details of book loan 
			}	
	  }
 }
  //this function will be used to get combobox data by default for both borrower and book
	public static DefaultComboBoxModel<String> getComboBoxData(String query,String comboBoxName)
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
		myRslt = myStmt.executeQuery(query);
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
	// this function is used to get data for other fields
	public static Vector<String> getData(String query,String columnName)
	{
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRslt = null;
		Vector<String> result= new Vector<String>();
		try
		{
		myConn = DriverManager.getConnection(
					         "jdbc:mysql://localhost:3306/INFO3136_Books?useSSL=false&allowPublicKeyRetrieval=true", 
					                       "root","password");		
		myStmt = myConn.createStatement();
		myRslt = myStmt.executeQuery(query);
		while(myRslt.next())
		{
		//result = myRslt.getString("Isbn");
			result.add(myRslt.getString(columnName));
		}
	  
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
			//clean up code if bad things start to happen. This code ALWAYS runs.					
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
		return result;
	}//end method
// this function is used to insert or update data
	public static int insertOrUpdateData(String query)
	{
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRslt = null;
		int resultVal = 0;
		try
		{
		myConn = DriverManager.getConnection(
					         "jdbc:mysql://localhost:3306/INFO3136_Books?useSSL=false&allowPublicKeyRetrieval=true", 
					                       "root","password");		
		myStmt = myConn.createStatement();
		resultVal = myStmt.executeUpdate(query);
	  
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
		return resultVal;
	}//end method
	//this function is used to set up connection at the beginning and prepare data for combo box Book and Borrower
	public static void makeConnection()
	{
		Connection myConn = null;
		Statement myStmtBorrower = null;
		Statement myStmtBook = null;
		ResultSet myRsltBorrower = null; 
		ResultSet myRsltBook = null; 
		try
		{
		myConn = DriverManager.getConnection(
					         "jdbc:mysql://localhost:3306/INFO3136_Books?useSSL=false&allowPublicKeyRetrieval=true", 
					                       "root","password");		
		 myStmtBorrower = myConn.createStatement();
		 myStmtBook = myConn.createStatement();		
		 myRsltBorrower = myStmtBorrower.executeQuery("SELECT last_name,first_name FROM Borrower");
		 myRsltBook = myStmtBook.executeQuery("SELECT title FROM Book Where Available = 1");
	   modelBorrower = BuildComboBoxUtility.resultSetToDefaultComboBoxModel(myRsltBorrower, "borrower");
	   modelBook = BuildComboBoxUtility.resultSetToDefaultComboBoxModel(myRsltBook, "book");
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
				if(myRsltBorrower != null)
				  myRsltBorrower.close();
				if(myRsltBook != null)
				  myRsltBook.close();
				if(myStmtBook != null)
					myStmtBook.close();
				if(myStmtBorrower != null)
					myStmtBorrower.close();				
			}//end try
			catch(SQLException ex)
			{
				System.out.println("SQLException INSIDE finally block: "+ ex.getMessage());
				ex.printStackTrace();
			}
		}//end finally
	}//end method
}	
	