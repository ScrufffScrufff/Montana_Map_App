import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Object_Model implements Serializable {
    private String name, type, filename, textfilepath;
    private int year,objectnumber;
    private int elevation;
    private ImagePanel spot, textspot;
    private Point objectloc;
    public Object_Model(String name, int year, int elevation, ImagePanel spot){
        this.name = name;
        this.year = year;
        this.elevation = elevation;
        this.spot = spot;
        this.type = "-";
    }
    public Object_Model(String name, int year, int elevation){
        this.name = name;
        this.year = year;
        this.elevation = elevation;
        this.type = "-";
    }
    public Object_Model(String name, int year, int elevation, String type){
        this.name = name;
        this.year = year;
        this.elevation = elevation;
        this.type = type;
    }
    public Object_Model(){
        name = null;
        year = 0;
        elevation = 0;
        spot = null;
        type = null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        //out.defaultWriteObject();
        out.writeObject(this.name);
        out.writeObject(this.type);
        out.writeObject(this.year);
        out.writeObject(this.objectnumber);
        out.writeObject(this.elevation);
        out.writeObject(this.filename);
        out.writeObject(this.objectloc);
    }

    private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException {
        //in.defaultReadObject();
        this.name = (String) in.readObject();
        this.type = (String) in.readObject();
        this.year = (int) in.readObject();
        this.objectnumber = (int) in.readObject();
        this.elevation = (int) in.readObject();
        this.filename = (String) in.readObject();
        this.objectloc = (Point) in.readObject();
    }

    public ImagePanel getSpot() {
        return spot;
    }

    public int getElevation() {
        return elevation;
    }

    public int getYear() {
        return year;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSpot(ImagePanel spot) {
        this.spot = spot;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getObjectnumber() {
        return objectnumber;
    }

    public void setObjectnumber(int objectnumber) {
        this.objectnumber = objectnumber;
    }

    public String getFile_path() {
        return filename;
    }

    public void setFile_path(String filename) {
        this.filename = filename;
    }

    public void setObjectloc(Point objectloc) {
        this.objectloc = objectloc;
    }

    public Point getObjectloc() {
        return objectloc;
    }

    public String gettextFile_path() {
        return textfilepath;
    }

    public void settextFile_path(String textfilepath) {
        this.textfilepath = textfilepath;
    }

    public ImagePanel gettextSpot() {
        return textspot;
    }

    public void settextSpot(ImagePanel textspot) {
        this.textspot = textspot;
    }
}
