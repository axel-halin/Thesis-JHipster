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

		createCircleTypeApp(re);

		createCircleBuildResult(re);
		
		createBoxplotTimeBuild(re);
		
		createBoxplotTimeCompile(re);
		
		createBoxplotTimeGeneration(re);

		createBoxplotCoverage(re);

		//data <- data[- grep("ND", data$TimeToCompile.secs.),]
		//data <- data[- grep("KO", data$TimeToCompile.secs.),]

		re.end();
		System.out.println("end");
	}
	
	public static void createCircleTypeApp(Rengine re) {
		// Read CSV.
		re.eval("data<-read.csv(file='jhipster.csv', head=TRUE, sep=';')");
		// Create Circle + Save jpg
		re.eval("jpeg('typeApp.jpg')");
		//extract number of types in table
		System.out.println(re.eval("type <- table(data$applicationType)"));
		//data vecteur
		re.eval("as.numeric(type)");
		// WARNING VERIFY GOOD NUMBER WITH GOOD TYPE -> alphabetic order
		re.eval("vecteur <- c(type[1],type[2],type[3],type[4])");
		//names
		re.eval("A <- gl(5,1,5,labels=c(\"Gateway\",\"Microservice\",\"Monolithic\",\"Uaa\"))");
		re.eval("names(vecteur) <- levels(A)");
		re.eval("pie(vecteur/100,main=\"Types d'application générées\")");
		
		re.eval("dev.off()");
	}
	
	public static void createCircleBuildResult(Rengine re) {
		// Read CSV.
		re.eval("data<-read.csv(file='jhipster.csv', head=TRUE, sep=';')");
		// Create Circle + Save jpg
		re.eval("jpeg('buildResult.jpg')");
		//data with Docker
		re.eval("dataDocker <- data[grep(\"true\", data$Docker),]");
		//data without Docker
		re.eval("dataNotDocker <- data[grep(\"false\", data$Docker),]");
		//extract number of OK and KO with Docker in table
		re.eval("docker <- table(dataDocker$Build)");
		//extract number of OK and KO without Docker in table
		re.eval("notDocker <- table(dataNotDocker$Build)");
		//data vecteur
		re.eval("as.numeric(docker)");
		re.eval("as.numeric(notDocker)");
		// WARNING VERIFY GOOD NUMBER WITH GOOD TYPE -> alphabetic order
		re.eval("vecteur <- c(docker[1],docker[2],notDocker[1],notDocker[2])");
		//names
		re.eval("A <- gl(5,1,5,labels=c(\"DockerKO\",\"DockerOK\",\"NotDockerKO\",\"NotDockerOK\"))");
		re.eval("names(vecteur) <- levels(A)");
		re.eval("pie(vecteur/100,main='Build OK-KO With-Without Docker')");
		
		re.eval("dev.off()");
	}

	public static void createBoxplotTimeCompile(Rengine re) {
		// Read CSV.
		re.eval("data<-read.csv(file='jhipster.csv', head=TRUE, sep=';')");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotTimeToCompile.jpg')");
		//System.out.println(re.eval("data$TimeToGenerate.secs."));
		//System.out.println(re.eval("boxplot(data$TimeToGenerate.secs.)"));
		System.out.println(re.eval("boxplot(data$TimeToCompile.secs.~data$applicationType, ylab='Time To Compile(secs)',"
				+ "main='Boxplot Distribution:Time Compilation of different JHipster apps')"));

		System.out.println(re.eval("dev.off()"));
	}
	
	public static void createBoxplotTimeBuild(Rengine re) {
		// Read CSV.
		re.eval("data<-read.csv(file='jhipster.csv', head=TRUE, sep=';')");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotTimeToBuild.jpg')");
		//System.out.println(re.eval("data$TimeToGenerate.secs."));
		//System.out.println(re.eval("boxplot(data$TimeToGenerate.secs.)"));
		System.out.println(re.eval("boxplot(data$TimeToBuild.secs.~data$applicationType, ylab='Time To Build(secs)',"
				+ "main='Boxplot Distribution:Time to build - different JHipster apps')"));

		System.out.println(re.eval("dev.off()"));
	}
	
	public static void createBoxplotTimeGeneration(Rengine re) {
		// Read CSV.
		re.eval("data<-read.csv(file='jhipster.csv', head=TRUE, sep=';')");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotTimeToGenerate.jpg')");
		//System.out.println(re.eval("data$TimeToGenerate.secs."));
		//System.out.println(re.eval("boxplot(data$TimeToGenerate.secs.)"));
		System.out.println(re.eval("boxplot(data$TimeToGenerate.secs.~data$applicationType, ylab='Time To Generate(secs)',"
				+ "main='Boxplot Distribution:Time Generation of different JHipster apps')"));

		System.out.println(re.eval("dev.off()"));
	}

	public static void createBoxplotCoverage(Rengine re) {
		// Read CSV.
		re.eval("data<-read.csv(file='jhipster.csv', head=TRUE, sep=';')");

		re.eval("data <- data[- grep(\"ND\", data$CoverageInstructions...),]");
		re.eval("data <- data[- grep(\"X\", data$CoverageInstructions...),]");

		//set numerical

		re.eval("data$CoverageInstructions... <- as.numeric(as.character(data$CoverageInstructions...))");
		re.eval("data$CoverageBranches... <- as.numeric(as.character(data$CoverageBranches...))");

		re.eval("jpeg('boxplotCoverage.jpg')");
		//System.out.println(re.eval("boxplot(data$CoverageInstructions...)"));
		re.eval("lmts <- range(data$CoverageInstructions...,data$CoverageBranches...)");

		re.eval("par(mfrow = c(1, 2))");
		re.eval("boxplot(data$CoverageInstructions...,ylim=lmts, ylab='CoverageInstruction')");
		re.eval("boxplot(data$CoverageBranches...,ylim=lmts, ylab='CoverageBranches')");
		re.eval("title(\"Boxplot Distribution of Coverage JHipster Tests\", outer=TRUE)");
		
		/*System.out.println(re.eval("data$CoverageInstructions..."));
		System.out.println(re.eval("boxplot(data$CoverageInstructions...~data$CoverageBranches..., ylab='CoverageInstruction',"
				+ "main='Boxplot Distribution of Coverage Instructions JHipster')"));*/

		System.out.println(re.eval("dev.off()"));

	}

}