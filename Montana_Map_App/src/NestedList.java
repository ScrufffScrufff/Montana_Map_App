import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class NestedList extends JPanel implements ListSelectionListener {

    private DefaultListModel<NestedItem> model = new DefaultListModel();

    private JList<NestedItem> jlocationlist = new JList(model);
    private JList<NestedItem> jobjectlist = new JList();

    private transient JScrollPane lstscroller;

    public NestedList(){
            super(new GridLayout(0, 2));

            this.setBorder(BorderFactory.createLoweredSoftBevelBorder());
            jlocationlist.setBackground(new java.awt.Color(120, 123, 170 ));
            jobjectlist.setBackground(new java.awt.Color(120, 123, 170 ));
            add(jlocationlist);
            add(jobjectlist);
            jlocationlist.addListSelectionListener(this);
    }

    public void addItems(String name, String[] subitems) {
            model.addElement(new NestedItem(name, subitems));
    }

    public void addItems(String name, ArrayList<Object_Model> subitems){
        model.addElement(new NestedItem(name, subitems));
    }

    public void addItem(String name) {
        model.addElement(new NestedItem(name));
    }

    public void addSubItem(String name){
        model.getElementAt(jlocationlist.getSelectedIndex()).addSubitem(name);
    }

    public void removeSubItems(){
        model.getElementAt(jlocationlist.getSelectedIndex()).subitems.removeAllElements();

    }

    public void removeSubItem(){
        model.getElementAt(jlocationlist.getSelectedIndex()).subitems.remove(jobjectlist.getSelectedIndex());
    }

    public void removeItem(){
        model.remove(jlocationlist.getSelectedIndex());
    }

    public void valueChanged(ListSelectionEvent e) {
        if (!(jlocationlist.getSelectedValue() == null)) {
            NestedItem item = jlocationlist.getSelectedValue();
            jobjectlist.setModel(item.subitems);
        }
    }


    public JScrollPane getListScroller() {
        return lstscroller;
    }

    public void setListScroller(JScrollPane scrollPane){
        lstscroller = scrollPane;
    }

    public JList<NestedItem> getJfirstList(){
        return jlocationlist;
    }

    public JList<NestedItem> getJsecondList() {
        return jobjectlist;
    }



    public static class NestedItem {
        private String name = null;
        private DefaultListModel subitems = new DefaultListModel();

        NestedItem(String name, String[] subitems) {
            this.name = name;
            for (String subitem : subitems)
                this.subitems.addElement(subitem);
        }

        NestedItem(String name){
            this.name = name;
        }

        NestedItem(String name, ArrayList<Object_Model> items){
            this.name = name;
            for (Object_Model subitem : items){
                this.subitems.addElement(subitem.getName());
            }

        }

        public void addSubitem(String item){
            this.subitems.addElement(item);
        }

        public void addSubitems(String [] subitems){
            for (String subitem : subitems)
                this.subitems.addElement(subitem);
        }

        public void addSubitems(ArrayList<Object_Model> items){
            for (Object_Model subitem : items){
                this.subitems.addElement(subitem.getName());
            }
        }

        public void removeSubitem(String item){
            subitems.removeElement(item);
        }

        public String getSubitem(int index){
            return (String)this.subitems.getElementAt(index);
        }
        public int getSubitemsize(){
            return subitems.getSize();
        }

        public String toString() {
                return name;
            }
    }
}