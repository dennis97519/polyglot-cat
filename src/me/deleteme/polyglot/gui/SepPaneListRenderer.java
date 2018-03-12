package me.deleteme.polyglot.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import sun.swing.DefaultLookup;

public class SepPaneListRenderer extends DefaultListCellRenderer {

	public SepPaneListRenderer() {
        super();
    }
	@Override
	public Component getListCellRendererComponent(
	        JList<?> list,
	        Object value,
	        int index,
	        boolean isSelected,
	        boolean cellHasFocus)
	    {
			if(value instanceof SepPane){
				SepPane pane=(SepPane)value;
				if(!pane.getLp().getCode().equals("newl")){
					Component c=super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					JLabel cell=(JLabel)c;
					cell.setText(pane.getLp().toString()+" ("+pane.getName()+")");
					return cell;
				}
				else{
					Component empty=new JLabel("");
					return empty;
				}
			}
			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	    }
}
