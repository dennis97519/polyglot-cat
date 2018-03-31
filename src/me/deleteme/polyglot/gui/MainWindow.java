package me.deleteme.polyglot.gui;

import java.awt.EventQueue;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
//import napkin.NapkinLookAndFeel;//for some IB requirement that prototype must look like prototype lel
import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JSeparator;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;

import me.deleteme.polyglot.util.LangPair;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;


/**
 * Root GUI container for everything
 * @author Dennis
 *
 */
//TODO implement undo and redo features
//TODO implement file save features
//TODO refactor code to split listeners into different classes
//TODO add find/search function
//TODO allow image as original text. Use opencv or something
//TODO add pinyin romaji and other transcription as languages (probably different script, jp-Latn, zh-Latn etc)
//TODO Localise, externalise string and add translation
public class MainWindow implements ActionListener,Serializable, ListSelectionListener, ListDataListener {
	
	/**
	 * Main JFrame
	 */
	public static JFrame frmPolyglot;
	
	/**
	 * Launch the application.
	 * @param args command line arguments, not used
	 */
	public static void main(String[] args) {
		/**
		 * Set system UI Look and Feel
		 */
		try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        	java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
		AddEdit.loadList();
		LangPair.readList();
		languageProfiles=null;
		try {
			languageProfiles = new LanguageProfileReader().readAllBuiltIn();
			languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
	        .withProfiles(languageProfiles)
	        .build();
		} catch (IOException|NullPointerException e) {
			e.printStackTrace();
		}
		textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainWindow();
					MainWindow.frmPolyglot.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}
	public static void revalidate(){
		frmPolyglot.revalidate();
		frmPolyglot.repaint();
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPolyglot = new JFrame();
		frmPolyglot.setMinimumSize(new Dimension(1000, 700));
		frmPolyglot.setTitle("Polyglot");
		frmPolyglot.setIconImage(Toolkit.getDefaultToolkit().getImage(MainWindow.class.getResource("/com/sun/javafx/scene/control/skin/modena/HTMLEditor-Cut-Black@2x.png")));
		
		frmPolyglot.setBounds(100, 100, 1080, 766);
		frmPolyglot.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		{
			//menu bar stuff
			menuBar = new JMenuBar();
			menuFile = new JMenu();
			menuFile.setText("File");
			menuFile.setMnemonic(KeyEvent.VK_F);
			menuBar.add(menuFile);
			menuEdit = new JMenu();
			menuEdit.setMnemonic(KeyEvent.VK_E);
			menuEdit.setText("Edit");
			menuFile.setMnemonic(KeyEvent.VK_E);
			menuBar.add(menuEdit);
			
			mntmUndo = new JMenuItem("Undo");
			menuEdit.add(mntmUndo);
			
			mntmRedo = new JMenuItem("Redo");
			menuEdit.add(mntmRedo);
			
			separator = new JSeparator();
			menuEdit.add(separator);
			
			mntmCut = new JMenuItem("Cut");
			menuEdit.add(mntmCut);
			
			mntmCopy = new JMenuItem("Copy");
			menuEdit.add(mntmCopy);
			
			mntmPaste = new JMenuItem("Paste");
			menuEdit.add(mntmPaste);
			
			separator_2 = new JSeparator();
			menuEdit.add(separator_2);
			
			mntmDelete = new JMenuItem("Delete");
			menuEdit.add(mntmDelete);
			
			mntmSelectAll = new JMenuItem("Select All");
			menuEdit.add(mntmSelectAll);
			menuHelp = new JMenu();
			menuHelp.setMnemonic(KeyEvent.VK_H);
			menuHelp.setText("Help");
			menuFile.setMnemonic(KeyEvent.VK_H);
			
			mntmNew = new JMenuItem("New");
			menuFile.add(mntmNew);
			
			mntmOpen = new JMenuItem("Open...");
			menuFile.add(mntmOpen);
			
			mntmSave = new JMenuItem("Save");
			menuFile.add(mntmSave);
			
			mntmSaveAs = new JMenuItem("Save as...");
			mntmSaveAs.addActionListener(this);
			menuFile.add(mntmSaveAs);
			
			separator_1 = new JSeparator();
			menuFile.add(separator_1);
			
			mntmExit = new JMenuItem("Exit");
			menuFile.add(mntmExit);
			menuBar.add(menuHelp);
			
			mntmHowToUse = new JMenuItem("How to use");
			menuHelp.add(mntmHowToUse);
			
			mntmAbout = new JMenuItem("About");
			menuHelp.add(mntmAbout);
			frmPolyglot.getContentPane().add(menuBar, BorderLayout.NORTH);
		}
		
		//Main window setting panel (which contains most of the logic)
		{
			westPane = new JPanel();
			westPane.setLayout(new BoxLayout(westPane,BoxLayout.X_AXIS));
			combText = new SepPane();
			paneFormat=new JPanel();
			initMwsComponents();
			
			westPane.add(combText);
			westPane.add(paneFormat);
			frmPolyglot.getContentPane().add(westPane, BorderLayout.WEST);
		}
		
		//panel holder, holds all the separate panels
		{
			ph = new JPanel();
			ph.setLayout(new BoxLayout(ph, BoxLayout.X_AXIS));
			ph.add(Box.createHorizontalStrut(strutWidth));
			plHolder = new JScrollPane();
			plHolder.setViewportBorder(null);
			plHolder.setViewportView(ph);
			frmPolyglot.getContentPane().add(plHolder, BorderLayout.CENTER);
			
			statusBar = new JPanel();
			statusBar.setMinimumSize(new Dimension(10, 16));
			statusBar.setBorder(null);
			frmPolyglot.getContentPane().add(statusBar, BorderLayout.SOUTH);
			statusBar.setLayout(new BoxLayout(statusBar, BoxLayout.X_AXIS));
			
			lblStatus = new JLabel(" ");
			lblStatus.setHorizontalAlignment(SwingConstants.LEFT);
			statusBar.add(lblStatus);
		}
		
	}
	private void initMwsComponents() {
		paneFormat.setLayout(new BoxLayout(paneFormat, BoxLayout.Y_AXIS));
		border=new TitledBorder(null, "Format:", TitledBorder.LEADING, TitledBorder.TOP, null, null);
		paneFormat.setBorder(border);
		paneFormat.setName("Lang");
		/**
		 * Container for the separated languages panes
		 */
		plListModel = new DefaultListModel<SepPane>();
		plListModel.addListDataListener(this);
		formatPanel = new JPanel();
		paneFormat.add(formatPanel);
		formatPanel.setLayout(new BoxLayout(formatPanel, BoxLayout.Y_AXIS));
		plList = new JList<SepPane>();
		plList.setDragEnabled(true);
		plList.addListSelectionListener(this);
		plList.setTransferHandler(new ListItemTransferHandler());
		plList.setDropMode(DropMode.INSERT);
		plList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		plList.setModel(plListModel);
		scrList = new JScrollPane(plList);
		formatPanel.add(scrList);
		
		//button for modifying the list
		buttonPane = new JPanel();
		formatPanel.add(buttonPane);
		btnAdd = new JButton("+");
		btnAdd.setPreferredSize(new Dimension(40, 20));//so mac doesn't expand the button
		btnAdd.addActionListener(this);
		btnRmv = new JButton("-");
		btnRmv.setPreferredSize(new Dimension(40, 20));
		btnRmv.setEnabled(false);
		btnRmv.addActionListener(this);
		btnEdit= new JButton("Edit...");
		btnEdit.setEnabled(false);
		btnEdit.addActionListener(this);
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
		buttonPane.add(btnAdd);
		buttonPane.add(btnRmv);
		
		btnAddEmptyLine = new JButton("Add Empty Line");
		buttonPane.add(btnAddEmptyLine);
		btnAddEmptyLine.addActionListener(this);
		btnAddEmptyLine.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonPane.add(btnEdit);
		
		//looks like this is unused, may have planned for some additional format back then
		paneFormatEdit = new JPanel();
		formatPanel.add(paneFormatEdit);
		paneFormatEdit.setLayout(new BoxLayout(paneFormatEdit, BoxLayout.Y_AXIS));
		
		verticalGlue_3 = Box.createVerticalGlue();
		paneFormat.add(verticalGlue_3);
		
		sepSet1 = new JSeparator();
		paneFormat.add(sepSet1);
		
		verticalGlue = Box.createVerticalGlue();
		paneFormat.add(verticalGlue);
		
		langNotDetOptPane = new JPanel();
		langNotDetOptPane.setVisible(false);
		paneFormat.add(langNotDetOptPane);
		langNotDetOptPane.setLayout(new BoxLayout(langNotDetOptPane, BoxLayout.Y_AXIS));
		
		lblWhenLanguageIs = new JLabel("When language is not detected:");
		lblWhenLanguageIs.setAlignmentX(Component.CENTER_ALIGNMENT);
		langNotDetOptPane.add(lblWhenLanguageIs);
		
		radBtn = new JPanel();
		langNotDetOptPane.add(radBtn);
		radBtn.setLayout(new BoxLayout(radBtn, BoxLayout.X_AXIS));
		
		rdbtnAddToAll = new JRadioButton("Add to all boxes");
		rdbtnAddToAll.setActionCommand("ADD_TO_ALL");
		radBtn.add(rdbtnAddToAll);
		btnGrpWhenUnsure.add(rdbtnAddToAll);
		
		rdbtnPrompt = new JRadioButton("Prompt");
		rdbtnPrompt.setSelected(true);
		rdbtnPrompt.setActionCommand("PROMPT");
		radBtn.add(rdbtnPrompt);
		btnGrpWhenUnsure.add(rdbtnPrompt);
		
		rdbtnSkip = new JRadioButton("Skip");
		rdbtnSkip.setActionCommand("SKIP");
		radBtn.add(rdbtnSkip);
		btnGrpWhenUnsure.add(rdbtnSkip);
		
		verticalGlue_2 = Box.createVerticalGlue();
		paneFormat.add(verticalGlue_2);
		
		sepSet2 = new JSeparator();
		paneFormat.add(sepSet2);
		
		controlPane = new JPanel();
		paneFormat.add(controlPane);
		controlPane.setLayout(new BoxLayout(controlPane, BoxLayout.Y_AXIS));
		
		chckbxUnformattedText = new JCheckBox("Unformatted text");
		chckbxUnformattedText.addActionListener(this);
		controlPane.add(chckbxUnformattedText);
		chckbxUnformattedText.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		btnSep = new JButton("Split>>");
		controlPane.add(btnSep);
		btnSep.addActionListener(this);
		btnSep.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		
		
		lblProg = new JLabel("Line 0 of 0");
		controlPane.add(lblProg);
		lblProg.setAlignmentX(Component.CENTER_ALIGNMENT);
		prog = new JProgressBar();
		controlPane.add(prog);
		
		btnComb = new JButton("<<Combine");
		btnComb.setEnabled(false);
		controlPane.add(btnComb);
		btnComb.addActionListener(this);
		btnComb.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//Make the sizes of the two buttons equal
		Dimension dimComb = btnComb.getMinimumSize();
		Dimension dimSep = btnSep.getMinimumSize();
		double maxWidth = Math.max(dimSep.getWidth(),dimComb.getWidth());
		double maxHeight = Math.max(dimSep.getHeight(),dimComb.getHeight());
		Dimension button = new Dimension((int)maxWidth,(int)maxHeight);
		btnComb.setMinimumSize(button);
		btnComb.setMaximumSize(button);
		btnSep.setMinimumSize(button);
		btnSep.setMaximumSize(button);
		
		verticalGlue_1 = Box.createVerticalGlue();
		paneFormat.add(verticalGlue_1);
	}
	private JPanel statusBar;
	private JLabel lblStatus;
	private SepPane combText;
	private JPanel westPane;
	
	//Menu bar
	private JMenuBar menuBar;
	private JMenu menuFile,menuEdit,menuHelp;
	private JMenuItem mntmOpen;
	private JMenuItem mntmSave;
	private JMenuItem mntmSaveAs;
	private JMenuItem mntmUndo;
	private JMenuItem mntmRedo;
	private JSeparator separator;
	private JMenuItem mntmCut;
	private JMenuItem mntmCopy;
	private JMenuItem mntmPaste;
	private JSeparator separator_1;
	private JMenuItem mntmExit;
	private JMenuItem mntmNew;
	private JSeparator separator_2;
	private JMenuItem mntmDelete;
	private JMenuItem mntmSelectAll;
	private JMenuItem mntmAbout;
	private JMenuItem mntmHowToUse;
	
	//Setting panel
	private JPanel paneFormat;
	private TitledBorder border;
	private JScrollPane scrList;
	private JList<SepPane> plList;
	private static List<LanguageProfile> languageProfiles;
	private static LanguageDetector languageDetector;
	private static TextObjectFactory textObjectFactory;
	/**
	 * Container for all the language panes. Displayed in plHolder
	 */
	private DefaultListModel<SepPane> plListModel;
	private HashMap <LdLocale,SepPane> locPaneMap;
	DefaultListCellRenderer ren;
	private JPanel buttonPane;
	private JButton btnAdd;
	private JButton btnRmv;
	private JButton btnEdit;
	private JPanel paneFormatEdit;
	private JButton btnSep;
	private JButton btnComb;
	private JProgressBar prog;
	private JLabel lblProg;
	private JSeparator sepSet1;
	private Component verticalGlue;
	private JButton btnAddEmptyLine;
	private Component verticalGlue_1;
	private JCheckBox chckbxUnformattedText;
	private JRadioButton rdbtnAddToAll;
	private JRadioButton rdbtnPrompt;
	private JRadioButton rdbtnSkip;
	private final ButtonGroup btnGrpWhenUnsure = new ButtonGroup();
	private JPanel radBtn;
	private JLabel lblWhenLanguageIs;
	private JPanel langNotDetOptPane;
	private JSeparator sepSet2;
	private JPanel controlPane;
	private JPanel formatPanel;
	private Component verticalGlue_3;
	private Component verticalGlue_2;
	
	
	//multilang panes
	/**
	 * Shows the panels stored in {@link MWsetting.plListModel}
	 */
	private JScrollPane plHolder;
	private JPanel ph;
	public static int strutWidth = 10;
	
	
	/**
	 * Get combined text panel for saving file
	 * @return combText
	 */
	public SepPane getComb(){
		return combText;
	}
	/**
	 * Get the list model containing panes for saving file
	 * @return plListModel
	 */
	public DefaultListModel<SepPane> getPanes(){
		return plListModel;
	}
	/**
	 * Get status of the unformatted text checkbox
	 * @return true if text is unformatted
	 */
	public boolean getUnformatText(){
		return chckbxUnformattedText.isSelected();
	}
	/**
	 * Load panes from specified list model
	 * @param panes
	 */
	public void loadPanes(DefaultListModel<SepPane> panes){
		plListModel=panes;
		syncPaneFromList(panes);
	}
	/**
	 * Sync plHolder with the given panes. Should be used for loading from file
	 * @param panes
	 */
	private void syncPaneFromList(DefaultListModel<SepPane> panes){
		ph.removeAll();
		ph.add(Box.createHorizontalStrut(strutWidth));
		for(Object pane:panes.toArray()){
			SepPane sPane=(SepPane)pane;
			if(!sPane.getLp().getCode().equals("newl")) ph.add(sPane);
		}
		MainWindow.revalidate();
	}
	/**
	 * Sync plHolder with plListModel
	 */
	private void syncPanes(){
		syncPaneFromList(plListModel);
	}
	/**
	 * Add SepPane pane after index<sup>th</sup> element in the list
	 * @param index index to add after. put -1 to add at end
	 * @param pane pane to add
	 */
	void listAdd(int index,SepPane pane){
		if(index!=-1){
			plListModel.add(index+1,pane);
			plList.setSelectedIndex(index+1);
		}else{
			plListModel.addElement(pane);
			plList.setSelectedIndex(plListModel.getSize()-1);
		}
		if(!pane.getLp().getCode().equals("newl"))syncPanes();
	}
	/**
	 * Append SepPane at end of list
	 * @param pane pane to append
	 */
	void listAppend(SepPane pane){
		listAdd(-1,pane);
	}
	void listClear(){
		plListModel.removeAllElements();
		syncPanes();
	}
	boolean isListFormatPresent(){
		if(plListModel.isEmpty())return false;
		else{
			for(Object obj:plListModel.toArray()){
				SepPane pane=(SepPane)obj;
				if(!pane.getLp().getCode().equals("newl")){
					return true;
				}
			}
		}
		return false;
	}
	//TODO probably write a method to get a iterable instance (array) of plListModel that point to the panes
	int getMaxPaneLength(){
		Object [] objs=plListModel.toArray();
		int maxLength=0;
		for(Object obj:objs){
			SepPane pane=(SepPane)obj;
			maxLength=Math.max(maxLength, pane.getLineCount()-1);
		}
		return maxLength;
	}
	void appendToAllPanes(String text){
		if (plListModel.isEmpty())return;
		else{
			for(int i=0;i<plListModel.getSize();i++){
				plListModel.get(i).append(text);
			}
		}
	}
	void clearAllPanes(){
		if (plListModel.isEmpty())return;
		else{
			for(int i=0;i<plListModel.getSize();i++){
				plListModel.get(i).clear();
			}
		}
	}
	boolean allPanesAreEmpty(){
		if (plListModel.isEmpty())return true;
		else{
			for(int i=0;i<plListModel.getSize();i++){
				if(!plListModel.get(i).isEmpty())return false;
			}
			return true;
		}
	}
	/**
	 * Set lblStatus with a status message
	 * @param stat
	 */
	void setStatus(final String stat){
		if(!stat.equals("")){
			(new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					lblStatus.setText(stat);
					Thread.sleep(2000);
					return null;
				}
				@Override
				protected void done(){
					clearStatus();
				}
				
			}).execute();
		}else{
			clearStatus();
		}
	}
	/**
	 * Clear the lblStatus status message
	 */
	void clearStatus(){
		lblStatus.setText(" ");
	}
	
	/**
	 * overall action listener to jump to the appropriate function
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chckbxUnformattedText) {
			do_chckbxUnformattedText_actionPerformed(e);
		}
		if (e.getSource() == mntmSaveAs) {
			do_mntmSaveAs_actionPerformed(e);
		}
		if (e.getSource() == btnComb) {
			do_btnComb_actionPerformed(e);
		}
		if (e.getSource() == btnAddEmptyLine) {
			do_btnAddEmptyLine_actionPerformed(e);
		}
		if (e.getSource() == btnSep) {
			do_btnSep_actionPerformed(e);
		}
		if (e.getSource() == btnEdit) {
			do_btnEdit_actionPerformed(e);
		}
		if (e.getSource() == btnRmv) {
			do_btnRmv_actionPerformed(e);
		}
		if (e.getSource() == btnAdd) {
			do_btnAdd_actionPerformed(e);
		}
	}
	//edit panel button actions
	protected void do_btnAdd_actionPerformed(ActionEvent e) {
		int index = plList.getSelectedIndex();
		AddEdit dialog = new AddEdit();
		dialog.setVisible(true);
		if(dialog.curr!=null){//determine whether cancel button is clicked. If cancel is clicked curr will be null
			listAdd(index,dialog.curr);
		}
		dialog.dispose();
	}
	protected void do_btnRmv_actionPerformed(ActionEvent e) {
		int index = plList.getSelectedIndex();
		if(index!=-1){
			plListModel.remove(index);
			plList.setSelectedIndex(index-1);
			syncPanes();
		}
	}
	protected void do_btnEdit_actionPerformed(ActionEvent e) {
		int index=plList.getSelectedIndex();
		if(index!=-1){
			SepPane curr = plListModel.getElementAt(index);
			AddEdit dialog = new AddEdit(curr);
			dialog.setVisible(true);
			plListModel.set(index,dialog.curr);
			syncPanes();
			dialog.dispose();
		}
	}
	protected void do_btnAddEmptyLine_actionPerformed(ActionEvent e) {
		int index = plList.getSelectedIndex();
		SepPane dummy=new SepPane(new LangPair("Empty Line","newl"),"Empty Line");
		listAdd(index,dummy);
	}
	
	private static Optional<LdLocale> detectLang(String text){
		TextObject textObject = textObjectFactory.forText(text);
		Optional<LdLocale> lang = languageDetector.detect(textObject);
		return lang;
	}
	private void detectFormat() throws Exception{
		String[] text=combText.getText().split("\n");
		boolean patternFound=false;
		ArrayList<LdLocale> pattern=new ArrayList<>();
		ArrayList<LdLocale> ptA = null;
		ArrayList<LdLocale> ptB = null;
		HashSet<LdLocale> langs=new HashSet<>();
		lblStatus.setText("Detecting format...");
		for(String t:text){
			if(t.equals("")){
				pattern.add(null);
			}else{
				Optional<LdLocale> lang = detectLang(t);
				if(lang.isPresent()){
					pattern.add(lang.get());
					langs.add(lang.get());
				}else{
					setStatus("Unable to detect language of one paragraph in the first two cycles of the pattern. Please manually set format, or start the text with longer paragraphs");
					throw new Exception("Cannot detect language");
				}
			}
			if(pattern.size()!=0&&pattern.size()%2==0&&langs.size()>0){
				ptA=new ArrayList<LdLocale>(pattern.subList(0, pattern.size()/2));
				ptB=new ArrayList<LdLocale>(pattern.subList(pattern.size()/2,pattern.size()));
				if(ptA.equals(ptB)){
					patternFound=true;
					break;
				}
			}
		}
		if(patternFound){
			listClear();
			for(LdLocale loc:ptA){
				LangPair lp=LangPair.fromLdLocale(loc);
				listAppend(new SepPane(lp));
			}
			clearStatus();
		}else{
			setStatus("Unable to detect format. Please manually set format, or check that your text contains at least two full cycles of the pattern and try again.");
			throw new Exception("Cannot detect format");
		}
	}
	/**
	 * Set progress bar display
	 * @param curr current line
	 * @param length total lines
	 */
	void setProg(int curr,int length){
		lblProg.setText("Line "+curr+" of "+length);
		prog.setMaximum(length);
		prog.setValue(curr);
	}
	void formattedSep(boolean emptyFormat,String []text){
		if(emptyFormat){
			try {
				detectFormat();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				return;
			}
		}
		int formatItems=plListModel.getSize();
		int currFormat=0;
		clearAllPanes();
		int length=text.length;
		String para="";
		for(int i=0;i<length;i++){
			setProg(i+1,length);
			para=text[i];
			plListModel.getElementAt(currFormat).append(para+"\n");
			
			currFormat++;
			if(currFormat==formatItems)currFormat=0;
		}
	}
	String unknowLangActionChoice() {
		return btnGrpWhenUnsure.getSelection().getActionCommand();
	}
	
	//TODO probably refactor code to reduce repetition
	void langSep(boolean emptyFormat,String []text){
		int length=text.length;
		String para="";
		String choice="PROMPT";
		Optional<LdLocale> loc=null;
		choice=unknowLangActionChoice();
		clearAllPanes();
		if(emptyFormat){
			//if no language is specified
			int numBlank=0;
			for(int i=0;i<length;i++){ 
				setProg(i+1,length);
				para=text[i];
				if(!para.equals("")){
					numBlank=0;
					loc=detectLang(para);
					if(loc.isPresent()){
						//if language is detected
						if (locPaneMap.containsKey(loc.get())){
							//if there is already a existing SepPane for the language
							locPaneMap.get(loc.get()).append(para+"\n");
						}else{
							//if there isn't
							LdLocale locale=loc.get();
							SepPane pane=new SepPane(LangPair.fromLdLocale(locale));
							listAppend(pane);
							locPaneMap.put(locale, pane);
							pane.append(para+"\n");
						}
					}else{
						//if language is not detected
						if(choice.equals("ADD_TO_ALL")){
							appendToAllPanes(para+"\n");
						}else if(choice.equals("PROMPT")){
							UnknownLangPrompt prompt=new UnknownLangPrompt(para,plListModel,emptyFormat,null);
							try {
								combText.highlight(i);
							} catch (BadLocationException e) {
								// TODO Auto-generated catch block
								System.out.println("line number"+i+"bad location in comb panel when highlighting");
								e.printStackTrace();
							}
							prompt.setVisible(true);
							String action=prompt.buttonAction;
							if(action.equals("OK")){
								LdLocale lang=prompt.getLang();
								if(locPaneMap.containsKey(lang)){
									locPaneMap.get(lang).append(para+"\n");
								}else{
									LdLocale locale=loc.get();
									SepPane pane=new SepPane(LangPair.fromLdLocale(locale));
									listAppend(pane);
									locPaneMap.put(locale, pane);
									pane.append(para+"\n");
								}
							}else if(action.equals("ADD_TO_ALL")){
								appendToAllPanes(para+"\n");
							}else if(action.equals("SKIP")){
								//TODO probably add a pane of list to contain all skipped paragraphs: position of the panes at the paragraph and the paragraph
							}else if(action.equals("STOP")){
								prompt.dispose();
								return;
							}else{
								System.out.println("check action command of prompt buttons");
							}
							prompt.dispose();
						}else if(choice.equals("SKIP")){
							//TODO probably add a pane to contain all skipped paragraphs
						}else{
							System.out.println("check action command of radiobuttons");
						}
					}
				}else{
					numBlank++;
					if(numBlank>=2){
						appendToAllPanes("\n");
						numBlank=0;
					}
				}
			}
		}else{
			//if language is specified
			int numBlank=0;
			for(int i=0;i<length;i++){ 
				setProg(i+1,length);
				para=text[i];
				loc=detectLang(para);
				if(!para.equals("")){
					numBlank=0;
					
					if(loc.isPresent()){
						//if language is detected
						if (locPaneMap.containsKey(loc.get())){
							//if the detected language is in the list
							locPaneMap.get(loc.get()).append(para+"\n");
						}else{
							//if detected language is not in the list
							if(choice.equals("ADD_TO_ALL")){
								appendToAllPanes(para+"\n");
							}else if(choice.equals("PROMPT")){
								UnknownLangPrompt prompt=new UnknownLangPrompt(para,plListModel,emptyFormat,loc.get());
								try {
									combText.highlight(i);
								} catch (BadLocationException e) {
									// TODO Auto-generated catch block
									System.out.println("line number"+i+"bad location in comb panel when highlighting");
									e.printStackTrace();
								}
								prompt.setVisible(true);
								String action=prompt.buttonAction;
								if(action.equals("OK")){
									LdLocale lang=prompt.getLang();
									if(locPaneMap.containsKey(lang)){
										locPaneMap.get(lang).append(para+"\n");
									}else{
										System.out.println("this should not happen");
									}
								}else if(action.equals("ADD_TO_ALL")){
									appendToAllPanes(para+"\n");
								}else if(action.equals("SKIP")){
									//TODO probably add a pane to contain all skipped paragraphs
								}else if(action.equals("STOP")){
									prompt.dispose();
									return;
								}else{
									System.out.println("check action command of prompt buttons");
								}
								prompt.dispose();
							}else if(choice.equals("SKIP")){
								//TODO probably add a pane to contain all skipped paragraphs
							}else{
								System.out.println("check action command of radiobuttons");
							}
						}
					}else{
						//if language is not detected
						if(choice.equals("ADD_TO_ALL")){
							appendToAllPanes(para+"\n");
						}else if(choice.equals("PROMPT")){
							UnknownLangPrompt prompt=new UnknownLangPrompt(para,plListModel,emptyFormat,null);
							try {
								combText.highlight(i);
							} catch (BadLocationException e) {
								// TODO Auto-generated catch block
								System.out.println("line number"+i+"bad location in comb panel when highlighting");
								e.printStackTrace();
							}
							prompt.setVisible(true);
							String action=prompt.buttonAction;
							if(action.equals("OK")){
								LdLocale lang=prompt.getLang();
								if(locPaneMap.containsKey(lang)){
									locPaneMap.get(lang).append(para+"\n");
								}else{
									System.err.println("Shouldn't happen: lang specified, prompt, selected pane not present");
								}
							}else if(action.equals("ADD_TO_ALL")){
								appendToAllPanes(para+"\n");
							}else if(action.equals("SKIP")){
								//TODO probably add a pane to contain all skipped paragraphs
							}else if(action.equals("STOP")){
								prompt.dispose();
								return;
							}else{
								System.out.println("check action command of prompt buttons");
							}
							prompt.dispose();
						}else if(choice.equals("SKIP")){
							//TODO probably add a pane to contain all skipped paragraphs
						}else{
							System.out.println("check action command of radiobuttons");
						}
					}
				}else{
					numBlank++;
					if(numBlank>=2){
						appendToAllPanes("\n");
						numBlank=0;
					}
				}
			}
		}
	}
	//TODO use swing worker here. use wait cursor
	//TODO rewrite the separation methods so that gui updates occurs outside doinbackground
	//separation and combination button action codes
	protected void do_btnSep_actionPerformed(ActionEvent e) {
		//TODO add text separation code
		boolean ufT=getUnformatText();
		final boolean emptyFormat=plListModel.isEmpty();
		final String[] text=this.getComb().getText().split("\n");
		btnSep.setEnabled(false);
		btnComb.setEnabled(false);
		if(!ufT){
			(new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					
					formattedSep(emptyFormat,text);
					return null;
				}
				@Override
				protected void done(){
					btnSep.setEnabled(true);
					btnCombStatCheck();
				}
			}).execute();
		}else{
			(new SwingWorker<Void,Void>(){
				@Override
				protected Void doInBackground() throws Exception {
					langSep(emptyFormat,text);
					return null;
				}
				@Override
			    protected void done(){
					UnknownLangPrompt.reset();
					btnSep.setEnabled(true);
					btnCombStatCheck();
				}
				
			}).execute();
		}
	}
	protected void do_btnComb_actionPerformed(ActionEvent e) {
		//TODO rewrite code to update GUI only in publish
		if(isListFormatPresent()){
			
			(new SwingWorker<Void,Void>(){

				@Override
				protected Void doInBackground() throws Exception {
					// TODO Auto-generated method stub
					combText.setText("");//clear combined panel
					Object[] objs=plListModel.toArray();
					ArrayList<String[]>paneText=new ArrayList<>();
					int length=getMaxPaneLength();
					for(Object obj:objs){
						//Store the text in panes into arraylist
						SepPane pane=(SepPane)obj;
						if(!pane.getLp().getCode().equals("newl")){
							//TODO change \n to a String variable for paragraph separation stored in SepPane instance, \n or \n\n
							String[] textArray=pane.getText().split("\n");
							paneText.add(textArray);
						}else{
							paneText.add(null);
						}
					}
					
					int maxFormat=plListModel.getSize();
					for(int i=0;i<length;i++){
						//assume paneText is properly segmented
						for(int numFormat=0;numFormat<maxFormat;numFormat++){
							setProg(i*maxFormat+numFormat+1,length*maxFormat);
							String [] text=paneText.get(numFormat);
							if(text!=null){
								try{
									combText.append(text[i]+"\n");
								}catch(ArrayIndexOutOfBoundsException ex){
									ex.printStackTrace();
									combText.append("\n");
									setStatus("Please ensure the texts have the same number of lines. Check output and try again.");
								}
							}else{
								combText.append("\n");
							}
						}
					}
				
					return null;
				}
				
			}).execute();
			}else{
				setStatus("Please specify the format with at least one language.");
			}
			
	}
	
	protected static void do_mntmSaveAs_actionPerformed(ActionEvent e) {
		//save = new ObjectStream();
	}
	
	//TODO probably change to a listener that listen to checked status change only
	/**
	 * Change layout for formatted text and unformatted text
	 * @param arg0
	 */
	protected void do_chckbxUnformattedText_actionPerformed(ActionEvent e) {
		boolean ufTStatus=chckbxUnformattedText.isSelected();
		syncFormatTextStat(ufTStatus);
	}
	
	/**
	 * Syncs UI components after the checkbox for unformatted text is checked or unchecked
	 * @param ufTStatus true - unformatted text mode
	 */
	void syncFormatTextStat(boolean ufTStatus){
		
		langNotDetOptPane.setVisible(ufTStatus);//display the option for what to do when language not detected
		paneFormatEdit.setVisible(!ufTStatus);//idk whats this
		btnAddEmptyLine.setEnabled(!ufTStatus);//disable the button to add empty line (hiding it seems to screw up the + and - buttons on mac)
		btnComb.setEnabled(!ufTStatus);//disable combine button as you can't combine without format
		if(ufTStatus){
			border.setTitle("Languages:");
			plList.setCellRenderer(new SepPaneListRenderer());//probably to hide the empty line option
		}
		else{
			border.setTitle("Format:");
			plList.setCellRenderer(new DefaultListCellRenderer());
		}
		syncLocPaneMap();
		revalidate();
	}
	void syncLocPaneMap(){
		locPaneMap=createLocPaneMap(plListModel);
	}
	HashMap<LdLocale,SepPane> createLocPaneMap(DefaultListModel<SepPane> model){
		Object[] panes=model.toArray();
		HashMap<LdLocale,SepPane> map=new HashMap<>();
		for(Object obj:panes){
			SepPane pane=(SepPane) obj;
			map.put(pane.getLp().getLocale(), pane);
		}
		return map;
	}
	//TODO add to list and remove from list hashmap sync
	public void valueChanged(ListSelectionEvent arg0) {
		if (arg0.getSource() == plList) {
			do_plList_valueChanged(arg0);
		}
	}
	/**
	 * Disable edit for newline items, disable edit and remove buttons when no selection
	 * @param arg0
	 */
	protected void do_plList_valueChanged(ListSelectionEvent arg0) {
		SepPane listItem=plList.getSelectedValue();
		if(listItem!=null){
			btnRmv.setEnabled(true);
			LangPair lp=listItem.getLp();
			String code=lp.getCode();
			if(!code.equals("newl"))
				btnEdit.setEnabled(true);
			else btnEdit.setEnabled(false);
		}else{
			btnEdit.setEnabled(false);
			btnRmv.setEnabled(false);
		}
	}
	//TODO implement an edited flag for checking whether fields are edited
	//TODO probably optimize the performance of the list data listener methods
	@Override
	public void intervalAdded(ListDataEvent e) {
		if(e.getSource()==plListModel)listChangeSync();
	}

	@Override
	public void intervalRemoved(ListDataEvent e) {
		if(e.getSource()==plListModel)listChangeSync();
	}

	@Override
	public void contentsChanged(ListDataEvent e) {
		if(e.getSource()==plListModel)listChangeSync();
	}
	void listChangeSync(){
		if(!isListFormatPresent())btnComb.setEnabled(false);
		else if(!getUnformatText())btnComb.setEnabled(true);
		syncPanes();
		if(getUnformatText())syncLocPaneMap();
	}
	void btnCombStatCheck(){
		if(getUnformatText())btnComb.setEnabled(false);
		if(!isListFormatPresent())btnComb.setEnabled(false);
		else if(!getUnformatText())btnComb.setEnabled(true);
		
	}
	
}
