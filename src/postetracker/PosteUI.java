/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postetracker;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import javax.swing.table.DefaultTableModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import postetracker.tools.DBManager;
import postetracker.tools.MultiLineCellRenderer;
import postetracker.tools.MyTableModel;

/**
 * Main class of Poste Tracker.
 * @author m.piccinelli
 */
public class PosteUI extends javax.swing.JFrame {

    static List<Product> productList;
    static String url = "http://www.poste.it/online/dovequando/ricerca.do";
    static DBManager dbManager;
    
    /**
     * Creates new form PosteUI
     */
    public PosteUI() {
        
        dbManager = new DBManager();
        
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
                    if (jTableLista.getSelectedRow() == -1) {
                        jTextAreaDescription.setText("");
                        return;
                    }
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
    
    /**
     * Updates the main UI by re-reading data from the product list.
     */
    public void updateUI(){
        update();
        ((MyTableModel) jTableLista.getModel()).setData(productList.toArray(new Product[0]));
        jTableLista.repaint();
        jTableLista.clearSelection();
        jTextAreaDescription.setText("");
    }
    
    /**
     * Calls the updateProductList() procedure and meanwhile displays a 
     * "please wait" popup.
     */
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
                updateProductList();
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
    
    /**
     * Creates the produce list.
     */
    public static void initList(){
        String[] codes = new String[]{"RI001384165CN", "RL040393084CN", "RO400390995CN", "RG058010681CN", "RG053889040CN", "RJ210248923CN"};
        String[] descs = new String[]{"Schermo GoPro", "Velcro Patch: Zombie Outbreak Response Team", "Tasca Cellulare", "Portachiavi tattico", "Ganci MOLLE", "Tasche cellulare (2x)"};
     
        productList = dbManager.retrieveProductList();
        
    }
    
    public void newProduct(Product product){
        
        dbManager.storeNewProduct(product);
        
        updateProduct(product);
        
        productList.add(product);
        
        Collections.sort(productList, new ProductCompareByDate());
        ((MyTableModel) jTableLista.getModel()).setData(productList.toArray(new Product[0]));
        
        MyTableModel dm = (MyTableModel)jTableLista.getModel();
        dm.fireTableDataChanged(); 
        
        jTableLista.clearSelection();
        jTextAreaDescription.setText(""); 
        
    }
    
    /**
     * Updates each product in the product list by retrieving the data from the
     * remote page. The product list is then sorted by first date.
     */
    public static void updateProductList(){
        
        for (Product prod : productList){
            updateProduct(prod);
        }   
        
        Collections.sort(productList, new ProductCompareByDate());
    }
    
    /**
     * Updates a single product by retrieving the tracking data from the remote
     * page.
     * @param product The Product instance to update
     */
    public static void updateProduct(Product product){
        
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
        
        if (!masthead.isEmpty()){
            for (int i = 1; i < masthead.size(); i++){
                String newStatus = masthead.get(i).select("li").text();
                boolean wasNew = product.addStatus(newStatus);
                if (wasNew) {
                    System.out.println("New status " + newStatus + " for product " + product.getDesc());
                    dbManager.storeNewStatus(product.getCode(), newStatus);
                }
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
        jButtonNewCode = new javax.swing.JButton();

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

        jButtonNewCode.setText("New code");
        jButtonNewCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewCodeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 668, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonRefresh)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonNewCode, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonRefresh)
                    .addComponent(jButtonNewCode))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Called when the user pushes the "refresh" button.
     * @param evt 
     */
    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        updateUI();
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void jButtonNewCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewCodeActionPerformed
        NewProduct dialog = new NewProduct(this, true);
        dialog.setVisible(true);
    }//GEN-LAST:event_jButtonNewCodeActionPerformed

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
    private javax.swing.JButton jButtonNewCode;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableLista;
    private javax.swing.JTextArea jTextAreaDescription;
    // End of variables declaration//GEN-END:variables
}


