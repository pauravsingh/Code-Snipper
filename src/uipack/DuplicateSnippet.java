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

/**
 * Created by paura on 2/18/2017.
 */
public class DuplicateSnippet extends AnAction {

    String projectName =null;
    String fileName = null;
    String choice = null;
    private String getProjectName() {
        return projectName;
    }

    private String getFileName() {
        return fileName;
    }

    private void setChoice(String c) {
        choice = c;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        //Get the virtual file
        Project project = e.getProject();
        projectName = project.getName();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        int cursorPosition = editor.getCaretModel().getOffset();
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

        JPanel jpanel = new JPanel();
        jpanel.setLayout(new VerticalFlowLayout());
        jpanel.setFocusable(true);

        JRadioButton fileButton = new JRadioButton("File", true);
        JRadioButton projectButton = new JRadioButton("Project");
        JRadioButton libraryButton = new JRadioButton("Library");
        ButtonGroup scopeOption = new ButtonGroup();
        scopeOption.add(fileButton);
        scopeOption.add(projectButton);
        scopeOption.add(libraryButton);

        //Drop down list, by default has file snippets list
        JComboBox snippetList = new JComboBox(fileHandler.getNamesList("duplicate", "F", getProjectName(), getFileName(), "Both"));


        ActionListener ScopeActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String[] snipList = null;
                if (fileButton.isSelected()) {
                    snipList = fileHandler.getNamesList("duplicate", "F", getProjectName(), getFileName(), "Both");
                }
                if (projectButton.isSelected()) {
                    snipList = fileHandler.getNamesList("duplicate", "P", getProjectName(), getFileName(), "Both");
                }
                if (libraryButton.isSelected()) {
                    snipList = fileHandler.getNamesList("duplicate", "L", getProjectName(), getFileName(), "Both");
                }
                snippetList.removeAllItems();
                for(int i =0; i < snipList.length; i++) {
                    snippetList.addItem(snipList[i]);
                    System.out.println(snipList[i]);
                }
            }
        };

        ActionListener ChoiceActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setChoice((String) snippetList.getSelectedItem());

            }
        };

        //add the components to the panel
        jpanel.add(new JLabel("Select Scope:"));
        jpanel.add(fileButton);
        jpanel.add(projectButton);
        jpanel.add(libraryButton);
        jpanel.add(snippetList);
        fileButton.addActionListener(ScopeActionListener);
        projectButton.addActionListener(ScopeActionListener);
        libraryButton.addActionListener(ScopeActionListener);
        snippetList.addActionListener(ChoiceActionListener);

        int result = JOptionPane.showConfirmDialog(null, jpanel, "Duplicate Snippet", JOptionPane.OK_CANCEL_OPTION);

        //Open the selected snippet file, read it and insert it in the editor
        if (result == JOptionPane.OK_OPTION && choice != null) {
            String nameList = fileHandler.getNamesWithStatus();
            if (nameList.contains("I--" + Scope + "--" + projectName + "--" + fileName + "--" + choice.split("--")[2]) && projectName.equals(choice.split("--")[0]) && fileName.equals(choice.split("--")[1]))
                inserted = true;
            else
                inserted = false;

            projectName = choice.split("--")[0];
            fileName = choice.split("--")[1];
            name = choice.split("--")[2];

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

            JPanel jpanel2 = new JPanel();
            jpanel2.setLayout(new VerticalFlowLayout());

            JTextField snippetName = new JTextField(name + "-Copy", 20);

            jpanel2.add(new JLabel("Enter Name:"));
            jpanel2.add(snippetName);
            jpanel2.add(fileButton);
            jpanel2.add(projectButton);
            jpanel2.add(libraryButton);
            snippetName.requestFocusInWindow();

            int result2 = JOptionPane.showConfirmDialog(null, jpanel2, "Duplicate Snippet", JOptionPane.OK_CANCEL_OPTION);

            if (result2 == JOptionPane.OK_OPTION) {
                name = snippetName.getText();
                if (fileButton.isSelected())
                    Scope = "F";
                if (projectButton.isSelected())
                    Scope = "P";
                if (libraryButton.isSelected())
                    Scope = "L";

                System.out.println("scope: " + Scope);
            }

            if (name != null) {
                String exists = fileHandler.getNamesWithStatus();
                if (!exists.contains("--" + projectName + "--" + fileName + "--" + name)) {
                    String commentStart = "/* CS:start-" + name + " */\n";
                    String commentEnd = "\n/* CS:end-" + name + " */";
                    final int start = cursorPosition;
                    final int end = start + commentStart.length() + data.length();
                    //Reset names to current file
                    projectName = project.getName();
                    fileName = virtualFile.getParent().getName() + "." + virtualFile.getNameWithoutExtension();

                    //Add snippet name to the file
                    fileHandler.addSnippetName(Scope, projectName, fileName, name);

                    //Create a new file of the snippet name and store the snippet in it
                    fileHandler.writeSnippet(projectName, fileName, name, data);

                    final String code = data;
                    final Runnable readRunner = new Runnable() {
                        @Override
                        public void run() {
                            document.insertString(start, commentStart);
                            document.insertString(start + commentStart.length(), code);
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
}
