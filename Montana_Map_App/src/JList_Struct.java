import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class JList_Struct implements Serializable {
    public static final long serialVersionUID = 1L;
    private transient NestedList nestedList;
    private transient DefaultListModel<LocationModel> locationList;

    public JList_Struct(DefaultListModel<LocationModel> list) {
        nestedList = new NestedList();
        nestedList.setVisible(true);
        locationList = list;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.locationList);
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
        in.defaultReadObject();
        this.locationList = (DefaultListModel<LocationModel>) in.readObject();
        nestedList = new NestedList();
        for (int i = 0; i < locationList.size(); i++){
            nestedList.addItems(locationList.get(i).getLocationname(), locationList.get(i).getObjects());
        }
        nestedList.setVisible(true);


    }

    public NestedList getNestedList(){
        return nestedList;
    }

    public JList<NestedList.NestedItem> getJFirstList() {
        return nestedList.getJfirstList();
    }

    public JList<NestedList.NestedItem> getJSecondList() {
        return nestedList.getJsecondList();
    }

    public DefaultListModel<LocationModel> getLocationList() {
        return locationList;
    }

}
