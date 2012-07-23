package de.uniheidelberg.cl.advprog.planet.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;

import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;

public class Serializer {

	
	public static URI serializeModelToDFS(DecisionTree model, JobConf conf) throws IOException, URISyntaxException {
		FileSystem fs = FileSystem.get(conf);
		Path hdfsPath = new Path("tree_model.ser");

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try 	{
			fos = new FileOutputStream("tree_model1.ser");
		    out = new ObjectOutputStream(fos);
		    out.writeObject(model);
		    out.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		// upload the file to hdfs. Overwrite any existing copy.
		fs.copyFromLocalFile(false, true, new Path("tree_model1.ser"),
				hdfsPath);
		URI uri = new URI("planet/tree_model.ser");
		DistributedCache.addCacheFile(uri, conf);
		return uri;
	}
	
	private static DecisionTree loadTree(Path cachePath) throws IOException, ClassNotFoundException {
		DecisionTree tree = null;
		FileInputStream fis = new FileInputStream(cachePath.getName());
		ObjectInputStream oin = new ObjectInputStream(fis);
		tree = (DecisionTree) oin.readObject();
		oin.close();
		return tree;
	  }
	
	public static DecisionTree readModelFromDFS() throws IOException, ClassNotFoundException {
		JobConf conf = new JobConf();
		Path [] cacheFiles = DistributedCache.getLocalCacheFiles(conf);
        if (null != cacheFiles && cacheFiles.length > 0) {
        	for (Path cachePath : cacheFiles) {
        		if (cachePath.getName().equals("tree_model.ser")) {
        			return loadTree(cachePath);
	          }
	        }
	      }
        return null;
	}
	
	
}
