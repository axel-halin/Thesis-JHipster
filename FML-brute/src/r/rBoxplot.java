package r;

import java.io.*;
import java.awt.Frame;
import java.awt.FileDialog;

import org.rosuda.JRI.Rengine;
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
		
		createBoxplotTimeBuildWithoutDockerApp(re);
		
		createBoxplotTimeBuildWithDockerApp(re);
		
		createBoxplotTimeBuildWithoutDockerBuildTool(re);
		
		createBoxplotTimeBuildWithDockerBuildTool(re);
		
		createBoxplotTimeCompile(re);
		
		createBoxplotTimeGeneration(re);

		createBoxplotCoverage(re);
		
		createBoxplotImageDockerApplications(re);
		
		createBoxplotImageDockerDB(re);

		createBalloonPlot(re);
		
		createBalloonPlotBugsFeatures(re);
		
		createPieChartBuildResultByBuildTool(re);
		
		createBoxplotCucumeberDatabase(re);
		
		re.end();
		System.out.println("end");
	}
	
	/*
	 * Read csv and remove empty rows.
	 * 
	 */
	public static void readCSV(Rengine re, String csvfile, String data){
	// Read CSV. and remove empty rows
	re.eval(""+data+"<-read.csv(file='"+ csvfile +"', na.strings = c(\"\", \"NA\"), head=TRUE, sep=',')");
	}
	
	public static void createCircleTypeApp(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
		// Create Circle + Save jpg
		re.eval("jpeg('typeApp.jpg')");
		//extract number of types in table
		re.eval("type <- table(data$applicationType)");
		//data vecteur
		re.eval("as.numeric(type)");
		// WARNING VERIFY GOOD NUMBER WITH GOOD TYPE -> alphabetic order
		re.eval("vecteur <- c(type[1],type[2],type[3],type[4])");
		//names
		re.eval("A <- gl(5,1,5,labels=c(\"Gateway\",\"Microservice\",\"Monolithic\",\"Uaa\"))");
		re.eval("names(vecteur) <- levels(A)");
		re.eval("pie(vecteur/100"
				//+ ",main=\"Types d'application générées\""
				+ ")");
		
		re.eval("dev.off()");
	}
	
	public static void createCircleBuildResult(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
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
		re.eval("pie(vecteur/100"
				//+ ",main='Build OK-KO With-Without Docker'"
				+ ")");
		
		re.eval("dev.off()");
	}

	public static void createBoxplotTimeCompile(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotTimeToCompile.jpg')");
		// drop doublon Docker
		re.eval("data <- data[- grep(\"true\", data$Docker),]");
		// drop KO timeToCompile
		re.eval("data <- data[- grep(\"KO\", data$TimeToCompile),]");
		//cast numerical
		re.eval("data$TimeToCompile <- as.numeric(as.character(data$TimeToCompile))");
		//System.out.println(re.eval("boxplot(data$TimeToGenerate.secs.)"));
		re.eval("boxplot(data$TimeToCompile~data$applicationType, ylab='Time To Compile(secs)'"
				//+ ","
				//+ "main='Boxplot Distribution:Time Compilation of different JHipster apps'"
				+ ")");

		re.eval("dev.off()");
	}
	
	public static void createBoxplotTimeBuildWithoutDockerApp(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotTimeToBuildWithoutDocker.jpg')");
		// drop Docker
		re.eval("data <- data[- grep(\"true\", data$Docker),]");
		// drop ND timeToBuild
		//re.eval("data <- data[- grep(\"ND\", data$TimeToBuild),]");
		// only OK BUILD !!
		re.eval("data <- data[- grep(\"KO\", data$Build),]");
		//cast numerical
		re.eval("data$TimeToBuild <- as.numeric(as.character(data$TimeToBuild))");

		re.eval("boxplot(data$TimeToBuild~data$applicationType, ylab='Time To Build(secs)'"
				//+ ",main='Boxplot Distribution:Time to build without Docker'
				+")");

		re.eval("dev.off()");
	}
	
	public static void createBoxplotTimeBuildWithDockerApp(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotTimeToBuildWithDocker.jpg')");
		// drop NotDocker
		re.eval("data <- data[- grep(\"false\", data$Docker),]");
		// drop ND Time To build
		//re.eval("data <- data[- grep(\"ND\", data$TimeToBuild),]");
		// drop KO Time To build Docker Package
		//re.eval("data <- data[- grep(\"ND\", data$TimeToBuildDockerPackage),]");
		// only OK BUILD !!
		re.eval("data <- data[- grep(\"KO\", data$Build),]");
		//cast numerical TimeToBuild and TimeToBuildDockerPackage
		re.eval("data$TimeToBuild <- as.numeric(as.character(data$TimeToBuild))");
		re.eval("data$TimeToBuildDockerPackage <- as.numeric(as.character(data$TimeToBuildDockerPackage))");
		//Add TimeToBuildDockerPackage to TimeToBuild
		re.eval("data$TimeToBuildTotal <- data$TimeToBuildDockerPackage + data$TimeToBuild");
		re.eval("boxplot(data$TimeToBuildTotal~data$applicationType, ylab='Time To Build(secs)'"
				//+ ",main='Boxplot Distribution:Time to build with Docker'"
				+ ")");

		re.eval("dev.off()");
	}
	
	public static void createBoxplotTimeBuildWithoutDockerBuildTool(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotTimeToBuildWithoutDockerBuildTool.jpg')");
		// drop Docker
		re.eval("data <- data[- grep(\"true\", data$Docker),]");
		// drop ND timeToBuil
		//re.eval("data <- data[- grep(\"ND\", data$TimeToBuild),]");
		// only OK BUILD !!
		re.eval("data <- data[- grep(\"KO\", data$Build),]");
		//cast numerical
		re.eval("data$TimeToBuild <- as.numeric(as.character(data$TimeToBuild))");

		re.eval("boxplot(data$TimeToBuild~data$buildTool, ylab='Time To Build(secs)'"
				//+ ",main='Boxplot Distribution:Time to build without Docker/buildTool'"
				+ ")");

		re.eval("dev.off()");
	}
	
	public static void createBoxplotTimeBuildWithDockerBuildTool(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotTimeToBuildWithDockerBuildTool.jpg')");
		// drop NotDocker
		re.eval("data <- data[- grep(\"false\", data$Docker),]");
		// drop ND timeToBuild
		//re.eval("data <- data[- grep(\"ND\", data$TimeToBuild),]");
		// only OK BUILD !!
		re.eval("data <- data[- grep(\"KO\", data$Build),]");
		//cast numerical TimeToBuild and TimeToBuildDockerPackage
		re.eval("data$TimeToBuild <- as.numeric(as.character(data$TimeToBuild))");
		re.eval("data$TimeToBuildDockerPackage <- as.numeric(as.character(data$TimeToBuildDockerPackage))");
		//Add TimeToBuildDockerPackage to TimeToBuild
		re.eval("data$TimeToBuildTotal <- data$TimeToBuildDockerPackage + data$TimeToBuild");
		re.eval("boxplot(data$TimeToBuildTotal~data$buildTool, ylab='Time To Build(secs)'"
				//+ ",main='Boxplot Distribution:Time to build with Docker/buildTool'"
				+ ")");

		re.eval("dev.off()");
	}
	
	public static void createBoxplotTimeGeneration(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotTimeToGenerate.jpg')");
		// drop doublon Docker
		re.eval("data <- data[- grep(\"true\", data$Docker),]");

		re.eval("boxplot(data$TimeToGenerate~data$applicationType, ylab='Time To Generate(secs)', xlab='Applications Type')");
				//+ "main='Boxplot Distribution:Time Generation of different JHipster apps')");

		re.eval("dev.off()");
	}

	public static void createBoxplotCoverage(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","dataJava");
		readCSV(re, "jhipster.csv","dataJS");

		re.eval("dataJava <- dataJava[- grep(\"ND\", dataJava$CoverageInstructions),]");
		//re.eval("dataJava <- dataJava[- grep(\"X\", dataJava$CoverageInstructions),]");
		re.eval("dataJS <- dataJS[- grep(\"ND\", dataJS$JSStatementsCoverage),]");
		//re.eval("dataJS <- dataJS[- grep(\"X\", dataJS$JSStatementsCoverage),]");

		//remove % char
		re.eval("dataJS$JSStatementsCoverage <- as.data.frame(sapply(dataJS$JSStatementsCoverage,gsub,pattern=\"%\",replacement=\"\"))");
		re.eval("dataJS$JSStatementsCoverage <- unlist(dataJS$JSStatementsCoverage)");
		re.eval("dataJS$JSBranchesCoverage <- as.data.frame(sapply(dataJS$JSBranchesCoverage,gsub,pattern=\"%\",replacement=\"\"))");
		re.eval("dataJS$JSBranchesCoverage <- unlist(dataJS$JSBranchesCoverage)");
		
		//set numerical

		re.eval("dataJava$CoverageInstructions <- as.numeric(as.character(dataJava$CoverageInstructions))");
		re.eval("dataJava$CoverageBranches <- as.numeric(as.character(dataJava$CoverageBranches))");
		re.eval("dataJS$JSStatementsCoverage <- as.numeric(as.character(dataJS$JSStatementsCoverage))");
		re.eval("dataJS$JSBranchesCoverage <- as.numeric(as.character(dataJS$JSBranchesCoverage))");

		re.eval("jpeg('boxplotJAVACoverage.jpg')");
		//System.out.println(re.eval("boxplot(data$CoverageInstructions...)"));
		re.eval("lmts <- range(dataJava$CoverageInstructions,dataJava$CoverageBranches,dataJS$JSStatementsCoverage,dataJS$JSBranchesCoverage)");

		re.eval("par(mfrow = c(2, 2))");
		re.eval("boxplot(dataJava$CoverageInstructions,ylim=lmts, xlab='CoverageInstruction(%)')");
		re.eval("boxplot(dataJava$CoverageBranches,ylim=lmts, xlab='CoverageBranches(%)')");
		re.eval("boxplot(dataJS$JSStatementsCoverage,ylim=lmts, xlab='CoverageJSStatements(%)')");
		re.eval("boxplot(dataJS$JSBranchesCoverage,ylim=lmts, xlab='CoverageJSBranches(%)')");
		//re.eval("title(\"Boxplot Distribution of JAVA Coverage JHipster Tests\", outer=TRUE)");
		//re.eval("(annotate(\"Boxplot Distribution of JAVA Coverage JHipster Tests\", side = 3, line = -21, outer = TRUE)");
		
		re.eval("dev.off()");

	}
	
	public static void createBoxplotImageDockerApplications(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotImageDockerApps.jpg')");
		// drop NotDocker
		re.eval("data <- data[- grep(\"false\", data$Docker),]");
		// drop ND imageDocker
		re.eval("data <- data[- grep(\"ND\", data$ImageDocker),]");
		//remove MB
		re.eval("data$ImageDocker <- as.data.frame(sapply(data$ImageDocker,gsub,pattern=\" MB\",replacement=\"\"))");
		//rempove quotes
		re.eval("data$ImageDocker <- as.data.frame(sapply(data$ImageDocker, function(x) gsub(\"\\\"\", \"\", x)))");
		re.eval("data$ImageDocker <- unlist(data$ImageDocker)");
		//cast numerical TimeToBuild and TimeToBuildDockerPackage
		re.eval("data$ImageDocker <- as.numeric(as.character(data$ImageDocker))");

		re.eval("boxplot(data$ImageDocker~data$applicationType, ylab='ImageDocker(MB)'"
				//+ ",main='Boxplot Distribution:Image Docker'"
				+ ")");

		re.eval("dev.off()");
	}
	
	public static void createBoxplotImageDockerDB(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotImageDockerDB.jpg')");
		// drop NotDocker
		re.eval("data <- data[- grep(\"false\", data$Docker),]");
		// drop ND imageDocker
		re.eval("data <- data[- grep(\"ND\", data$ImageDocker),]");
		//remove MB
		re.eval("data$ImageDocker <- as.data.frame(sapply(data$ImageDocker,gsub,pattern=\" MB\",replacement=\"\"))");
		//rempove quotes
		re.eval("data$ImageDocker <- as.data.frame(sapply(data$ImageDocker, function(x) gsub(\"\\\"\", \"\", x)))");
		re.eval("data$ImageDocker <- unlist(data$ImageDocker)");
		//cast numerical TimeToBuild and TimeToBuildDockerPackage
		re.eval("data$ImageDocker <- as.numeric(as.character(data$ImageDocker))");

		re.eval("boxplot(data$ImageDocker~data$prodDatabaseType, ylab='ImageDocker(MB)'"
				//+ ",main='Boxplot Distribution:Image Docker'"
				+ ")");

		re.eval("dev.off()");
	}

	
	/**
	 * Draw a balloon plot and generate a png file of it.
	 * The plot concerns ApplicationType and ProductionDatabase.
	 * 
	 * @param re Rengine to execute r commands and get feedback.
	 */
	public static void createBalloonPlot(Rengine re){
		// Install packge (only necessary once)
		//re.eval("install.packages(\"ggplot2\")");
		
		// Retrieve data and extract ApplicationType and DatabaseColumn, grouped and counted
		readCSV(re, "jhipster.csv","data");
		
		
		re.eval("temp <- data.frame(table(data$applicationType, data$prodDatabaseType))");
		//re.eval("print(names(temp))");
		re.eval("names(temp)[names(temp)==\"Freq\"] <- \"Proportion\"");
		//re.eval("print(temp)");
		// Draw the balloonPlot
        re.eval("library(ggplot2)");
        re.eval("p <- ggplot(temp, aes(x=Var1, y=Var2, size=Proportion)) + "
        						+ "geom_point(shape=21, colour=\"black\", fill=\"cornsilk\") +"
        						+ "xlab(\"Application Type\") + "
        						+ "ylab(\"Database\")");
        re.eval("ggsave(\"ggplot.jpg\")");
	}	
	
	/**
	 * Draw a balloon plot and generate a jpg file of it.
	 * The plot concerns KO build and features.
	 * 
	 * @param re Rengine to execute r commands and get feedback.
	 */
	public static void createBalloonPlotBugsFeatures(Rengine re){
		// Install packge (only necessary once)
		//re.eval("install.packages(\"ggplot2\")");
		
		// Retrieve data and extract ApplicationType and DatabaseColumn, grouped and counted
		readCSV(re, "jhipster.csv","data");
		
		
		re.eval("temp <- data.frame(table(data$applicationType, data$Build))");
		//re.eval("print(names(temp))");
		re.eval("names(temp)[names(temp)==\"Freq\"] <- \"Proportion\"");
		//re.eval("print(temp)");
		// Draw the balloonPlot
        re.eval("library(ggplot2)");
        re.eval("p <- ggplot(temp, aes(x=Var1, y=Var2, size=Proportion)) + "
        						+ "geom_point(shape=21, colour=\"black\", fill=\"cornsilk\") +"
        						+ "xlab(\"Build\") + "
        						+ "ylab(\"Features\")");
        re.eval("ggsave(\"bugsFeatures.jpg\")");
	}	
		
	public static void createPieChartBuildResultByBuildTool(Rengine re){
		readCSV(re, "jhipster.csv","data");
		// drop NotDocker
		re.eval("data <- data[- grep(\"false\", data$Docker),]");
		re.eval("dataBuildToolBuildResult <- data.frame(table(data$buildTool, data$Build))");
		
		re.eval("buildOK <- dataBuildToolBuildResult[- grep(\"KO\", dataBuildToolBuildResult$Var2),]");
		re.eval("buildKO <- dataBuildToolBuildResult[- grep(\"OK\", dataBuildToolBuildResult$Var2),]");
		
		re.eval("buildOK <- as.vector(buildOK$Freq)");
		re.eval("buildKO <- as.vector(buildKO$Freq)");
		
		re.eval("labels <- c(\"Gradle\", \"Maven\")");
		
		re.eval("jpeg('buildOKPie.jpeg')");
		re.eval("pie(buildOK, labels = labels, main=\"Proportion of build success by build tool\")");
		re.eval("dev.off()");
		
		re.eval("jpeg('buildKOPie.jpeg')");
		re.eval("pie(buildKO, labels = labels"
				//+ ", main=\"Proportion of build failed by build tool\""
				+ ")");
		re.eval("dev.off()");
	}

	public static void createBoxplotCucumeberDatabase(Rengine re) {
		// Read CSV.
		readCSV(re, "jhipster.csv","data");
		readCSV(re, "cucumber.csv","data2");
		
		re.eval("mergeData = merge(data, data2)");
		
		// Create Boxplot + Save jpg
		re.eval("jpeg('boxplotCucumberDB.jpg')");
		
		re.eval("mergeData <- mergeData[- grep(\"ND\", mergeData$getCurrentUserLogin),]");

		//cast numerical getCurrentUserLogin
		re.eval("mergeData$getCurrentUserLogin <- as.numeric(as.character(mergeData$getCurrentUserLogin))");

		re.eval("boxplot(mergeData$getCurrentUserLogin~mergeData$prodDatabaseType, ylab='seconds'"
				//+ ",main='Boxplot Distribution:Cucumber Database'"
				+ ")");

		re.eval("dev.off()");
	}
}