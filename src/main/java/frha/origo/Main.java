package frha.origo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;

public class Main {

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
		List<Any> res = getFromEndpoint("http://gbfs.urbansharing.com/oslobysykkel.no/gbfs.json", "feeds");
		Map<String, String> endpoints = extractEndpoints(res);
		res = getFromEndpoint(endpoints.get("station_information"), "stations");
		Map<Integer, Station> stations = extractStations(res);
		res = getFromEndpoint(endpoints.get("station_status"), "stations");
		populateStationsWithBikeInformation(stations, res);
		for (Station station : stations.values()) {
			System.out.println(station);
		}
	}

	private static void populateStationsWithBikeInformation(Map<Integer, Station> stations, List<Any> res) {
		for (Any any : res) {
			Map<String, Any> tmp = any.asMap();
			Station station = stations.get(tmp.get("station_id").toInt());
			station.numBikes = Integer.valueOf(tmp.get("num_bikes_available").toString().trim());
			station.numLocks = Integer.valueOf(tmp.get("num_docks_available").toString().trim());
		}
	}

	private static Map<Integer, Station> extractStations(List<Any> res)
			throws InstantiationException, IllegalAccessException {
		Map<Integer, Station> stations = new HashMap<Integer, Station>();
		for (Any any : res) {
			Map<String, Any> tmp = any.asMap();
			stations.put(tmp.get("station_id").toInt(), createStation(tmp));
		}
		return stations;
	}

	private static Station createStation(Map<String, Any> tmp) {
		Station station = new Station();
		station.station_id = tmp.get("station_id").toInt();
		station.name = tmp.get("name").toString();
		return station;
	}

	private static Map<String, String> extractEndpoints(List<Any> res) throws IOException, MalformedURLException {
		Map<String, String> endpoints = new HashMap<String, String>();
		for (Any any : res) {
			Map<String, Any> tmp = any.asMap();
			endpoints.put(tmp.get("name").toString(), tmp.get("url").toString());
		}
		return endpoints;
	}

	private static List<Any> getFromEndpoint(String surl, String key) throws MalformedURLException, IOException {
		URL url = new URL(surl);
		URLConnection con = url.openConnection();
		con.setRequestProperty("Client-Identifier", "frha");
		JsonIterator res = JsonIterator.parse(con.getInputStream(), 16384 * 16384);

		Map<String, Any> tmp = res.readAny().asMap().get("data").asMap();
		if (tmp.containsKey("nb"))
			tmp = tmp.get("nb").asMap();
		return tmp.get(key).asList();
	}

}
