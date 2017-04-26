package uipack;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;


/**
 * Created by paurav on 1/28/2017.
 */
public class CreateSnippet extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        //Get the virtual file
        Project project = e.getProject();
        String projectName = project.getName();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Document document = editor.getDocument();
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String fileName = virtualFile.getParent().getName()+"."+virtualFile.getNameWithoutExtension();
        SelectionModel selectionModel = editor.getSelectionModel();
        MarkupModel markup = editor.getMarkupModel();
        GutterHandler gutterHandler = new GutterHandler();
        FileHandler fileHandler = new FileHandler();
        String Scope = "F";
        String name = null;

        //if text is highlighted
        if(selectionModel.hasSelection())
        {
            //Take the snippet name
            //String name = JOptionPane.showInputDialog( "Enter snippet name?","Name Snippet");
            JPanel jpanel = new JPanel();
            jpanel.setLayout(new VerticalFlowLayout());
            jpanel.setFocusable(true);

            JTextField snippetName = new JTextField(20);


            JRadioButton fileButton = new JRadioButton("File",true);
            JRadioButton projectButton = new JRadioButton("Project");
            JRadioButton libraryButton = new JRadioButton("Library");
            ButtonGroup scopeOption = new ButtonGroup();
            scopeOption.add(fileButton);
            scopeOption.add(projectButton);
            scopeOption.add(libraryButton);

            jpanel.add(new JLabel("Enter Name:"));
            jpanel.add(snippetName);
            jpanel.add(fileButton);
            jpanel.add(projectButton);
            jpanel.add(libraryButton);
            snippetName.requestFocusInWindow();

            int result = JOptionPane.showConfirmDialog(null,jpanel,"Create Snippet",JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                name = snippetName.getText();
                if(fileButton.isSelected())
                    Scope = "F";
                if(projectButton.isSelected())
                    Scope = "P";
                if(libraryButton.isSelected())
                    Scope = "L";

            }

            if(name != null) {
                String exists = fileHandler.getNamesWithStatus();
                if (!exists.contains("--" + projectName + "--" + fileName + "--" + name)) {
                    String commentStart = "/* CS:start-" + name + " */\n";
                    String commentEnd = "\n/* CS:end-" + name + " */";
                    final int start = selectionModel.getSelectionStart();
                    final int end = selectionModel.getSelectionEnd() + commentStart.length();
                    String data = selectionModel.getSelectedText();

                    //Add snippet name to the file
                    fileHandler.addSnippetName(Scope, projectName, fileName, name);

                    //Create a new file of the snippet name and store the snippet in it
                    fileHandler.writeSnippet(projectName, fileName, name, data);
                    final String sName = name;
                    final Runnable readRunner = new Runnable() {
                        @Override
                        public void run() {
                            document.insertString(start, commentStart);
                            document.insertString(end, commentEnd);
                            selectionModel.removeSelection();
                            gutterHandler.setGutterInserted(projectName,fileName,sName,markup,start,start+commentStart.length());
                        }
                    };
                    ApplicationManager.getApplication().invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            CommandProcessor.getInstance().executeCommand(project, new Runnable() {
                                @Override
                                public void run() {
                                    ApplicationManager.getApplication().runWriteAction(readRunner);
                                }
                            }, "DiskRead", null);
                        }
                    });
                } else {
                    JOptionPane.showMessageDialog(null, new JPanel().add(new JLabel("Name already exists!")), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}
