package me.deleteme.polyglot.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import me.deleteme.polyglot.util.LangList;
import me.deleteme.polyglot.util.LangPair;
import net.miginfocom.swing.MigLayout;
/**
 * Dialog that add or edit item in plListModel in MWsetting. It either creates a new SepPane object or modify an existing SepPane object.
 * @author Dennis
 * 
 */
public class AddEdit extends JDialog implements ActionListener, ItemListener {
	/**
	 * Dialog container
	 */
	private final JPanel contentPanel = new JPanel();
	private JTextField txtName;
	private JButton cancelButton;
	private JButton okButton;
	JComboBox<LangPair> comboBox;
	LangPair currSel;
	/**
	 * Pane to be edited or created
	 */
	SepPane curr;
	/**
	 * List of languages only needed by the combobox in this dialog, but dialog is created on demand, so made static to make it accessible all the time
	 */
	public static DefaultComboBoxModel <LangPair> lList = new DefaultComboBoxModel<LangPair>();
	
	/**
	 * Load lList with LangList
	 */
	public static void loadList(){
		for(String[] sLang:LangList.list){
			lList.addElement(new LangPair(sLang[1],sLang[0]));
		}
	}
	/**
	 * Create the dialog for add operation.
	 */
	public AddEdit() {
		setResizable(false);
		initComponents();
		setTitle("Add");
	}
	/**
	 * Create the dialog for edit operation. Will load LangPair and Name information.
	 * @param obj the SepPane object to be edited
	 */
	public AddEdit(SepPane obj){
		curr=obj;
		initComponents();
		comboBox.setSelectedItem(curr.getLp());
		currSel=curr.getLp();
		txtName.setText(curr.getName());
		setTitle("Editing \""+curr.getName()+"\"");
	}
	/**
	 * Initialization of components of dialog
	 */
	private void initComponents() {
		setModalityType(ModalityType.APPLICATION_MODAL);
		setModal(true);//Set dialog as modal so the main window will be blocked when the dialog is visible
		setBounds(100, 100, 450, 153);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][grow]", "[][]"));
		{
			JLabel lblName = new JLabel("Name:");
			contentPanel.add(lblName, "trailing");
		}
		{
			txtName = new JTextField();
			contentPanel.add(txtName, "growx,wrap");
			txtName.setColumns(10);
		}
		{
			JLabel lblLanguage = new JLabel("Language:");
			contentPanel.add(lblLanguage, "trailing");
		}
		{
			comboBox = new JComboBox<LangPair>();//Creates ComboBox for type LangPair
			comboBox.addItemListener(this);
			comboBox.setModel(lList);//Uses the static DefaultComboBoxModel lList
			contentPanel.add(comboBox, "growx");
			txtName.setText(((LangPair)comboBox.getSelectedItem()).getName());//set the name field to default name
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
				cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(this);
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == okButton) {
			do_okButton_actionPerformed(e);
		}
		if (e.getSource() == cancelButton) {
			do_cancelButton_actionPerformed(e);
		}
	}
	/**
	 * hides the dialog box and return control to parent window
	 * @param e
	 */
	protected void do_cancelButton_actionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
	/**
	 * 
	 * @param e
	 */
	protected void do_okButton_actionPerformed(ActionEvent e) {
		if(curr==null){
			curr = new SepPane((LangPair) comboBox.getSelectedItem(),txtName.getText());
		}else{
			curr.setName(txtName.getText());
			curr.setLp((LangPair) comboBox.getSelectedItem());
			System.out.println(curr.getLp());
		}
		this.setVisible(false);
	}
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == comboBox) {
			do_comboBox_itemStateChanged(e);
		}
	}
	//TODO trace and investigate when item state event is fired
	protected void do_comboBox_itemStateChanged(ItemEvent e) {
		if(e.getStateChange()==ItemEvent.SELECTED){
			if(txtName.getText().equals("")||
					txtName.getText().equals(
							currSel.getName())){
				//set the name field to default language name
				txtName.setText(((LangPair)e.getItem()).getName());
			}
		}else if(e.getStateChange()==ItemEvent.DESELECTED){
			currSel = (LangPair) e.getItem();
		}
	}
}
