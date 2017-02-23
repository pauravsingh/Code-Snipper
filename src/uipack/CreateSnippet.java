package uipack;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import java.io.*;


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
        String fileName = virtualFile.getNameWithoutExtension();
        SelectionModel selectionModel = editor.getSelectionModel();
       //if text is highlighted
        if(selectionModel.hasSelection())
        {
            //Create a file to store list of snippets
            File file = new File("..\\plugins\\codesnipper\\files\\"+projectName);
            if(!file.exists())
            {
                try {
                    file.createNewFile();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            //Take the snippet name
            String name = JOptionPane.showInputDialog( "Enter snippet name?","Name Snippet");
            if(name != null) {
                String commentStart = "/* CS:start-" + name + " */\n";
                String commentEnd = "\n/* CS:end-" + name + " */";
                final int start = selectionModel.getSelectionStart();
                final int end = selectionModel.getSelectionEnd() + commentStart.length();
                String data = selectionModel.getSelectedText();
                BufferedWriter metaWriter = null;
                BufferedWriter dataWriter = null;
                try {
                    //Add snippet name to the file
                    metaWriter = new BufferedWriter(new FileWriter(file.getPath(),true));
                    metaWriter.append("I-"+name+"\n");
                    metaWriter.close();
                    //Create a new file of the snippet name and store the snippet in it
                    dataWriter = new BufferedWriter(new FileWriter("..\\plugins\\codesnipper\\data\\" + name));
                    dataWriter.append(data);
                    dataWriter.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


                final Runnable readRunner = new Runnable() {
            @Override
            public void run() {
                document.insertString(start,commentStart);
                document.insertString(end,commentEnd);
                selectionModel.removeSelection();
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
        }
        }
    }

}
