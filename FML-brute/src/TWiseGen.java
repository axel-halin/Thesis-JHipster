

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import no.sintef.ict.splcatool.CSVException;
import no.sintef.ict.splcatool.CoveringArray;
import no.sintef.ict.splcatool.CoveringArrayGenerationException;
import no.sintef.ict.splcatool.SPLCATool;
import splar.core.fm.FeatureModelException;
import de.ovgu.featureide.fm.core.io.UnsupportedModelException;


public class TWiseGen {
	
	public Config twise(Map<String, String> argsMap) {
		
		Config products = new Config();
		// trying with default value needs to use apache config to set all parameters
		SPLCATool vspl =  new SPLCATool();	
		
		try {
			CoveringArray array =  vspl.t_wise(argsMap);
			
			System.out.println("getRow count "+ array.getRowCount());
			for(int i = 0; i<array.getRowCount(); i++){
				int cpt=0;
				
				ArrayList<String> features = new ArrayList<String>(); 
				for (int id: array.getRow(i)) {
					
					//System.out.println("prod: " + array.getNrs().get(i)); // product number
					//System.out.print("feature "+  array.getNrId().get(array.getNrs().get(cpt)));
					
					if (id==0) {
						features.add(array.getNrId().get(array.getNrs().get(cpt)));
					}
					//System.out.println(" selected (0=Yes) "+  id); // selected
					
					cpt++;
				}
				int prodNum  = i;
				products.addProduct(prodNum,features);
				//System.out.println("");
			}
			
		} catch (UnsupportedModelException | IOException
				| FeatureModelException | TimeoutException | CSVException
				| CoveringArrayGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return products;
	
	}


}
