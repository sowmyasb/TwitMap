import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by annapurna on 9/25/14.
 */
public class AWSLaunchInstance {
  Random randomGenerator = new Random();
  private KeyPair keyPair;
  private String privateKey, privatekeyName, securityGroupName;
  private String createdInstanceId;
  private String ipAddress;
  private static final String DEFAULT_PEM_PATH = "/Users/annapurna/Downloads/";

  
  public static void main(String[] args) {
	  AWSElasticBeanstalkClient client=new AWSElasticBeanstalkClient();
	  client.createApplication(createApplicationRequest);
	  client.
  }
  
  public static void main2(String[] args) {
    AWSLaunchInstance launch = new AWSLaunchInstance();
    try {
      launch.launchInstance();
    } catch (Exception e) {
      System.out.println("Exception encountered while launching AWS instance");
      e.printStackTrace();
    }
  }

  private void launchInstance() throws Exception {
    AWSCredentials credentials = new PropertiesCredentials(AWSLaunchInstance.class
        .getResourceAsStream("AwsCredentials.properties"));
    AmazonEC2Client ec2 = new AmazonEC2Client(credentials);
    try {
      createNewSecurityGroup(ec2);
      createNewKeyValuePair(ec2);
      createAndStartInstance(ec2);
      //connectToInstance(ec2);
      //stopAndTerminateInstance(ec2);
    } finally {
      ec2.shutdown();
    }
  }

  private void connectToInstance(AmazonEC2Client amazonEC2Client) {

  }

  private void stopAndTerminateInstance(AmazonEC2Client ec2) {
    List<String> instanceIds = new LinkedList<String>();
    instanceIds.add(createdInstanceId);
    StopInstancesRequest stopIR = new StopInstancesRequest(instanceIds);
    ec2.stopInstances(stopIR);
    TerminateInstancesRequest tir = new TerminateInstancesRequest(instanceIds);
    ec2.terminateInstances(tir);
  }

  private void createAndStartInstance(AmazonEC2Client amazonEC2Client) throws
      InterruptedException {
    String imageId = "ami-08842d60";
    int numInstances = 1;
    RunInstancesRequest rir = new RunInstancesRequest();
    rir.withImageId(imageId)
        .withInstanceType("t2.micro")
        .withMinCount(numInstances)
        .withMaxCount(numInstances)
        .withKeyName(privatekeyName)
        .withSecurityGroups(securityGroupName);
    RunInstancesResult result = amazonEC2Client.runInstances(rir);

    List<Instance> resultInstance = result.getReservation().getInstances();
    createdInstanceId = null;
    for (Instance ins : resultInstance){
      createdInstanceId = ins.getInstanceId();
      System.out.println("New instance has been created: "+ins.getInstanceId());
    }

    List<String> resources = new LinkedList<String>();
    resources.add(createdInstanceId);

    List<Tag> tags = new LinkedList<Tag>();
    Tag nameTag = new Tag("Name", "CloudMiniHW2");
    tags.add(nameTag);
    CreateTagsRequest ctr = new CreateTagsRequest(resources, tags);
    amazonEC2Client.createTags(ctr);

    StartInstancesRequest startIR = new StartInstancesRequest(resources);
    amazonEC2Client.startInstances(startIR);

    for (Instance ins : resultInstance){
      Thread.sleep(60000);
      ipAddress = ins.getPrivateIpAddress();
      System.out.println("Private IP address of created instance:" + ipAddress);
      ipAddress = ins.getPublicIpAddress();
      System.out.println("Public IP address of created instance:" + ipAddress);
    }
  }

  private void createNewKeyValuePair(AmazonEC2Client amazonEC2Client) {
    String keyName = "MiniHWKey" + randomGenerator.nextInt();
    CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
    createKeyPairRequest.withKeyName(keyName);
    CreateKeyPairResult createKeyPairResult = amazonEC2Client.createKeyPair
        (createKeyPairRequest);
    keyPair = createKeyPairResult.getKeyPair();
    createKeyPairFile();
    privatekeyName = keyPair.getKeyName();
    privateKey = keyPair.getKeyMaterial();
    System.out.println("Private key " + keyName + " created with key " +
        "material:" + privateKey);
  }

  private void createKeyPairFile() {
    String fileName, key;
    fileName = keyPair.getKeyName();
    key = keyPair.getKeyMaterial();
    File file = new File(DEFAULT_PEM_PATH+fileName+".pem");
    try {
      file.createNewFile();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      FileWriter fw = new FileWriter(file.getAbsoluteFile());

      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(key);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    file.setReadOnly();
  }

  private void createNewSecurityGroup (AmazonEC2Client amazonEC2Client) {
    securityGroupName = "MiniHWGroup" + randomGenerator.nextInt();
    try {
      CreateSecurityGroupRequest securityGroupRequest = new
          CreateSecurityGroupRequest(securityGroupName, "Security group for mini " +
          "HW assignment");
      amazonEC2Client.createSecurityGroup(securityGroupRequest);
    } catch (AmazonServiceException ase) {
      System.out.println(ase.getMessage());
    }

    String ipAddr = "0.0.0.0/0";
    /*try {
      InetAddress addr = InetAddress.getLocalHost();
      ipAddr = addr.getHostAddress()+"/32";
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }*/

    List<String> ipRanges = new ArrayList<String>();
    ipRanges.add(ipAddr);

    List<IpPermission> ipPermissions = new ArrayList<IpPermission> ();
    IpPermission ipPermission = new IpPermission();
    ipPermission.setIpProtocol("tcp");
    ipPermission.setFromPort(new Integer(22));
    ipPermission.setToPort(new Integer(22));
    ipPermission.setIpRanges(ipRanges);
    ipPermissions.add(ipPermission);

    try {
      AuthorizeSecurityGroupIngressRequest ingressRequest =
          new AuthorizeSecurityGroupIngressRequest(securityGroupName, ipPermissions);
      amazonEC2Client.authorizeSecurityGroupIngress(ingressRequest);
    } catch (AmazonServiceException ase) {
      System.out.println(ase.getMessage());
    }
    System.out.println("Security group " + securityGroupName + " created");
  }
}
