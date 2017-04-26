package uipack;

import com.intellij.openapi.editor.markup.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import javax.swing.*;

public class GutterHandler {


    protected void setGutterInserted(String projectName, String fileName, String name,MarkupModel markup,int start, int end)
    {
        RangeHighlighter prevHighlighter = HighlighterManager.getHighLighter(projectName+"--"+fileName+"--"+name);
        if(prevHighlighter!=null)
        markup.removeHighlighter(prevHighlighter);
        final Icon  icon = new ImageIcon("F:\\Projects\\IntelliJ\\CodeSnipper\\resources\\inserted.png");
        RangeHighlighter rangeHighlighter = markup.addRangeHighlighter(start, end, HighlighterLayer.FIRST, null, HighlighterTargetArea.EXACT_RANGE);
        HighlighterManager.addHighLighter(projectName+"--"+fileName+"--"+name,rangeHighlighter);
        rangeHighlighter.setGutterIconRenderer(new GutterIconRenderer() {
            @Override
            public boolean equals(Object o) {
                return true;
            }

            @Override
            public int hashCode() {
                return 0;
            }

            @NotNull
            @Override
            public Icon getIcon() {
                return icon;
            }

            @Override
            @Nullable
            public String getTooltipText() {
                return name+" Snippet here";
            }
        });
    }

    protected void setGutterRemoved(String projectName, String fileName, String name, MarkupModel markup,int start, int end)
    {
        RangeHighlighter prevHighlighter = HighlighterManager.getHighLighter(projectName+"--"+fileName+"--"+name);
        if(prevHighlighter!=null)
        markup.removeHighlighter(prevHighlighter);
        final Icon icon = new ImageIcon("F:\\Projects\\IntelliJ\\CodeSnipper\\resources\\removed.png");
        RangeHighlighter rangeHighlighter = markup.addRangeHighlighter(start, end, HighlighterLayer.FIRST, null, HighlighterTargetArea.EXACT_RANGE);
        HighlighterManager.addHighLighter(projectName+"--"+fileName+"--"+name,rangeHighlighter);
        rangeHighlighter.setGutterIconRenderer(new GutterIconRenderer() {
            @Override
            public boolean equals(Object o) {
                return true;
            }

            @Override
            public int hashCode() {
                return 0;
            }

            @NotNull
            @Override
            public Icon getIcon() {
                return icon;
            }

            @Override
            @Nullable
            public String getTooltipText() {
                return name+" Snippet here";
            }
        });
    }

    protected void removeGutterIcon(String projectName, String fileName, String name, MarkupModel markup)
    {
        RangeHighlighter prevHighlighter = HighlighterManager.getHighLighter(projectName+"--"+fileName+"--"+name);
        if(prevHighlighter!=null)
            markup.removeHighlighter(prevHighlighter);
    }
}
