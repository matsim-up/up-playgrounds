package playground.onnene.transitScheduleMaker;



/**

 * A console application that tests the UnzipUtility class
 *
 * @author www.codejava.net
 *
 */
public class UnzipUtilityTest {

    public static void main(String[] args) {
        String zipFilePath = "C:/Users/NNNOB/Documents/GitHub/up-playgrounds/onnene/input/matsimInput/release.zip";
        String destDirectory = "C:/Users/NNNOB/Documents/GitHub/up-playgrounds/onnene/input/matsimInput/new/";
        UnzipUtility unzipper = new UnzipUtility();
        try {
            unzipper.unzip(zipFilePath, destDirectory);
        } catch (Exception ex) {
            // some errors occurred
            ex.printStackTrace();
        }
    }
}
