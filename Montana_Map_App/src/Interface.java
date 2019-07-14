import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Interface extends JFrame {
    private int  fileNumber = 0, objectnumber = 0, objecttextnumber = 0, locationtextnumber;
    private final int vGap = 5, hGap = 5;
    private JList_Struct lst;
    private JScrollPane scrollPane;
    private JPanel objectpanel, glasspane, buttonPanel;
    private Point pointStart = null;
    private Point pointEnd = null;
    private Point trueStart = null;
    private boolean lineDrawn = false;
    private ImagePanel panel;
    private ImageZoom zoom;
    private List<ImagePanel> imagePanelList = new ArrayList<>();
    private List<ImagePanel> objectPanelList = new ArrayList<>();
    private List<ImagePanel> objectTextPanelList = new ArrayList<>();
    private List<ImagePanel> locationTextPanelList = new ArrayList<>();


    ArrayList<Shape> shapeList = new ArrayList<>();
    Path2D.Float currentshape = null;



    public Interface() {
        super();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                if(!lst.getLocationList().isEmpty()) {
                    //save the contents of lst for future use
                    File f = new File("mapdata.ser");
                    for (int i = 0; i < lst.getLocationList().size(); i++){
                        if (lst.getLocationList().get(i).getFilepath() == null){
                            lst.getLocationList().remove(i);
                        }
                        for (int j = 0; j < lst.getLocationList().get(i).getObjects().size(); j++){
                            if (lst.getLocationList().get(i).getObjects().get(j).getFile_path() == null){
                                lst.getLocationList().get(i).getObjects().remove(j);
                            }
                        }
                    }
                    try {
                        // write object to file
                        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
                        oos.writeObject(lst);
                        oos.flush();
                        oos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                Runtime.getRuntime().halt(0);
            }
        }));

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Montana Map");
        run();
        this.setLayout(new BorderLayout());
        pack();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);

    }


    public void run() {
        File savefile = new File("mapdata.ser");
        panel = new ImagePanel( "examplemap.jpg");
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

        } catch (ClassNotFoundException e){
            e.printStackTrace();
        } catch (InstantiationException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e){
            e.printStackTrace();
        }
        panel.setLayout(null);
        scrollPane = new JScrollPane(panel);
        zoom = new ImageZoom(panel, this);
        this.add(BorderLayout.CENTER, scrollPane);
        if(savefile.exists()){
            try{
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savefile));
                lst = (JList_Struct)ois.readObject();
                ois.close();
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            catch (ClassNotFoundException e){
                e.printStackTrace();
            }
        } else {
            DefaultListModel<LocationModel> lst_n = new DefaultListModel<>();
            lst = new JList_Struct(lst_n);
        }
        JPanel buttonPanelholder = new JPanel();
        JPanel bufferPanel0 = new JPanel();

        bufferPanel0.setLayout(new BorderLayout());
        buttonPanelholder.setLayout(new GridLayout(2,1));
        buttonPanel = new JPanel();
        GridLayout layout = new GridLayout(4,2);
        buttonPanel.setLayout(layout);
        JLayeredPane lp = getLayeredPane();
        buttonPanelholder.add(buttonPanel);
        buttonPanelholder.add(bufferPanel0);



        final JButton remove_object_entry_button = new JButton("Remove selected object");
        final JButton remove_location_entry_button = new JButton("Remove selected map location");
        final JButton add_entry_button = new JButton("Add new location");
        final JButton add_object_button = new JButton("Add new object");

        buttonPanel.add(add_entry_button);
        buttonPanel.add(add_object_button);
        buttonPanel.add(remove_location_entry_button);
        buttonPanel.add(remove_object_entry_button);
        buttonPanel.add(zoom.getUIPanel());

        bufferPanel0.add(lst.getNestedList());
        buttonPanelholder.setBorder(BorderFactory.createEmptyBorder(hGap, vGap, hGap, vGap));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(hGap, vGap, hGap, vGap));
        this.add(BorderLayout.WEST,buttonPanelholder);


        pack();

        JPanel object_pane = drawObject();
        object_pane.setLocation(scrollPane.getLocation());
        objectpanel = object_pane;
        lp.add(object_pane);

        glasspane = drawLine();
        glasspane.setLocation(scrollPane.getLocation());
        lp.add(glasspane);


        remove_location_entry_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                removeLocationEntry();
            }
        });

        remove_object_entry_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeObjectEntry();
            }
        });

        add_entry_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel.getScale() == 1) {
                    if (addEntry(getInputFromUser())) {
                        buttonPanel.setVisible(false);
                        glasspane.setSize(scrollPane.getSize().width - 18, scrollPane.getSize().height - 18);
                        // -18 for the size of the scrollbars at the bottom of the panel/ side of panel.
                        lineDrawn = false;
                        glasspane.setEnabled(true);
                        glasspane.setVisible(true);
                    }
                }
                else {
                    imagePanelList.forEach(imagePanel -> imagePanel.setScale(1));
                    objectPanelList.forEach(objectpanel -> objectpanel.setScale(1));
                    objectTextPanelList.forEach(objecttextpanel -> objecttextpanel.setScale(1));
                    locationTextPanelList.forEach(locationpanel -> locationpanel.setScale(1));
                    panel.setScale(1);

                    ChangeListener spinnerlistener = zoom.getSpinnerListener();
                    zoom.getSpinner().removeChangeListener(spinnerlistener); //this is needed, was firing when changed manually
                    zoom.getSpinner().setValue(1.00);
                    zoom.setPreviousScale(1);
                    zoom.getSpinner().addChangeListener(spinnerlistener);

                    JOptionPane.showMessageDialog(panel,"Can only draw at 100% zoom. Please try Again.");
                }
            }
        });

        add_object_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!(lst.getNestedList().getJfirstList().getSelectedValue() == null)) {
                    if (panel.getScale() == 1) {
                        if (addObjectEntry(getObjectInputFromUser())) {
                            buttonPanel.setVisible(false);
                            object_pane.setSize(scrollPane.getSize().width - 18, scrollPane.getSize().height - 18);
                            lineDrawn = false;
                            objectpanel.setEnabled(true);
                            objectpanel.setVisible(true);
                        }
                    } else {
                        imagePanelList.forEach(imagePanel -> imagePanel.setScale(1));
                        objectPanelList.forEach(objectpanel -> objectpanel.setScale(1));
                        objectTextPanelList.forEach(objecttextpanel -> objecttextpanel.setScale(1));
                        locationTextPanelList.forEach(locationpanel -> locationpanel.setScale(1));
                        panel.setScale(1);

                        ChangeListener spinnerlistener = zoom.getSpinnerListener();
                        zoom.getSpinner().removeChangeListener(spinnerlistener);
                        zoom.getSpinner().setValue(1.00);
                        zoom.setPreviousScale(1);
                        zoom.getSpinner().addChangeListener(spinnerlistener);

                        JOptionPane.showMessageDialog(panel, "Can only draw at 100% zoom. Please try Again.");
                    }
                }else {
                    JOptionPane.showMessageDialog(panel, "Must select a location to place an object.");
                }
            }
        });

        lst.getJFirstList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = lst.getJFirstList().getSelectedIndex();
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    scrollPane.getViewport().setViewPosition(lst.getLocationList().get(index).getLocationviewpoint());
                } else if (e.getClickCount() == 3) {
                    TitledBorder border = new TitledBorder(lst.getLocationList().get(index).getLocationname());
                    border.setTitleJustification(TitledBorder.CENTER);
                    border.setTitlePosition(TitledBorder.TOP);
                    JButton update_year = new JButton("Update Year");
                    JButton update_elevation = new JButton("Update Elevation");
                    JFrame updateframe = new JFrame();
                    JPanel myPanel = new JPanel();
                    JLabel elevationlable = new JLabel("Elevation is: " + lst.getLocationList().get(index).getElevation());
                    JLabel yearlabel = new JLabel("Year is: " + lst.getLocationList().get(index).getYear());
                    myPanel.add(elevationlable);
                    myPanel.add(update_elevation);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(yearlabel);
                    myPanel.add(update_year);
                    myPanel.setBorder(border);

                    update_elevation.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JTextField elevationfield = new JTextField(5);
                            myPanel.remove(update_elevation);
                            myPanel.remove(elevationlable);
                            myPanel.remove(update_year);
                            myPanel.remove(yearlabel);
                            myPanel.add(elevationlable);
                            myPanel.add(elevationfield);
                            myPanel.add(yearlabel);
                            int result = JOptionPane.showConfirmDialog(null, myPanel, "Please update elevation", JOptionPane.OK_CANCEL_OPTION);
                            if(result == JOptionPane.OK_OPTION && !elevationfield.getText().isEmpty() && elevationfield.getText().matches("-?\\d+(\\.\\d+)?")){
                                lst.getLocationList().get(index).setElevation(Integer.parseInt(elevationfield.getText()));
                            } else {
                                JOptionPane.showMessageDialog(panel, "Please enter a valid elevation");
                            }
                            updateframe.setVisible(false);
                        }
                    });

                    update_year.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JTextField yearfield = new JTextField(5);
                            myPanel.remove(update_elevation);
                            myPanel.remove(elevationlable);
                            myPanel.remove(update_year);
                            myPanel.remove(yearlabel);
                            myPanel.add(elevationlable);
                            myPanel.add(yearlabel);
                            myPanel.add(yearfield);
                            int result = JOptionPane.showConfirmDialog(null, myPanel, "Please update elevation", JOptionPane.OK_CANCEL_OPTION);
                            if(result == JOptionPane.OK_OPTION && !yearfield.getText().isEmpty()&& yearfield.getText().matches("-?\\d+?")){
                                lst.getLocationList().get(index).setYear(Integer.parseInt(yearfield.getText()));
                            } else {
                                JOptionPane.showMessageDialog(panel, "Please enter a valid Year");

                            }
                            updateframe.setVisible(false);
                        }
                    });
                    myPanel.setVisible(true);
                    updateframe.add(myPanel);
                    updateframe.setSize(600,100);
                    updateframe.setVisible(true);

                }
            }
        });

        lst.getJSecondList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = lst.getJSecondList().getSelectedIndex();
                super.mouseClicked(e);
                if (e.getClickCount() == 3) {
                    TitledBorder border = new TitledBorder(lst.getLocationList().get(index).getLocationname());
                    border.setTitleJustification(TitledBorder.CENTER);
                    border.setTitlePosition(TitledBorder.TOP);
                    JButton update_year = new JButton("Update Year");
                    JButton update_elevation = new JButton("Update Elevation");
                    JButton update_type = new JButton("Update type of object");
                    JFrame updateframe = new JFrame();
                    JPanel myPanel = new JPanel();
                    JLabel elevationlable = new JLabel("Elevation is: " + lst.getLocationList().get(lst.getJFirstList().getSelectedIndex()).getObjects().get(index).getElevation());
                    JLabel yearlabel = new JLabel("Year is: " + lst.getLocationList().get(lst.getJFirstList().getSelectedIndex()).getObjects().get(index).getYear());
                    JLabel typelabel = new JLabel("Object type is: " + lst.getLocationList().get(lst.getJFirstList().getSelectedIndex()).getObjects().get(index).getType());
                    JLabel elevationlableminus = new JLabel("Elevation is: " );
                    JLabel yearlabelminus = new JLabel("Year is: " );
                    JLabel typelabelminus = new JLabel("Object type is: " );

                    myPanel.add(elevationlable);
                    myPanel.add(update_elevation);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(yearlabel);
                    myPanel.add(update_year);
                    myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                    myPanel.add(typelabel);
                    myPanel.add(update_type);
                    myPanel.setBorder(border);


                    update_elevation.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JTextField elevationfield = new JTextField(5);
                            myPanel.remove(update_elevation);
                            myPanel.remove(elevationlable);
                            myPanel.remove(update_year);
                            myPanel.remove(yearlabel);
                            myPanel.remove(update_type);
                            myPanel.remove(typelabel);

                            myPanel.add(elevationlableminus);
                            myPanel.add(elevationfield);
                            myPanel.add(yearlabel);
                            myPanel.add(typelabel);
                            int result = JOptionPane.showConfirmDialog(null, myPanel, "Please update elevation", JOptionPane.OK_CANCEL_OPTION);
                            if(result == JOptionPane.OK_OPTION && !elevationfield.getText().isEmpty() && elevationfield.getText().matches("-?\\d+(\\.\\d+)?")){
                                lst.getLocationList().get(lst.getJFirstList().getSelectedIndex()).getObjects().get(index).setElevation(Integer.parseInt(elevationfield.getText()));
                            } else {
                                JOptionPane.showMessageDialog(panel, "Please enter a valid elevation");
                            }
                            updateframe.setVisible(false);
                        }
                    });

                    update_year.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JTextField yearfield = new JTextField(5);
                            myPanel.remove(update_elevation);
                            myPanel.remove(elevationlable);
                            myPanel.remove(update_year);
                            myPanel.remove(yearlabel);
                            myPanel.remove(update_type);
                            myPanel.remove(typelabel);

                            myPanel.add(elevationlable);
                            myPanel.add(yearlabelminus);
                            myPanel.add(yearfield);
                            myPanel.add(typelabel);
                            int result = JOptionPane.showConfirmDialog(null, myPanel, "Please update year", JOptionPane.OK_CANCEL_OPTION);
                            if(result == JOptionPane.OK_OPTION && !yearfield.getText().isEmpty()&& yearfield.getText().matches("-?\\d+?")){
                                lst.getLocationList().get(lst.getJFirstList().getSelectedIndex()).getObjects().get(index).setYear(Integer.parseInt(yearfield.getText()));
                            } else {
                                JOptionPane.showMessageDialog(panel, "Please enter a valid Year");

                            }
                            updateframe.setVisible(false);
                        }
                    });

                    update_type.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            JTextField typefield = new JTextField(5);
                            myPanel.remove(update_elevation);
                            myPanel.remove(elevationlable);
                            myPanel.remove(update_year);
                            myPanel.remove(yearlabel);
                            myPanel.remove(update_type);
                            myPanel.remove(typelabel);

                            myPanel.add(elevationlable);
                            myPanel.add(yearlabel);
                            myPanel.add(typelabelminus);
                            myPanel.add(typefield);
                            int result = JOptionPane.showConfirmDialog(null, myPanel, "Please update object type", JOptionPane.OK_CANCEL_OPTION);
                            if(result == JOptionPane.OK_OPTION && !typefield.getText().isEmpty()&& typefield.getText().matches("([A-Z]|[a-z]| |-)*")){
                                lst.getLocationList().get(lst.getJFirstList().getSelectedIndex()).getObjects().get(index).setType(typefield.getText());
                            } else {
                                JOptionPane.showMessageDialog(panel, "Please enter a valid Object Type ");

                            }
                            updateframe.setVisible(false);
                        }
                    });
                    myPanel.setVisible(true);
                    updateframe.add(myPanel);
                    updateframe.setSize(800,100);
                    updateframe.setVisible(true);

                }
            }
        });

        lst.getJFirstList().setVisible(true);

        //for every location and object place all of the images and get the max numbers for each
        for (int i = 0; i < lst.getLocationList().size(); i++){
            placeImage(lst.getLocationList().getElementAt(i).getFilepath());
            placeTextLocation(lst.getLocationList().getElementAt(i).getLocationtextfilepath());

            for(int j = 0; j < (lst.getLocationList().getElementAt(i).getObjects()).size(); j++){
                placeTextObject(lst.getLocationList().getElementAt(i).getObjects().get(j).gettextFile_path());
                placeObject(lst.getLocationList().getElementAt(i).getObjects().get(j).getFile_path());
                objectnumber = (lst.getLocationList().getElementAt(i).getObjects().get(j).getObjectnumber() > objectnumber) ?
                        lst.getLocationList().getElementAt(i).getObjects().get(j).getObjectnumber() + 1 : objectnumber + 1;

                objecttextnumber = (lst.getLocationList().getElementAt(i).getObjects().get(j).getObjecttextnumber() > objecttextnumber) ?
                        lst.getLocationList().getElementAt(i).getObjects().get(j).getObjecttextnumber() + 1 : objecttextnumber + 1;
            }

            fileNumber = (lst.getLocationList().getElementAt(i).getFilenumber() > fileNumber) ?
                    lst.getLocationList().getElementAt(i).getFilenumber() + 1 : fileNumber + 1; // increase to 1 + highest file found this may be wrong

            locationtextnumber = (lst.getLocationList().getElementAt(i).getLocationnumber() > locationtextnumber) ?
                    lst.getLocationList().getElementAt(i).getLocationnumber() + 1 : locationtextnumber + 1;
        }
    } // run()





    public boolean addEntry(LocationModel loc) {
        if (loc.getLocationname() != null && loc.getElevation() != 0 && loc.getYear() != 0 ) {
            lst.getLocationList().addElement(loc);
            lst.getNestedList().addItem(loc.getLocationname());
            return true;
        } else {
            return false;
        }
    }

    public boolean addObjectEntry(Object_Model obj) {
        if (obj.getName() != null && obj.getElevation() != 0 && obj.getYear() != 0 && lst.getJFirstList().getSelectedIndex() != -1) {
            if(obj.getType() == null){
                obj.setType("-");
            }
            lst.getLocationList().getElementAt(lst.getJFirstList().getSelectedIndex()).addObject(obj);
            lst.getNestedList().addSubItem(obj.getName());
            return true;
        } else {
            return false;
        }
    }

    public void removeLocationEntry() {
        if (lst.getLocationList().isEmpty() || lst.getJFirstList().getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(panel,"Please select a Location to delete.");
        } else {
            int index = lst.getJFirstList().getSelectedIndex();

            File loc_to_delete = new File(lst.getLocationList().get(index).getFilepath());
            File loc_text_to_delete = new File(lst.getLocationList().get(index).getLocationtextfilepath());
            loc_text_to_delete.delete();
            loc_to_delete.delete();
            lst.getNestedList().removeSubItems();
            for (int i = 0; i < lst.getLocationList().get(index).getObjects().size(); i++){
                panel.remove(lst.getLocationList().get(index).getObjects().get(i).getObjectpanel());
                panel.remove(lst.getLocationList().get(index).getObjects().get(i).gettextSpot());
                File obj_to_delete = new File(lst.getLocationList().get(index).getObjects().get(i).getFile_path());
                obj_to_delete.delete();
                File txt_obj_to_delete = new File(lst.getLocationList().get(index).getObjects().get(i).gettextFile_path());
                txt_obj_to_delete.delete();
            }
            lst.getNestedList().removeItem();
            lst.getLocationList().getElementAt(index).removeObjects();
            lst.getLocationList().remove(index);
            panel.remove(imagePanelList.get(index));
            panel.remove(locationTextPanelList.get(index));
            imagePanelList.remove(index);
            locationTextPanelList.remove(index);
            repaint();
        }
    }

    public void removeObjectEntry() {
        if (!lst.getLocationList().isEmpty() && !(lst.getLocationList().getElementAt(lst.getJFirstList().getSelectedIndex()).getObjects().isEmpty())) {
            int first_index = lst.getJFirstList().getSelectedIndex();
            int second_index = lst.getJSecondList().getSelectedIndex();

            lst.getNestedList().removeSubItem();
            panel.remove(lst.getLocationList().get(first_index).getObjects().get(second_index).getObjectpanel());
            panel.remove(lst.getLocationList().get(first_index).getObjects().get(second_index).gettextSpot());

            File obj_to_delete = new File(lst.getLocationList().get(first_index).getObjects().get(second_index).getFile_path());
            obj_to_delete.delete();
            File txt_obj_to_delete = new File(lst.getLocationList().get(first_index).getObjects().get(second_index).gettextFile_path());
            txt_obj_to_delete.delete();
            lst.getLocationList().getElementAt(first_index).getObjects().remove(second_index);
            repaint();
        }
        else{
            JOptionPane.showMessageDialog(panel,"Please select a Object to delete.");
        }
    }

    public LocationModel getInputFromUser() {
        JTextField loc_name = new JTextField(5);
        JTextField year = new JTextField(5);
        JTextField elevation = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Location Name:"));
        myPanel.add(loc_name);
        myPanel.add(Box.createHorizontalStrut(10)); // a spacer
        myPanel.add(new JLabel("Year Found/Entered:"));
        myPanel.add(year);
        myPanel.add(Box.createHorizontalStrut(10)); // a spacer
        myPanel.add(new JLabel("Elevation of area:"));
        myPanel.add(elevation);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter Location name, Year found/current year, Elevation of the area", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && loc_name.getText().matches("([A-Z]|[a-z]| -)*")  && year.getText().matches("-?\\d+(\\.\\d+)?") && elevation.getText().matches("-?\\d+(\\.\\d+)?")) {
            try{
                Integer.parseInt(elevation.getText());
            }catch (NumberFormatException e) {
                return new LocationModel();
            }
            LocationModel location = new LocationModel(loc_name.getText(),Integer.parseInt(year.getText())
                    ,Integer.parseInt(elevation.getText()));
            return location;
        } else {
            JOptionPane.showMessageDialog(panel,"Please enter all relevant fields.");

        }
        return new LocationModel();
    }

    public Object_Model getObjectInputFromUser() {
        JTextField obj_name = new JTextField(5);
        JTextField year = new JTextField(5);
        JTextField elevation = new JTextField(5);
        JTextField object_type = new JTextField(5);

        JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Object Name:"));
        myPanel.add(obj_name);
        myPanel.add(Box.createHorizontalStrut(10)); // a spacer
        myPanel.add(new JLabel("Year Found/Entered:"));
        myPanel.add(year);
        myPanel.add(Box.createHorizontalStrut(10)); // a spacer
        myPanel.add(new JLabel("Elevation of area:"));
        myPanel.add(elevation);
        myPanel.add(new JLabel("Type of object:"));
        myPanel.add(object_type);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter Object name, Year found/current year, Elevation of the area, and optional object type", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && obj_name.getText().matches("([A-Z]|[a-z]| |-)*") && year.getText().matches("-?\\d+(\\.\\d+)?") && elevation.getText().matches("-?\\d+(\\.\\d+)?")) {
                try {
                    Integer.parseInt(elevation.getText());
                    Integer.parseInt(year.getText());
                } catch (NumberFormatException e) {
                    return new Object_Model();
                }
                if (object_type.getText().matches("([A-Z]|[a-z]| |-)*")) { //has a type
                    Object_Model object = new Object_Model(obj_name.getText(), Integer.parseInt(year.getText())
                             , Integer.parseInt(elevation.getText()), object_type.getText());
                    return object;
                }
                else{ //no type
                    Object_Model object = new Object_Model(obj_name.getText(), Integer.parseInt(year.getText())
                            , Integer.parseInt(elevation.getText()));
                    return object;
                }
        }
        else{
            JOptionPane.showMessageDialog(panel,"Please enter all relevant fields.");
        }
        return new Object_Model();
    }

    public JPanel drawLine() {
        JPanel p = new JPanel(){
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                for(Shape s : shapeList) {
                    g2.draw(s);
                }
            }
        };
        p.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(!lineDrawn) {
                    trueStart = e.getPoint();
                    pointStart = e.getPoint();
                    currentshape = new Path2D.Float();
                    currentshape.moveTo(pointStart.getX(),pointStart.getY());
                    shapeList.add(currentshape);
                }

            }
            public void mouseReleased(MouseEvent e) {
                if(!lineDrawn) {
                    pointStart = null;
                    lineDrawn = true;
                    currentshape.lineTo(trueStart.getX(), trueStart.getY());
                    saveImage(captureImage(currentshape));
                    saveTextImage(texttoImage(lst.getLocationList().getElementAt(lst.getLocationList().getSize() - 1).getLocationname(),new Point(
                            (int)trueStart.getX() + (int)scrollPane.getViewport().getViewPosition().getX(),
                            (int)trueStart.getY() + (int)scrollPane.getViewport().getViewPosition().getY())));

                    placeImage();
                    placeTextLocation();
                    lst.getLocationList().getElementAt(lst.getLocationList().getSize() - 1).setLocationviewpoint(scrollPane.getViewport().getViewPosition());
                    lst.getLocationList().getElementAt(lst.getLocationList().getSize() - 1).setDrawingloc(trueStart);
                    shapeList.remove(currentshape);
                    currentshape.reset();
                    glasspane.setEnabled(false);
                    glasspane.setVisible(false);
                    buttonPanel.setVisible(true);
                    return;
                }
            }
        });


        p.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if(!lineDrawn) {
                    pointStart = pointEnd;
                    pointEnd = e.getPoint();
                    currentshape.lineTo(pointEnd.getX(),pointEnd.getY());
                    glasspane.repaint();
                }
            }
        });
        p.setBackground(new Color(0,0,0,0));
        p.setOpaque(false);
        p.setVisible(false);
        p.setEnabled(false);
        return p;
    }

    public JPanel drawObject() {
        JPanel p = new JPanel(){
            @Override
            public void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                for(Shape s : shapeList) {
                    g2.draw(s);
                }
            }
        };
        p.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if(!lineDrawn) {
                    pointStart = e.getPoint();
                    currentshape = new Path2D.Float();
                    currentshape.moveTo(pointStart.getX(),pointStart.getY());
                    shapeList.add(currentshape);
                }

            }

            public void mouseReleased(MouseEvent e) {
                if(!lineDrawn) {
                    lineDrawn = true;
                    //save the current shape then place it on panel.
                    saveObjectImage(captureImage(currentshape));
                    placeObject();


                    // !!!!!!!!!!!! THIS ONLY WORKS IF THEY ALWAYS CHOOSE THE LAST ITEM IN THE ARRAY, DOES NOT WORK IF THEY GO LOC1, LOC2, LOC1->OBJ. BREAKS !!!!!!!!!!!!!!!!!!!!!!
                    lst.getLocationList().getElementAt(lst.getJFirstList().getSelectedIndex())
                            .getObjects()
                            .get(lst.getLocationList().getElementAt(lst.getJFirstList().getSelectedIndex()).getObjects().size()-1)
                            .setObjectloc(new Point(
                                    (int)pointStart.getX() + (int)scrollPane.getViewport().getViewPosition().getX(),
                                    (int)pointStart.getY() + (int)scrollPane.getViewport().getViewPosition().getY()));

                    BufferedImage textimg = texttoImage(lst.getNestedList().getJfirstList().getSelectedValue().getSubitem(lst.getNestedList().getJfirstList().getSelectedValue().getSubitemsize() - 1),new Point(
                            (int)pointStart.getX() + (int)scrollPane.getViewport().getViewPosition().getX(),
                            (int)pointStart.getY() + (int)scrollPane.getViewport().getViewPosition().getY()));
                    //save the current text then places it on panel.
                    saveObjectTextImage(textimg);
                    placeTextObject();

                    shapeList.remove(currentshape);
                    currentshape.reset();
                    objectpanel.setEnabled(false);
                    objectpanel.setVisible(false);
                    buttonPanel.setVisible(true);
                    pointStart = null;
                    return;
                }
            }
        });

        p.setBackground(new Color(0,0,0,0));
        p.setOpaque(false);
        p.setVisible(false);
        p.setEnabled(false);
        return p;
    }



    private void saveImage(JPanel imagepanel){
        try {
            String filename = "SAVE" + fileNumber + ".png";
            int w = imagepanel.getWidth(), h = imagepanel.getHeight();
            BufferedImage image = new BufferedImage(w, h,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            imagepanel.printAll(g2);
            g2.dispose();
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    private void saveObjectImage(JPanel imagepanel){
        try {
            String filename = "Object" + objectnumber + ".png";
            int w = imagepanel.getWidth(), h = imagepanel.getHeight();
            BufferedImage image = new BufferedImage(w, h,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            imagepanel.printAll(g2);
            g2.dispose();
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    private void saveTextImage(BufferedImage txtimg){
        try {
            String filename = "LocationText" + locationtextnumber + ".png";
            ImageIO.write(txtimg, "png", new File(filename));
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    private void saveObjectTextImage(BufferedImage txtimg){
        try {
            String filename = "ObjectText" + objecttextnumber + ".png";
            ImageIO.write(txtimg, "png", new File(filename));
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void placeImage(){
        String filename = "SAVE" + fileNumber + ".png";
        ImageIcon imageIcon = new ImageIcon(filename);
        lst.getLocationList().getElementAt(lst.getLocationList().getSize()-1).setFilenumber(fileNumber);
        fileNumber++;
        Image tmpImage = imageIcon.getImage();
        lst.getLocationList().getElementAt(lst.getLocationList().getSize()-1).setFilepath(filename);
        BufferedImage image = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(tmpImage, 0, 0, null);
        tmpImage.flush();

        ImagePanel imagePanel = new ImagePanel(image, panel.getScale());
        imagePanelList.add(imagePanel);
        panel.add(imagePanel);
        imagePanel.setBounds(
                0,0,
                panel.getWidth(),
                panel.getHeight());


    }
    private void placeImage(String filename){
        ImageIcon imageIcon = new ImageIcon(filename);
        Image tmpImage = imageIcon.getImage();
        BufferedImage image = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        image.getGraphics().drawImage(tmpImage, 0, 0, null);
        tmpImage.flush();

        ImagePanel imagePanel = new ImagePanel(image, panel.getScale());
        imagePanelList.add(imagePanel);
        panel.add(imagePanel);
        imagePanel.setBounds(
                0,0,
                panel.getWidth(),
                panel.getHeight());
        imagePanel.setVisible(true);
    }

    private void placeObject(){
        String filename = "Object" + objectnumber + ".png";
        ImageIcon objectIcon = new ImageIcon(filename);
        Image tmpImage = objectIcon.getImage();
        BufferedImage object = new BufferedImage(objectIcon.getIconWidth(), objectIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        object.getGraphics().drawImage(tmpImage, 0, 0, null);
        objectnumber++;
        tmpImage.flush();

        ImagePanel objectPanel = new ImagePanel(object, panel.getScale());
        int index = lst.getJFirstList().getSelectedIndex();
        ArrayList<Object_Model> objects = lst.getLocationList().getElementAt(index).getObjects();
        objects.get(objects.size() - 1).setFilePath(filename);
        objects.get(objects.size() - 1).setObjectpanel(objectPanel);
        objectPanelList.add(objectPanel);
        panel.add(objectPanel);
        objectPanel.setBounds(
                0,0,
                panel.getWidth(),
                panel.getHeight());
        objectPanel.setVisible(true);
    }

    private void placeObject(String filename){
        ImageIcon objectIcon = new ImageIcon(filename);
        Image tmpImage = objectIcon.getImage();
        BufferedImage object = new BufferedImage(objectIcon.getIconWidth(), objectIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        object.getGraphics().drawImage(tmpImage, 0, 0, null);
        tmpImage.flush();

        ImagePanel objectPanel = new ImagePanel(object, panel.getScale());
        objectPanelList.add(objectPanel);
        panel.add(objectPanel);
        objectPanel.setBounds(
                0,0,
                panel.getWidth(),
                panel.getHeight());
        objectPanel.setVisible(true);
    }
    private void placeTextObject(){
        String filename = "ObjectText" + objecttextnumber + ".png";
        ImageIcon objecttextIcon = new ImageIcon(filename);
        Image tmpImage = objecttextIcon.getImage();
        BufferedImage objecttext = new BufferedImage(objecttextIcon.getIconWidth(), objecttextIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        objecttext.getGraphics().drawImage(tmpImage, 0, 0, null);
        //add the object to the location/object_model
        objecttextnumber++;
        tmpImage.flush();

        ImagePanel objecttextPanel = new ImagePanel(objecttext, panel.getScale());
        int index = lst.getJFirstList().getSelectedIndex();
        ArrayList<Object_Model> objects = lst.getLocationList().getElementAt(index).getObjects();
        objects.get(objects.size() - 1).settextFile_path(filename);
        objects.get(objects.size() - 1).settextPanel(objecttextPanel);
        objectTextPanelList.add(objecttextPanel);
        panel.add(objecttextPanel);
        objecttextPanel.setBounds(
                0,0,
                panel.getWidth(),
                panel.getHeight());
        objecttextPanel.setVisible(true);
    }

    private void placeTextObject(String filename){
        ImageIcon objecttextIcon = new ImageIcon(filename);
        Image tmpImage = objecttextIcon.getImage();
        BufferedImage object = new BufferedImage(objecttextIcon.getIconWidth(), objecttextIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        object.getGraphics().drawImage(tmpImage, 0, 0, null);
        tmpImage.flush();

        ImagePanel objecttextPanel = new ImagePanel(object, panel.getScale());
        objectTextPanelList.add(objecttextPanel);
        panel.add(objecttextPanel);
        objecttextPanel.setBounds(
                0,0,
                panel.getWidth(),
                panel.getHeight());
        objecttextPanel.setVisible(true);
    }

    private void placeTextLocation(){
        int index = lst.getLocationList().getSize() - 1;
        String filename = "LocationText" + locationtextnumber + ".png";
        ImageIcon locationtextIcon = new ImageIcon(filename);
        Image tmpImage = locationtextIcon.getImage();
        BufferedImage locationtext = new BufferedImage(locationtextIcon.getIconWidth(), locationtextIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        locationtext.getGraphics().drawImage(tmpImage, 0, 0, null);
        //add the object to the location/object_model
        lst.getLocationList().getElementAt(index).setLocationnumber(locationtextnumber);
        locationtextnumber++;
        tmpImage.flush();

        ImagePanel locationtextPanel = new ImagePanel(locationtext, panel.getScale());
        lst.getLocationList().get(index).setLocationtextfilepath(filename);
        locationTextPanelList.add(locationtextPanel);
        panel.add(locationtextPanel);
        locationtextPanel.setBounds(
                0,0,
                panel.getWidth(),
                panel.getHeight());
        locationtextPanel.setVisible(true);
    }

    private void placeTextLocation(String filename){
        ImageIcon locationtextIcon = new ImageIcon(filename);
        Image tmpImage = locationtextIcon.getImage();
        BufferedImage locationimg = new BufferedImage(locationtextIcon.getIconWidth(), locationtextIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        locationimg.getGraphics().drawImage(tmpImage, 0, 0, null);
        tmpImage.flush();

        ImagePanel locationtextPanel = new ImagePanel(locationimg, panel.getScale());
        locationTextPanelList.add(locationtextPanel);
        panel.add(locationtextPanel);
        locationtextPanel.setBounds(
                0,0,
                panel.getWidth(),
                panel.getHeight());
        locationtextPanel.setVisible(true);
    }

    public void updateMapInterfaceScale(double scale){
        updateImagePanelListscale(scale);
        updateObjectPanelListscale(scale);
        updateObjectTextPanelListscale(scale);
        updateLocationTextPanelListscale(scale);
    }


    public void updateImagePanelListscale(double scale){
        imagePanelList.forEach(imagePanel -> imagePanel.changeScale(scale));
    }
    public void updateObjectPanelListscale(double scale){
        objectPanelList.forEach(object_panel -> object_panel.changeScale(scale));
    }
    public void updateObjectTextPanelListscale(double scale){
        objectTextPanelList.forEach(objecttexpanel -> objecttexpanel.changeScale(scale));
    }
    public void updateLocationTextPanelListscale(double scale){
        locationTextPanelList.forEach(locationtextpanel -> locationtextpanel.changeScale(scale));
    }



    public JPanel captureImage(Shape p){

        AffineTransform affineTransform = new AffineTransform();
        affineTransform.translate(scrollPane.getViewport().getViewPosition().getX(),scrollPane.getViewport().getViewPosition().getY());
        PathIterator pathIterator = p.getPathIterator(affineTransform);
        final float coords[] = new float[2];
        Path2D scaleddrawing = new Path2D.Float();
        pathIterator.currentSegment(coords);
        scaleddrawing.moveTo(coords[0],coords[1]);
        pathIterator.next();
        for (;!pathIterator.isDone(); pathIterator.next()){
            pathIterator.currentSegment(coords);
            scaleddrawing.lineTo(coords[0],coords[1]);
        }

        JPanel mappanel = new JPanel(){
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER));
                g2.draw(scaleddrawing);

            }
            public Dimension getPreferredSize() {
                int w = (int) (panel.getScale() * panel.getWidth());
                int h = (int) (panel.getScale() * panel.getHeight());
                return new Dimension(w, h);
            }
        };
        mappanel.setBackground(new Color(0,0,0,0));
        mappanel.setOpaque(false);
        mappanel.setVisible(true);
        mappanel.setEnabled(true);
        mappanel.setSize(panel.getWidth(),panel.getHeight());
        mappanel.repaint();

        return mappanel;
    }
    public BufferedImage texttoImage(String text, Point location) {
        Color outlineColor = Color.black;
        Color fillColor = Color.white;
        BasicStroke outlineStroke = new BasicStroke(5.0f);
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.HANGING_BASELINE, 20);
        g2d.setFont(font);
        int width = panel.getWidth();
        int height = panel.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);

        GlyphVector glyphVector = getFont().createGlyphVector(g2d.getFontRenderContext(), text);
        Shape textShape = glyphVector.getOutline((float)location.getX(),(float)location.getY());

        g2d.setColor(outlineColor);
        g2d.setStroke(outlineStroke);
        g2d.draw(textShape); // draw outline

        g2d.setColor(fillColor);
        g2d.fill(textShape); //fill for black bordered white text
        g2d.dispose();
        return img;
    }
}
// TODO: 7/13/2019 lettering layering top layer
// TODO: 7/13/2019 swap between two images (sat/topographic) 
// TODO: 7/13/2019 stitching map fragments together so they are both the same maps