package r;

import java.io.*;
import java.awt.Frame;
import java.awt.FileDialog;

import java.util.Enumeration;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.RVector;
import org.rosuda.JRI.RMainLoopCallbacks;

class TextConsole implements RMainLoopCallbacks
{
    public void rWriteConsole(Rengine re, String text, int oType) {
        System.out.print(text);
    }
    
    public void rBusy(Rengine re, int which) {
        System.out.println("rBusy("+which+")");
    }
    
    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
        System.out.print(prompt);
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
            String s=br.readLine();
            return (s==null||s.length()==0)?s:s+"\n";
        } catch (Exception e) {
            System.out.println("jriReadConsole exception: "+e.getMessage());
        }
        return null;
    }
    
    public void rShowMessage(Rengine re, String message) {
        System.out.println("rShowMessage \""+message+"\"");
    }
	
    public String rChooseFile(Rengine re, int newFile) {
	FileDialog fd = new FileDialog(new Frame(), (newFile==0)?"Select a file":"Select a new file", (newFile==0)?FileDialog.LOAD:FileDialog.SAVE);
	fd.show();
	String res=null;
	if (fd.getDirectory()!=null) res=fd.getDirectory();
	if (fd.getFile()!=null) res=(res==null)?fd.getFile():(res+fd.getFile());
	return res;
    }
    
    public void   rFlushConsole (Rengine re) {
    }
	
    public void   rLoadHistory  (Rengine re, String filename) {
    }			
    
    public void   rSaveHistory  (Rengine re, String filename) {
    }			
}

public class rBoxplot {
    public static void main(String[] args) {
	// just making sure we have the right version of everything
	if (!Rengine.versionCheck()) {
	    System.err.println("** Version mismatch - Java files don't match library version.");
	    System.exit(1);
	}
        System.out.println("Creating Rengine (with arguments)");
		// 1) we pass the arguments from the command line
		// 2) we won't use the main loop at first, we'll start it later
		//    (that's the "false" as second argument)
		// 3) the callbacks are implemented by the TextConsole class above
		Rengine re=new Rengine(args, false, new TextConsole());
        System.out.println("Rengine created, waiting for R");
		// the engine creates R is a new thread, so we should wait until it's ready
        if (!re.waitForR()) {
            System.out.println("Cannot load R");
            return;
        }
        
        // Read CSV.
        re.eval("data<-read.csv(file='jhipster.csv', head=TRUE, sep=';')");
        // Create Boxplot + Save jpg
        re.eval("head(data)");
        re.eval("jpeg('boxplotTimeToGenerate.jpg')");
        //System.out.println(re.eval("boxplot(data$TimeToGenerate.secs.)"));
        System.out.println(re.eval("boxplot(data$TimeToGenerate.secs., ylab='Time To Generate(secs)',"
        		+ "main='Boxplot Distribution of Time to Generate Applications JHipster')"));
        
        System.out.println(re.eval("dev.off()"));
        
        re.eval("jpeg('boxplotTimeToCompile.jpg')");
        System.out.println(re.eval("boxplot(data$TimeToCompile.secs.)"));
        //System.out.println(re.eval("boxplot(data$TimeToCompile.secs., ylab='Time To Compile(secs)',"
        //		+ "main='Boxplot Distribution of Time to Compile Applications JHipster')"));
        
        System.out.println(re.eval("dev.off()"));
        //createBoxplot(re);
        
	    re.end();
	    System.out.println("end");
    }
    
    public static void createBoxplot(Rengine re) {
    	//System.out.println(re.eval("df <- data.frame(data)"));
    	//System.out.println(re.eval("dfWithDockerFalse <- df$Docker[df$x==FALSE]"));
        System.out.println(re.eval("jpeg('boxplotTimeToGenerate.jpg')"));
        System.out.println(re.eval("boxplot(data$TimeToGenerate.secs."));
        //System.out.println(re.eval("boxplot(df$TimeToGenerate.secs., ylab='Time To Generate(secs)',"
        //		+ "main='Boxplot Distribution of Time to Generate Applications from JHipster)"));
        System.out.println(re.eval("dev.off()"));
    }
}