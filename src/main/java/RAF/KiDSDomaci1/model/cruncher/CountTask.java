package RAF.KiDSDomaci1.model.cruncher;

import RAF.KiDSDomaci1.app.Config;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

public class CountTask extends RecursiveTask<Map<String, Integer>> {

    private final int arity;
    private int start;
    private final int end;
    private final int L = Integer.parseInt(Config.getProperty("counter_data_limit"));
    private final String data;
    private final String name;

    public CountTask(int arity, String data, String name, int start, int end) {
        this.arity = arity;
        this.data = data;
        this.name = name;
        this.start = start;
        this.end = end;

        //System.out.println("Created job: " + start + " | " + end + " ");
    }

    @Override
    protected Map<String, Integer> compute() {
        Map<String, Integer> map = new ConcurrentHashMap<>();
        List<String> words = new ArrayList<>();

        if(end - start < L){
            //data.trim();
            int wordStart = start;
            while (start < end) {
                if (data.charAt(start) == ' ') {
                    words.add(data.substring(wordStart, start));
                    start++;
                    wordStart = start;
                } else {
                    start++;
                }
            }

            List<String> wordsArity = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < words.size() - arity; i += arity) {
                for(int j = arity - 1; j >= 0; j--) {
                    sb.append(words.get(i)).append(" ");
                    i++;
                }
                wordsArity.add(sb.toString());
                sb.setLength(0);
            }

            Map<String, Integer> map2 = new ConcurrentHashMap<>();
            for (String word : wordsArity) {
                Integer i = map2.get(word);
                if (i == null) {
                    map2.put(word, 1);
                } else {
                    map2.put(word, i + 1);
                }
            }
            map2.forEach((k, v) -> map.merge(k, v, (v1, v2) -> v1 + v2));

        } else {
            int mid = ((end - start) / 2) + start;

            CountTask left = new CountTask(arity, data, name, start, mid);
            CountTask right = new CountTask(arity, data, name, mid, end);

            left.fork();
            Map<String, Integer> rightResult = right.compute();
            Map<String, Integer> leftResult = left.join();

            leftResult.forEach((k, v) -> map.merge(k, v, (v1, v2) -> v1 + v2));
            rightResult.forEach((k, v) -> map.merge(k, v, (v1, v2) -> v1 + v2));
        }

        List<Map.Entry<String, Integer>> list2 = new ArrayList<>(map.entrySet());
        list2.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        Map<String, Integer> sorted = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : list2) {
            sorted.put(entry.getKey(), entry.getValue());
        }

        return sorted;
    }
}
