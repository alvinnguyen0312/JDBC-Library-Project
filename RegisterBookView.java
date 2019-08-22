import javax.swing.*;
import java.awt.*;



public class RegisterBookView extends JFrame
{

    private static final long serialVersionUID = 1L;

    // For sizing purposes to size the JFrame
    private static final double FRAME_WIDTH_FACTOR = 0.25;
    private static final double FRAME_HEIGHT_FACTOR = 0.75;


    // Constructor method..accepts a ListModel object
    public RegisterBookView(ListModel<String> bookSummaryList) throws HeadlessException
    {
        super("Registered Book Summary");

        DefaultListModel<String> summary = new DefaultListModel<>();

        //JList<String> registeredBookSummaryList;

        // Boilerplate
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setLayout(new BorderLayout() );

        // Variation: using the current dimensions of your screen resolution
        this.setSize(
                (int)(this.getToolkit().getScreenSize().width * FRAME_WIDTH_FACTOR),
                (int)(this.getToolkit().getScreenSize().height * FRAME_HEIGHT_FACTOR)
        );

        this.setLocationRelativeTo(null);

        String temp = "";

        for(int i = 0; i< bookSummaryList.getSize(); i++)
            temp =  bookSummaryList.getElementAt(i);


        String summaryArray[] = temp.split("/");

        // To display a new book's summary (Adding them in different line) in the new View
        for(int i = 0; i< summaryArray.length; i++)
            summary.addElement(summaryArray[i]);


        JList<String> registeredBookSummaryList = new JList<String>(summary);

        JPanel summaryPanel = new JPanel();
        summaryPanel.add(registeredBookSummaryList);


        this.add(summaryPanel,BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(summaryPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        this.add(scrollPane);
        //the last line
        this.setVisible(true);
    }//end constructor

}
