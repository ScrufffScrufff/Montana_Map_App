import java.util.ArrayList;

public class Location_Model {

    String location_name;
    String yr;
    ArrayList<Bones_Model> bones = new ArrayList();
    int elevtion;


    public Location_Model(){

    }

    public Location_Model(String name_location, String year, Bones_Model bone, int elevation){
        location_name = name_location;
        yr = year;
        bones.add(bone);
        elevtion = elevation;
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

    public String getYr() {
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
