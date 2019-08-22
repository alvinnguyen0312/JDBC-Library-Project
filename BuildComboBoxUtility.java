import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Vector; 


public class BuildComboBoxUtility
{
	
	public static DefaultComboBoxModel<String> resultSetToDefaultComboBoxModel(ResultSet rs,String type)
	{
		DefaultComboBoxModel<String> model = null;
		try
		{
			//create the vector to hold the string elements
			Vector<String> borrowers = new Vector<String>();
			Vector<String> books = new Vector<String>();
			Vector<String> authors = new Vector<String>();
			Vector<String> subjects = new Vector<String>();
			
			//loop through result set and add each surname and first name to the vector
			if(type.equals("borrower"))
			{
				while(rs.next())
				{					
					borrowers.add(rs.getString("Last_name") + "," + rs.getString("first_Name"));
				}//end while
				model = new DefaultComboBoxModel<String>(borrowers); 
			}
			else if(type.equals("book"))
			{
				while(rs.next())
				{
					books.add(rs.getString("Title"));
				}//end while
				model = new DefaultComboBoxModel<String>(books); 
			}
			else if(type.equals("author"))
			{
				while(rs.next())
				{
					if(!authors.contains(rs.getString("Last_Name") + "," + rs.getString("First_name"))) // remove duplicate in author list
					authors.add(rs.getString("Last_Name") + "," + rs.getString("First_name"));
				}//end while
				model = new DefaultComboBoxModel<String>(authors); 
			}
			else if(type.equals("subject"))
			{
				while(rs.next())
				{
					if(!subjects.contains(rs.getString("subject"))) // remove duplicate in subject list
					{
						subjects.add(rs.getString("subject"));
					}
				}//end while
				model = new DefaultComboBoxModel<String>(subjects); 
			}
			
		}//end try
		catch(Exception ex)
		{
			System.out.println("Exception caught, message is: " + ex.getMessage());
			ex.printStackTrace();
		}
		//IF no exception was caught, then return the model to the calling line
		return model;		
	}//end method
	
}//end class