Massively parallelized growing of random decision trees for machine learning using Hadoop.



*************************
Building
*************************

Build the code with the following command: 
> ant jar
This should compile all classes and create the jar file planet.jar.

*************************
Running training
*************************

1) Run the init.sh script to copy instances to the appropriate hdfs location
2) Remove any _local_ output directories (planet_train_out.*)
3) Start training with 
   > hadoop jar planet.jar
4) The resulting model will be written to tree_model_local.ser

*************************
Testing the model
*************************
1) Start testing the produced model with the command 
   > ant ClassifierTest
For testing, data/test.txt is used


************************************
Data format 
************************************
1) The description of instance files is expected at data/features.txt.
   This file contains one line for each feature, specifying the name and the feature range. 
   Name and value range are separated by a colon.
   For ordered attributes, the feature range can be input as MIN_VALUE-MAX-VALUE.
   For unordered attributes, each possible value needs to be specified and values are separated by 
   a semicolon.
   Note that only numeric values are supported and that the feature range for ordered attributes 
   a step size of 1.0 for values (i.e. only integer values are supported). Any input data set needs 
   to be preprocessed and normalized to follow this notation.
   
 2) The instance file itself consists of one instance per line, while features are separated by a 
    comma. The last line is used as the class label.
    
     
