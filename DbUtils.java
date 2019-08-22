

import java.sql.ResultSet;
import java.sql.ResultSetMetaData; 
import java.util.Vector; 
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
 
public class DbUtils
{
     public static TableModel resultSetToTableModel(ResultSet rs)
     {
         try {
        	 //get the metadata for number of columns and column names
             ResultSetMetaData metaData = rs.getMetaData();
             int numberOfColumns = metaData.getColumnCount();
             Vector<String> columnNames = new Vector<String>();
 
            // Get the column names and store in vector
             for (int column = 0; column < numberOfColumns; column++)
             {
                 columnNames.addElement(metaData.getColumnLabel(column + 1));                
             }
             Vector<Vector<Object>> rows = new Vector<Vector<Object>>();
 
             while (rs.next())
             {
                 Vector<Object> newRow = new Vector<Object>();
 
                for (int i = 1; i <= numberOfColumns; i++)
                {
                     newRow.addElement(rs.getObject(i));
                }//end for

                 rows.addElement(newRow);
             }//end while

            //return the DefaultTableModel object to the line that called it		
             return new DefaultTableModel(rows, columnNames);
         } catch (Exception e) 
         {
        	 System.out.println("Exception in DbUtils method resultSetToTableModel()...");
             e.printStackTrace();
             return null;
         }//end catch
     }//end method
 }//end class
