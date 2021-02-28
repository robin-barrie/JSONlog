package NerdyLogger;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.*;

public class ConfigureList implements  ActionListener{

    JList itemList, shoppingList;
    JButton buttonin, buttonout;
    
    // The ListModels we will be using in the example.
    DefaultListModel shopping, items;

    public JPanel createContentPane (){

        // Create the final Panel.
        JPanel totalGUI = new JPanel();
        
        // Instantiate the List Models.
        shopping = new DefaultListModel();
        items = new DefaultListModel();

        // Things to be in the list.
        String shoppingItems[] = {"Milk", "Cheese", "Bread", "Butter", "Beans",
        "Soup", "Bacon", "Chicken", "Curry Sauce", "Chocolate"};

        // Using a for loop, we add every item in the String array
        // into the ListModel.

        for(int i = 0; i < shoppingItems.length; i++)
        {
            shopping.addElement(shoppingItems[i]);
        }

        // Creation of the list.
        // We set the cells in the list to be 20px x 140px.
        
        itemList = new JList(shopping);
        itemList.setVisibleRowCount(10);
        itemList.setFixedCellHeight(20);
        itemList.setFixedCellWidth(140);
        itemList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // We then add them to a JScrollPane.
        // This means when we remove items from the JList
        // it will not shrink in size.
        JScrollPane list1 = new JScrollPane(itemList);
        
        shoppingList = new JList(items);
        shoppingList.setVisibleRowCount(10);
        shoppingList.setFixedCellHeight(20);
        shoppingList.setFixedCellWidth(140);
        shoppingList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // We add this list to a JScrollPane too.
        // This is so the list is displayed even though there are 
        // currently no items in the list.
        // Without the scrollpane, the list would not show.
        JScrollPane list2 = new JScrollPane(shoppingList);

        // We create the buttons to be placed between the lists.
        JPanel buttonPanel = new JPanel();

        buttonin = new JButton(">>");
        buttonin.addActionListener(this);
        buttonPanel.add(buttonin);

        buttonout = new JButton("<<");
        buttonout.addActionListener(this);
        buttonPanel.add(buttonout);

        // This final bit of code uses a BoxLayout to space out the widgets
        // in the GUI.

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.LINE_AXIS));

        bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));
        bottomPanel.add(list1);
        bottomPanel.add(Box.createRigidArea(new Dimension(5,0)));
        bottomPanel.add(buttonPanel);
        bottomPanel.add(Box.createRigidArea(new Dimension(5,0)));
        bottomPanel.add(list2);
        bottomPanel.add(Box.createRigidArea(new Dimension(10,0)));

        totalGUI.add(bottomPanel);
        totalGUI.setOpaque(true);
        return totalGUI;
    }

    // In this method, we create a square JPanel of a colour and set size
    // specified by the arguments.

    private JPanel createSquareJPanel(Color color, int size) {
        JPanel tempPanel = new JPanel();
        tempPanel.setBackground(color);
        tempPanel.setMinimumSize(new Dimension(size, size));
        tempPanel.setMaximumSize(new Dimension(size, size));
        tempPanel.setPreferredSize(new Dimension(size, size));
        return tempPanel;
    }

    // valueChanged is the method that deals with a ListSelectionEvent.
    // This simply changes the boxes that are selected to true.

    public void actionPerformed(ActionEvent e) 
    {
        int i = 0;
        
        // When the 'in' button is pressed,
        // we take the indices and values of the selected items
        // and output them to an array.

        if(e.getSource() == buttonin)
        {
            int[] fromindex = itemList.getSelectedIndices();
            Object[] from = itemList.getSelectedValues();

            // Then, for each item in the array, we add them to
            // the other list.
            for(i = 0; i < from.length; i++)
            {
                items.addElement(from[i]);
            }
            
            // Finally, we remove the items from the first list.
            // We must remove from the bottom, otherwise we try to 
            // remove the wrong objects.
            for(i = (fromindex.length-1); i >=0; i--)
            {
                shopping.remove(fromindex[i]);
            }
        }
        
        // If the out button is pressed, we take the indices and values of
        // the selected items and output them to an array.
        else if(e.getSource() == buttonout)
        {
            Object[] to = shoppingList.getSelectedValues();
            int[] toindex = shoppingList.getSelectedIndices();
            
            // Then, for each item in the array, we add them to
            // the other list.
            for(i = 0; i < to.length; i++)
            {
                shopping.addElement(to[i]);
            }
            
            // Finally, we remove the items from the first list.
            // We must remove from the bottom, otherwise we try to
            // remove the wrong objects.
            for(i = (toindex.length-1); i >=0; i--)
            {
                items.remove(toindex[i]);
            }
        }
    }

    public void createAndShowGUI() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("[=] JListExample - Adding and Removing [=]");

        ConfigureList demo = new ConfigureList();
        frame.setContentPane(demo.createContentPane());
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        //while(true){
        //    System.out.print("12");
        //}
    }
/** 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
*/

}