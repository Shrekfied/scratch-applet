import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

class FilePrims extends Primitives {
  String readtext;
  
  int textoffset;
  
  static String[] primlist = new String[] { 
      "filetostring", "1", "resourcetostring", "1", "load", "1", "stringtofile", "2", "file?", "1", 
      "setread", "1", "readline", "0", "eot?", "0", "lineback", "0", "filenamefrompath", "1", 
      "dirnamefrompath", "1", "dir", "1" };
  
  public String[] primlist() {
    return primlist;
  }
  
  public Object dispatch(int paramInt, Object[] paramArrayOfObject, LContext paramLContext) {
    switch (paramInt) {
      case 0:
        return prim_filetostring(paramArrayOfObject[0], paramLContext);
      case 1:
        return prim_resourcetostring(paramArrayOfObject[0], paramLContext);
      case 2:
        return prim_load(paramArrayOfObject[0], paramLContext);
      case 3:
        return prim_stringtofile(paramArrayOfObject[0], paramArrayOfObject[1], paramLContext);
      case 4:
        return prim_filep(paramArrayOfObject[0], paramLContext);
      case 5:
        return prim_setread(paramArrayOfObject[0], paramLContext);
      case 6:
        return prim_readline(paramLContext);
      case 7:
        return prim_eot(paramLContext);
      case 8:
        return prim_lineback(paramLContext);
      case 9:
        return prim_filenamefrompath(paramArrayOfObject[0], paramLContext);
      case 10:
        return prim_dirnamefrompath(paramArrayOfObject[0], paramLContext);
      case 11:
        return prim_dir(paramArrayOfObject[0], paramLContext);
    } 
    return null;
  }
  
  Object prim_filetostring(Object paramObject, LContext paramLContext) {
    String str = Logo.prs(paramObject);
    return fileToString(str, paramLContext);
  }
  
  Object prim_resourcetostring(Object paramObject, LContext paramLContext) {
    String str = Logo.prs(paramObject);
    return resourceToString(str, paramLContext);
  }
  
  Object prim_load(Object paramObject, LContext paramLContext) {
    String str1 = Logo.prs(paramObject);
    String str2 = System.getProperty("file.separator");
    String str3 = resourceToString(str1 + ".logo", paramLContext);
    Logo.readAllFunctions(str3, paramLContext);
    return null;
  }
  
  String resourceToString(String paramString, LContext paramLContext) {
    InputStream inputStream = FilePrims.class.getResourceAsStream(paramString);
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(new BufferedWriter(stringWriter), true);
    try {
      String str;
      for (; (str = bufferedReader.readLine()) != null; printWriter.println(str));
      return stringWriter.toString();
    } catch (IOException iOException) {
      Logo.error("Can't open file " + paramString, paramLContext);
      return null;
    } 
  }
  
  String fileToString(String paramString, LContext paramLContext) {
    byte[] arrayOfByte = null;
    Object object = null;
    try {
      File file = new File(paramString);
      int i = (int)file.length();
      FileInputStream fileInputStream = new FileInputStream(file);
      DataInputStream dataInputStream = new DataInputStream(fileInputStream);
      arrayOfByte = new byte[i];
      dataInputStream.readFully(arrayOfByte);
      fileInputStream.close();
    } catch (IOException iOException) {
      Logo.error("Can't open file " + paramString, paramLContext);
    } 
    return new String(arrayOfByte);
  }
  
  Object prim_stringtofile(Object paramObject1, Object paramObject2, LContext paramLContext) {
    String str1 = Logo.prs(paramObject1);
    String str2 = Logo.prs(paramObject2);
    try {
      FileWriter fileWriter = new FileWriter(str1);
      fileWriter.write(str2, 0, str2.length());
      fileWriter.close();
    } catch (IOException iOException) {
      Logo.error("Can't write file " + str1, paramLContext);
    } 
    return null;
  }
  
  Object prim_filep(Object paramObject, LContext paramLContext) {
    String str = Logo.prs(paramObject);
    return new Boolean((new File(str)).exists());
  }
  
  Object prim_setread(Object paramObject, LContext paramLContext) {
    this.readtext = Logo.prs(paramObject);
    this.textoffset = 0;
    return null;
  }
  
  Object prim_readline(LContext paramLContext) {
    String str = "";
    int i = this.readtext.indexOf("\n", this.textoffset);
    if (i == -1) {
      if (this.textoffset < this.readtext.length()) {
        str = this.readtext.substring(this.textoffset, this.readtext.length());
        this.textoffset = this.readtext.length();
      } 
    } else {
      str = this.readtext.substring(this.textoffset, i);
      this.textoffset = i + 1;
    } 
    if (str.length() == 0)
      return str; 
    if (str.charAt(str.length() - 1) == '\r')
      str = str.substring(0, str.length() - 1); 
    return str;
  }
  
  Object prim_eot(LContext paramLContext) {
    return new Boolean((this.textoffset >= this.readtext.length()));
  }
  
  Object prim_lineback(LContext paramLContext) {
    int i = this.readtext.lastIndexOf("\n", this.textoffset - 2);
    if (i < 0) {
      this.textoffset = 0;
    } else {
      this.textoffset = i + 1;
    } 
    return null;
  }
  
  Object prim_filenamefrompath(Object paramObject, LContext paramLContext) {
    return (new File(Logo.prs(paramObject))).getName();
  }
  
  Object prim_dirnamefrompath(Object paramObject, LContext paramLContext) {
    File file = new File(Logo.prs(paramObject));
    if (file.isDirectory())
      return file.getPath(); 
    return file.getParent();
  }
  
  Object prim_dir(Object paramObject, LContext paramLContext) {
    String[] arrayOfString = (new File(Logo.prs(paramObject))).list();
    if (arrayOfString == null)
      return new Object[0]; 
    return arrayOfString;
  }
}
