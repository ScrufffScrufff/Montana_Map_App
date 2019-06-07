

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.xml.soap.Text;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Interface extends JFrame {
    private int lstactvfrm;
    private int  fileNumber = 0, objectnumber = 0, objecttextnumber = 0;
    private JList_Struct lst;
    private JScrollPane scrollPane;
    private DefaultListModel<JPanel> lst_jp;
    private JPanel object_panel;
    private Point pointStart = null;
    private Point pointEnd = null;
    private Point trueStart = null;
    private boolean lineDrawn = false;
    private ImagePanel panel;
    private ImageZoom zoom;
    private List<ImagePanel> imagePanelList = new ArrayList<>();
    private List<ImagePanel> objectPanelList = new ArrayList<>();
    private List<ImagePanel> objectTextPanelList = new ArrayList<>();



    ArrayList<Shape> shapeList = new ArrayList<>();
    Path2D.Float currentshape = null;



    public Interface() {
        super();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                if(!lst.getRlist().isEmpty()) {
                    File f = new File("mapdata.ser");
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
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        run();
        setVisible(true);
    }


    public void run() {
        File savefile = new File("mapdata.ser");
        panel = new ImagePanel( "C:\\Users\\Xain\\Pictures\\Montanaappmap\\Montana_Topo_Map.png");
        panel.setLayout(null);
        lstactvfrm = 0;
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
            DefaultListModel<Location_Model> lst_n = new DefaultListModel<>();
            lst = new JList_Struct(lst_n);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        JLayeredPane lp = getLayeredPane();
        lst_jp = new DefaultListModel<>();





        final JButton remove_entry_button = new JButton("Remove the selected map location");
        final JButton add_entry_button = new JButton("Add new map location");
        final JButton add_object_button = new JButton("Add new object to the location");
        buttonPanel.add(BorderLayout.EAST, add_object_button);
        buttonPanel.add(BorderLayout.NORTH,remove_entry_button);
        buttonPanel.add(BorderLayout.CENTER,add_entry_button);
        buttonPanel.add(BorderLayout.WEST,zoom.getUIPanel());
        buttonPanel.add(BorderLayout.AFTER_LAST_LINE, lst.getListScroller());
        this.add(BorderLayout.WEST, buttonPanel);
        remove_entry_button.setPreferredSize(new Dimension(remove_entry_button.getPreferredSize().width, 100));
        add_entry_button.setPreferredSize(new Dimension(add_entry_button.getPreferredSize().width, 100));
        add_object_button.setPreferredSize(new Dimension(add_entry_button.getPreferredSize().width, 100));
        pack();

        JPanel object_pane = Draw_Object();
        object_pane.setLocation(scrollPane.getLocation());
        object_panel = object_pane;
        lp.add(object_pane);

        JPanel glass_pane = Draw_Line();
        glass_pane.setLocation(scrollPane.getLocation());
        lst_jp.addElement(glass_pane);
        lp.add(glass_pane);


        lp.addComponentListener(new JLayeredPaneListener(scrollPane));
        //allow user to doubbleclick on the already entered item to draw additional or re-draw


        remove_entry_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Remove_Entry();
            }
        });

        add_entry_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel.getScale() == 1) {
                    if (Add_Entry(getInputFromUser())) {
                        glass_pane.setSize(scrollPane.getSize().width - 18, scrollPane.getSize().height - 18);
                        lineDrawn = false;
                        lst_jp.get(lstactvfrm).setEnabled(true);
                        lst_jp.get(lstactvfrm).setVisible(true);
                    }
                }
                else {
                    imagePanelList.forEach(imagePanel -> imagePanel.setScale(1));
                    objectPanelList.forEach(objectpanel -> objectpanel.setScale(1));
                    objectTextPanelList.forEach(objecttextpanel -> objecttextpanel.setScale(1));
                    panel.setScale(1);
                    ChangeListener spinnerlistener = zoom.getSpinnerlistener();
                    zoom.getSpinner().removeChangeListener(spinnerlistener);
                    zoom.getSpinner().setValue(1.00);
                    zoom.setpreviousScale(1);
                    zoom.getSpinner().addChangeListener(spinnerlistener);

                    JOptionPane.showMessageDialog(panel,"Can only draw at 100% zoom. Please try Again.");
                }
            }
        });

        add_object_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (panel.getScale() == 1) {
                    if (Add_Object_Entry(getObjectInputFromUser())) {
                        object_pane.setSize(scrollPane.getSize().width - 18, scrollPane.getSize().height - 18);
                        lineDrawn = false;
                        object_panel.setEnabled(true);
                        object_panel.setVisible(true);
                    }
                }
                else {
                    imagePanelList.forEach(imagePanel -> imagePanel.setScale(1));
                    objectPanelList.forEach(objectpanel -> objectpanel.setScale(1));
                    objectTextPanelList.forEach(objecttextpanel -> objecttextpanel.setScale(1));
                    panel.setScale(1);
                    ChangeListener spinnerlistener = zoom.getSpinnerlistener();
                    zoom.getSpinner().removeChangeListener(spinnerlistener);
                    zoom.getSpinner().setValue(1.00);
                    zoom.setpreviousScale(1);
                    zoom.getSpinner().addChangeListener(spinnerlistener);

                    JOptionPane.showMessageDialog(panel,"Can only draw at 100% zoom. Please try Again.");
                }
            }
        });

        lst.getJlist().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = lst.getJlist().getSelectedIndex();
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    scrollPane.getViewport().setViewPosition(lst.getRlist().get(index).getPoint()); //center it, currently off because topleft corner
                } else if (e.getClickCount() == 3) {

                }
            }
        });

        lst.getJlist().setVisible(true);
        for (int i = 0; i < lst.getRlist().size(); i++){ //place object?
            placeImage(lst.getRlist().getElementAt(i).getFile_path());
            placeName(lst.getRlist().getElementAt(i));


            for(int j = 0; j < (lst.getRlist().getElementAt(i).getObjects()).size(); j++){
                placeObjectName(lst.getRlist().getElementAt(i).getObjects().get(j));
                placeObject(lst.getRlist().getElementAt(i).getObjects().get(j).getFile_path());
                objectnumber = (lst.getRlist().getElementAt(i).getObjects().get(j).getObjectnumber() > objectnumber) ?
                        lst.getRlist().getElementAt(i).getObjects().get(j).getObjectnumber() + 1 : objectnumber + 1;
            }
            fileNumber = (lst.getRlist().getElementAt(i).getFilenumber() > fileNumber) ?
                    lst.getRlist().getElementAt(i).getFilenumber() + 1 : fileNumber + 1; // increase to 1 + highest file found this may be wrong
        }
    } // run()

    public boolean Add_Entry(Location_Model loc) {
        if (loc.getLocation_name() != null && loc.getElevtion() != 0 && loc.getYear() != null ) {
            lst.getStrlist().addElement(loc.getLocation_name());
            lst.getRlist().addElement(loc);
            return true;
        } else {
            return false;
        }
    }

    public boolean Add_Object_Entry(Object_Model obj) {
        if (obj.getName() != null && obj.getElevation() != 0 && obj.getYear() != 0 && lst.getJlist().getSelectedIndex() != -1) {
            if(obj.getType() == null){
                obj.setType("-");
            }
            lst.getRlist().getElementAt(lst.getJlist().getSelectedIndex()).addObject(obj);
            return true;
        } else {
            return false;
        }
    }

    public void Remove_Entry() {
        if (lst.getStrlist().isEmpty() || lst.getJlist().getSelectedIndex() == -1) {
        } else {
            int index = lst.getJlist().getSelectedIndex();
            File to_delete = new File(lst.getRlist().get(index).file_path);
            to_delete.delete();
            lst.getRlist().getElementAt(index).removeObjects();
            lst.getStrlist().remove(index);
            lst.getRlist().remove(index);
            panel.remove(imagePanelList.get(index));
            imagePanelList.remove(index);
            repaint();
        }
    }

    public Location_Model getInputFromUser() {
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
        if (result == JOptionPane.OK_OPTION) {
            try{
                Integer.parseInt(elevation.getText());
            }catch (NumberFormatException e) {
                return new Location_Model();
            }
            Location_Model location = new Location_Model(loc_name.getText(),year.getText()
                    ,Integer.parseInt(elevation.getText()));
            return location;
        }
        return new Location_Model();
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
        myPanel.add(object_type);//finish here

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Please Enter Object name, Year found/current year, Elevation of the area, and optional object type", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
                try {
                    Integer.parseInt(elevation.getText());
                    Integer.parseInt(year.getText());
                } catch (NumberFormatException e) {
                    return new Object_Model();
                }
                if (object_type.getText() != null) { //has a type
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
        return new Object_Model();
    }

    public JPanel Draw_Line() {
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
                    placeImage();
                    lst.getRlist().getElementAt(lst.getRlist().getSize() - 1).setPoint(scrollPane.getViewport().getViewPosition());
                    lst.getRlist().getElementAt(lst.getRlist().getSize() - 1).setDrawingloc(trueStart);
                    placeName(new Point(
                            (int)trueStart.getX() + (int)scrollPane.getViewport().getViewPosition().getX(),
                            (int)trueStart.getY() + (int)scrollPane.getViewport().getViewPosition().getY()));
                    shapeList.remove(currentshape);
                    currentshape.reset();
                    lst_jp.get(lstactvfrm).setEnabled(false);
                    lst_jp.get(lstactvfrm).setVisible(false);
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
                    lst_jp.get(lstactvfrm).repaint();
                }
            }
        });
        p.setBackground(new Color(0,0,0,0));
        p.setOpaque(false);
        p.setVisible(false);
        p.setEnabled(false);
        return p;
    }

    public JPanel Draw_Object() {
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
                    saveObjectImage(captureImage(currentshape));
                    placeObject();
                    lst.getRlist().getElementAt(lst.getRlist().getSize() - 1)
                            .getObjects()
                            .get(lst.getRlist().getElementAt(lst.getRlist().getSize() -1).getObjects().size()-1)
                            .setObjectloc(new Point(
                                    (int)pointStart.getX() + (int)scrollPane.getViewport().getViewPosition().getX(),
                                    (int)pointStart.getY() + (int)scrollPane.getViewport().getViewPosition().getY()));
                    placeObjectName(new Point(
                            (int)pointStart.getX() + (int)scrollPane.getViewport().getViewPosition().getX(),
                            (int)pointStart.getY() + (int)scrollPane.getViewport().getViewPosition().getY()));
                    shapeList.remove(currentshape);
                    currentshape.reset();
                    object_panel.setEnabled(false);
                    object_panel.setVisible(false);
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

//

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
    private void saveObjectTextImage(JPanel imagepanel){
        try {
            String filename = "ObjectText" + objecttextnumber + ".png";
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

    private void placeImage(){
        String filename = "SAVE" + fileNumber + ".png";
        ImageIcon imageIcon = new ImageIcon(filename);
        lst.getRlist().getElementAt(lst.getRlist().getSize()-1).setFilenumber(fileNumber);
        fileNumber++;
        Image tmpImage = imageIcon.getImage();
        lst.getRlist().getElementAt(lst.getRlist().getSize()-1).setFile_path(filename);
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
        //add the object to the location/object_model
        objectnumber++;
        tmpImage.flush();

        ImagePanel objectPanel = new ImagePanel(object, panel.getScale());
        int index = lst.getJlist().getSelectedIndex();
        ArrayList<Object_Model> objects = lst.getRlist().getElementAt(index).getObjects();
        objects.get(objects.size() - 1).setFile_path(filename);
        objects.get(objects.size() - 1).setSpot(objectPanel);
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
        // TODO: 6/6/2019 got it as image/imapgepanel just have to place it (call this somewhere)and then place it correctly within the right object model
        // TODO: 6/6/2019 keep track of it and load/save them correctly and delete correctly. brain mush atm.
        // TODO: 6/6/2019 Gotta make 2 of these for the location text as well now.
        String filename = "Object" + objecttextnumber + ".png";
        ImageIcon objecttextIcon = new ImageIcon(filename);
        Image tmpImage = objecttextIcon.getImage();
        BufferedImage objecttext = new BufferedImage(objecttextIcon.getIconWidth(), objecttextIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        objecttext.getGraphics().drawImage(tmpImage, 0, 0, null);
        //add the object to the location/object_model
        objecttextnumber++;
        tmpImage.flush();

        ImagePanel objecttextPanel = new ImagePanel(objecttext, panel.getScale());
        int index = lst.getJlist().getSelectedIndex();
        ArrayList<Object_Model> objects = lst.getRlist().getElementAt(index).getObjects();
        objects.get(objects.size() - 1).settextFile_path(filename);
        objects.get(objects.size() - 1).settextSpot(objecttextPanel);
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


    public void updateImagePanelListscale(double scale){
        imagePanelList.forEach(imagePanel -> imagePanel.changeScale(scale));
    }
    public void updateObjectPanelListscale(double scale){
        objectPanelList.forEach(object_panel -> object_panel.changeScale(scale));
    }
    public void updateObjectTextPanelListscale(double scale){
        objectTextPanelList.forEach(objecttexpanel -> objecttexpanel.changeScale(scale));
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
    public BufferedImage TexttoImage(String text, Point location) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font("Arial", Font.BOLD, 48);
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
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, (int)location.getX(), (int)location.getY());
        g2d.dispose();
        try {
            ImageIO.write(img, "png", new File("Text.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return img;
    }
    //   private void placeName(Point location){ //legacy
//        JLabel label = new JLabel((lst.getRlist().getElementAt(lst.getRlist().getSize() - 1).getLocation_name()));
//        int textbuffer = 25;
//        label.setSize(new Dimension(
//                (int) label.getPreferredSize().getWidth() + textbuffer ,
//                (int) label.getPreferredSize().getHeight() + textbuffer));
//        label.setLocation(location);
//        Font labelFont = label.getFont();
//        String labelText = label.getText();
//
//        int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
//        int componentWidth = label.getWidth();
//
//        // Find out how much the font can grow in width.
//        double widthRatio = (double)componentWidth / (double)stringWidth;
//
//        int newFontSize = (int)(labelFont.getSize() * widthRatio);
//        int componentHeight = label.getHeight();
//
//        // Pick a new font size so it will not be larger than the height of label.
//        int fontSizeToUse = Math.min(newFontSize, componentHeight);
//
//        // Set the label's font size to the newly determined size.
//        label.setFont(new Font(labelFont.getName(), Font.BOLD, fontSizeToUse));
//        label.setForeground(Color.RED);
//        panel.add(label);
//        label.setVisible(true);
//        label.setOpaque(false);
//    }
//
//    private void placeName(Location_Model loc){ //legacy
//        JLabel label = new JLabel(loc.getLocation_name());
//        int textbuffer = 25;
//        label.setSize(new Dimension(
//                (int) label.getPreferredSize().getWidth() + textbuffer ,
//                (int) label.getPreferredSize().getHeight() + textbuffer));
//        label.setLocation(new Point(
//                (int)(loc.getDrawingloc().getX() + loc.getPoint().getX()),
//                (int)(loc.getDrawingloc().getY() + loc.getPoint().getY())));
//        Font labelFont = label.getFont();
//        String labelText = label.getText();
//
//        int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
//        int componentWidth = label.getWidth();
//
//        // Find out how much the font can grow in width.
//        double widthRatio = (double)componentWidth / (double)stringWidth;
//
//        int newFontSize = (int)(labelFont.getSize() * widthRatio);
//        int componentHeight = label.getHeight();
//
//        // Pick a new font size so it will not be larger than the height of label.
//        int fontSizeToUse = Math.min(newFontSize, componentHeight);
//
//        // Set the label's font size to the newly determined size.
//        label.setFont(new Font(labelFont.getName(), Font.BOLD, fontSizeToUse));
//        label.setForeground(Color.RED);
//        panel.add(label);
//        label.setVisible(true);
//        label.setOpaque(false);
//    }
//
//    private void placeObjectName(Object_Model obj){ //legacy
//        saveObjectTextImage(new  ImagePanel(TexttoImage(obj.getName(),obj.getObjectloc()),panel.getScale()));
//    }
//    private void placeObjectName(Point location){ //legacy
//        // this long call is getting the name of the object.
//        JLabel label = new JLabel((lst.getRlist().getElementAt(lst.getRlist().getSize() - 1).getObjects()
//        .get(lst.getRlist().getElementAt(lst.getRlist().getSize() - 1).getObjects().size() -1)).getName());
//        int textbuffer = 25;
//        label.setSize(new Dimension(
//                (int) label.getPreferredSize().getWidth() + textbuffer ,
//                (int) label.getPreferredSize().getHeight() + textbuffer));
//        label.setLocation(location);
//        Font labelFont = label.getFont();
//        String labelText = label.getText();
//
//        int stringWidth = label.getFontMetrics(labelFont).stringWidth(labelText);
//        int componentWidth = label.getWidth();
//
//        // Find out how much the font can grow in width.
//        double widthRatio = (double)componentWidth / (double)stringWidth;
//
//        int newFontSize = (int)(labelFont.getSize() * widthRatio);
//        int componentHeight = label.getHeight();
//
//        // Pick a new font size so it will not be larger than the height of label.
//        int fontSizeToUse = Math.min(newFontSize, componentHeight);
//
//        // Set the label's font size to the newly determined size.
//        label.setFont(new Font(labelFont.getName(), Font.BOLD, fontSizeToUse));
//        label.setForeground(Color.GREEN);
//        panel.add(label);
//        label.setVisible(true);
//        label.setOpaque(false);
//    }
}
// TODO: 6/2/2019 fix up the button panel, allow deletion of objects/seeing objects/seeing details about the lists.