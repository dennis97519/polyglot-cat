package me.deleteme.polyglot.gui;

import javax.swing.JPanel;

import me.deleteme.polyglot.util.LangPair;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.text.BadLocationException;

public class SepPane extends JPanel{
	private LangPair lang;
	private JScrollPane scrollPane;
	private JLabel lblName;
	private JTextArea txtArea;
	/**
	 * Create SepPane with name and language
	 * @param lp LangPair Language
	 * @param n String Name
	 */
	public SepPane(LangPair lp, String n) {
		lang = lp;
		lblName = new JLabel(n);
		initComponents();
	}
	/**
	 * Create SepPane with default locale name
	 * @param lp Language
	 */
	public SepPane(LangPair lp){
		this(lp,lp.getName());
	}
	/**
	 * Create multilang SepPane 
	 */
	public SepPane(){
		lang = new LangPair("Combined","comb");
		lblName = new JLabel("Combined");
		initComponents();
	}
	private void initComponents() {
		setPreferredSize(new Dimension(300, 400));
		setMinimumSize(new Dimension(300, 400));
		setLayout(new BorderLayout());
		add(lblName,BorderLayout.NORTH);
		txtArea = new JTextArea();
		txtArea.setLineWrap(true);
		scrollPane = new JScrollPane();
		scrollPane.setViewportView(txtArea);
		add(scrollPane,BorderLayout.CENTER);
		add(Box.createHorizontalStrut(MainWindow.strutWidth),BorderLayout.EAST);
	}
	
	//get and set for name label
	/**
	 * Used for the list’s renderer
	 * @return string representation of the pane as the content of lblName or Empty Line
	 * 
	 */
	@Override
	public String toString(){
		if(!getLp().getCode().equals("newl")) return lblName.getText()+" ("+getLp().toString()+")";
		else return getName();
	}
	/**
	 * set content of lblName
	 */
	public void setName(String n){
		lblName.setText(n);
	}
	/**
	 * returns the content of lblName(Useless?)
	 */
	public String getName(){
		return lblName.getText();
	}
	
	//get and set for language properties
	/**
	 * get LangPair object of this panel
	 * @return the language
	 */
	public LangPair getLp(){
		return lang;
	}
	/**
	 * Set new language pair from the edit box
	 * @param lp the new stuff
	 */
	public void setLp(LangPair lp){
		lang=lp;
	}
	/**
	 * for checking the language code when used with the library
	 * @return string of the language code
	 */
	public String getCode(){
		return lang.getCode();
	}
	
	//get and set for the text box
	/**
	 * Get text from text area in this pane
	 * @return the content of the text area
	 */
	public String getText(){
		return txtArea.getText();
	}
	public int getLineCount(){
		return txtArea.getLineCount();
	}
	/**
	 * Set text of txtArea
	 * @param txt the text to set
	 */
	public void setText(String txt){
		txtArea.setText(txt);
	}
	/**
	 * Append text string to text area inside this pane
	 * @param txt the string to append
	 */
	public void append(String txt){
		if(lang.getCode()!="newl"){
			txtArea.append(txt);
		}
	}
	public void highlight(int para) throws BadLocationException{
		txtArea.setCaretPosition(txtArea.getLineStartOffset(para));
		txtArea.moveCaretPosition(txtArea.getLineEndOffset(para));
	}
	public boolean isEmpty(){
		return txtArea.getText().equals("");
	}
	/**
	 * Clear the text area
	 */
	public void clear(){
		txtArea.setText("");
	}
}
