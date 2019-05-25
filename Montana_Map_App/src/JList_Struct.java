import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class JList_Struct implements Serializable {
    public static final long serialVersionUID = 1L;
    private transient JList<String> lst;
    private transient DefaultListModel<String> str_list;
    private transient DefaultListModel<Location_Model> rlist;
    private transient JScrollPane lstscroller;

    public JList_Struct(DefaultListModel<Location_Model> list) {
        //make r list not == list, but add every item's string location to r list instead
        str_list = new DefaultListModel<>();
        for (int i = 0; i < list.size(); i++){
            str_list.addElement(list.getElementAt(i).getLocation_name());
        }
        lst = new JList<>(str_list);
        rlist = list;

        //size the array and populate it based on saved information
        lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lst.setLayoutOrientation(JList.VERTICAL);
        lst.setVisibleRowCount(10);
        lst.setFixedCellWidth(30);
        lstscroller = new JScrollPane(lst);
        lstscroller.createVerticalScrollBar();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.str_list);
        out.writeObject(this.rlist);
        //out.writeObject(this.lstscroller);
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
        in.defaultReadObject();
        this.str_list = (DefaultListModel<String>) in.readObject();
        this.rlist = (DefaultListModel<Location_Model>) in.readObject();
        lst = new JList<>(str_list);
        lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lst.setLayoutOrientation(JList.VERTICAL);
        lst.setVisibleRowCount(10);
        lst.setFixedCellWidth(30);
        lstscroller = new JScrollPane(lst);
        lstscroller.createVerticalScrollBar();
        //this.lstscroller = (JScrollPane) in.readObject();
    }

    public JList<String> getJlist() {
        return lst;
    }

    public DefaultListModel<Location_Model> getRlist() {
        return rlist;
    }

    public DefaultListModel<String> getStrlist(){return str_list;}

    public JScrollPane getListScroller() {
        return lstscroller;
    }

}
