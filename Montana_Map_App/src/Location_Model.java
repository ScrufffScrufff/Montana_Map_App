import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Location_Model implements Serializable {
    public static final long serialVersionUID = 1L;
    String file_path;
    String location_name;
    String yr;
    ArrayList<Object_Model> objects = new ArrayList();
    int elevtion,filenumber;
    Point point,drawingloc;


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
        out.writeObject(this.objects);
        out.writeObject(this.elevtion);
        out.writeObject(this.file_path);
        out.writeObject(this.filenumber);
        out.writeObject(this.point);
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
        in.defaultReadObject();
        this.location_name = (String) in.readObject();
        this.yr = (String) in.readObject();
        this.objects = (ArrayList<Object_Model>) in.readObject();
        this.elevtion = (int) in.readObject();
        this.file_path = (String) in.readObject();
        this.filenumber = (int) in.readObject();
        this.point = (Point) in.readObject();
    }

    public String getLocation_name() {
        return location_name;
    }

    public ArrayList<Object_Model> getObjects() {
        return objects;
    }

    public int getElevtion() {
        return elevtion;
    }

    public String getYear() {
        return yr;
    }

    public String getFile_path() {
        return file_path;
    }

    public void addObject(Object_Model obj){
        this.objects.add(obj);
    }
    public void removeObject(Object_Model obj){
        this.objects.remove(obj);
    }

    public void removeObjects(){
        this.objects.removeAll(objects);
    }

    public void setElevtion(int elevtion) {
        this.elevtion = elevtion;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getFilenumber() {
        return filenumber;
    }

    public void setFilenumber(int filenumber) {
        this.filenumber = filenumber;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Point getDrawingloc() {
        return drawingloc;
    }

    public void setDrawingloc(Point drawingloc) {
        this.drawingloc = drawingloc;
    }
}
