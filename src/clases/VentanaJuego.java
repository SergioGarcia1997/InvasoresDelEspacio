package clases;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;


/**
 *
 * @author jorgecisneros
 */
public class VentanaJuego extends javax.swing.JFrame {
        //alto y ancho de la ventana
    int anchoPantalla = 600;
    int altoPantalla = 450;

    //buffer para dibujar
    BufferedImage buffer = null;

    //declaro un objeto de tipo nave
    Nave miNave = new Nave(anchoPantalla);
    //declaro dos variables booleanas que controlen el movimiento de la nave
    boolean pulsadaDerecha = false;
    boolean pulsadaIzquierda = false;
   
    ArrayList <Disparo> listaDisparos = new ArrayList();
    Disparo disparoAux ;
    
    ArrayList <Marciano> listaMarcianos = new ArrayList();
    int velocidadMarciano = 1;
    ArrayList <Explosion> listaExplosiones = new ArrayList();
    
    int contadorTiempo = 0;
    
    //control del fin de partida
    boolean gameOver = false;
    
    
    Timer temporizador = new Timer(10, new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            //aqui llamo al método del juego
            bucleDelJuego();
        }
    });

    /**
     * Creates new form VentanaJuego
     */
    public VentanaJuego() {
        initComponents();
        //cambiamos las dimensiones de la ventana
        this.setSize(anchoPantalla, altoPantalla+40);

        //creamos el buffer a partir del jPanel
        buffer = (BufferedImage) jPanel1.createImage(anchoPantalla, altoPantalla);
        buffer.createGraphics();

        //inicio el juego
        temporizador.start();
        
        //posiciona la nave abajo del todo
        miNave.setX(anchoPantalla /2);
        miNave.setY(altoPantalla - miNave.imagenNave.getHeight(null));
        
        //inicializo el arraylist de los marcianos
        for(int j=0; j<4; j++){
            for (int i=0; i< 10; i++){
                Marciano m = new Marciano();
                m.setX(i * (m.ancho + 15) );
                m.setY(m.ancho * j);
                listaMarcianos.add(m);
            }
        }
     
    }
private void pintaMarcianos(Graphics2D miGrafico ){

        //recorre la lista de marcianos
        //los va pintando en su coordenada correspondiente
        //uso una variable booleana para indicar si ha tocado
        //en la pared derecha o en la izquierda
        boolean cambia = false;
        
        for (int i=0; i<listaMarcianos.size(); i++){
            Marciano m = listaMarcianos.get(i);
            m.setX(m.getX() + velocidadMarciano);
            //si choca en la pared derecha
            if ( (m.getX() + m.ancho) > anchoPantalla ){
                cambia = true;
            }
            //si choca en la pared izquierda
            if (m.getX() <= 0){
                cambia = true;
            }
            //dibujo la imagen correspondiente de los marcianos
            if (contadorTiempo < 50){
                miGrafico.drawImage(m.imagen1, m.getX(), m.getY(),null);
            }
            else {
                miGrafico.drawImage(m.imagen2, m.getX(), m.getY(),null);
            }
        }
        //si ha tocado, cambio la velocidad
        if (cambia){
            velocidadMarciano = -velocidadMarciano;
            for (int i=0; i<listaMarcianos.size(); i++){
                Marciano m = listaMarcianos.get(i);
                m.setY(m.getY() + m.ancho/2);
            }
        }
}

private void pintaNave( Graphics2D g2){
        if (pulsadaIzquierda){
            miNave.setX(miNave.getX() - 1);
        }
        else if (pulsadaDerecha){
            miNave.setX(miNave.getX() + 1);
        }        
        g2.drawImage(miNave.imagenNave, miNave.getX(), miNave.getY(), null);
}

private void pintaDisparos( Graphics2D g2){
            //pinto los disparos
        for (int i=0; i<listaDisparos.size(); i++){
            disparoAux = listaDisparos.get(i);
            disparoAux.setY( disparoAux.getY() - 3);
            if (disparoAux.getY() < 0){
              listaDisparos.remove(i);
            }
            g2.drawImage(disparoAux.imagenDisparo, disparoAux.getX(), disparoAux.getY(), null);
        }
}
 private void pintaExplosiones( Graphics2D g2){
            //pinto las explosiones
        for (int i=0; i<listaExplosiones.size(); i++){
            Explosion e = listaExplosiones.get(i);
            e.setTiempoDeVida(e.getTiempoDeVida() - 1);
            if (e.getTiempoDeVida() > 25){
                g2.drawImage(e.imagenExplosion, e.getX(), e.getY(), null);
            }
            else {
                g2.drawImage(e.imagenExplosion2, e.getX(), e.getY(), null);
            }
            
             //si el tiempo de vida de la explosión es menor que 0 la elimino
            if (e.getTiempoDeVida() <= 0){
                listaExplosiones.remove(i);
            }
        }
}
 
private void chequeaColisionMarcianoConNave(){
            //creo un marco para guardar el borde de la imagen de la nave
    Rectangle2D.Double rectanguloNave = new Rectangle2D.Double();
    rectanguloNave.setFrame(miNave.getX(), miNave.getY(), miNave.getAnchoNave(), miNave.getAnchoNave());
            //creo un marco para guardar el borde de la imagen del marciano
    Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
    for (int i=0; i< listaMarcianos.size(); i++){
            Marciano m = listaMarcianos.get(i);
            rectanguloMarciano.setFrame(m.getX(), m.getY(), m.ancho, m.ancho);
            if (rectanguloNave.intersects(rectanguloMarciano)){
                 //algún marciano ha tocado con la nave
                gameOver = true;
            }
    }
}

private void chequeaColision(){
        //creo un marco para guardar el borde de la imagen del marciano
    Rectangle2D.Double rectanguloMarciano = new Rectangle2D.Double();
        //creo un marco para guardar el borde de la imagen del disparo
    Rectangle2D.Double rectanguloDisparo = new Rectangle2D.Double(); 
    
        //ahora leo la lista de disparos 
    for (int j=0; j<listaDisparos.size(); j++){
        Disparo d = listaDisparos.get(j);
        //asigno al rectángulo las dimensiones del disparo y su posicion
        rectanguloDisparo.setFrame(d.getX(), d.getY(), d.imagenDisparo.getWidth(null), d.imagenDisparo.getHeight(null));
        boolean disparoABorrar = false;
        //leo la lista de marcianos y comparo uno a uno con el disparo
        for (int i=0; i< listaMarcianos.size(); i++){
            Marciano m = listaMarcianos.get(i);
            rectanguloMarciano.setFrame(m.getX(), m.getY(), m.ancho, m.ancho);
            if (rectanguloDisparo.intersects(rectanguloMarciano)){
                Explosion e = new Explosion();
                e.setX(m.getX()+10);
                e.setY(m.getY()+10);
                listaExplosiones.add(e);
                e.sonidoExplosion.start();
                listaMarcianos.remove(i);
                //no borro aqui el disparo para evitar que se cuelgue 
                //listaDisparos.remove(j);
                disparoABorrar = true;
            }
        }
        if (disparoABorrar){
            
            listaDisparos.remove(j);
        }
    }
    
}

private void actualizaContadorTiempo(){
    contadorTiempo ++;
    if (contadorTiempo > 100){
        contadorTiempo = 0;
    }
}

private void finDePartida (Graphics2D ramon){
        try {
            Image imagenLuser = ImageIO.read((getClass().getResource("/imagenes/luser.png")));
            ramon.drawImage(imagenLuser, 100, 100, null);
        } catch (IOException ex) {
        }
}

private void bucleDelJuego() {
    Graphics2D g2 = (Graphics2D) buffer.getGraphics();
    if (!gameOver){
            //pinto un rectángulo negro del tamaño de la ventana
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, anchoPantalla, altoPantalla);
            /////////////////////////////////////////////////////
            //////    codigo del juego    ///////////////////////
            pintaMarcianos(g2);
            pintaNave(g2);
            pintaDisparos(g2);
            chequeaColision();
            pintaExplosiones(g2);
            actualizaContadorTiempo();
            chequeaColisionMarcianoConNave();
            }
        else{
            finDePartida(g2);
        }
        /////////////////////////////////////////////////////
        //apunto al jPanel y dibujo el buffer sobre el jPanel
        g2 = (Graphics2D) jPanel1.getGraphics();
        g2.drawImage(buffer, 0, 0, null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_LEFT)
        {
           pulsadaIzquierda = true;
           pulsadaDerecha = false;
        }
        if (evt.getKeyCode() == KeyEvent.VK_RIGHT)
        {
           pulsadaIzquierda = false;
           pulsadaDerecha = true;
        }
        // añado un disparo si se ha pulsado la barra espaciadora
        if( (evt.getKeyCode() == KeyEvent.VK_SPACE) && (listaDisparos.size() < 6))
        {
           Disparo d = new Disparo();
           d.setX( miNave.getX()+ miNave.getAnchoNave()/2 - d.imagenDisparo.getWidth(null)/2);
           d.setY( miNave.getY());
           d.sonidoDisparo.start();
           //agrego el disparo a la lista de disparos
           listaDisparos.add(d);
        }
    }//GEN-LAST:event_formKeyPressed

    private void formKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyReleased
        if (evt.getKeyCode() != KeyEvent.VK_SPACE)
        {   
           pulsadaIzquierda = false;
           pulsadaDerecha = false;
        }
    }//GEN-LAST:event_formKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(VentanaJuego.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new VentanaJuego().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
