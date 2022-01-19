import java.util.Arrays;
import java.util.List;

public class PFIDiscoverer {
  public static List<String> fetchPFIHostAddresses() {
      return Arrays.asList(
          "http://localhost:9002",
          "http://localhost:9003"
      );
  }
}
