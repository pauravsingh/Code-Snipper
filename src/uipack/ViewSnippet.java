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
public class ViewSnippet extends AnAction {

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
        JComboBox snippetList = new JComboBox(fileHandler.getNamesList("view", "F", getProjectName(), getFileName(), "Both"));


        ActionListener ScopeActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String[] snipList = null;
                if (fileButton.isSelected()) {
                    snipList = fileHandler.getNamesList("view", "F", getProjectName(), getFileName(), "Both");
                }
                if (projectButton.isSelected()) {
                    snipList = fileHandler.getNamesList("view", "P", getProjectName(), getFileName(), "Both");
                }
                if (libraryButton.isSelected()) {
                    snipList = fileHandler.getNamesList("view", "L", getProjectName(), getFileName(), "Both");
                }
                snippetList.removeAllItems();
                for(int i =0; i < snipList.length; i++) {
                    snippetList.addItem(snipList[i]);
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
        jpanel.setPreferredSize(new Dimension(400,200));

        int result = JOptionPane.showConfirmDialog(null, jpanel, "View Snippet", JOptionPane.OK_CANCEL_OPTION);

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
                    f.setSize(500, 500);
                    pane.setEditable(false);
                    pane.setBackground(Color.white);
                    Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
                    f.setLocation(dimension.width - f.getSize().width - 20, f.getSize().height / 2);


                    // Create and add the style
                    final Style codeStyle = sc.addStyle("default", null);
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

