package panels;




import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


public class PanelBorder extends javax.swing.JPanel { //panelborder is child class and it extends from parent class Jpanel thus inherits all its properties

  
    public PanelBorder() {
        initComponents();
        setOpaque(false);//making the form itself transparent and then we create a rounded rectangular shape to get the rounded form
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 44, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    
   @Override//we are overriding the normal painting of a panel bcz we need a custom panel
protected void paintComponent(Graphics grphcs) { //Graphics is a Type(class) like String and grphcs is the variable name

    super.paintComponent(grphcs);    //calls the original JPanel painting method before custom drawing bcz it clears the panel area to prevent drawing glitches and ensures correct handling of opacity
    Graphics2D g2 = (Graphics2D) grphcs; //converting basic Graphics class to Graphics2D
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //smoothing
    g2.setColor(getBackground()); //returns panel bgcolor
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);//creating a rounded rectangle which starts at x,y 0,0(top left) and getting the full height and width of the panel and making the radius width and height to 20,20
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
