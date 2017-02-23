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
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;

/**
 * Created by paura on 2/18/2017.
 */
public class ViewSnippet extends AnAction {

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
        String data = "";
        boolean inserted = false;
        boolean removed = false;

        //Read the list of snippets
        try {
            br = new BufferedReader(new FileReader("..\\plugins\\codesnipper\\files\\" + projectName));

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        try {
            line = br.readLine();
            while (line != null)
            {
                listBuilder.append(line.split("-")[1] + "~");
                line = br.readLine();
            }

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //Display the list
        String[] snipList = listBuilder.toString().split("~");
        if (snipList[0].length() > 1)
        {
            String name = (String) JOptionPane.showInputDialog(null, "Select Snippet", "Insert Snippet", JOptionPane.QUESTION_MESSAGE, null,
                    snipList,
                    snipList[0]);
            //Open the selected snippet file, read it and insert it in the editor
            if (name != null)
            {

                try {
                    br = new BufferedReader(new FileReader("..\\plugins\\codesnipper\\files\\" + projectName));

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
                //Check if snippet is inserted or removed
                try {
                    line = br.readLine();
                    while (line != null) {
                        if(line.split("-")[1].equals(name))
                        {
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

                //If inserted fetch current code from the editor
                if(inserted)
                {
                    String commentStart = "/* CS:start-" + name + " */\n";
                    String commentEnd = "\n/* CS:end-" + name + " */";
                    int start = contents.indexOf(commentStart)+commentStart.length();
                    int end = contents.indexOf(commentEnd);
                    data = contents.substring(start,end);

                }
                //If removed read the saved file
                if(removed)
                {
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
                    data = sb.toString();

                }
                final String code = data;

                // Create the Frame, StyleContext, the document and the pane
                JFrame f = new JFrame(name);
                StyleContext sc = new StyleContext();
                final DefaultStyledDocument doc = new DefaultStyledDocument(sc);
                JTextPane pane = new JTextPane(doc);
                f.setLocationRelativeTo(null);
                f.getContentPane().add(new JBScrollPane(pane));
                f.setSize(400, 300);
                f.setVisible(true);
                f.setAlwaysOnTop(true);
                f.setSize(500,500);
                pane.setEditable(false);
                pane.setBackground(Color.white);
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                f.setLocation(dimension.width-f.getSize().width-20, f.getSize().height/2);


                // Create and add the style
                final Style codeStyle = sc.addStyle("default",null);
                codeStyle.addAttribute(StyleConstants.Foreground, Color.black);
                codeStyle.addAttribute(StyleConstants.FontSize, new Integer(17));
                codeStyle.addAttribute(StyleConstants.FontFamily, "arial");



                final int length = code.length();
                final Runnable readRunner = new Runnable() {
                @Override
                public void run() {
                    try {
                        doc.insertString(0, code, null);
                        doc.setCharacterAttributes(0, length, codeStyle, false);
                        f.getCursor();
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
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
        }
    }

