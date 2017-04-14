package uipack;

import com.intellij.openapi.editor.markup.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Created by paura on 4/1/2017.
 */
public class GutterHandler {

    protected void setGutterInserted(MarkupModel markup,int start, int end)
    {
        final Icon  icon = new ImageIcon("F:\\Projects\\IntelliJ\\CodeSnipper\\resources\\inserted.png");
        final RangeHighlighter highlighter = markup.addRangeHighlighter(start, end, HighlighterLayer.FIRST, null, HighlighterTargetArea.EXACT_RANGE);
        highlighter.setGutterIconRenderer(new GutterIconRenderer() {
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
                return "Snippet here";
            }
        });
    }

    protected void setGutterRemoved(MarkupModel markup,int start, int end)
    {
        final Icon icon = new ImageIcon("F:\\Projects\\IntelliJ\\CodeSnipper\\resources\\removed.png");

        final RangeHighlighter highlighter = markup.addRangeHighlighter(start, end, HighlighterLayer.FIRST, null, HighlighterTargetArea.EXACT_RANGE);

        highlighter.setGutterIconRenderer(new GutterIconRenderer() {
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
                return "Snippet here";
            }
        });
    }
}
