import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class JList_Struct implements Serializable {
    public static final long serialVersionUID = 1L;
    private transient JList<Location_Model> lst;
    private transient DefaultListModel<Location_Model> rlist;
    private transient JScrollPane lstscroller;

    public JList_Struct(DefaultListModel<Location_Model> list) {
        lst = new JList<>(list);
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
        //out.writeObject(this.lst);
        out.writeObject(this.rlist);
        //out.writeObject(this.lstscroller);
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
        in.defaultReadObject();
        //this.lst = (JList<Location_Model>) in.readObject();
        this.rlist = (DefaultListModel<Location_Model>) in.readObject();
        lst = new JList<>(rlist);
        lst.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lst.setLayoutOrientation(JList.VERTICAL);
        lst.setVisibleRowCount(10);
        lst.setFixedCellWidth(30);
        lstscroller = new JScrollPane(lst);
        lstscroller.createVerticalScrollBar();
        //this.lstscroller = (JScrollPane) in.readObject();
    }

    public JList<Location_Model> getJlst() {
        return lst;
    }

    public DefaultListModel getRlist() {
        return rlist;
    }

    public JScrollPane getListScroller() {
        return lstscroller;
    }

}
