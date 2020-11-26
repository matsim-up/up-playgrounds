package playground.onnene.gtfs;

import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.gtfs.GtfsConverter;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.network.io.NetworkWriter;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.utils.geometry.CoordinateTransformation;
import org.matsim.core.utils.geometry.transformations.TransformationFactory;
import org.matsim.pt.transitSchedule.api.TransitScheduleWriter;
import org.matsim.pt.utils.CreatePseudoNetwork;
import org.matsim.pt.utils.CreateVehiclesForSchedule;
import org.matsim.up.utils.Header;
import org.matsim.vehicles.VehicleWriterV1;

import com.conveyal.gtfs.GTFSFeed;

//import com.conveyal.gtfs.GTFSFeed;

/**
 * Class to read in a GTFS feed (single *.zip file) and write the appropriate
 * MATSim transit network, schedule and vehicle files.
 * 
 * @author jwjoubert
 */
public class ConvertGtfsToMatsimTransit {
	
	final private static Logger log = Logger.getLogger(ConvertGtfsToMatsimTransit.class);
	final private static String CRS_CT = TransformationFactory.HARTEBEESTHOEK94_LO19;
	
	
	public static void main(String[] args) {
		Header.printHeader(ConvertGtfsToMatsimTransit.class, args);
		run(args);
		Header.printFooter();
	
	}
	
	public static void run(String[] args) {
		String gtfsFile = "./input/gtfsInputs/31Mar2018_MyCiTi_gtfs.zip";
		String outputFolder = "./input/gtfsInputs/gtfsOutput/";
		//String gtfsFile = DirectoryConfig.GTFS_FEED;
		//String outputFolder = DirectoryConfig.COMPRESSED_GTFS_GZIP_DIRECTORY;
		String date = "2018-05-29"; 
		
		
		//Scenario sc = parseGtfs(gtfsFile);
		Scenario sc = parseGtfs(gtfsFile, date);
		System.out.println(sc.toString());
		writeTransitScenario(sc, outputFolder);
	}
	
	/**
	 * Builds a complete transit scenario.
	 * 
	 * @param gtfsFile
	 * @return
	 */
	public static Scenario parseGtfs(String gtfsFile, String date){
		
		Config config = ConfigUtils.createConfig();
		config.global().setCoordinateSystem(CRS_CT);
		config.controler().setLastIteration(0);
		config.transit().setUseTransit(true);
		
		Scenario sc = ScenarioUtils.createScenario(config);
		
		log.info("Parse the GTFS file...");
		GTFSFeed feed = GTFSFeed.fromFile(gtfsFile);
		CoordinateTransformation ct = TransformationFactory.getCoordinateTransformation("WGS84", CRS_CT);
		GtfsConverter gtfs = new GtfsConverter(feed, sc, ct);
		
		//System.out.println(LocalDate.parse(date));
		gtfs.setDate(LocalDate.parse(date));
		gtfs.convert();
		log.info("Number of transit lines: " + sc.getTransitSchedule().getTransitLines().size());
		
		log.info("Create transit vehicles...");
		CreateVehiclesForSchedule cvs = new CreateVehiclesForSchedule(sc.getTransitSchedule(), sc.getTransitVehicles());
		cvs.run();
		
		log.info("Create transit pseudonetwork...");
		CreatePseudoNetwork cpn = new CreatePseudoNetwork(sc.getTransitSchedule(),sc.getNetwork(),"MyCiTi_");
		cpn.createNetwork();

		return sc;
		
		
	}
	
	/**
	 * Just writes out the three transit -related files to a given folder.
	 * 
	 * @param sc
	 * @param outputFolder
	 */
	public static void writeTransitScenario(Scenario sc, String outputFolder){
		outputFolder += outputFolder.endsWith("/") ? "" : "/";
		
		new TransitScheduleWriter(sc.getTransitSchedule()).writeFile(outputFolder + "transitSchedule.xml.gz");
		new VehicleWriterV1(sc.getTransitVehicles()).writeFile(outputFolder + "transitVehicles.xml.gz");
		new NetworkWriter(sc.getNetwork()).write(outputFolder + "transitNetwork.xml.gz");
	}

}
