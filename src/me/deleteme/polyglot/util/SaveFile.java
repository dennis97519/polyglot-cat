package me.deleteme.polyglot.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.undo.UndoManager;

import me.deleteme.polyglot.gui.MainWindow;
import me.deleteme.polyglot.gui.SepPane;

class PaneText{
	LangPair lang;
	String name;
	String text;
	/**
	 * Object describing data in SepPane
	 * @param l LangPair language of the SepPane
	 * @param n String name of the SepPane
	 * @param s String text in the SepPane
	 */
	PaneText(LangPair l,String n,String s){
		lang=l;
		name=n;
		text=s;
	}
	/**
	 * Create PaneText from SepPane
	 * @param pane
	 */
	PaneText(SepPane pane){
		this(pane.getLp(),pane.getName(),pane.getText());
	}
	/**
	 * Apply data into SepPane
	 */
	SepPane toSepPane(){
		SepPane pane=new SepPane(lang,name);
		pane.setText(text);
		return pane;
	}
	
}
public class SaveFile implements Serializable {
	private PaneText comb;
	private List<PaneText> panes;
	private boolean unformattedText;
	private UndoManager history;
	public SaveFile(SepPane cmb,DefaultListModel<SepPane> pane,boolean uft){
		comb=new PaneText(cmb);
		panes=toList(pane);
		unformattedText=uft;
		//history=undo;
	}
	public SaveFile(MainWindow mw){
		this(mw.getComb(),mw.getPanes(),mw.getUnformatText());
	}
	private ArrayList<PaneText> toList(DefaultListModel<SepPane> list){
		ArrayList<PaneText> l=new ArrayList<>();
		for(Object sp:list.toArray()){
			l.add(new PaneText((SepPane)sp));
		}
		return l;
	}
	public SepPane getComb(){
		return comb.toSepPane();
	}
	public DefaultListModel<SepPane> getPanes(){
		DefaultListModel<SepPane> paneListModel=new DefaultListModel<>();
		for(PaneText pt:panes){
			paneListModel.addElement(pt.toSepPane());
		}
		return paneListModel;
	}
	public UndoManager getUndo(){
		return history;
	}
	//public ReadFile(File)
}
