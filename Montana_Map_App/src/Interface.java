

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
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
    private int  fileNumber = 0;
    private JList_Struct lst;
    private JScrollPane scrollPane;
    private DefaultListModel<JPanel> lst_jp;
    private Point pointStart = null;
    private Point pointEnd = null;
    private Point trueStart = null;
    private boolean lineDrawn = false;
    private ImagePanel panel;
    private ImageZoom zoom;
    private List<ImagePanel> imagePanelList = new ArrayList<>();



    ArrayList<Shape> shapeList = new ArrayList<>();
    Path2D.Float currentshape = null;



    public Interface() {
        super();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            public void run() {
                if(lst != null) {
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

        panel = new ImagePanel( "C:\\Users\\Xain\\Pictures\\Montanaappmap\\Montana_Topo_Map.png");
        panel.setLayout(null);
        lstactvfrm = 0;
        scrollPane = new JScrollPane(panel);
        zoom = new ImageZoom(panel, this);


        this.add(BorderLayout.CENTER, scrollPane);
        final JButton remove_entry_button = new JButton("Remove the selected map location");
        final JButton add_entry_button = new JButton("Add new map location");
        buttonPanel.add(BorderLayout.NORTH,remove_entry_button);
        buttonPanel.add(BorderLayout.CENTER,add_entry_button);
        buttonPanel.add(BorderLayout.WEST,zoom.getUIPanel());
        buttonPanel.add(BorderLayout.AFTER_LAST_LINE, lst.getListScroller());
        this.add(BorderLayout.WEST, buttonPanel);
        remove_entry_button.setPreferredSize(new Dimension(remove_entry_button.getPreferredSize().width, 100));
        add_entry_button.setPreferredSize(new Dimension(add_entry_button.getPreferredSize().width, 100));
        pack();

        JPanel glass_pane = Draw_Line();
        glass_pane.setLocation(scrollPane.getLocation());
        lst_jp.addElement(glass_pane);
        //glass_pane.setBackground(Color.GREEN);
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

        lst.getJlst().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            }
        });


        lst.getJlst().setVisible(true);
    } // run()

    public boolean Add_Entry(Location_Model loc) {
        if (loc.getLocation_name() != null && loc.getElevtion() != 0 && loc.getYear() != null ) {
            lst.getRlist().addElement(loc.getLocation_name());

            return true;
        } else {
            return false;
        }
    }

    public void Remove_Entry() {
        if (lst.getRlist().isEmpty() || lst.getJlst().getSelectedIndex() == -1) {
        } else {
            int index = lst.getJlst().getSelectedIndex();
            lst.getRlist().remove(index);

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
            Location_Model location = new Location_Model(loc_name.getText(),year.getText()
                    ,Integer.parseInt(elevation.getText()));
            return location;
        }
        return new Location_Model();
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

    private void placeImage(){
        String filename = "SAVE" + fileNumber + ".png";
        ImageIcon imageIcon = new ImageIcon(filename);
        fileNumber++;
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



    }
    public void updateImagePanelListscale(double scale){
        imagePanelList.forEach(imagePanel -> imagePanel.changeScale(scale));
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
}
