package me.deleteme.polyglot.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import me.deleteme.polyglot.util.LangPair;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JComboBox;

import com.google.common.base.Optional;
import com.optimaize.langdetect.i18n.LdLocale;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;

public class UnknownLangPrompt extends JDialog implements ActionListener {

	private final JPanel contentPanel = new JPanel();
	private JLabel lblUnableToDetect = new JLabel();
	private String title;
	private JComboBox<LangPair> language;
	private JTextArea paragraph;
	private JButton okButton;
	private JButton cancelButton;
	public String buttonAction="SKIP";//default to skip when close button is pressed
	private JButton btnAddToAll;
	private JButton btnStop;
	private static boolean showCurrentlyPresent=false;
	private JCheckBox chckbxShowCurrentlyPresent;
	private DefaultComboBoxModel<LangPair> currentlyPresent;
	private DefaultComboBoxModel<LangPair> def;
	private static LangPair lastSel=null;
	/**
	 * create a new unknown language prompt or wrong language prompt
	 * @param para the text that have unknown language or wrong language
	 * @param plListModel current 
	 * @param emptyFormat if the user did not specify language
	 * @param lang the wrongly detected language, or null if lanugage is not detected
	 */
	public UnknownLangPrompt(String para, DefaultListModel<SepPane> plListModel,boolean emptyFormat,LdLocale lang) {
		if(lang!=null){
			lblUnableToDetect.setText("The language of the following paragraph was detected as "+LangPair.fromLdLocale(lang).toString());
			title="Detected language does not match specified languages";
			
		}else{
			lblUnableToDetect.setText("Unable to detect the language of the following paragraph");
			title="Unable to detect language";
			
		}
		currentlyPresent=createChoice(plListModel,false);
		def=createChoice(plListModel,emptyFormat);
		
		initComponents();
		//write code that require non null components after this line
		if(!emptyFormat) chckbxShowCurrentlyPresent.setVisible(false);
		else chckbxShowCurrentlyPresent.setVisible(true);
		language.setModel(def);
		if(lastSel!=null)language.setSelectedItem(lastSel);
		chckbxShowCurrentlyPresent.setSelected(showCurrentlyPresent);
		do_chckbxShowCurrentlyPresent_actionPerformed(null);
		paragraph.setText(para);
		addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		        language.requestFocusInWindow();
		    }
		});
		this.getRootPane().setDefaultButton(okButton);
	}
	/**
	 * create combobox model for the language choices
	 * @param plListModel current list of lanugages
	 * @param emptyFormat if the user did not specify language
	 * @return
	 */
	private DefaultComboBoxModel<LangPair> createChoice(DefaultListModel<SepPane> plListModel,boolean emptyFormat){
		if (emptyFormat)return AddEdit.lList;
		else{
			Object[] panes=plListModel.toArray();
			DefaultComboBoxModel<LangPair> choices=new DefaultComboBoxModel<>();
			for(Object obj:panes){
				SepPane pane=(SepPane)obj;
				if(!pane.getLp().getCode().equals("newl"))choices.addElement(pane.getLp());
			}
			return choices;
		}
	}
	private void initComponents(){
		setTitle(title);
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);
		setBounds(100, 100, 611, 436);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow]", "[][grow][][][]"));
		{
			
			contentPanel.add(lblUnableToDetect, "cell 0 0");
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, "cell 0 1,grow");
			{
				paragraph = new JTextArea();
				paragraph.setLineWrap(true);
				scrollPane.setViewportView(paragraph);
			}
		}
		{
			JLabel lblPleaseManuallySelect = new JLabel("Please manually select the langauge of the paragraph below.");
			contentPanel.add(lblPleaseManuallySelect, "cell 0 2");
		}
		{
			language = new JComboBox<>();
			contentPanel.add(language, "cell 0 3,growx");
		}
		{
			chckbxShowCurrentlyPresent = new JCheckBox("Show currently present languages");
			chckbxShowCurrentlyPresent.setActionCommand("");
			chckbxShowCurrentlyPresent.addActionListener(this);
			contentPanel.add(chckbxShowCurrentlyPresent, "cell 0 4");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.addActionListener(this);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Skip");
				cancelButton.addActionListener(this);
				{
					btnAddToAll = new JButton("Add to all");
					btnAddToAll.addActionListener(this);
					btnAddToAll.setActionCommand("ADD_TO_ALL");
					buttonPane.add(btnAddToAll);
				}
				cancelButton.setActionCommand("SKIP");
				buttonPane.add(cancelButton);
			}
			{
				btnStop = new JButton("Stop");
				btnStop.addActionListener(this);
				btnStop.setActionCommand("STOP");
				buttonPane.add(btnStop);
			}
		}
		
	}
	public LdLocale getLang(){
		Object obj=language.getSelectedItem();
		LangPair lp=(LangPair)obj;
		LdLocale lang=lp.getLocale();
		return lang;
	}
	public static void reset(){
		lastSel=null;
		showCurrentlyPresent=false;
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == chckbxShowCurrentlyPresent) {
			do_chckbxShowCurrentlyPresent_actionPerformed(e);
		}else{
			buttonAction=e.getActionCommand();
			lastSel=(LangPair)language.getSelectedItem();
			setVisible(false);
		}
	}
	protected void do_chckbxShowCurrentlyPresent_actionPerformed(ActionEvent e) {
		if(chckbxShowCurrentlyPresent.isSelected()){
			language.setModel(currentlyPresent);
			showCurrentlyPresent=true;
		}else{
			language.setModel(def);
			showCurrentlyPresent=false;
		}
	}

}
