import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.time.LocalDateTime;


public class BookModel implements ListModel<String>
{

    // DATA MEMBERS...implement a private inner class
    private class BookRow
    {
        public String title;
        public String editionNum;
        public String isbn;
        public String subject;
        public int available;
        public HashMap<String, String> authorList;
    }

    // ArrayList to hold the BookRow objects
    private ArrayList<BookRow> bookArrayList = new ArrayList<BookRow>();

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
        return bookArrayList.size();
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
        if(index < bookArrayList.size())
        {
            BookRow row = bookArrayList.get(index);

            //now append each data member of the row object to a StringBuilder object
            //StringBuilder itemsArrayListString = new StringBuilder();
            StringBuilder itemsArrayListString = new StringBuilder();


            itemsArrayListString.append("*** This book is successfully registered. ***");
            itemsArrayListString.append("/Title: " + row.title);
            itemsArrayListString.append("/ISBN: " + row.isbn);
            itemsArrayListString.append("/Edition Number: " + row.editionNum);
            itemsArrayListString.append("/Subject: " + row.subject);
            itemsArrayListString.append("/Author(s): ");

            Set set = row.authorList.entrySet();
            Iterator iterator = set.iterator();

            int tempIndex = 1;
            while(iterator.hasNext())
            {
                Map.Entry mentry = (Map.Entry)iterator.next();

                itemsArrayListString.append("/Author " + tempIndex);
                itemsArrayListString.append("/First name: " + mentry.getKey().toString() + ", Last name: " + mentry.getValue().toString());

                tempIndex++;
            }

            itemsArrayListString.append("/Status: Available");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();

            itemsArrayListString.append("/Registered Date: " + dtf.format(now));


            //now convert the StringBuilder object to a String object so that it can
            // be stored in the JList, which is set up for just Strings
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
     * Method Name; addBookElement()
     * Purpose: creates a Row object from submitted arguments
     * Accepts: Book information Strings that are read in from
     *          the JTextFields in the Controller class GUI.
     * Returns: NOTHING! Void method.
     */
    public synchronized void addBookElement(String title, String isbn, String editionNum, String subject, HashMap<String, String> authorList) throws SQLException
    {

        // CREATE a new Row object
        BookRow row = new BookRow();

        row.title = title;
        row.editionNum = editionNum;
        row.isbn = isbn;
        row.subject = subject;
        row.available = 1;
        row.authorList = authorList;


        // Add the Row object to the ArrayList
        bookArrayList.add(row);

        Connection myConn = null;
        // For insert a new book
        PreparedStatement myPrepStmtBook = null;
        // For insert a new Author
        PreparedStatement myPrepStmtAuthor = null;
        // For assign author to book
        PreparedStatement myPrepStmtAssignment = null;

        ResultSet myRslt = null;


        // Transaction start!
        try
        {
            // Create a Connection object by calling a static method of DriverManager class
            myConn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/INFO3136_Books?useSSL=false&serverTimezone=UTC","root","password"
            );

            // Set auto commit to false
            myConn.setAutoCommit(false);

            // Insert candidate
            String insertDb = "";

            insertDb = "INSERT INTO Book (Title, ISBN, Edition_Number, Subject, Available) " +
                       "VALUES (?,?,?,?,?) ";

            // To get a bookId
            myPrepStmtBook = myConn.prepareStatement(insertDb, Statement.RETURN_GENERATED_KEYS);


            myPrepStmtBook.setString(1, row.title);
            myPrepStmtBook.setString(2, row.isbn);
            myPrepStmtBook.setString(3, row.editionNum);
            myPrepStmtBook.setString(4, row.subject);
            myPrepStmtBook.setInt(5, row.available);


            int bookRowAffected = myPrepStmtBook.executeUpdate();

            // Get Book Id
            myRslt = myPrepStmtBook.getGeneratedKeys();
            int newBookId = 0;
            if (myRslt.next())
                newBookId = myRslt.getInt(1);


            // If there is only ONE author for a new book
            if(row.authorList.size() == 1)
            {
                String tempFName ="";
                String tempLName ="";

                Set set = row.authorList.entrySet();
                Iterator iterator = set.iterator();

                // Assign a new author's name
                while(iterator.hasNext()) {
                    Map.Entry mentry = (Map.Entry)iterator.next();
                    tempFName =  mentry.getKey().toString();
                    tempLName =  mentry.getValue().toString();
                }


                // If the new author is NOT on the DB
                if(compareAuthorFullName(tempFName, tempLName) == -1)
                {

                    insertDb = "INSERT INTO Author(Last_Name, First_Name) " +
                            "VALUES (?,?) ";

                    myPrepStmtAuthor = myConn.prepareStatement(insertDb, Statement.RETURN_GENERATED_KEYS);

                    myPrepStmtAuthor.setString(1, tempLName);
                    myPrepStmtAuthor.setString(2, tempFName);

                    int authorRowAffected = myPrepStmtAuthor.executeUpdate();

                    // Get Book Id
                    myRslt = myPrepStmtAuthor.getGeneratedKeys();

                    int newAuthorId = 0;

                    if (myRslt.next())
                        newAuthorId = myRslt.getInt(1);

                    if(bookRowAffected == 1 && authorRowAffected == 1)
                    {
                        // Assign authorID and bookID to the junction table
                        String pivotDb = "INSERT INTO Book_Author (Book_BookID, Author_AuthorID) "
                                + "VALUES(?,?)";

                        myPrepStmtAssignment = myConn.prepareStatement(pivotDb);

                        myPrepStmtAssignment.setInt(1, newBookId);
                        myPrepStmtAssignment.setInt(2, newAuthorId);

                        myPrepStmtAssignment.executeUpdate();

                        myConn.commit();


                        System.out.print("\nAdded new author (first name: "+ tempFName + ", last name: " + tempLName +")");
                    }
                    else
                    {
                        myConn.rollback();
                    }

                }
                // If the new author is ALREADY on the DB
                else
                {

                    // Get the authorID from the DB
                    int newAuthorId = compareAuthorFullName(tempFName, tempLName);

                    if(bookRowAffected == 1)
                    {
                        // Assign authorID and bookID to the junction table
                        String pivotDb = "INSERT INTO Book_Author (Book_BookID, Author_AuthorID) "
                                + "VALUES(?,?)";

                        myPrepStmtAssignment = myConn.prepareStatement(pivotDb);

                        myPrepStmtAssignment.setInt(1, newBookId);
                        myPrepStmtAssignment.setInt(2, newAuthorId);

                        myPrepStmtAssignment.executeUpdate();

                        myConn.commit();



                        System.out.print("\nAuthor is already existed. Used a existing author (first name: "+ tempFName + ", last name: " + tempLName +") to register a new book.");
                    }
                    else
                    {
                        myConn.rollback();
                    }

                }


            }
            // If there are CO-AUTHOR for a new book
            else
            {
                String tempFName ="";
                String tempLName ="";


                Set set = row.authorList.entrySet();
                Iterator iterator = set.iterator();
                while(iterator.hasNext())
                {
                    Map.Entry mentry = (Map.Entry)iterator.next();

                    tempFName =  mentry.getKey().toString();
                    tempLName =  mentry.getValue().toString();


                    // If new author is NOT on the DB
                    if(compareAuthorFullName(tempFName, tempLName) == -1)
                    {
                        insertDb = "INSERT INTO Author(Last_Name, First_Name) " +
                                "VALUES (?,?) ";

                        myPrepStmtAuthor = myConn.prepareStatement(insertDb, Statement.RETURN_GENERATED_KEYS);


                        myPrepStmtAuthor.setString(1, mentry.getValue().toString());//sets lower salary limit
                        myPrepStmtAuthor.setString(2, mentry.getKey().toString());
                        System.out.print("\nAdded new author (first name: "+ mentry.getKey() + ", last name: " + mentry.getValue() + ")");


                        int authorRowAffected = myPrepStmtAuthor.executeUpdate();

                        // Get AuthorId
                        myRslt = myPrepStmtAuthor.getGeneratedKeys();
                        int newAuthorId = 0;
                        if (myRslt.next())
                            newAuthorId = myRslt.getInt(1);


                        if(bookRowAffected == 1 && authorRowAffected == 1)
                        {
                            // Assign authorID and bookID to the junction table
                            String pivotDb = "INSERT INTO Book_Author (Book_BookID, Author_AuthorID) "
                                    + "VALUES(?,?)";

                            myPrepStmtAssignment = myConn.prepareStatement(pivotDb);

                            myPrepStmtAssignment.setInt(1, newBookId);
                            myPrepStmtAssignment.setInt(2, newAuthorId);

                            myPrepStmtAssignment.executeUpdate();

                            myConn.commit();
                        }
                        else
                        {
                            myConn.rollback();
                        }
                    }
                    // If new author is ALREADY on the DB
                    else
                    {
                        // Get the authorID from the DB
                        int newAuthorId = compareAuthorFullName(tempFName, tempLName);

                        if(bookRowAffected == 1)
                        {
                            // Assign authorID and bookID to the junction table
                            String pivotDb = "INSERT INTO Book_Author (Book_BookID, Author_AuthorID) "
                                    + "VALUES(?,?)";

                            myPrepStmtAssignment = myConn.prepareStatement(pivotDb);

                            myPrepStmtAssignment.setInt(1, newBookId);
                            myPrepStmtAssignment.setInt(2, newAuthorId);

                            myPrepStmtAssignment.executeUpdate();

                            myConn.commit();

                            System.out.print("\nAuthor is already existed. Used a existing author (first name: "+ tempFName + ", last name: " + tempLName +") to register a new book.");

                        }
                        else
                        {
                            myConn.rollback();
                        }


                    }
                }

            }
        }
        catch (SQLException ex)
        {
            // Roll back the transaction
            try
            {
                if(myConn !=null)
                    myConn.rollback();
            }
            catch(SQLException e)
            {
                Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE,null,e);
                e.printStackTrace();

            }
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
            if(myPrepStmtBook != null)
                myPrepStmtBook.close();
            if(myPrepStmtAuthor != null)
                myPrepStmtAuthor.close();
            if(myPrepStmtAssignment != null)
                myPrepStmtAssignment.close();
            if(myConn != null)
                myConn.close();
        }

        // Reset
        //bookArrayList = new ArrayList<BookRow>();

    } // end addBookElement method


    public synchronized boolean compareNewISBNToDB(String isbn) throws SQLException
    {

        List<String> allISBN = new ArrayList<String>();

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
            myRslt = myStmt.executeQuery("SELECT * FROM Book");

            // Step 4: PROCESS the myRslt result set object using a while loop
            while(myRslt.next())
            {
                allISBN.add( myRslt.getString("isbn"));
            }

            // Compare a new isbn to existing isbn
            for(String eachIsbn : allISBN)
            {
                if(isbn.equals(eachIsbn))
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
            if(myRslt != null)
                myRslt.close();
            if(myStmt != null)
                myStmt.close();
            if(myConn != null)
                myConn.close();
        }

        return false;

    } // end compareNewISBNToDB method

    public synchronized int compareAuthorFullName(String fName, String lName) throws SQLException
    {

        int authorId = -1;

        HashMap<Integer, Pair<String,String> > existAuthorList = new HashMap<Integer, Pair<String, String>>();

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
            myRslt = myStmt.executeQuery("SELECT * FROM Author");

            Pair<String,String> fullName;

            // Step 4: PROCESS the myRslt result set object using a while loop
            while(myRslt.next())
            {

                fullName = new Pair<String, String>(myRslt.getString("first_name"),myRslt.getString("last_name"));
                existAuthorList.put(Integer.parseInt(myRslt.getString("authorId")), fullName );

            }

            Set set = existAuthorList.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext())
            {
                Map.Entry mentry = (Map.Entry)iterator.next();

                // Get a authorId from the DB
                int tempAuthorId = Integer.parseInt(mentry.getKey().toString());

                // Get a author's full name from the DB
                fullName = (Pair)mentry.getValue();

                // If we find a same name of author that is already existed in the DB, we get the authorId
                if(fName.toLowerCase().equals(fullName.getFirstName().toLowerCase()) && lName.toLowerCase().equals(fullName.getLastName().toLowerCase()))
                    authorId = tempAuthorId;
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
            if(myRslt != null)
                myRslt.close();
            if(myStmt != null)
                myStmt.close();
            if(myConn != null)
                myConn.close();
        }
        return authorId;

    } // end compareAuthorFullName method

    // To hold a full name of author, create a Pair class
    public class Pair<A, B>
    {
        A first = null;
        B second = null;

        Pair(A first, B second)
        {
            this.first = first;
            this.second = second;
        }

        public A getFirstName()
        {
            return first;
        }

        public void setFirstName(A first)
        {
            this.first = first;
        }

        public B getLastName()
        {
            return second;
        }

        public void setLastName(B second)
        {
            this.second = second;
        }

    } // end Pair class

}// end BookModel class
