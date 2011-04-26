package srl.visgo.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.openide.awt.DropDownButtonFactory;

import srl.visgo.data.Workspace;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.DocumentListEntry;


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

		//Upload document button
		JButton uploadButton = new JButton(new ImageIcon("image/docupload.png"));
		uploadButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		uploadButton.setToolTipText("Upload a new document");
		uploadButton.addActionListener(uploadCallback);

		//Create folder button
		JButton folderButton = new JButton(new ImageIcon("image/add_folder.png"));
		folderButton.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
		folderButton.setToolTipText("Create a new group");
		folderButton.addActionListener(folderCallback);
		
		this.add(docsDropDownButton);
		this.add(folderButton);
		this.add(uploadButton);
	}

	/**
	 * The event for clicking the Upload button
	 */
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

	/**
	 * Helper function for uploading files
	 * @param file The file
	 * @param title The file's name
	 * @return success
	 */
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

	/**
	 * The event for clicking the Create New Document button
	 */
	private ActionListener createDocCallback = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			//Prompt for a file name
			String name = null;
			ImageIcon icon = new ImageIcon("image/add_page.png");
			name = (String) JOptionPane.showInputDialog(
					null, "Enter file name:",
					"Create a new file", JOptionPane.PLAIN_MESSAGE, 
					icon, null, 
					"new file");
			if(name != null)
			{
				if(mWorkspace.createDocument(e.getActionCommand(), name))
				{
					JOptionPane.showMessageDialog(null, "File created successfully");
				}
				else
				{
					JOptionPane.showMessageDialog(null, "There was an error creating the file.");
				}
			}
			else
			{
				JOptionPane.showMessageDialog(null, "No name specified");
			}
		}
	};

	/**
	 * The event for clicking the Create New Group button
	 */
	private ActionListener folderCallback = new ActionListener()
	{
		public void actionPerformed(ActionEvent e)
		{
			//Prompt for new group name
			String name = null;
			ImageIcon icon = new ImageIcon("image/add_to_folder.png");
			name = (String) JOptionPane.showInputDialog(
					null, "Enter group name:",
					"Create a new group", JOptionPane.PLAIN_MESSAGE, 
					icon, null, 
					"new group");
			if(name != null)
			{
				mWorkspace.createGroup(name);
				JOptionPane.showMessageDialog(null, "Group created successfully");
			}
			else
			{
				JOptionPane.showMessageDialog(null, "No name specified");
			}
		}
	};
}