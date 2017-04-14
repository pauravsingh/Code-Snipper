package uipack;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.*;
import java.io.*;

/**
 * Created by paurav on 1/29/2017.
 */
public class InsertSnippet extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        //Get the virtual file
        Project project = e.getProject();
        String projectName = project.getName();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Document document = editor.getDocument();
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String fileName = virtualFile.getParent().getName() + "." + virtualFile.getNameWithoutExtension();
        MarkupModel markup = editor.getMarkupModel();
        GutterHandler gutterHandler = new GutterHandler();
        FileHandler fileHandler = new FileHandler();
        String contents = document.getText();


        //Read the list of snippets
        String[] snipList = fileHandler.getNamesList("insert", "F", projectName, fileName, "A");
        //Display list
        if (snipList[0].length() > 1) {
            String name = (String) JOptionPane.showInputDialog(null, "Select Snippet", "Insert Snippet", JOptionPane.QUESTION_MESSAGE, null, snipList, snipList[0]);

            if (name != null) {
                //Open the selected snippet file, read it and insert it in the editor
                final String data = fileHandler.readSnippet(projectName, fileName, name);

                String commentStart = "/* CS:start-" + name + " */";
                String commentEnd = "/* CS:end-" + name + " */";

                //find the location of start comment in the editor
                int start = contents.indexOf("/* CS:start-" + name + " */") + commentStart.length();
                int end = start + data.length();

                //Modify the snippet list to indicate snippet is inserted
                fileHandler.changeStatus(projectName, fileName, name, "I");


                final Runnable readRunner = new Runnable() {
                    @Override
                    public void run() {
                        document.insertString(start, data);
                        document.insertString(end, commentEnd);
                        //gutterHandler.setGutterInserted(markup,start,end);
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
