package de.uniheidelberg.cl.advprog.planet.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.filecache.DistributedCache;

import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;

public class Serializer {
	public static final String HDFS_MODEL_PATH="tree_model.ser";
	
	public static URI serializeModelToDFS(DecisionTree model, Configuration conf) throws IOException, URISyntaxException {
		FileSystem fs = FileSystem.get(conf);
		Path hdfsPath = new Path(HDFS_MODEL_PATH);

		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try 	{
			fos = new FileOutputStream("tree_model_local.ser");
		    out = new ObjectOutputStream(fos);
		    out.writeObject(model);
		    out.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
		// upload the file to hdfs. Overwrite any existing copy.
		fs.copyFromLocalFile(false, true, new Path("tree_model_local.ser"),
				hdfsPath);
		DistributedCache.addCacheFile(hdfsPath.toUri(), conf);
		return hdfsPath.toUri();
	}
	
	private static DecisionTree loadTree(Path cachePath, FileSystem fileSystem) throws IOException, ClassNotFoundException {
		DecisionTree tree = null;
		FSDataInputStream in = fileSystem.open(cachePath);
		//FileInputStream fis = new FileInputStream(cachePath.getName());
		ObjectInputStream oin = new ObjectInputStream(in);
		tree = (DecisionTree) oin.readObject();
		oin.close();
		return tree;
	  }
	
	public static DecisionTree readModelFromDFS(Configuration conf) throws IOException, ClassNotFoundException {
		URI[] cacheFiles = DistributedCache.getCacheFiles(conf);
		FileSystem fileSystem = FileSystem.get(conf);
        if (null != cacheFiles && cacheFiles.length > 0) {
        	System.out.println("We've got paths");
        	for (URI cachePath : cacheFiles) {
        		if (cachePath.getPath().equals(HDFS_MODEL_PATH)) {
        			return loadTree(new Path(cachePath), fileSystem);
	          }
	        }
	      } else { System.out.println("We dont have paths"); }
        return null;
	}
	
	
}
