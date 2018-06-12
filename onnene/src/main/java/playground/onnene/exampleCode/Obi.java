package playground.onnene.exampleCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.scenario.ScenarioUtils;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//"C:\Users\NNNOB\Documents\GitHub\matsim-sa\src\main\java\org\matsim\onnene\ga\input\config.xml";

public class Obi extends AbstractProblem {
	// C:\Users\NNNOB\Documents\GitHub\matsim-sa\src\main\java\org\matsim\onnene\exampleCode\outputFolder\transitNetwork_final
	// private static final String CONFIG_FILE =
	// "C:\\Users\\NNNOB\\Documents\\GitHub\\input\\config.xml";

	private static final String CONFIG_FILE = "C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\ga\\input\\config.xml";
	private static final String OUTPUT_FOLDER_PATH = "C:\\Users\\NNNOB\\Documents\\GitHub\\matsim-sa\\src\\main\\java\\org\\matsim\\onnene\\ga\\input\\output\\";

	// private static final String OUTPUT_FOLDER_PATH =
	// "C:\\Users\\NNNOB\\Documents\\GitHub\\input\\outputFolder\\Example";
	// private static final String OUTPUT_FOLDER_PATH =
	// "E:\\workspace\\sts_workspace\\matsim-example-project\\output\\";
	private static final int MAX_MOEA_EVALUATIONS = 1;
	private static final int MATSIM_ITERATION_NUMBER = 10;

	// private static FileOutputStream FOS;
	private int currentEvaluationNumber = 0;

	// static {
	// try {
	// FOS = new FileOutputStream(new File(
	// "C:\\\\Users\\\\NNNOB\\\\Documents\\\\GitHub\\\\matsim-sa\\\\src\\\\main\\\\java\\\\org\\\\matsim\\\\onnene\\\\exampleCode\\\\outputFolder\\\\transitNetwork_final\\\\log.txt"));
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// }
	// }

	public static void main(String[] args) throws Exception {

		Obi obi = new Obi();

		NondominatedPopulation result = new Executor()
				// .withProblem("UFI")
				.withProblem(obi).withAlgorithm("NSGAII").withMaxEvaluations(MAX_MOEA_EVALUATIONS).run();

		// display the results
		System.out.format("Objective1  Objective2%n");
		// FOS.write("\nObjective1 Objective2%n".getBytes());

		for (Solution solution : result) {
			System.out.format("%.4f      %.4f%n", solution.getObjective(0), solution.getObjective(1));
			// FOS.write(
			// String.format("\n%.4f %.4f%n", solution.getObjective(0),
			// solution.getObjective(1)).getBytes());
		}
	}

	public Obi() throws FileNotFoundException {
		// 4 variables (q, t, f, l)
		// 2 objectives
		// 3 constraints
		// super(4, 2, 3);
		super(4, 2);

	}

	@Override
	public Solution newSolution() {
		// Solution solution = new Solution(getNumberOfVariables(),
		// getNumberOfObjectives(), getNumberOfConstraints());
		Solution solution = new Solution(getNumberOfVariables(), getNumberOfObjectives());

		solution.setVariable(0, new RealVariable(0.0, 1.0));
		for (int i = 1; i < getNumberOfVariables(); i++) {
			solution.setVariable(i, new RealVariable(-1.0, 1.0));
		}

		return solution;
	}

	@Override
	public void evaluate(Solution solution) {

		currentEvaluationNumber++;

		// try {
		// FOS.write("\nMOEA evaluate(...) function called".getBytes());
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }

		double[] vars = EncodingUtils.getReal(solution); // Assuming your decision variables are all reals

		// Generate the input file with these variables
		// File file = new File("input.txt");
		// create the input file

		// Call your simulation model (if the model procures a lot of output, redirect
		// the output stream to stdout)
		// Process p = Runtime.getRuntime().exec(new String[] { "mySimulation.exe",
		// "-i", "input.txt", "-o", "output.txt" });
		// p.waitFor();

		// Read the output file
		// File file = new File("output.txt");
		// read the contents of the file ...

		runMatsim(CONFIG_FILE);

		// try {
		// Thread.sleep(5 * 60 * 1000);
		// } catch (InterruptedException e2) {
		// // TODO Auto-generated catch block
		// e2.printStackTrace();
		// }

		try {
			Objectives objectives = processOutputFiles(OUTPUT_FOLDER_PATH, MATSIM_ITERATION_NUMBER);
			printObjectives(objectives);

			solution.setObjectives(objectives.objectives);
			// solution.setConstraints();
		} catch (Exception e) {
			// try {
			// FOS.write(("\nError while processing outputFiles " +
			// e.getMessage()).getBytes());
			// FOS.flush();
			// solution.setObjectives(new double[] { 0.0, 0.0 });
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// }
			System.err.println(e.getMessage());
			// e.printStackTrace();
		}

	}

	public void runMatsim(String configFile) {

		// try {
		// FOS.write("\nrunMatsim(...) function called".getBytes());
		// } catch (IOException e1) {
		// e1.printStackTrace();
		// }

		Config config = ConfigUtils.loadConfig(configFile);
		config.controler().setLastIteration(MATSIM_ITERATION_NUMBER);
		// config.controler().setOutputDirectory(OUTPUT_FOLDER_PATH +
		// currentEvaluationNumber + "\\");
		config.controler().setOutputDirectory(OUTPUT_FOLDER_PATH);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);

		Scenario scenario = ScenarioUtils.loadScenario(config);
		Controler controler = new Controler(scenario);
		controler.run();
	}

	private void printObjectives(Objectives objectives) {
		System.out.println("\n=========================");
		System.out.println("Total time objective: " + objectives.objectives[0]);
		System.out.println("Frequency objective: " + objectives.objectives[1]);
		System.out.println("=========================");

		// try {
		// FOS.write("\n=========================".getBytes());
		// FOS.write(("Total time objective: " + objectives.objectives[0]).getBytes());
		// FOS.write(("Second objective: " + objectives.objectives[1]).getBytes());
		// FOS.write("=========================".getBytes());
		// FOS.flush();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
	}

	private Objectives processOutputFiles(String outputFolderPath, int iterationNumber) throws Exception {
		String iterationFolderPath = outputFolderPath + "ITERS\\it." + iterationNumber + "\\";

		File eventsZipFile = new File(iterationFolderPath + iterationNumber + ".events.xml.gz");
		File eventsFile = new File(iterationFolderPath + iterationNumber + ".events.xml");
		if (!eventsZipFile.exists()) {
			throw new Exception(eventsZipFile.getAbsolutePath() + " doesn't exists");
		}
		if (eventsFile.exists())
			eventsFile.delete();
		eventsFile.createNewFile();

		File linkStatsZipFile = new File(iterationFolderPath + iterationNumber + ".linkstats.txt.gz");
		File linkStatsFile = new File(iterationFolderPath + iterationNumber + ".linkstats.txt");
		if (!linkStatsZipFile.exists()) {
			throw new Exception(linkStatsZipFile.getAbsolutePath() + " doesn't exists");
		}
		if (linkStatsFile.exists())
			linkStatsFile.delete();
		linkStatsFile.createNewFile();

		gUnzip(eventsZipFile, eventsFile);
		gUnzip(linkStatsZipFile, linkStatsFile);

		Map<String, List<Event>> personToEventsMap = processEventsXmlFile(eventsFile, true);
		Map<String, Link> linksMap = processLinkStatsFile(linkStatsFile);

		// Calculate total time objective
		BigDecimal totalTimeObjective = getTotalTimeObjective(personToEventsMap, linksMap);

		// Calculate second objective
		BigDecimal secondObjective = getsecondObjective(eventsFile, linksMap);

		// System.out.println("\n=========================");
		// System.out.println("Total time objective: " + totalTimeObjective);
		//
		// System.out.println("\n=========================");
		// System.out.println("Second objective: " + secondObjective);

		Objectives objectives = new Objectives();
		objectives.objectives[0] = totalTimeObjective.doubleValue();
		objectives.objectives[1] = secondObjective.doubleValue();

		return objectives;
	}

	private BigDecimal getsecondObjective(File eventsFile, Map<String, Link> linksMap)
			throws ParserConfigurationException, SAXException, IOException {
		Map<String, List<Event>> personToEventsMap = processEventsXmlFile(eventsFile, false);

		List<Event> events = new ArrayList<>();
		for (List<Event> list : personToEventsMap.values()) {
			events.addAll(list);
		}

		// 4091 + 4091
		BigDecimal nVehicles = new BigDecimal(events.size());
		System.out.println("nVehicles: " + nVehicles);

		// Calculate duration
		BigDecimal smallestTime = new BigDecimal(0);
		BigDecimal biggestTime = new BigDecimal(0);
		for (Event event : events) {
			if (event.time.compareTo(smallestTime) == -1)
				smallestTime = event.time;

			if (event.time.compareTo(biggestTime) == +1)
				biggestTime = event.time;
		}
		BigDecimal durationInHr = biggestTime.subtract(smallestTime).divide(new BigDecimal(3600), 2,
				RoundingMode.HALF_UP);
		System.out.println("Duration (in Hours): " + durationInHr);

		// Calculate total link length
		BigDecimal totalLinkLength = new BigDecimal(0);
		Link link;
		for (Event event : events) {
			link = linksMap.get(event.link);
			totalLinkLength = totalLinkLength.add(link.length);
		}
		totalLinkLength = totalLinkLength.divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);
		System.out.println("Total link length (in km): " + totalLinkLength);

		// Calculate objective
		BigDecimal secondObjective = (nVehicles.divide(durationInHr, 2, RoundingMode.HALF_UP))
				.multiply(totalLinkLength);

		return secondObjective;
	}

	private BigDecimal getTotalTimeObjective(Map<String, List<Event>> personToEventsMap, Map<String, Link> linksMap) {
		BigDecimal totalTimeObjective = new BigDecimal(0);
		for (String person : personToEventsMap.keySet()) {

			List<Event> events = personToEventsMap.get(person);

			BigDecimal time = new BigDecimal(0);
			for (Event event : events) {
				Link link = linksMap.get(event.link);
				// if (person.equals("48")) {
				// System.out.println(link.avgAvg10_11);
				// }
				time = time.add(link.avgAvg10_11);

				totalTimeObjective = totalTimeObjective.add(link.avgAvg10_11);
			}

			// if (person.equals("48")) {
			// System.out.println(events.size());
			// System.out.println("Total time for person 48 is " + time);
			// }
		}
		System.out.println("Total time for all passengers in minutes is " + totalTimeObjective);

		totalTimeObjective = totalTimeObjective.divide(new BigDecimal(3600.0), 2, RoundingMode.HALF_UP);
		System.out.println("Total time for all passengers in hours is " + totalTimeObjective);

		return totalTimeObjective;
	}

	public void gUnzip(File gZipFile, File outputFile) {

		byte[] buffer = new byte[1024];

		try (GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(gZipFile));
				FileOutputStream out = new FileOutputStream(outputFile);) {

			int len;
			while ((len = gzis.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public Map<String, List<Event>> processEventsXmlFile(File xmlFile, boolean totalTimeObjective)
			throws ParserConfigurationException, SAXException, IOException {

		Map<String, List<Event>> events = new HashMap<>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		// try {
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		NodeList nodeList = doc.getElementsByTagName("event");

		System.out.println("Nodelist size: " + nodeList.getLength());

		// now XML is loaded as Document in memory, lets convert it to Object List
		Node eventNode;
		for (int i = 0; i < nodeList.getLength(); i++) {

			eventNode = nodeList.item(i);

			Event event;
			if (totalTimeObjective)
				event = getEventsForTotalTimeObjective(eventNode);
			else
				event = getEventsForSecondObjective(eventNode);

			if (event != null) {
				List<Event> list = events.get(event.person);
				if (list == null) {
					list = new ArrayList<>();
					events.put(event.person, list);
				}
				list.add(event);
			}
		}

		System.out.println("Total events: " + events.values().size());
		// }

		return events;
	}

	private Map<String, Link> processLinkStatsFile(File linkStatsFile) throws Exception {
		Map<String, Link> linksMap = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(linkStatsFile))) {

			String line;
			String[] columns;

			// process header line
			line = br.readLine();
			columns = line.split("\\s+");
			int nColumns = columns.length;

			// for (int i = 0; i < columns.length; i++) {
			// String header = columns[i];
			// System.out.println(i + " " + header);
			// }

			while ((line = br.readLine()) != null) {
				columns = line.split("\\s+");

				// if (columns.length != nColumns) {
				// throw new Exception("In linkstats.txt file, number of columns doesn't match:
				// " + nColumns + " " + columns.length + " " + line);
				// }

				Link link = new Link();
				link.linkId = columns[0];
				link.avgAvg10_11 = new BigDecimal(columns[113]); // TRAVELTIME10-11avg column
				link.length = new BigDecimal(columns[3]); // link length

				linksMap.put(link.linkId, link);

				// if ("MyCiTi_85".equals(link.linkId)) {
				// for (int i = 0; i < columns.length; i++) {
				// String header = columns[i];
				// System.out.println(i + " " + header);
				// }
				// }
			}
		}

		return linksMap;
	}

	private static Event getEventsForTotalTimeObjective(Node node) {
		// XMLReaderDOM domReader = new XMLReaderDOM();

		Event event = null;

		if (node.getNodeType() == Node.ELEMENT_NODE) {

			Element element = (Element) node;

			String type = element.getAttribute("type");
			String legmode = element.getAttribute("legMode");
			if ("departure".equalsIgnoreCase(type) && "pt".equalsIgnoreCase(legmode)) {

				event = new Event();
				event.person = element.getAttribute("person");
				event.link = element.getAttribute("link");
			}

		}

		return event;
	}

	private static Event getEventsForSecondObjective(Node node) {
		// XMLReaderDOM domReader = new XMLReaderDOM();

		Event event = null;

		if (node.getNodeType() == Node.ELEMENT_NODE) {

			Element element = (Element) node;
			String type = element.getAttribute("type");

			if ("vehicle enters traffic".equalsIgnoreCase(type) || "vehicle leaves traffic".equalsIgnoreCase(type)) {

				event = new Event();

				event.type = type;
				event.link = element.getAttribute("link");
				event.vehicles = element.getAttribute("vehicle");
				event.time = new BigDecimal(element.getAttribute("time"));
			}

		}

		return event;
	}

	// private static String getTagValue(String tag, Element element) {
	// NodeList nodeList =
	// element.getElementsByTagName(tag).item(0).getChildNodes();
	// Node node = (Node) nodeList.item(0);
	// return node.getNodeValue();
	// }

	private static class Event {
		String type;

		// String legmode;
		String person;
		String link;

		String vehicles;
		BigDecimal time;

	}

	private static class Link {
		String linkId;
		BigDecimal avgAvg10_11;
		BigDecimal length;
	}

	private static class Objectives {
		double[] objectives;
	}
}
