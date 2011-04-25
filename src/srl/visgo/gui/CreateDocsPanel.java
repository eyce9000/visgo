package srl.visgo.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.openide.awt.DropDownButtonFactory;

import srl.visgo.data.Workspace;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentEntry;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.PresentationEntry;
import com.google.gdata.data.docs.SpreadsheetEntry;
import com.google.gdata.util.ServiceException;


@SuppressWarnings("serial")
public class CreateDocsPanel extends JPanel
{
	private JFileChooser fileChooser;
	private Workspace mWorkspace;

	public CreateDocsPanel(Workspace workspace)
	{
		mWorkspace = workspace;
		
		JPopupMenu docTypesMenu = new JPopupMenu();
		JButton docsDropDownButton;
		String[] docTypes = {"Document", "Presentation", "Spreadsheet", "Form"}; //, "Drawing"};
		for (int i = 0; i < docTypes.length; i++)
		{
			String name = docTypes[i];
			JMenuItem item = new JMenuItem(name);
			item.setName(name);
			item.setIcon(new ImageIcon("image/" + name + "_small.png"));
			item.addActionListener(createDocCallback);
			docTypesMenu.add(item);
		}
		
		// use the org.openide.awt.DropDownButtonFactory to create a DropDownButton,
		// define the icon and then assign our JPopupMenu to the JButton:
		docsDropDownButton = DropDownButtonFactory.createDropDownButton(new ImageIcon("image/add.png"), docTypesMenu);

		// the JButton will be quite long by default,
		// let's trim it by removing borders:
		docsDropDownButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

		docsDropDownButton.setToolTipText("Create a new document");

		JButton uploadButton = new JButton(new ImageIcon("image/docupload.png"));
		uploadButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		uploadButton.setToolTipText("Upload a new document");
		uploadButton.addActionListener(uploadCallback);
		
		this.add(docsDropDownButton);
		this.add(uploadButton);
	}
	
	private ActionListener uploadCallback = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			fileChooser = new JFileChooser();
			int returnVal = fileChooser.showOpenDialog(CreateDocsPanel.this);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fileChooser.getSelectedFile();
                
                if(uploadFile(file, file.getName()))
                {
                	JOptionPane.showMessageDialog(null, "File uploaded successfully");
                }
                else
                {
                	JOptionPane.showMessageDialog(fileChooser, "File upload failed");
                }
            }
		}
	};
	
	public boolean uploadFile(File file, String title)
	{
		String mimeType;
		
		try
		{
			mimeType = DocumentListEntry.MediaType.fromFileName(file.getName()).getMimeType();
		}
		catch(Exception e)	//Apparently that function doesn't correctly check the extensions and will throw an Enum exception if it's wrong
		{
			JOptionPane.showMessageDialog(fileChooser, "Invalid file type selected");
			return false;
		}

		DocumentListEntry newDocument = new DocumentListEntry();
		newDocument.setFile(file, mimeType);
		newDocument.setTitle(new PlainTextConstruct(title));

		return mWorkspace.createDocumentFromExisting(newDocument);
	}
	
	private ActionListener createDocCallback = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			if(mWorkspace.createDocument(e.getActionCommand()))
			{
				JOptionPane.showMessageDialog(null, "File created successfully");
			}
			else
			{
				JOptionPane.showMessageDialog(null, "There was an error creating the file.");
			}
		}
	};
}