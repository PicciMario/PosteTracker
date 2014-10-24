/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package postetracker;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import postetracker.tools.DBManager;
import postetracker.tools.MultiLineCellRenderer;
import postetracker.tools.MyTableModel;
import postetracker.tools.TableDetailsModel;

/**
 * Main class of Poste Tracker.
 * @author m.piccinelli
 */
public class PosteUI extends javax.swing.JFrame implements ActionListener, ChangeListener {
    
    static List<Product> productList;
    static String url = "http://www.poste.it/online/dovequando/ricerca.do";
    static DBManager dbManager;
    
    PosteNewsWindow posteNewsWindow = new PosteNewsWindow(this, false);
    
    JButton newButton, deleteButton, refreshButton, archiveButton;
    JCheckBox showArchivedCheck, timerEnabledCheck;
    JSpinner timerSpinner;
    
    Timer refreshClock;
    
    /**
     * Creates new form PosteUI
     */
    public PosteUI() {
        
        dbManager = new DBManager();
        
        initComponents();
        
        initList();
        
        // Get current classloader
        ClassLoader cl = this.getClass().getClassLoader();
        // Create icons
        Icon newIcon = new ImageIcon(cl.getResource("resources/new.gif"));
        Icon delIcon = new ImageIcon(cl.getResource("resources/delete.gif"));
        Icon refreshIcon = new ImageIcon(cl.getResource("resources/refresh.gif"));
        Icon archiveIcon = new ImageIcon(cl.getResource("resources/archive.gif"));
        
        URL logoUrl = cl.getResource("resources/logo.png");
        Toolkit kit = Toolkit.getDefaultToolkit();
        Image logoImg = kit.createImage(logoUrl);
        setIconImage(logoImg);
        
        // NEW button
        newButton = new JButton();
        newButton.setIcon(newIcon);
        newButton.setText("Nuovo");
        newButton.setToolTipText("Nuovo codice...");
        newButton.addActionListener(this);
        jToolBar1.add(newButton);
        
        // DELETE button
        deleteButton = new JButton();
        deleteButton.setIcon(delIcon);
        deleteButton.setText("Cancella");
        deleteButton.setToolTipText("Cancella codice...");
        deleteButton.addActionListener(this);
        jToolBar1.add(deleteButton);
        
        // ARCHIVE button
        archiveButton = new JButton();
        archiveButton.setIcon(archiveIcon);
        archiveButton.setText("Archivia");
        archiveButton.setToolTipText("Archivia...");
        archiveButton.addActionListener(this);
        jToolBar1.add(archiveButton);        
        
        // separatore
        jToolBar1.addSeparator();
        
        // REFRESH button
        refreshButton = new JButton();
        refreshButton.setIcon(refreshIcon);
        refreshButton.setText("Aggiorna");
        refreshButton.setToolTipText("Aggiorna");
        refreshButton.addActionListener(this);
        jToolBar1.add(refreshButton);     
        
        // separatore
        jToolBar1.addSeparator();
        
        showArchivedCheck = new JCheckBox("Mostra archiviati");
        showArchivedCheck.addActionListener(this);
        jToolBar1.add(showArchivedCheck);
        
        // separatore
        jToolBar1.addSeparator();
        
        // timer
        timerEnabledCheck = new JCheckBox("Auto refresh minuti: ");
        timerEnabledCheck.addActionListener(this);
        jToolBar1.add(timerEnabledCheck);   
        timerSpinner = new JSpinner();
        timerSpinner.addChangeListener(this);
        SpinnerModel spinnerModel = new SpinnerNumberModel(30, 1, 1000, 1);
        timerSpinner.setModel(spinnerModel);
        jToolBar1.add(timerSpinner); 
        
        // products list table configuration
        MyTableModel model = new MyTableModel();
        model.setData(productList.toArray(new Product[0]));
        jTableLista.setModel(model);
        
        jTableLista.setRowHeight(jTableLista.getRowHeight()*2 + 5);
        jTableLista.setDefaultRenderer(String.class, new MultiLineCellRenderer());
        jTableLista.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        jTableLista.getColumnModel().getColumn(0).setMaxWidth(130);
        jTableLista.getColumnModel().getColumn(0).setPreferredWidth(130);
        
        // statuses list table configuration
        TableDetailsModel model2 = new TableDetailsModel();
        model.setData(null);
        jTableDetails.setModel(model2);
        
        jTableDetails.getColumnModel().getColumn(0).setMaxWidth(30);
        jTableDetails.getColumnModel().getColumn(0).setPreferredWidth(30);
        jTableDetails.getColumnModel().getColumn(1).setMaxWidth(80);
        jTableDetails.getColumnModel().getColumn(1).setPreferredWidth(80);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        jTableDetails.getColumnModel().getColumn(0).setCellRenderer(rightRenderer);        
        jTableDetails.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);        
        
        // product list table onclick action
        jTableLista.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()){
                    
                    // if no row selected, clear details table and return
                    if (jTableLista.getSelectedRow() == -1) {
                        TableDetailsModel mod = (TableDetailsModel)jTableDetails.getModel();
                        mod.setData(null);
                        mod.fireTableDataChanged();
                        return;
                    }
                    
                    // retrieve product from list table model
                    MyTableModel model = (MyTableModel)jTableLista.getModel();
                    Product prod = model.getProductByRow(jTableLista.getSelectedRow());
                    
                    // send product data to details table
                    TableDetailsModel mod = (TableDetailsModel)jTableDetails.getModel();
                    mod.setData(prod);
                    mod.fireTableDataChanged();
                }
            }
        });
        
        timerEnabledCheck.setSelected(true);
        refreshClock = new Timer();
        int mins = (int)timerSpinner.getValue();
        refreshClock.schedule(new TimerThread(this), mins*60*1000, mins*60*1000);
        
    }
    
    /**
     * Updates the main UI by re-reading data from the product list.
     */
    public void updateUI(){
        update();
        updateTableContent();
    }
    
    /**
     * Calls the updateProductList() procedure and meanwhile displays a 
     * "please wait" popup.
     */
    public boolean updating = false;
    public void update() {
        
        // semaphore for update process
        if (updating){
            System.out.println("Skipped, already updating...");
            return;
        }
        updating = true;
        
        // glass panel over frame
        WaitingGlassPane panel = new WaitingGlassPane();
        panel.setOpaque(false);
        setGlassPane(panel);
        panel.setVisible(true);  
        
        SwingWorker<?, ?> worker = new SwingWorker<Void, Integer>() {
            
            @Override
            protected Void doInBackground() throws InterruptedException {
                updateProductList();
                return null;
            }
            
            @Override
            protected void done() {
                updating = false;
                panel.setVisible(false);
            }
        };
        worker.execute();
    }    
    
    /**
     * Creates the product list.
     */
    public static void initList(){

        productList = dbManager.retrieveProductList();
        
    }
    
    public int newProduct(Product product){
        
        // save new product into database
        int insertStatus = dbManager.storeNewProduct(product);
        
        if (insertStatus == 1){
            JOptionPane.showMessageDialog(null, "Errore durante l'inserimento.");
            return 1;
        }
        else if (insertStatus == 2){
            JOptionPane.showMessageDialog(null, "Impossibile completare l'inserimento: codice già presente.");
            return 1;            
        }
        
        // update product by retrieving data from site
        updateProduct(product);
        
        // add product to local product list
        productList.add(product);
        
        // sort updated product list
        Collections.sort(productList, new ProductCompareByDate());
        
        // update table data and visual
        updateTableContent();
        
        return 0;
        
    }
    
    public void updateTableContent(){
        
        List<Product> filteredProductList = new ArrayList<>();
        for (Product prod : productList){
            if (prod.isArchived() == true){
                if (showArchivedCheck.isSelected() == true){
                    filteredProductList.add(prod);
                }
            }
            else {
                filteredProductList.add(prod);
            }
        }
        
        // set updated product list as data source for table in main UI
        ((MyTableModel) jTableLista.getModel()).setData(filteredProductList.toArray(new Product[0]));

        // update the table
        MyTableModel dm = (MyTableModel) jTableLista.getModel();
        dm.fireTableDataChanged();

        // clear table selection
        jTableLista.clearSelection();
        
        // clear details table
        TableDetailsModel mod = (TableDetailsModel)jTableDetails.getModel();
        mod.setData(null);
        mod.fireTableDataChanged();
        
    }
    
    public void deleteProduct(Product product){
        
        String message = "Vuoi cancellare l'elemento " + product.getDesc() + " e tutti i suoi aggiornamenti? \nL'operazione è irreversibile!";
        
        int reply = JOptionPane.showConfirmDialog(null, message, "Conferma cancellazione", JOptionPane.YES_NO_OPTION);
        
        if (reply == JOptionPane.YES_OPTION) {
            
            // delete from database
            dbManager.deleteProduct(product);
            
            // deleted from internal product list
            productList.remove(product);

            updateTableContent();
            
        }

    }
    
    public void archiveProduct(Product product){
        
        // delete from database
        dbManager.archiveProduct(product);
        
        if (product.isArchived()){
            product.setArchiveStatus(0);
        }
        else {
            product.setArchiveStatus(1);
        }

        updateTableContent();
                    
    }
    
    /**
     * Updates each product in the product list by retrieving the data from the
     * remote page. The product list is then sorted by first date.
     * @return An array of Strings, each one representing a new status found 
     * during the update process.
     */
    public String[] updateProductList(){
        
        // the new statuses found during this update process
        List<String> updates = new ArrayList<>();

//        Old version without parallelization
//        for (Product prod : productList){
//            if (prod.isArchived()) continue;
//            String[] prodUpdates = updateProduct(prod);
//            for (String upd : prodUpdates){
//                updates.add(upd);
//            }
//        }   
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<String[]>> list = new ArrayList<>();

        for (Product prod : productList){
            if (prod.isArchived()) continue;
            
            Callable<String[]> worker = new RetrieveProductData(prod, dbManager);
            Future<String[]> submit = executor.submit(worker);
            list.add(submit);
        }           
        
        for (Future<String[]> future : list){
            try{
            String[] prodUpdates = future.get();
                for (String productUpdate : prodUpdates){
                    updates.add(productUpdate);
                }
            }
            catch (InterruptedException | ExecutionException e){
                System.out.println(e.toString());
            }
        }
        
        // sort the whole product list by date of the first (chronological) 
        // update in each of the products.
        try{
            Collections.sort(productList, new ProductCompareByDate());
        }
        catch (Exception e){
            System.out.println(e.toString());
        }
        
        // if there are new statuses, show them in the PosteNewsWindow
        if(updates.size() > 0){
            for (String updateStringElem : updates){
                posteNewsWindow.addNews(updateStringElem);
            }            
            posteNewsWindow.setVisible(true);
            posteNewsWindow.flash();
        }
        
        // set status bar text with the timestamp of the last update (i.e. now)
        jLabelStatusBar.setText("Ultimo aggiornamento " + new SimpleDateFormat("HH:mm dd/MM/yyyy").format(Calendar.getInstance().getTime()));
        
        // returns the new statuses
        return updates.toArray(new String[0]);
        
    }
    
    /**
     * Updates a single product by retrieving the tracking data from the remote
     * page.
     * @param product The Product instance to update
     * @return An array of Strings with the new status updates.
     */
    public static String[] updateProduct(Product product){
        
        List<String> updates = new ArrayList<>();
        
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
            System.out.println("Errore durante lettura status: " + e.toString());
            return updates.toArray(new String[0]);
        }        
        
        // parsing 
        Document doc = Jsoup.parse(document);
        Elements masthead = doc.select("div.statoDoveQuandoLavorazione ul");
        
        if (masthead.isEmpty()){
            masthead = doc.select("div.statoDoveQuandoConsegnato ul");
        }
        
        if (!masthead.isEmpty()){
            for (int i = 1; i < masthead.size(); i++){
                String newStatus = masthead.get(i).select("li").text();
                boolean wasNew = product.addStatus(newStatus);
                if (wasNew) {
                    dbManager.storeNewStatus(product.getCode(), newStatus);
                    updates.add(product.getDesc() + ": " + newStatus);
                }
            }
        }
        
        return updates.toArray(new String[0]);
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        jLabelStatusBar = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableLista = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableDetails = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Poste Tracker <mario.piccinelli@gmail.com>");

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabelStatusBar.setForeground(java.awt.Color.gray);
        jLabelStatusBar.setText(" ");
        jLabelStatusBar.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        jSplitPane1.setDividerLocation(300);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setResizeWeight(1.0);

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

        jSplitPane1.setLeftComponent(jScrollPane2);

        jTableDetails.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(jTableDetails);

        jSplitPane1.setRightComponent(jScrollPane3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
                    .addComponent(jLabelStatusBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 453, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelStatusBar)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
                if ("Windows".equals(info.getName())) {
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
            @Override
            public void run() {
                PosteUI poste = new PosteUI();
                poste.setVisible(true);
                poste.updateUI();
            }
        });

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelStatusBar;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTableDetails;
    private javax.swing.JTable jTableLista;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // New Button
        if (e.getSource() == newButton) {
            NewProduct dialog = new NewProduct(this, true);
            dialog.setVisible(true);
        }
        
        else if (e.getSource() == deleteButton){
            // get selected product
            if (jTableLista.getSelectedRow() == -1) {
                return;
            }
            
            MyTableModel model = (MyTableModel)jTableLista.getModel();
            Product prod = model.getProductByRow(jTableLista.getSelectedRow());
            deleteProduct(prod);
        }
        
        else if (e.getSource() == refreshButton){
            updateUI();
            if (timerEnabledCheck.isSelected()){
                int mins = (int)timerSpinner.getValue();
                refreshClock.cancel();
                refreshClock = new Timer();
                refreshClock.schedule(new TimerThread(this), mins*60*1000, mins*60*1000);
            }            
        }
        
        else if (e.getSource() == showArchivedCheck){
            updateTableContent();
        }
        
        else if (e.getSource() == archiveButton){
            // get selected product
            if (jTableLista.getSelectedRow() == -1) {
                return;
            }
            
            MyTableModel model = (MyTableModel)jTableLista.getModel();
            Product prod = model.getProductByRow(jTableLista.getSelectedRow());
            archiveProduct(prod);            
        }
        
        else if (e.getSource() == timerEnabledCheck) {
            
            if (timerEnabledCheck.isSelected()){
                int mins = (int)timerSpinner.getValue();
                refreshClock.cancel();
                refreshClock = new Timer();
                refreshClock.schedule(new TimerThread(this), mins*60*1000, mins*60*1000);
            }
            else{
                refreshClock.cancel();
            }
        }
        
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (timerEnabledCheck.isSelected()){
            int mins = (int)timerSpinner.getValue();
            refreshClock.cancel();
            refreshClock = new Timer();
            refreshClock.schedule(new TimerThread(this), mins*60*1000, mins*60*1000);
        }
        else{
            refreshClock.cancel();
        }        
    }
}

class TimerThread extends java.util.TimerTask {

    PosteUI parent;
    
    public TimerThread(PosteUI parent){
        this.parent = parent;
    }
    
    @Override
    public void run() {
        parent.update();
    }
    
}


class WaitingGlassPane extends JPanel implements MouseListener, MouseMotionListener, FocusListener{

    public WaitingGlassPane(){
        addMouseListener(this);
        addMouseMotionListener(this);
        addFocusListener(this);   
        
        setLayout(new GridBagLayout());
        
        JLabel l = new JLabel();
        l.setOpaque(true);
        l.setBackground(Color.GREEN);
        l.setForeground(Color.BLACK);
        
        l.setFont(new Font("Serif", Font.BOLD, 16));
        
        l.setText("Aggiornamento in corso.. Attendere...");
        l.setBorder(new LineBorder(Color.BLACK, 1));
        

        add(l);
    }
    
    @Override
    public void setVisible(boolean v) {
        // Make sure we grab the focus so that key events don't go astray.
        if (v) {
            requestFocus();
        }
        super.setVisible(v);
    } 
    
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {}

    @Override
    public void focusGained(FocusEvent e) {}

    @Override
    public void focusLost(FocusEvent fe) {
        if (isVisible()) {
            requestFocus();
        }
    }
    
}


class RetrieveProductData implements Callable<String[]>{

    Product product;
    List<String> updates;
    DBManager dbManager;
    
    public RetrieveProductData(Product product, DBManager dbManager){
        this.product = product;
        this.dbManager = dbManager;
        updates = new ArrayList<>();
    }
    
    @Override
    public String[] call() throws Exception {
        
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
            System.out.println("Errore durante lettura status: " + e.toString());
            return updates.toArray(new String[0]);
        }        
        
        // parsing 
        Document doc = Jsoup.parse(document);
        Elements masthead = doc.select("div.statoDoveQuandoLavorazione ul");
        
        if (masthead.isEmpty()){
            masthead = doc.select("div.statoDoveQuandoConsegnato ul");
        }
        
        if (!masthead.isEmpty()){
            for (int i = 1; i < masthead.size(); i++){
                String newStatus = masthead.get(i).select("li").text();
                boolean wasNew = product.addStatus(newStatus);
                if (wasNew) {
                    dbManager.storeNewStatus(product.getCode(), newStatus);
                    updates.add(product.getDesc() + ": " + newStatus);
                }
            }
        }
        
        return updates.toArray(new String[0]);
    }
    
}

