import javax.swing.*;
import java.awt.*;

public class LayeredTransparentPane extends JLayeredPane {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(new Color(0,0,0,0));
    }

}
