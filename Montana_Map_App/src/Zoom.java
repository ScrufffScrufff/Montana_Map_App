
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;


class ImagePanel extends JPanel {
    private BufferedImage image;
    private double scale;
    private boolean ismap; //to tell the paintcomponent what comp it is painting

    public ImagePanel(String s)
    {
        ismap = true;
        loadImage(s);
        scale = 1.0;
        setBackground(Color.black);
    }

    public ImagePanel(BufferedImage b, double scle){
        ismap = false;
        image = b;
        scale = scle;
        setOpaque(false);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        double x,y;
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        int w = getWidth();
        int h = getHeight();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();


        if(ismap){
            x = (w - (scale * imageWidth))/2;
            y = (h - (scale * imageHeight))/2;
        }else {
            int wi = (int)(image.getWidth() * scale);
            int he = (int)(image.getHeight() * scale);

            x = (wi - (scale * imageWidth))/2;
            y = (he - (scale * imageHeight))/2;
        }
        AffineTransform at = AffineTransform.getTranslateInstance(x,y);
        at.scale(scale, scale);
        g2.drawRenderedImage(image, at);

    }

    /**
     * For the scroll pane.
     */
    public Dimension getPreferredSize() {
            int w = (int) (scale * image.getWidth());
            int h = (int) (scale * image.getHeight());
            return new Dimension(w, h);
    }



    public void changeScale(double s){
        scale += s;
        revalidate();
        repaint();
    }

    public void setScale(double s){
        scale = s;
        revalidate();
        repaint();
    }

    private void loadImage(String s)
    {
        String fileName = s;
        try
        {
            image = ImageIO.read(new File(fileName));
        }
        catch(MalformedURLException mue)
        {
            System.out.println("URL trouble: " + mue.getMessage());
        }
        catch(IOException ioe)
        {
            System.out.println("read trouble: " + ioe.getMessage());
        }
    }

    public double getScale(){
        return scale;
    }



}

class ImageZoom
{
    ImagePanel imagepanel;
    SpinnerNumberModel model =  new SpinnerNumberModel(1.0, 0.45, 10, .05);
    Interface aninterface;
    float previousscale;
    float scale;
    final JSpinner spinner = new JSpinner(model);
    ChangeListener spinnerlistener;

    public ImageZoom(ImagePanel ip, Interface inter)
    {
        imagepanel = ip;
        aninterface = inter;
        previousscale = 0;
        scale = 0;
    }

    public JSpinner getSpinner(){
        return spinner;
    }
    public void setPreviousScale(float scle){
        scale = scle;
    }

    public ChangeListener getSpinnerListener(){
        return spinnerlistener;
    }

    public JPanel getUIPanel()
    {
        spinner.setPreferredSize(new Dimension(45, spinner.getPreferredSize().height));
        spinnerlistener = new ChangeListener()
        {
            public void stateChanged(ChangeEvent e) {
                previousscale = scale;
                scale = ((Double) spinner.getValue()).floatValue();

                if (!(previousscale == scale)) {
                    if (previousscale > scale || (previousscale == 0 && scale < 1.00)) {
                        aninterface.updateMapInterfaceScale(-0.05);
                        imagepanel.changeScale(-0.05);
                    } else {
                        aninterface.updateMapInterfaceScale(0.05);
                        imagepanel.changeScale(0.05);
                    }

                }
            }
        };
        spinner.addChangeListener(spinnerlistener);

        JPanel panel = new JPanel();
        panel.add(new JLabel("scale"));
        panel.add(spinner);
        return panel;
    }

}