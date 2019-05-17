import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Location_Model implements Serializable {
    public static final long serialVersionUID = 1L;
    String location_name;
    String yr;
    ArrayList<Bones_Model> bones = new ArrayList();
    int elevtion;


    public Location_Model(){

    }

    public Location_Model(String name_location, String year, int elevation){
        location_name = name_location;
        yr = year;
        elevtion = elevation;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.location_name);
        out.writeObject(this.yr);
        out.writeObject(this.bones);
        out.writeObject(elevtion);
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
        in.defaultReadObject();
        this.location_name = (String) in.readObject();
        this.yr = (String) in.readObject();
        this.bones = (ArrayList<Bones_Model>) in.readObject();
        this.elevtion = (int) in.readObject();
    }

    public String getLocation_name() {
        return location_name;
    }

    public ArrayList<Bones_Model> getBones() {
        return bones;
    }

    public int getElevtion() {
        return elevtion;
    }

    public String getYear() {
        return yr;
    }

    public void addBone(Bones_Model bone) {
        this.bones.add(bone);
    }
    public void removeBone(Bones_Model bone){
        this.bones.remove(bone);
    }

    public void setElevtion(int elevtion) {
        this.elevtion = elevtion;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }
}
