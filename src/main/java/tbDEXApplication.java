import io.javalin.Javalin;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

// figure out the approach we want to take for config files
//  - json config file vs. a properties file
// figure out what database we want to use
//

public class tbDEXApplication {

  public static void main(String[] args) {
    Javalin app = Javalin.create().start(9001);

    List<String> pfiList = PFIDiscoverer.fetchPFIHostAddresses();

    PFIOffer pfiOffer = pfiList
        .stream()
        .map(pfi -> new PFIOffer(pfi, tbDEXApplication.getOffer(pfi)))
        .sorted(Comparator.comparing(offer -> (Double) offer.offer.get("amount")))
        .collect(Collectors.toList())
        .get(1);

    String token = (String) pfiOffer.offer.get("token");
    System.out.println(token);
    Double amount = (Double) pfiOffer.offer.get("amount");
    System.out.println(amount);

    app.get("/", ctx -> ctx.result(executeOffer(pfiOffer.url, token)));
  }

  private static class PFIOffer {
    String url;
    JSONObject offer;

    PFIOffer(String url, JSONObject offer) {
      this.url = url;
      this.offer = offer;
    }
  }

  private static JSONObject getOffer(String endpoint) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(String.format("%s/getOffer/25", endpoint))
        .build();

    try {
      Response response = client.newCall(request).execute();
      String output = response.body().string();
      return (JSONObject) JSONValue.parse(output);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static String executeOffer(String endpoint, String token) throws Exception {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(String.format("%s/executeOffer/%s", endpoint, token))
        .build();

    Response response = client.newCall(request).execute();
    return response.body().string();
  }
}