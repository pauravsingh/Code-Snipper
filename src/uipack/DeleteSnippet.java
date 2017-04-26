package uipack;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.markup.MarkupModel;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

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
        String fileName = virtualFile.getParent().getName() + "." + virtualFile.getNameWithoutExtension();
        MarkupModel markup = editor.getMarkupModel();
        GutterHandler gutterHandler = new GutterHandler();
        FileHandler fileHandler = new FileHandler();
        String contents = document.getText();

        String comment;
        String name = null;
        String startComment;
        String endComment;

        int sc_s = 0;          //start comment start
        int sc_e = 0;          //start comment end
        int ec_s = 0;          //end comment start
        int ec_e = 0;          //end comment end


        SelectionModel selectionModel = editor.getSelectionModel();
        //if text is highlighted
        if (selectionModel.hasSelection()) {
            //get the snippet name
            comment = selectionModel.getSelectedText();
            name = comment.substring(comment.indexOf("/* CS:start-") + new String("/* CS:start-").length(), comment.indexOf(" */"));
            startComment = "/* CS:start-" + name + " */";
            endComment = "/* CS:end-" + name + " */";
            sc_s = contents.indexOf(startComment);
            sc_e = sc_s+startComment.length();
            if (contents.contains(endComment)) {
                ec_s = contents.indexOf(endComment);
                ec_e = ec_s+ endComment.length();
            }
            //Modify the snippet list to delete the snippet
            fileHandler.removeSnippetName(projectName, fileName, name);

            //delete the stored snippet
            fileHandler.fileDelete(projectName, fileName, name);


            final int startComment_start = sc_s;
            final int startComment_end = sc_e + 1;        //+1 removes new line as well
            final int endComment_start = ec_s;
            final int endComment_end = ec_e + 1;          //+1 removes new line as well
            final String sName = name;
            final Runnable readRunner = new Runnable() {
                @Override
                public void run() {
                    //remove highlight
                    selectionModel.removeSelection();
                    //delete the comments
                    if (startComment_start != 0 && startComment_end != 1) {
                        if (endComment_start != 0 && endComment_end != 1) {
                            document.replaceString(endComment_start, endComment_end, "");
                        }
                        document.replaceString(startComment_start, startComment_end, "");
                    }
                    gutterHandler.removeGutterIcon(projectName,fileName,sName,markup);
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
