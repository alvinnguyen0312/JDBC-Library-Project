import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserModel implements ListModel<String>
{

    // DATA MEMBERS...implement a private inner class
    private class UserRow
    {
        public String borrowerFName;
        public String borrowerLName;
        public String borrowerEmail;
    }


    // ArrayList to hold the Row objects
    private ArrayList<UserRow> userArrayList = new ArrayList<UserRow>();

    // Second arrayList to hold data listeners
    private ArrayList<ListDataListener> dataListenerList = new ArrayList<ListDataListener>();

    /*
     * Method Name: getSize()
     * Purpose: tells JList how many ArrayList items are to be displayed in the GUI
     * Accepts: nothing
     * Returns: an int that is the size of the itemsArrayList object.
     */
    @Override
    public int getSize()
    {
        // this method informs the JList how many ArrayList items are to be displayed
        return userArrayList.size();
    }


    /*
     * Method Name: getElementAt()
     * Purpose: retrieves a Row object from the ArrayList and creates
     *          a StringBuilder object from the data values of the Row object.
     *          The StringBuilder object is then converted to a String and returned
     * Accepts: an int representing the index number of the row desired
     * Returns: a String object representation of the values in the Row object,
     *          which can then be displayed in the JList.
     */
    @Override
    public String getElementAt(int index)
    {
        if(index < userArrayList.size())
        {
            UserRow row = userArrayList.get(index);

            //now append each data member of the row object to a StringBuilder object
            StringBuilder itemsArrayListString = new StringBuilder();

            itemsArrayListString.append(row.borrowerFName + " " + row.borrowerLName + " " + row.borrowerEmail);

            return itemsArrayListString.toString();

        }//end outer if

        return null;//leave this here in case an if statement fails above.
    }


    /*
     * Method Name: addListDataListener()
     * Purpose: allows the JList view to register its ListDataListener
     *          with the Model object so that if a change occurs in the
     *          model, the JList will be able to adjust what is displayed.
     * Accepts: an object of type ListDataListener
     * Returns: NOTHING! Void method.
     */
    @Override
    public void addListDataListener(ListDataListener listener)
    {
        // register it
        dataListenerList.add(listener);
    }

    @Override
    public void removeListDataListener(ListDataListener listener)
    {
        // if there is a listener,remove it
        if(dataListenerList.contains(listener) )
        {
            dataListenerList.remove(listener);
        }

    }


    /*
     * Method Name; addUserElement()
     * Purpose: creates a Row object from submitted arguments
     * Accepts: a double and three Strings that are read in from
     *          the JTextFields in the RegisterPanelController class GUI.
     * Returns: NOTHING! Void method.
     */
    public synchronized void addUserElement(String fName, String lName, String email) throws SQLException
    {
        // CREATE a new Row object
        UserRow row = new UserRow();
        row.borrowerFName = fName;
        row.borrowerLName = lName;
        row.borrowerEmail = email;

        // Add the Row object to the ArrayList
        userArrayList.add(row);

        // Standard boilerplate code often seen
        Connection myConn = null;
        PreparedStatement myPrepStmt = null;
        ResultSet myRslt = null;


        // Step 1: Use a try-catch to attempt the database connection
        try
        {
            // Create a Connection object by calling a static method of DriverManager class
            myConn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/INFO3136_Books?useSSL=false&serverTimezone=UTC","root","password"
            );

            //step 2: create the PreparedStatement here
            String insertDb = "INSERT INTO Borrower" +
                    " (last_name, first_name, borrower_email)" +
                    " VALUES (?,?,?)";

            myPrepStmt = myConn.prepareStatement(insertDb);

            // Step 3: we'll just hard code the parameter values to be assigned to '
            //         the place holders here
            myPrepStmt.setString(1, row.borrowerLName);//sets lower salary limit
            myPrepStmt.setString(2, row.borrowerFName);
            myPrepStmt.setString(3, row.borrowerEmail);


            // Catch the returned int value after the update is executed.
            int returnedValue = myPrepStmt.executeUpdate();

            // rows affected
            System.out.println("New " + row + " Borrower updated.");

        }
        catch (SQLException ex)
        {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE,null,ex);
            ex.printStackTrace();
        }
        catch(Exception ex)
        {
            System.out.println("Exception caught, message is " + ex.getMessage());
        }
        finally
        {
            if(myRslt != null)
                myRslt.close();
            if(myPrepStmt != null)
                myPrepStmt.close();
            if(myConn != null)
                myConn.close();
        }


    }//end method


    // This method to compare an new user's email to all existing email address in the DB
    // If the new user's email is already in the DB, it will prevent the user to register.
    public synchronized boolean compareNewUserToDB(String userEmail) throws SQLException
    {

        List<String> allUser = new ArrayList<String>();

        // Standard JDBC BOILERPLATE code
        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRslt = null;

        // Use a try-catch to attempt the database connection
        try
        {
            // Step 1: Create a Connection object by calling a static method of DriverManager class
            myConn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/INFO3136_Books?useSSL=false&serverTimezone=UTC","root","password"
            );


            // Step 2: create Statement object by calling a method of the Connection object
            myStmt = myConn.createStatement();

            // Step 3: pass in a query to the Statement Object using a method called executeQuery()
            // and Assign the returned ResultSet object to myRslt.
            myRslt = myStmt.executeQuery("SELECT * FROM Borrower");

            // Step 4: PROCESS the myRslt result set object using a while loop
            while(myRslt.next())
            {

                allUser.add(myRslt.getString("Borrower_email"));
            }

            // Compare a new isbn to existing isbn
            for(String eachUser : allUser)
            {
                if(userEmail.equals(eachUser))
                    return true;
            }

        }
        catch(SQLException ex)
        {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE,null,ex);
            ex.printStackTrace();
        }
        catch(Exception ex)
        {
            System.out.println("Exception caught, message is " + ex.getMessage());
        }
        finally
        {
            // Put your clean up  code here to close the objects.
            // Standard practice is to close them in REVERSE ORDER of creation

            if(myRslt != null)
                myRslt.close();
            if(myStmt != null)
                myStmt.close();
            if(myConn != null)
                myConn.close();
        }

        return false;

    }

}
