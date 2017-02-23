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
public class InsertSnippet extends AnAction {

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
        BufferedWriter metaWriter = null;
        BufferedReader br = null;
        StringBuilder listBuilder = new StringBuilder("");
        String line;

        //Read the list of snippets
        try {
            br = new BufferedReader(new FileReader("..\\plugins\\codesnipper\\files\\" + projectName));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            line = br.readLine();
            //Add the snippets which are available
            while (line != null) {
                if (line.split("-")[0].equalsIgnoreCase("A"))
            {
                listBuilder.append(line.split("-")[1] + "~");
            }
                line = br.readLine();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //Display the list
        String[] snipList = listBuilder.toString().split("~");
        if (snipList[0].length() > 1) {
            String name = (String) JOptionPane.showInputDialog(null, "Select Snippet", "Insert Snippet", JOptionPane.QUESTION_MESSAGE, null,
                    snipList,
                    snipList[0]);
            //Open the selected snippet file, read it and insert it in the editor
            if (name != null) {
                try {
                    br = new BufferedReader(new FileReader("..\\plugins\\codesnipper\\data\\" + name));
                    line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = br.readLine();
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            final String data = sb.toString();
            String commentStart = "/* CS:start-" + name + " */";
            String commentEnd = "/* CS:end-" + name + " */";
            //find the location of start comment in the editor
            int start = contents.indexOf("/* CS:start-" + name + " */") + commentStart.length();
            int end = start + data.length();

            //Modify the snippet list to indicate snippet is inserted
            sb = new StringBuilder();
            try {
                br = new BufferedReader(new FileReader("..\\plugins\\codesnipper\\files\\" + projectName));
                line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = br.readLine();
                }
                contents = sb.toString();
                contents = contents.replace("A-"+name,"I-"+name);
                metaWriter = new BufferedWriter(new FileWriter("..\\plugins\\codesnipper\\files\\" + projectName));
                metaWriter.append(contents);
                metaWriter.close();

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            finally { };

            final Runnable readRunner = new Runnable() {
                @Override
                public void run() {
                    document.insertString(start, data);
                    document.insertString(end, commentEnd);
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
