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
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CompareSnippet extends AnAction {

    String projectName =null;
    String fileName = null;
    String choice1 = null;
    String choice2 = null;

    private String getProjectName() {
        return projectName;
    }

    private String getFileName() {
        return fileName;
    }

    private void setChoice1(String c) {
        choice1 = c;
    }

    private void setChoice2(String c) {
        choice2 = c;
    }




    @Override
    public void actionPerformed(AnActionEvent e) {
        //Get the virtual file
        Project project = e.getProject();
        projectName = project.getName();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        Document document = editor.getDocument();
        VirtualFile virtualFile = e.getData(PlatformDataKeys.VIRTUAL_FILE);
        fileName = virtualFile.getParent().getName() + "." + virtualFile.getNameWithoutExtension();
        MarkupModel markup = editor.getMarkupModel();
        GutterHandler gutterHandler = new GutterHandler();
        FileHandler fileHandler = new FileHandler();
        String contents = document.getText();
        String Scope = null;
        String name = null;
        String data = "";
        boolean inserted = false;


        JPanel jpanel1 = new JPanel();
        jpanel1.setLayout(new VerticalFlowLayout());
        jpanel1.setFocusable(true);

        JRadioButton fileButton1 = new JRadioButton("File", true);
        JRadioButton projectButton1 = new JRadioButton("Project");
        JRadioButton libraryButton1 = new JRadioButton("Library");
        ButtonGroup scopeOption1 = new ButtonGroup();
        scopeOption1.add(fileButton1);
        scopeOption1.add(projectButton1);
        scopeOption1.add(libraryButton1);

        //Drop down list, by default has file snippets list
        String[] snipList = fileHandler.getNamesList("view", "F", getProjectName(), getFileName(), "Both");
        JComboBox snippetList1 = new JComboBox(snipList);
        choice1 = snipList[0];

        ActionListener ScopeActionListener1 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String[] snipList = null;
                if (fileButton1.isSelected()) {
                    snipList = fileHandler.getNamesList("view", "F", getProjectName(), getFileName(), "Both");
                }
                if (projectButton1.isSelected()) {
                    snipList = fileHandler.getNamesList("view", "P", getProjectName(), getFileName(), "Both");
                }
                if (libraryButton1.isSelected()) {
                    snipList = fileHandler.getNamesList("view", "L", getProjectName(), getFileName(), "Both");
                }
                snippetList1.removeAllItems();
                for(int i =0; i < snipList.length; i++) {
                    snippetList1.addItem(snipList[i]);
                }
            }
        };

        ActionListener ChoiceActionListener1 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setChoice1((String) snippetList1.getSelectedItem());

            }
        };

        //add the components to the panel
        jpanel1.add(new JLabel("Snippet 1"));
        jpanel1.add(fileButton1);
        jpanel1.add(projectButton1);
        jpanel1.add(libraryButton1);
        jpanel1.add(snippetList1);
        fileButton1.addActionListener(ScopeActionListener1);
        projectButton1.addActionListener(ScopeActionListener1);
        libraryButton1.addActionListener(ScopeActionListener1);
        snippetList1.addActionListener(ChoiceActionListener1);

        //Second panel
        JPanel jpanel2 = new JPanel();
        jpanel2.setLayout(new VerticalFlowLayout());
        jpanel2.setFocusable(true);

        JRadioButton fileButton2 = new JRadioButton("File", true);
        JRadioButton projectButton2 = new JRadioButton("Project");
        JRadioButton libraryButton2 = new JRadioButton("Library");
        ButtonGroup scopeOption2 = new ButtonGroup();
        scopeOption2.add(fileButton2);
        scopeOption2.add(projectButton2);
        scopeOption2.add(libraryButton2);

        //Drop down list, by default has file snippets list
        snipList = fileHandler.getNamesList("view", "F", getProjectName(), getFileName(), "Both");
        JComboBox snippetList2 = new JComboBox(snipList);
        choice2 = snipList[0];

        ActionListener ScopeActionListener2 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String[] snipList = null;
                if (fileButton2.isSelected()) {
                    snipList = fileHandler.getNamesList("view", "F", getProjectName(), getFileName(), "Both");
                }
                if (projectButton2.isSelected()) {
                    snipList = fileHandler.getNamesList("view", "P", getProjectName(), getFileName(), "Both");
                }
                if (libraryButton2.isSelected()) {
                    snipList = fileHandler.getNamesList("view", "L", getProjectName(), getFileName(), "Both");
                }
                snippetList2.removeAllItems();
                for(int i =0; i < snipList.length; i++) {
                    snippetList2.addItem(snipList[i]);
                }
            }
        };

        ActionListener ChoiceActionListener2 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setChoice2((String) snippetList2.getSelectedItem());

            }
        };

        //add the components to the panel
        jpanel2.add(new JLabel("Snippet 2"));
        jpanel2.add(fileButton2);
        jpanel2.add(projectButton2);
        jpanel2.add(libraryButton2);
        jpanel2.add(snippetList2);
        fileButton2.addActionListener(ScopeActionListener2);
        projectButton2.addActionListener(ScopeActionListener2);
        libraryButton2.addActionListener(ScopeActionListener2);
        snippetList2.addActionListener(ChoiceActionListener2);


        JPanel jpanel = new JPanel();
        jpanel.setLayout(new GridLayout(1,2));
        jpanel.add(jpanel1);
        jpanel.add(jpanel2);

        int result = JOptionPane.showConfirmDialog(null, jpanel, "Compare Snippet", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION && choice1 != null && choice2 != null) {
            String nameList = fileHandler.getNamesWithStatus();

            //First snippet code retrieval
            if (nameList.contains("I--" + Scope + "--" + projectName + "--" + fileName + "--" + choice1.split("--")[2]) && projectName.equals(choice1.split("--")[0]) && fileName.equals(choice1.split("--")[1]))
                inserted = true;
            else
                inserted = false;

            projectName = choice1.split("--")[0];
            fileName = choice1.split("--")[1];
            name = choice1.split("--")[2];

            //If inserted fetch current code from the editor
            if (inserted) {
                String commentStart = "/* CS:start-" + name + " */\n";
                String commentEnd = "\n/* CS:end-" + name + " */";
                int start = contents.indexOf(commentStart) + commentStart.length();
                int end = contents.indexOf(commentEnd);
                data = contents.substring(start, end);
            } else {
                data = fileHandler.readSnippet(projectName, fileName, name);
            }

            final String snippet1_code = data;

            //Second Snippet Code retrieval
            if (nameList.contains("I--" + Scope + "--" + projectName + "--" + fileName + "--" + choice2.split("--")[2]) && projectName.equals(choice2.split("--")[0]) && fileName.equals(choice2.split("--")[1]))
                inserted = true;
            else
                inserted = false;

            projectName = choice2.split("--")[0];
            fileName = choice2.split("--")[1];
            name = choice2.split("--")[2];

            //If inserted fetch current code from the editor
            if (inserted) {
                String commentStart = "/* CS:start-" + name + " */\n";
                String commentEnd = "\n/* CS:end-" + name + " */";
                int start = contents.indexOf(commentStart) + commentStart.length();
                int end = contents.indexOf(commentEnd);
                data = contents.substring(start, end);
            } else {
                data = fileHandler.readSnippet(projectName, fileName, name);
            }

            final String snippet2_code = data;

            // Create the Frame, StyleContext, the document and the pane
            JFrame f = new JFrame("Compare Snippets");

            //Panel 1
            JTextArea pane1 = new JTextArea();
            pane1.setForeground(Color.black);
            pane1.setFont(new Font("Arial", Font.PLAIN, 14));
            pane1.setEditable(false);
            pane1.setBackground(Color.white);
            pane1.setBorder(BorderFactory.createLineBorder(Color.black,1));
            pane1.setText(snippet1_code);

            //Panel 2
            JTextArea pane2 = new JTextArea();
            pane2.setForeground(Color.black);
            pane2.setFont(new Font("Arial", Font.PLAIN, 14));
            pane2.setEditable(false);
            pane2.setBackground(Color.white);
            pane2.setBorder(BorderFactory.createLineBorder(Color.black,1));
            pane2.setText(snippet2_code);

            JPanel jPanel3 = new JPanel();
            jPanel3.setLayout(new GridLayout(1,2));
            jPanel3.add(pane1);
            jPanel3.add(pane2);
            jPanel3.setBackground(Color.WHITE);

            f.setLocationRelativeTo(null);
            f.getContentPane().add(new JBScrollPane(jPanel3));
            f.setSize(1000, 300);
            f.setVisible(true);
            f.setAlwaysOnTop(true);
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            f.setLocation(dimension.width - f.getSize().width - 20, f.getSize().height / 2);

            String[] temp1 = snippet1_code.split("\n");
            String[] temp2 = snippet2_code.split("\n");
            Highlighter highlighter1 = pane1.getHighlighter();
            Highlighter highlighter2 = pane2.getHighlighter();
            Highlighter.HighlightPainter redPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.red);
            Highlighter.HighlightPainter greenPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.GREEN);

            //Snippet 1 checks snippet 2
            for(int i = 0; i < temp1.length; i++)
            {
                boolean match = false;
                for(int j=0; j < temp2.length; j++)
                {

                    if((temp1[i].toString().equals(temp2[j].toString())))
                    {
                        match = true;
                          break;
                    }
                }
                if(match)
                {
                    try {
                        highlighter1.addHighlight(snippet1_code.indexOf(temp1[i]),snippet1_code.indexOf(temp1[i])+temp1[i].length(),greenPainter);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
                else
                {
                    try {
                        //highlighter1.changeHighlight(snippet1_code,snippet1_code.indexOf(temp1[i]),snippet1_code.indexOf(temp1[i])+temp1[i].length());
                        highlighter1.addHighlight(snippet1_code.indexOf(temp1[i]),snippet1_code.indexOf(temp1[i])+temp1[i].length(),redPainter);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            //Snippet 2 checks snippet 1
            for(int i = 0; i < temp2.length; i++)
            {
                boolean match = false;
                for(int j=0; j < temp1.length; j++)
                {

                    if((temp2[i].toString().equals(temp1[j].toString())))
                    {
                        match = true;
                        break;
                    }
                }
                if(match)
                {
                    try {
                        highlighter2.addHighlight(snippet2_code.indexOf(temp2[i]),snippet2_code.indexOf(temp2[i])+temp2[i].length(),greenPainter);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
                else
                {
                    try {
                        highlighter2.addHighlight(snippet2_code.indexOf(temp2[i]),snippet2_code.indexOf(temp2[i])+temp2[i].length(),redPainter);
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
            }


            final Runnable readRunner = new Runnable() {
                @Override
                public void run() {

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

