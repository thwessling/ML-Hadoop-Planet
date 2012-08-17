package de.uniheidelberg.cl.advprog.planet.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import de.uniheidelberg.cl.advprog.planet.tree.DecisionTree;

/**
 * {@link Serializer} handles serialization and de-serialization of 
 * a {@link DecisionTree} model both in the HDFS file system and 
 * via standard files.
 *  
 * @author boegel
 *
 */
@SuppressWarnings("deprecation")
public class Serializer {
	public static final String HDFS_MODEL_PATH="tree_model.ser";
	public static final String LOCAL_MODEL_PATH="tree_model_local.ser";
	
	/**
	 * Serializes a decision tree to a local file.
	 * 
	 * @param model The model to serialize.
	 */
	public static void serializeModelToFile(DecisionTree model) {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(LOCAL_MODEL_PATH);
		    out = new ObjectOutputStream(fos);
		    out.writeObject(model);
		    out.close();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static URI serializeModelToDFS(DecisionTree model, Configuration conf) throws IOException, URISyntaxException {
		FileSystem fs = FileSystem.get(conf);
		Path hdfsPath = new Path(HDFS_MODEL_PATH);

		serializeModelToFile(model);
		// upload the file to hdfs. Overwrite any existing copy.
		fs.copyFromLocalFile(false, true, new Path(LOCAL_MODEL_PATH),hdfsPath);
		DistributedCache.addCacheFile(hdfsPath.toUri(), conf);
		return hdfsPath.toUri();
	}
	
	private static DecisionTree loadTree(InputStream in) throws IOException, ClassNotFoundException {
		DecisionTree tree = null;
		//FileInputStream fis = new FileInputStream(cachePath.getName());
		ObjectInputStream oin = new ObjectInputStream(in);
		tree = (DecisionTree) oin.readObject();
		oin.close();
		return tree;
	  }
	
	public static DecisionTree readModelFromFile(String path) throws IOException, ClassNotFoundException {
		FileInputStream in = new FileInputStream(path);
		return loadTree(in);
	}
	
	public static DecisionTree readModelFromDFS(Configuration conf) throws IOException, ClassNotFoundException {
		URI[] cacheFiles = DistributedCache.getCacheFiles(conf);
		FileSystem fileSystem = FileSystem.get(conf);
        if (null != cacheFiles && cacheFiles.length > 0) {
        	for (URI cachePath : cacheFiles) {
        		if (cachePath.getPath().equals(HDFS_MODEL_PATH)) {
        			FSDataInputStream in = fileSystem.open(new Path(cachePath));
        			return loadTree(in);
	          }
	        }
	      } 
        return null;
	}
	
	
}
