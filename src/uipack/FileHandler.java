package uipack;

import com.intellij.openapi.editor.markup.GutterIconRenderer;

import java.io.*;

/**
 * Created by paura on 3/24/2017.
 */
public class FileHandler {
    private BufferedWriter metaWriter = null;
    private BufferedWriter dataWriter = null;
    private BufferedReader br = null;
    private String registerPath = "..\\plugins\\codesnipper\\list\\register";
    private String dataPath = "..\\plugins\\codesnipper\\data\\";

    protected File fileCreate(String projectName, String fileName, String snippetName)
    {
        File file = new File(dataPath+projectName+"\\"+fileName+"--"+snippetName);
        if(!file.exists())
        {
            try {
                file.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return file;
    }

    protected void fileDelete(String projectName, String fileName, String snippetName)
    {
        File file = new File(dataPath+projectName+"\\"+fileName+"--"+snippetName);
        if(file.exists())
        {
            file.delete();
        }
    }

    protected boolean folderCreate(String folderName)
    {
        boolean flag =false;
        File file = new File(dataPath+folderName);
        if(!file.exists())
        {
            file.mkdir();
        }
        if(file.exists() && file.isDirectory())
        {
            flag = true;
        }
        return flag;
    }

    protected boolean folderDelete(String folderName)
    {
        boolean flag =false;
        File file = new File(dataPath+folderName);
        if(file.exists())
        {
            file.delete();
        }
        if(!file.exists())
        {
            flag = true;
        }
        return flag;
    }

    protected void addSnippetName(String Scope, String projectName, String fileName, String snippetName)
    {
        try {
            metaWriter = new BufferedWriter(new FileWriter(registerPath,true));
            metaWriter.append("I--"+Scope+"--"+projectName+"--"+fileName+"--"+snippetName+"\n");
            metaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void removeSnippetName( String projectName, String fileName, String snippetName)
    {
        String contents = getNamesWithStatus();
        try {
            int start = contents.indexOf("--"+projectName+"--"+fileName+"--"+snippetName+"\n")-4;
            String path = "--"+projectName+"--"+fileName+"--"+snippetName+"\n";
            int len = path.length()+4;
            String entry = contents.substring(start, start+len );      //4 characters: example: I--F
            contents = contents.replace(entry, "");
            metaWriter = new BufferedWriter(new FileWriter(registerPath));
            metaWriter.append(contents);
            metaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    protected void changeStatus(String projectName, String fileName, String snippetName, String Status) {
        String contents = getNamesWithStatus();
        int start = contents.indexOf("--"+projectName+"--"+fileName+"--"+snippetName+"\n");
        String path = "--"+projectName+"--"+fileName+"--"+snippetName+"\n";
        String entry = contents.substring(start-3, start+path.length());;
        if (Status.equals("A"))
        {
            contents = contents.replace("I"+entry, "A"+entry);
        }
        if (Status.equals("I"))
        {
            contents = contents.replace("A"+entry, "I"+entry);
        }
        try {
            metaWriter = new BufferedWriter(new FileWriter(registerPath));
            metaWriter.append(contents);
            metaWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected String getNamesWithStatus()
    {
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            br = new BufferedReader(new FileReader(registerPath));
            line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String contents = sb.toString();
        return contents;

    }



    protected String[] getNamesList(String action, String Scope, String projectName, String fileName, String Status)
    {
        StringBuilder sb = new StringBuilder();
        String line;
        String[] parts;
        try {
            br = new BufferedReader(new FileReader(registerPath));
            line = br.readLine();
            if(action.equals("insert")||action.equals("remove"))
            {
                while (line != null) {
                    parts = line.split("--");
                    if(parts[2].equals(projectName)&&parts[3].equals(fileName))
                    {
                        if (Status.equals("Both"))
                        {
                            sb.append(parts[4] + "~");
                        }
                        else
                        {
                            if (parts[0].equalsIgnoreCase(Status)) {
                                sb.append(parts[4] + "~");
                            }
                        }
                    }
                    line = br.readLine();
                }
            }

            if(action.equals("view")||action.equals("duplicate"))
            {
                if(Scope.equals("L"))
                {
                    while (line != null) {
                        parts = line.split("--");
                        if(parts[1].equals("L"))
                        {
                            sb.append(parts[2]+"--"+parts[3]+"--"+parts[4] + "~");
                        }
                        line = br.readLine();
                    }
                }
                else if(Scope.equals("P"))
                {
                    while (line != null) {
                        parts = line.split("--");
                        if(parts[1].equals("P") && parts[2].equals(projectName))
                        {
                            sb.append(parts[2]+"--"+parts[3]+"--"+parts[4] + "~");
                        }
                        line = br.readLine();
                    }

                }
                else if(Scope.equals("F"))
                {
                    while (line != null) {
                        parts = line.split("--");
                        if(parts[1].equals("F") && parts[2].equals(projectName) && parts[3].equals(fileName))
                        {
                            sb.append(parts[2]+"--"+parts[3]+"--"+parts[4] + "~");
                        }
                        line = br.readLine();
                    }

                }
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
            String[] contents = sb.toString().split("~");
        return contents;
    }

    protected void writeSnippet(String projectName, String fileName, String snippetName, String data)
    {
        folderCreate(projectName);
        fileCreate(projectName,fileName,snippetName);
        try {
            dataWriter = new BufferedWriter(new FileWriter(dataPath+projectName+"\\"+fileName+"--"+snippetName));
            dataWriter.append(data);
            dataWriter.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    protected String readSnippet(String projectName, String fileName, String snippetName)
    {
        String data;
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            br = new BufferedReader(new FileReader(dataPath+projectName+"\\"+fileName+"--"+snippetName));
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
        return data;

    }
}
