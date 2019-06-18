import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class LocationModel implements Serializable {
    public static final long serialVersionUID = 1L;
    String filepath, locationtextfilepath;
    String locationname;
    String yr;
    ArrayList<Object_Model> objects = new ArrayList();
    int elevation,filenumber, locationnumber;
    Point point,drawingloc;


    public LocationModel(){

    }

    public LocationModel(String name_location, String year, int elevation){
        this.locationname = name_location;
        this.yr = year;
        this.elevation = elevation;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.locationname);
        out.writeObject(this.yr);
        out.writeObject(this.objects);
        out.writeObject(this.elevation);
        out.writeObject(this.filepath);
        out.writeObject(this.filenumber);
        out.writeObject(this.point);
        out.writeObject(this.drawingloc);
        out.writeObject(this.locationnumber);
        out.writeObject(this.locationtextfilepath);
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
        in.defaultReadObject();
        this.locationname = (String) in.readObject();
        this.yr = (String) in.readObject();
        this.objects = (ArrayList<Object_Model>) in.readObject();
        this.elevation = (int) in.readObject();
        this.filepath = (String) in.readObject();
        this.filenumber = (int) in.readObject();
        this.point = (Point) in.readObject();
        this.drawingloc = (Point) in.readObject();
        this.locationnumber = (int) in.readObject();
        this.locationtextfilepath = (String) in.readObject();
    }

    public String getLocationname() {
        return locationname;
    }

    public ArrayList<Object_Model> getObjects() {
        return objects;
    }

    public int getElevation() {
        return elevation;
    }

    public String getYear() {
        return yr;
    }

    public String getFilepath() {
        return filepath;
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

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }

    public void setLocationname(String locationname) {
        this.locationname = locationname;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public int getFilenumber() {
        return filenumber;
    }

    public void setFilenumber(int filenumber) {
        this.filenumber = filenumber;
    }

    public Point getLocationviewpoint() {
        return point;
    }

    public void setLocationviewpoint(Point point) {
        this.point = point;
    }

    public Point getDrawingloc() {
        return drawingloc;
    }

    public void setDrawingloc(Point drawingloc) {
        this.drawingloc = drawingloc;
    }

    public int getLocationnumber() {
        return locationnumber;
    }

    public void setLocationnumber(int locationnumber) {
        this.locationnumber = locationnumber;
    }

    public String getLocationtextfilepath() {
        return locationtextfilepath;
    }

    public void setLocationtextfilepath(String locationtextfilepath) {
        this.locationtextfilepath = locationtextfilepath;
    }
}
