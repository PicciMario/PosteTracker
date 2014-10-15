/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postetracker;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import postetracker.tools.MultiLineCellRenderer;
import postetracker.tools.MyTableModel;

/**
 *
 * @author m.piccinelli
 */
public class PosteUI extends javax.swing.JFrame {

    static List<Product> productList;
    static String url = "http://www.poste.it/online/dovequando/ricerca.do";
    
    /**
     * Creates new form PosteUI
     */
    public PosteUI() {
        initComponents();
        
        initList();
        
        MyTableModel model = new MyTableModel();
        model.setData(productList.toArray(new Product[0]));
        jTableLista.setModel(model);
        
        jTableLista.setRowHeight(jTableLista.getRowHeight()*2 + 5);
        jTableLista.setDefaultRenderer(String.class, new MultiLineCellRenderer());
        jTableLista.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        jTableLista.getColumnModel().getColumn(0).setMaxWidth(130);
        jTableLista.getColumnModel().getColumn(0).setPreferredWidth(130);
        
        jTableLista.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()){
                    Product prod = productList.get(jTableLista.getSelectedRow());
                    String textarea = "";
                    for (ProductStatus status : prod.getStatuses()){
                        textarea += (status.getDateString() + " -> " + status.getStatus() + "\n");
                    }
                    jTextAreaDescription.setText(textarea);
                }
            }
        });
        
    }
    
    public void updateUI(){
        update();
        ((MyTableModel) jTableLista.getModel()).setData(productList.toArray(new Product[0]));
        jTableLista.repaint();
        jTextAreaDescription.setText("");
    }
    
    public static void update() {
        final JDialog d = new JDialog();
        JPanel p1 = new JPanel(new GridBagLayout());
        p1.add(new JLabel("Attendere..."), new GridBagConstraints());
        d.getContentPane().add(p1);
        d.setSize(200, 100);
        d.setTitle("Please wait...");
        d.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        d.setModal(true);
        
        SwingWorker<?, ?> worker = new SwingWorker<Void, Integer>() {
            
            @Override
            protected Void doInBackground() throws InterruptedException {
                aggiornaLista();
                return null;
            }
            
            @Override
            protected void done() {
                d.dispose();
            }
        };
        worker.execute();
        d.setVisible(true);
    }    
    
    
    public static void initList(){
        String[] codes = new String[]{"RI001384165CN", "RL040393084CN", "RO400390995CN", "RG058010681CN", "RG053889040CN", "RJ210248923CN"};
        String[] descs = new String[]{"Schermo GoPro", "Velcro Patch: Zombie Outbreak Response Team", "Tasca Cellulare", "Portachiavi tattico", "Ganci MOLLE", "Tasche cellulare (2x)"};
     
        // creazione lista con prodotti
        productList = new ArrayList<>();
        for (int i = 0; i < codes.length; i++){
            Product element = new Product();
            element.setCode(codes[i]);
            element.setDesc(descs[i]);
            productList.add(element);
        }        
        
    }
    
    public static void aggiornaLista(){
        
        for (Product prod : productList){
            leggiDettagli(prod);
        }   
        
        Collections.sort(productList, new ProductCompareByDate());
    }
    
    public static void leggiDettagli(Product product){
        
        product.clearStatuses();
        
        String urlDettagli = "http://www.poste.it/online/dovequando/ricerca.do?action=dettaglioCorrispondenza&mpdate=0&mpcode=" + product.getCode();
        
        String document = "";
        
        // acquisizione pagina remota
        try{
            URL siteUrl = new URL(urlDettagli);
            HttpURLConnection conn = (HttpURLConnection) siteUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String line;
                while ((line = in.readLine()) != null) {
                    document += (line + "\n");
                }
            }
            
        } catch (Exception e){
            product.addStatus("Errore durante lettura status");
            return;
        }        
        
        // parsing 
        Document doc = Jsoup.parse(document);
        Elements masthead = doc.select("div.statoDoveQuandoLavorazione ul");
        
        if (masthead.isEmpty()){
            product.addStatus("Unknown");
        }
        else{
            for (int i = 1; i < masthead.size(); i++){
                product.addStatus(masthead.get(i).select("li").text());
            }
        }
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTableLista = new javax.swing.JTable();
        jButtonRefresh = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaDescription = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Poste Tracker <mario.piccinelli@gmail.com>");

        jTableLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTableLista);

        jButtonRefresh.setText("Refresh");
        jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRefreshActionPerformed(evt);
            }
        });

        jTextAreaDescription.setColumns(20);
        jTextAreaDescription.setRows(3);
        jScrollPane1.setViewportView(jTextAreaDescription);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addComponent(jButtonRefresh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonRefresh)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        updateUI();
    }//GEN-LAST:event_jButtonRefreshActionPerformed

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
            java.util.logging.Logger.getLogger(PosteUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PosteUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PosteUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PosteUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PosteUI poste = new PosteUI();
                poste.setVisible(true);
                poste.updateUI();
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableLista;
    private javax.swing.JTextArea jTextAreaDescription;
    // End of variables declaration//GEN-END:variables
}


