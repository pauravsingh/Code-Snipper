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
import java.io.*;

/**
 * Created by paura on 2/18/2017.
 */
public class HoldSnippet extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        
        // Create the Frame, StyleContext, the document and the pane
        JFrame f = new JFrame("Temp");
        JTextArea pane = new JTextArea();
        f.setLocationRelativeTo(null);
        f.getContentPane().add(new JBScrollPane(pane));
        f.setSize(400, 300);
        f.setVisible(true);
        f.setAlwaysOnTop(true);
        f.setSize(500,500);
        pane.setEditable(true);
        pane.setBackground(Color.white);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        f.setLocation(dimension.width-f.getSize().width-20, f.getSize().height/2);

        final Runnable readRunner = new Runnable() {
            @Override
            public void run() {
                    f.getCursor();

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


