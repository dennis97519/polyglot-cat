package me.deleteme.polyglot.gui;

import java.awt.datatransfer.DataFlavor;

import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

//Cite source http://java-swing-tips.blogspot.com/2010/08/drag-and-drop-between-jlists.html
//TODO look up on transfer between windows
class ListItemTransferHandler extends TransferHandler {
    private final DataFlavor localObjectFlavor;
    private JList source;
    private int[] indices;
    private int addIndex = -1; //Location where items were added
    private int addCount; //Number of items added.

    public ListItemTransferHandler() {
        super();
        localObjectFlavor = new ActivationDataFlavor(Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
    }
    @Override protected Transferable createTransferable(JComponent c) {
        source = (JList) c;
        indices = source.getSelectedIndices();
        @SuppressWarnings("deprecation") Object[] transferedObjects = source.getSelectedValues();
        return new DataHandler(transferedObjects, localObjectFlavor.getMimeType());
    }
    @Override public boolean canImport(TransferSupport info) {
    	JList comp=(JList)info.getComponent();
    	int action=info.getDropAction();
    	/*if(comp!=source){
    		System.out.println("set drop action copy:"+TransferHandler.COPY+"bitwise and with copyormove"+(COPY&COPY_OR_MOVE));
    		info.setDropAction(TransferHandler.COPY);
    	}*/
    	if((comp==source&&action==MOVE)||(comp!=source&&action==COPY))
    		return info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
    	else return false;
    }
    @Override public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE; //TransferHandler.COPY_OR_MOVE;
    }
    @SuppressWarnings("unchecked")
    @Override public boolean importData(TransferSupport info) {
        if (!canImport(info)) {
            return false;
        }
        TransferHandler.DropLocation tdl = info.getDropLocation();
        if (!(tdl instanceof JList.DropLocation)) {
            return false;
        }
        JList.DropLocation dl = (JList.DropLocation) tdl;
        JList target = (JList) info.getComponent();
        DefaultListModel listModel = (DefaultListModel) target.getModel();
        int index = dl.getIndex();
        //boolean insert = dl.isInsert();
        int max = listModel.getSize();
        if (index < 0 || index > max) {
            index = max;
        }
        addIndex = index;

        try {
            Object[] values = (Object[]) info.getTransferable().getTransferData(localObjectFlavor);
            for (int i = 0; i < values.length; i++) {
                int idx = index++;
                listModel.add(idx, values[i]);
                target.addSelectionInterval(idx, idx);
            }
            addCount = target.equals(source) ? values.length : 0;
            return true;
        } catch (UnsupportedFlavorException | IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    @Override protected void exportDone(JComponent c, Transferable data, int action) {
        cleanup(c, action == MOVE);
    }
    private void cleanup(JComponent c, boolean remove) {
        if (remove && indices != null) {
            //If we are moving items around in the same list, we
            //need to adjust the indices accordingly, since those
            //after the insertion point have moved.
            if (addCount > 0) {
                for (int i = 0; i < indices.length; i++) {
                    if (indices[i] >= addIndex) {
                        indices[i] += addCount;
                    }
                }
            }
            JList source = (JList) c;
            DefaultListModel model  = (DefaultListModel) source.getModel();
            for (int i = indices.length - 1; i >= 0; i--) {
                model.remove(indices[i]);
            }
        }
        indices  = null;
        addCount = 0;
        addIndex = -1;
    }
}