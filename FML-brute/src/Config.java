


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;



public class Config {

	private HashMap<Integer,ArrayList<String>> products;

	public Config()  {
		products =  new HashMap<Integer,ArrayList<String>>();
		
	}
	
	
	public void printProducts() {
		for (int i  : products.keySet()) {
			System.out.println("product Number "+i);
			for (String feat:  products.get(i)) {
				System.out.print(" "+feat+" ");
			}
			System.out.println("");
		}
	}


	public void addProduct(int prodNum, ArrayList<String> features) {
		// TODO Auto-generated method stub
		products.put(prodNum,features);
		
	}


	public Set<Set<String>> getConfs() {
		 Set<Set<String>> confs = new HashSet<Set<String>>();
		 for (int i  : products.keySet()) {
				Set<String> conf = new HashSet<String>();
				for (String feat:  products.get(i)) {
					conf.add(feat);
				}
				confs.add(conf);
			}
		 
		return confs;
	}
	
}
