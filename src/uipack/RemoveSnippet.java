package uipack;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.*;

/**
 * Created by paurav on 1/29/2017.
 */
public class RemoveSnippet extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        //Get the virtual file
        Project project = e.getProject();
        String projectName = project.getName();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Document document = editor.getDocument();
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String fileName = virtualFile.getParent().getName()+"."+virtualFile.getNameWithoutExtension();
        MarkupModel markup = editor.getMarkupModel();
        GutterHandler gutterHandler = new GutterHandler();
        FileHandler fileHandler = new FileHandler();
        String contents = document.getText();

        //Read the list of snippets
        String[] snipList = fileHandler.getNamesList("remove","F",projectName,fileName,"I");

        //Display the list
        if (snipList[0].length() > 1) {
            String name = (String) JOptionPane.showInputDialog(null, "Select Snippet", "Remove Snippet", JOptionPane.QUESTION_MESSAGE, null,
                    snipList,
                    snipList[0]);

            if (name != null) {
                //Locate the snippet in editor and copy it to its file
                String commentStart = "/* CS:start-" + name + " */";
                String commentEnd = "/* CS:end-" + name + " */";
                int start = contents.indexOf("/* CS:start-" + name + " */") + commentStart.length();
                int end = contents.indexOf("/* CS:end-" + name + " */") + commentEnd.length();
                String data = contents.substring(start,end - commentEnd.length()-1);

                fileHandler.writeSnippet(projectName,fileName,name,data);
                //Modify the snippet list to show snippet is available for insert
                fileHandler.changeStatus(projectName,fileName,name,"A");


                final Runnable readRunner = new Runnable() {
                    @Override
                    public void run() {
                        //remove the snippet content from editor
                        document.replaceString(start,end,"");
                        gutterHandler.setGutterRemoved(projectName,fileName,name,markup,start,start+commentStart.length());
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
