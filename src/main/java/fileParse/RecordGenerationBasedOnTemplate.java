package fileParse;

import helpers.Utils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

/**
 *TODO: Documentation
 */
public class RecordGenerationBasedOnTemplate {
	public static void main(String[] args) {


		File inputFile;
		// Read all the input files here
		// Get a Random number with in the size of the list
		int randomNumber;
		int size = 0;
		String inputFileName = "";
		int numberRecordsToBeAdded = 100;

		try {

			// Loads the Key and value from Text File
			Properties properties = new Properties();
			properties.load(new FileReader("qa\\dictionary\\property.txt"));
			System.out.println("Key and Value Ready");
			/* This Block would read all the xml files under one directory */
			List<File> listOfXMLFiles= new ArrayList<File>();
			File homeDirectory = new File("qa\\dictionary\\xml files");
			String fileType = ".xml";
			listOfXMLFiles = parseDirectoryWithSpecificFileType(homeDirectory,fileType);
			for(File file : listOfXMLFiles){
				BufferedWriter writer = null;
				String text = "";
				String flatFileName = "qa\\dictionary\\output\\";
				flatFileName += FilenameUtils.removeExtension(file.getName());
				flatFileName += ".txt";
				List<String> nodeValues = new ArrayList<String>();
				List<String> inputTexts = new ArrayList<String>();
				// Now parse this xml file(template)
				DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = dBuilder.parse(file);
				System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
				// Prepare five flat files based on one template

				///////////////////////////////////////////
				System.out.println("*****************************************************************************");
				System.out.println("XML Name : " + file.getName());
				System.out.println("*****************************************************************************");
				if (doc.hasChildNodes()) {
					listNodes(doc.getChildNodes(), nodeValues);
				}
				System.out.println("XML File Reading is completed");
				System.out.println(nodeValues);

				List<List<String>> arr = new ArrayList<List<String>>();
				Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

				System.out.println("Array List of ArrayList Declared");

				// Prepare an Hash Map of String and ArrayList<String>
				for(String node : nodeValues){
					System.out.println("node :" + node);
					inputFileName = properties.getProperty(node);
					System.out.println("inputFile Name = " + inputFileName);
					inputFile = new File(inputFileName);
					inputTexts = readTextFile(inputFile);
					System.out.println("inputTexts = " + inputTexts);
					map.put(node, (ArrayList<String>) inputTexts);
//					arr.add(inputTexts);

				}
				System.out.println("Map : \n\n");
				System.out.println(map);

				// Now get the size of the arrayList of arrayList
				int maxNumberOfNodes = map.size();
				for(int count = 1; count <= numberRecordsToBeAdded; count++){
					List<String> inputStream = new ArrayList<String>();
					int i = 0;
					for(String node : nodeValues){
						inputStream = map.get(node);
						size = inputStream.size();
						randomNumber = Utils.randomNumberBetweenTwoValues(0, size);
						// Avoid extra tab being added at the end of String which would be copied in the flat file
						text += inputStream.get(randomNumber);
						if(i <(maxNumberOfNodes-1))
							text += "\t";
						i++;
					}
					if(count != numberRecordsToBeAdded){
						text += "\n";
					}
					System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

					System.out.println(count);
				}
				try {
					writer = new BufferedWriter(new FileWriter(flatFileName));

					writer.write(text);
					text = "";
				} catch (IOException ex1) {
					System.err.println(ex1);
				} finally {
					if (writer != null) {
						try {
							writer.close();
						} catch (IOException ex2) {
							System.err.println(ex2);
						}
					}
				}

			}

			System.out.println("****************************");
			// Now Loop Through the returned List which contains Node Values
			// If the node value




		} catch (Exception e1) {
			System.out.println(e1.getMessage());
		}

	}



	private static List<String> listNodes(NodeList nodeList, List<String> list) {

//		List<String> list = new ArrayList<String>();
		for (int count = 0; count < nodeList.getLength(); count++) {

			Node tempNode = nodeList.item(count);

			// make sure it's element node.
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {

				if (tempNode.hasAttributes()) {

					// get attributes names and values
					NamedNodeMap nodeMap = tempNode.getAttributes();

					for (int i = 0; i < nodeMap.getLength(); i++) {

						Node node = nodeMap.item(i);
						if(StringUtils.equalsIgnoreCase(node.getNodeName(), "FL_AtlasFieldName")){
							list.add(node.getNodeValue());
						}
					}

				}
			}

			// Check if another level of child nodes present, but don't dig more than 2 levels
			if (tempNode.hasChildNodes()) {
				// loop again if has child nodes
				listNodes(tempNode.getChildNodes(), list);
			}


		}
		return list;
	}

	private static List<String> readTextFile(File file){

		String value = "";
		List<String> possibleValues = new ArrayList<String>();
		StringBuilder stringBuilder = new StringBuilder();
		Scanner scanner;
		try{
			scanner = new Scanner(file);

			while (true){
				value = scanner.useDelimiter("\\t").next();
				possibleValues.add(value);
				if(StringUtils.isBlank(value) || StringUtils.isEmpty(value))
					break;
			}
		}catch (Exception ex){

		}
		return possibleValues;
	}

	private static List<File> parseDirectoryWithSpecificFileType(File sourceDirectory, String fileExtension){
		File listOfFiles[] = null;
		List<File> listOfFilesWithSpecificExtension = new ArrayList<File>();
		listOfFiles = sourceDirectory.listFiles();
		for(File file : listOfFiles){
			if(file.isDirectory()){
				parseDirectoryWithSpecificFileType(file, fileExtension);
			}
			else if(file.getName().endsWith(fileExtension)){
				listOfFilesWithSpecificExtension.add(file);
			}
		}
		return listOfFilesWithSpecificExtension;
	}

}

