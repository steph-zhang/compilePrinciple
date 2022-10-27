package main;

import java.io.*;

public class utils {
    public static String readTxt(String txtPath) throws IOException {
        File file = new File(txtPath);
        if(file.isFile() && file.exists()){
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String text = null;
            while((text = bufferedReader.readLine()) != null){
                sb.append(text);
            }
            return sb.toString();
        }
        return null;
    }

    public static void writeTxt(String txtPath,String content) throws IOException {
        FileOutputStream fileOutputStream = null;
        File file = new File(txtPath);

        content += "\n";
        fileOutputStream = new FileOutputStream(file, true);
        fileOutputStream.write(content.getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
    }

}
