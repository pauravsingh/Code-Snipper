package uipack;

import com.intellij.openapi.editor.markup.RangeHighlighter;

import javax.swing.text.Highlighter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

    public class HighlighterManager {
    private static Map<String,RangeHighlighter> highLighterMap = new HashMap<String,RangeHighlighter>();

    static public void addHighLighter(String name, RangeHighlighter highlighter)
    {
        highLighterMap.put(name,highlighter);
    }

    static public RangeHighlighter getHighLighter(String name)
    {
        RangeHighlighter highlighter = highLighterMap.get(name);
        return  highlighter;
    }

    static public void removeHighLighter(String name)
    {
        highLighterMap.remove(name);
    }

    static public void showHighLighter()
    {
        System.out.println(highLighterMap.size());
        Set<String> s = highLighterMap.keySet();
        System.out.println(s.toString());

    }

}
