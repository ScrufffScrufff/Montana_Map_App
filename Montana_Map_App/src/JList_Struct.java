import javax.swing.*;
import java.io.Serializable;


public class JList_Struct implements Serializable {
    private JList<Location_Model> lst;
    private DefaultListModel<Location_Model> rlist;
    final private JScrollPane lstscroller;

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
