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
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.*;
import java.io.*;

/**
 * Created by paura on 2/18/2017.
 */
public class DeleteSnippet extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        //Get the virtual file
        Project project = e.getProject();
        String projectName = project.getName();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Document document = editor.getDocument();
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        String fileName = virtualFile.getNameWithoutExtension();
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        BufferedWriter metaWriter = null;
        StringBuilder listBuilder = new StringBuilder("");
        String contents;
        String line;
        String comment;
        String name;
        Boolean inserted = false;
        Boolean removed = false;
        int sc_s=0;          //start comment start
        int sc_e=0;          //start comment end
        int ec_s=0;          //end comment start
        int ec_e=0;          //end comment end


        SelectionModel selectionModel = editor.getSelectionModel();
        //if text is highlighted
        if(selectionModel.hasSelection()) {
            comment = selectionModel.getSelectedText();
            name = comment.substring(12, comment.length() - 3);
            sc_s = selectionModel.getSelectionStart();
            sc_e = selectionModel.getSelectionEnd();


            try {
                br = new BufferedReader(new FileReader("..\\plugins\\codesnipper\\files\\" + projectName));
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            //find the snippet in the list of snippets
            try {
                line = br.readLine();
                while (line != null) {
                    if(line.split("-")[1].equals(name)){
                        if(line.split("-")[0].equalsIgnoreCase("I")){
                            inserted = true;
                        }
                        else{
                            removed = true;
                        }
                    }
                    line = br.readLine();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            if(inserted || removed)
            {
                //Modify the snippet list to delete the snippet
                try {
                    br = new BufferedReader(new FileReader("..\\plugins\\codesnipper\\files\\" + projectName));
                    line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append("\n");
                        line = br.readLine();
                    }
                    contents = sb.toString();
                    if(inserted) {
                        contents = contents.replace("I-" + name+"\n", "");
                    }
                    else {
                        contents = contents.replace("A-" + name+"\n", "");
                    }
                    metaWriter = new BufferedWriter(new FileWriter("..\\plugins\\codesnipper\\files\\" + projectName));
                    metaWriter.append(contents);
                    metaWriter.close();

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //if snippet is inserted, find the position of end comment
                if(inserted) {
                    String documentConent = document.getText();
                    String end = "/* CS:end-"+name+" */";
                    ec_s = documentConent.indexOf(end);
                    ec_e = ec_s + end.length();
                }
                //delete the stored snippet
                File file = new File("..\\plugins\\codesnipper\\data\\" + name);
                if(file.exists()) {
                  file.delete();
                }
            }
            }
            //Bug: Leaves an / from start comment when deleting snippets that are removed
            if(removed){
            sc_s++;
            }
            final int startComment_start = sc_s;
            final int startComment_end = sc_e+1;        //+1 removes new line as well
            final int endComment_start = ec_s;
            final int endComment_end = ec_e+1;          //+1 removes new line as well

                final Runnable readRunner = new Runnable() {
                    @Override
                    public void run() {
                        //remove highlight
                        selectionModel.removeSelection();
                        //delete the comments
                        if(startComment_start != 0 && startComment_end != 0) {
                            document.replaceString(endComment_start, endComment_end, "");
                            document.replaceString(startComment_start, startComment_end, "");
                        }

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
