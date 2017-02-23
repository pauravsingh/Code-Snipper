package uipack;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

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
        String fileName = virtualFile.getNameWithoutExtension();
        String contents = document.getText();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        BufferedWriter metaWriter = null;
        BufferedWriter dataWriter = null;
        StringBuilder listBuilder = new StringBuilder("");
        String line;
        //Read the list of snippets
        try {
            br = new BufferedReader(new FileReader("..\\plugins\\codesnipper\\files\\" + projectName));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        //Add to the list snippets that inserted
        try {
            line = br.readLine();
            while (line != null) {
                if (line.split("-")[0].equalsIgnoreCase("I"))
                {
                    listBuilder.append(line.split("-")[1] + "~");
                }
                line = br.readLine();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        String[] snipList = listBuilder.toString().split("~");
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

                try {
                    dataWriter = new BufferedWriter(new FileWriter("..\\plugins\\codesnipper\\data\\" + name));
                    dataWriter.append(data);
                    dataWriter.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //Modify the snippet list to show snippet is available for insert
                try {
                    br = new BufferedReader(new FileReader("..\\plugins\\codesnipper\\files\\" + projectName));
                    line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = br.readLine();
                    }
                    contents = sb.toString();
                    contents = contents.replace("I-"+name,"A-"+name);
                    metaWriter = new BufferedWriter(new FileWriter("..\\plugins\\codesnipper\\files\\" + projectName));
                    metaWriter.append(contents);
                    metaWriter.close();

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                final Runnable readRunner = new Runnable() {
                    @Override
                    public void run() {
                        //remove the snippet content from editor
                        document.replaceString(start,end,"");

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
