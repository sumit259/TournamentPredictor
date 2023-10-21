import java.util.*;

public class Main {
    public static void main(String[] args) {

        /*
            Teams	                        Mat	Won	Lost	Tied	NR	Pts	NRR
            ----------------------------------------------------------------------
            New Zealand 	                4	4	0	    0	    0	8	+1.923
            India           	            4	4	0	    0	    0	8	+1.659
            South Africa        	        4	3	1	    0	    0	4	+1.385
            Australia               	    4	2	2   	0	    0	4	-0.193
            Pakistan    	                4	2	2   	0	    0	4	-0.456
            England         	            4	1	3   	0	    0	2	-0.084
            Bangladesh          	        4	1	3	    0	    0	2	-0.784
            Netherlands 	                4	1	3	    0	    0	2	-0.790
            Sri Lanka   	                4	1	3	    0	    0	2	-1.048
            Afghanistan                     4	1	3   	0	    0	2	-1.250
        */

        // team -> (Matches, Won, Lost, Tied, NR, points)
        HashMap<String, List<Integer>> points = new HashMap<>();
        points.put("NZ",  Arrays.asList(4,4,0,0,0,8));
        points.put("IND", Arrays.asList(4,4,0,0,0,8));
        points.put("SA",  Arrays.asList(4,3,1,0,0,6));
        points.put("AUS", Arrays.asList(4,2,2,0,0,4));
        points.put("PAK", Arrays.asList(4,2,2,0,0,4));
        points.put("ENG", Arrays.asList(4,1,3,0,0,2));
        points.put("BAN", Arrays.asList(4,1,3,0,0,2));
        points.put("NED", Arrays.asList(4,1,3,0,0,2));
        points.put("SL",  Arrays.asList(4,1,3,0,0,2));
        points.put("AFG", Arrays.asList(4,1,3,0,0,2));

        String valid = validatePointsTable(points);

        if(!Objects.equals(valid, "")){
            System.out.println(valid);
            return;
        }

        List<List<String>> matchesRemaining = Arrays.asList(
                Arrays.asList("IND", "NZ"),
                Arrays.asList("PAK", "AFG"),
                Arrays.asList("SA",  "BAN"),
                Arrays.asList("AUS", "NED"),
                Arrays.asList("ENG", "SL"),
                Arrays.asList("PAK", "SA"),
                Arrays.asList("AUS", "NZ"),
                Arrays.asList("NED", "BAN"),
                Arrays.asList("IND", "ENG"),
                Arrays.asList("AFG", "SL"),
                Arrays.asList("PAK", "BAN"),
                Arrays.asList("NZ",  "SA"),
                Arrays.asList("IND", "SL"),
                Arrays.asList("NED", "AFG"),
                Arrays.asList("NZ",  "PAK"),
                Arrays.asList("ENG", "AUS"),
                Arrays.asList("IND", "SA"),
                Arrays.asList("BAN", "SL"),
                Arrays.asList("AUS", "AFG"),
                Arrays.asList("ENG", "NED"),
                Arrays.asList("NZ",  "SL"),
                Arrays.asList("SA",  "AFG"),
                Arrays.asList("AUS", "BAN"),
                Arrays.asList("ENG", "PAK"),
                Arrays.asList("IND", "NED")
        );
        if(!validateMatchesRemaining(matchesRemaining, points)){
            System.out.println("Issues in matches remaining, please check");
            return;
        }

        HashMap<String, Integer> current = getFromPoints(points);
        List<HashMap<String, Integer>> scenarios = new ArrayList<>();
        backtrack(scenarios, current, matchesRemaining, 0);

        HashMap<String, Integer> guaranteedQualifiers = new HashMap<>();
        HashMap<String, Integer> teamWiseCount = new HashMap<>();

        for (HashMap<String, Integer> scenario: scenarios) {
            List<List<Object>> l = sortHashMap(scenario);
            System.out.println(l);
            StringBuilder key = new StringBuilder(l.get(0).get(0) + "," + l.get(1).get(0) + "," + l.get(2).get(0));
            teamWiseCount.put((String) l.get(0).get(0), 1 + teamWiseCount.getOrDefault(l.get(0).get(0), 0));
            teamWiseCount.put((String) l.get(1).get(0), 1 + teamWiseCount.getOrDefault(l.get(1).get(0), 0));
            teamWiseCount.put((String) l.get(2).get(0), 1 + teamWiseCount.getOrDefault(l.get(2).get(0), 0));
            int score = (int) l.get(3).get(1);
            for(int i = 3; i < 10 && (int) l.get(i).get(1) == score; i++){
                teamWiseCount.put((String) l.get(i).get(0), 1 + teamWiseCount.getOrDefault(l.get(i).get(0), 0));
                key.append(",").append(l.get(i).get(0));
            }
            String key1 = key.toString();
//            possibleQualifiers.put(key, 1 + possibleQualifiers.getOrDefault(key, 0));
//            if(!key1.contains("GT".subSequence(0,1))){
//                System.out.println(l);
//            }
            int minScore = (int) l.get(4).get(1);
            for(int i = 0; (int) l.get(i).get(1) > minScore; i++){
                String team = (String) l.get(i).get(0);
                guaranteedQualifiers.put(team, 1 + guaranteedQualifiers.getOrDefault(team, 0));
            }
        }

        List<List<Object>> sortedQualifiers = sortHashMap(teamWiseCount);
        System.out.println("Qualification probabilities (max): ");
        for(List<Object> l1: sortedQualifiers){
            String tabs = ":\t";
            String team = (String) l1.get(0);
            if(team.length() < 3)
                tabs = ":\t\t";
            System.out.println(team + tabs + 100.0*(Integer) l1.get(1)/scenarios.size() + " %");
        }

        sortedQualifiers = sortHashMap(guaranteedQualifiers);
        System.out.println("Qualification probabilities (min): ");
        for(List<Object> l1: sortedQualifiers){
            String tabs = ":\t";
            String team = (String) l1.get(0);
            if(team.length() < 3)
                tabs = ":\t\t";
            System.out.println(team + tabs + 100.0*(Integer) l1.get(1)/scenarios.size() + " %");
        }
    }

    private static List<List<Object>> sortHashMap(HashMap<String, Integer> map){
        List<List<Object>> l = new ArrayList<>();
        for(String key: map.keySet()){
            List<Object> l1 = new ArrayList<>();
            l1.add(key);
            l1.add(map.get(key));
            l.add(l1);
        }
        l.sort(Comparator.comparingInt(o1 -> -((Integer) o1.get(1))));
        return l;
    }

    private static void backtrack(List<HashMap<String, Integer>> scenarios, HashMap<String, Integer> current,
                                  List<List<String>> matchesRemaining, int index){
        if(index == matchesRemaining.size()){
            // no matches left
            scenarios.add((HashMap<String, Integer>) current.clone());
            return;
        }

        String team1 = matchesRemaining.get(index).get(0);
        String team2 = matchesRemaining.get(index).get(1);

        current.put(team1, current.get(team1)+2);
        backtrack(scenarios, current, matchesRemaining, index+1);
        current.put(team1, current.get(team1)-2);

        current.put(team2, current.get(team2)+2);
        backtrack(scenarios, current, matchesRemaining, index+1);
        current.put(team2, current.get(team2)-2);
    }

    private static HashMap<String, Integer> getFromPoints(HashMap<String, List<Integer>> points) {
        HashMap<String, Integer> result = new HashMap<>();
        for(String k: points.keySet()){
            result.put(k, points.get(k).get(5));
        }
        return result;
    }

    private static boolean validateMatchesRemaining(List<List<String>> matchesRemaining, HashMap<String, List<Integer>> points) {
        int totalMatches = 0;
        for(String k: points.keySet()){
            totalMatches += points.get(k).get(0);
        }
        totalMatches /= 2;
        System.out.println("Total Matches so far: " + totalMatches);
        System.out.println("Number of Matches remaining: " + matchesRemaining.size());
        totalMatches += matchesRemaining.size();
        return totalMatches == 45;
    }

    private static String validatePointsTable(HashMap<String, List<Integer>> points) {
        for(String k: points.keySet()){
            List<Integer> list = points.get(k);
            if(list.get(0) != list.get(1)+list.get(2)+list.get(3)+list.get(4)){
                return "Sum of matches won/lost/tied/NR does not match total matches played for the team "+k;
            }
            if(list.get(5) != 2*list.get(1)+list.get(3)+list.get(4)){
                return "Total points is marked wrong for the team "+k;
            }
        }
        return "";
    }
}