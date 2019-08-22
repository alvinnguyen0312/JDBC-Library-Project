

import javax.swing.*;
import java.awt.*;

public class JDBCLibraryAppGUI
{

	public static void main(String[] args)
	{
	    //create the top level container
			JFrame frame = new JFrame("JDBC Library Application - Kiet Nguyen - JunYong Oh - Tien Nguyen");
			//boilerplate
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800, 500);
			//default will be BorderLayout
			frame.setLocationRelativeTo(null);
			
			JTabbedPane tPane = new JTabbedPane();
			
			tPane.add("Register", new RegisterPanelController());
			tPane.add("Borrow/Return", new BorrowReturnPanel());
			tPane.add("Library Management", new LibManagementPanel());			
			
			frame.add(tPane);			
			////last line
			frame.setVisible(true);

	}
}
//end class