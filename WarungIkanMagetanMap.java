import java.util.Map;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class WarungIkanMagetanMap {
  private static final InputReader in = new InputReader(System.in);
  private static final PrintWriter out = new PrintWriter(System.out);
  private static GraphMap graphMap;
  private static int totalCities; // might change
  private static int totalRoads; // might change
  private static int totalPass; // might change
  private static int[] passCities;
  private static int currPass;

  /**
   * Main method goes here: construct all the objects and call the necessary methods
   * @param args
   */
  public static void main(String[] args) {

    // Initialise the total cities and the total edges / roads
    totalCities = in.nextInteger();
    totalRoads = in.nextInteger();

    // Construct the cities / vertices
    graphMap = new GraphMap(totalCities);

    // Construct the roads / edges
    for (int i = 0; i < totalRoads; i++) {
      int A = in.nextInteger();
      int B = in.nextInteger();
      int dist = in.nextInteger();
      graphMap.addRoad(A, B, dist);
    }

    // DEBUG: Print the map
    // graphMap.printMap();

    // Get the total passwords to be retrieved
    totalPass = in.nextInteger();
    passCities = new int[totalPass];
    for (int i = 0; i < totalPass; i++) {
      passCities[i] = in.nextInteger();
    }
    currPass = 0;

    // Input the activities queries
    int totalQueries = in.nextInteger();
    for (int i = 0; i < totalQueries; i++) {
      char query = in.next().charAt(0);
      switch (query) {
        case 'R':
          /* 
           * TODO: Sofita can probabily go to cities within the range of Energy entried
           * if a particular city is within the range, she can go to that particular city with the energy refilled when arrived
           * if she can arrive at a particular city, she can refill her energy to the maximum (query entried) and possibly go to another city
           * if a particular city is not within the range, she won't go to that city
           * returns the total cities that Sofita can possibly go to
           * else returns -1
           */
          out.println(methodR(in.nextInteger()));
          break;
          case 'F':
          /*
          * TODO: Sofita can go to the city with the entried id
          * returns the minimum distance to the destination city
          */
          out.println(methodF(in.nextInteger()));
          break;
          case 'M':
          /*
          * TODO: Sofita will go to the city with the entried id and solve the password
          * if the password is solved, Sofita will go to the entried city
          * returns the minimum combinations of passCities that Sofita has solved
          * else returns -1
          */
          out.println(methodM(in.nextInteger(), in.nextInteger()));
          break;
          case 'J':
          /*
          * TODO: Sofita will go to the city with the entried id and start connect all the cities with the minimum total distance
          * returns the minimum total distance to connect all the cities
          */
          out.println(methodJ(in.nextInteger()));
          break;
        }
        out.flush();
        graphMap.resetDistances();

        // DEBUG mode
        // out.println(graphMap.getSofita());
      }
    out.close();
  }

  public static int methodR(int energy) {
    return graphMap.energyRangeBoundedwBFS(graphMap.getSofita(), energy);
    // return 0;
  }

  public static long methodF(int cityId) {
    VertexCity sofita = graphMap.getSofita();
    VertexCity city = graphMap.cities.get(cityId - 1);
    return graphMap.dijkstra(sofita, city);
    // return 0;
  }

  public static int methodM(int cityId, int pass) {
    graphMap.setSofita(graphMap.cities.get(cityId - 1));
    return findMinSteps(currPass, pass); 
  }

  public static long methodJ(int cityId) {
    return graphMap.modifiedPrimMST(graphMap.cities.get(cityId - 1));
    // return 0;
  }

  private static int combineNumbers(int num1, int num2) {
      int[] digits1 = new int[4];
      int[] digits2 = new int[4];
      int[] result = new int[4];
      
      for (int i = 3; i >= 0; i--) {
          digits1[i] = num1 % 10;
          digits2[i] = num2 % 10;
          num1 /= 10;
          num2 /= 10;
      }
      
      for (int i = 0; i < 4; i++) {
          result[i] = (digits1[i] + digits2[i]) % 10;
      }
      
      int finalResult = 0;
      for (int i = 0; i < 4; i++) {
          finalResult = finalResult * 10 + result[i];
      }
      
      return finalResult;
  }

  public static int findMinSteps(int currentPassword, int targetPassword) {
      Set<Integer> visited = new HashSet<>();
      Queue<int[]> queue = new LinkedList<>();
      queue.add(new int[]{currentPassword, 0});
      visited.add(currentPassword);
      
      while (!queue.isEmpty()) {
          int[] current = queue.poll();
          int password = current[0];
          int steps = current[1];
          
          if (password == targetPassword) {
            currPass = targetPassword;
            return steps;
          }

          for (int i = 0; i < passCities.length; i++) {
              int newPassword2 = combineNumbers(password, passCities[i]);
              if (!visited.contains(newPassword2)) {
                  visited.add(newPassword2);
                  queue.add(new int[]{newPassword2, steps + 1});
              }
          }
      }
      return -1;
  }

  static class EdgeRoad implements Comparable<EdgeRoad> {
    private VertexCity cityA;
    private VertexCity cityB;
    private int distance;

    public EdgeRoad(VertexCity cityA, VertexCity cityB, int distance) {
      this.cityA = cityA;
      this.cityB = cityB;
      this.distance = distance;
    }

    public VertexCity getCityA() {
      return cityA;
    }

    public VertexCity getCityB() {
      return cityB;
    }

    public int getDistance() {
      return distance;
    }


    @Override
    public String toString() {
      return "City " + cityA.id + " to City " + cityB.id + " with distance " + distance;
    }

    @Override
    public int compareTo(EdgeRoad o) {
      return Integer.compare(this.distance, o.distance);
    }
  }

  static class VertexCity implements Comparable<VertexCity> {
    private int id;
    private List<EdgeRoad> roads;
    private long distance;

    public VertexCity(int id) {
      this.id = id;
      this.roads = new ArrayList<>();
      this.distance = Long.MAX_VALUE;
    }

    public int getId() {
      return id;
    }

    public List<EdgeRoad> getRoads() {
      return roads;
    }

    public long getDistance() {
      return distance;
    }

    public void setDistance(long distance) {
      this.distance = distance;
    }

    public void addRoad(EdgeRoad road) {
      roads.add(road);
    }
    
    @Override
    public int compareTo(VertexCity o) {
      if (this.distance != o.distance) return (int) Long.compare(this.distance, o.distance);
      return Integer.compare(this.id, o.id);
    }

    @Override
    public String toString() {
      return "City " + id;
    }
  }

  static class GraphMap {
    private VertexCity sofita;
    private final List<VertexCity> cities;

    public GraphMap(int totalCities) {
      cities = new ArrayList<>();
      for (int i = 1; i <= totalCities; i++) {
        cities.add(new VertexCity(i));
      }
      sofita = cities.get(0);
    }

    public VertexCity getSofita() {
      return this.sofita;
    }

    public void setSofita(VertexCity sofita) {
      this.sofita = sofita;
    }

    public void addRoad(int A, int B, int dist) {
      VertexCity cityA = cities.get(A - 1);
      VertexCity cityB = cities.get(B - 1);
      EdgeRoad road1 = new EdgeRoad(cityA, cityB, dist);
      EdgeRoad road2 = new EdgeRoad(cityB, cityA, dist);
      cityA.addRoad(road1);
      cityB.addRoad(road2);
    }

    public void printMap() {
      for (VertexCity city : cities) {
        out.println(city + ": ");
        out.flush();
        for (EdgeRoad road : city.getRoads()) {
          out.println(road);
          out.flush();
        }
      }
    }

    void resetDistances() {
      for (VertexCity city : cities) {
        city.setDistance(0);
      }
    }

    public int energyRangeBoundedwBFS(VertexCity start, int energy) {
      final Queue<VertexCity> queue = new LinkedList<>();
      final Map<VertexCity, Integer> visited = new HashMap<>();
      int result = 0;

      queue.add(start);
      visited.put(start, 0);

      while (!queue.isEmpty()) {
        VertexCity city = queue.poll();
        int energyLeft = energy - visited.get(city);
        if (energyLeft < 0) continue;

        for (EdgeRoad road : city.getRoads()) {
          VertexCity nextCity = road.getCityB();
          if ((!visited.containsKey(nextCity) && road.getDistance() <= energy)) {
            visited.put(nextCity, energy);
            queue.add(nextCity);
            result++;
          }
        }
      }
      
      if (result == 0) return -1;
      return result;
    }
    
    public long dijkstra(VertexCity start, VertexCity end) {
      FibonacciHeap<VertexCity> heap = new FibonacciHeap<>();
      Map<VertexCity, FibonacciHeap.Entry<VertexCity>> entries = new HashMap<>();
      for (VertexCity city : cities) {
        city.setDistance(Long.MAX_VALUE);
      }
      start.setDistance(0);
      entries.put(start, heap.insert(start, 0));

      while (!heap.isEmpty()) {
        VertexCity city = heap.deleteMin().getValue();
        for (EdgeRoad road : city.getRoads()) {
          VertexCity nextCity = road.getCityB();
          long newDistance = city.getDistance() + road.getDistance();
          if (newDistance < nextCity.getDistance()) {
            nextCity.setDistance(newDistance);
            if (entries.containsKey(nextCity)) {
              heap.decreaseKey(entries.get(nextCity), newDistance);
            } else {
              entries.put(nextCity, heap.insert(nextCity, newDistance));
            }
          }
        }
      }

      return end.getDistance();
    }

    // This method is already done (properly implemented and working)
    /**
     * Modified Prim's Algorithm using Fibonacci Heap. This method is used to find the minimum spanning tree
     * of the graph. The algorithm is modified to start from a specific vertex and return the total weight of the
     * minimum spanning tree while also including all the graphs from the starting vertex.
     * @param start
     * @return result of modified (all edges from the starting vertex included) minimum spanning tree: long
     */
    public long modifiedPrimMST(VertexCity start) {
      long result = 0;

      FibonacciHeap<VertexCity> heap = new FibonacciHeap<>();
      boolean[] visited = new boolean[cities.size()+1];
      visited[start.getId()] = true;
      Map<VertexCity, FibonacciHeap.Entry<VertexCity>> map = new HashMap<>();

      for (EdgeRoad road : start.getRoads()) {
        result += road.getDistance();
        visited[road.getCityB().getId()] = true;
      }

      for (int i = 1; i <= cities.size(); i++) {
        if (visited[i]) {
          for (EdgeRoad road : cities.get(i-1).getRoads()) {
            if (!visited[road.getCityB().getId()]) {
              map.put(road.getCityB(), heap.insert(road.getCityB(), road.getDistance()));
            }
          }
        }
      }

      while (!heap.isEmpty()) {
        FibonacciHeap.Entry<VertexCity> temp = heap.deleteMin();
        VertexCity city = temp.getValue();
        double distance = temp.getPriority();
        if (!visited[city.getId()]) {
          visited[city.getId()] = true;
          result += distance;
          
          for (EdgeRoad road : city.getRoads()) {
            if (!visited[road.getCityB().getId()]) {
              long newDistance = road.getDistance();
              if (map.containsKey(road.getCityB()) && newDistance < map.get(road.getCityB()).getPriority()) {
                heap.decreaseKey(map.get(road.getCityB()), newDistance);
              } else {
                map.put(road.getCityB(), heap.insert(road.getCityB(), newDistance));
              }
            }
          }
        }
      }

      return result;
    }

    /**
     * TODO: Fix the possible infinite loops and recursions at addition, deletion, and merging of the heap
     */
    static class FibonacciHeap<T> {
      private Entry<T> min = null;
      private int size = 0;

      static class Entry<T> {
        private int degree = 0;
        private boolean marked = false;

        private Entry<T> next;
        private Entry<T> prev;

        private Entry<T> parent;
        private Entry<T> child;

        private T value;
        private double priority;

        public Entry(T value, double priority) {
          this.value = value;
          this.priority = priority;
          this.next = this.prev = this;
        }
      
        public T getValue() {
          return this.value;
        }

        public double getPriority() {
          return this.priority;
        }

        public Map<T, Double> getValuePriority() {
          Map<T, Double> map = new HashMap<>();
          map.put(this.value, this.priority);
          return map;
        }
      }

      public boolean isEmpty() {
        return min == null;
      }

      public Entry<T> min() {
        if (isEmpty()) return null;
        return min;
      }

      public int size() {
        return size;
      }

      public void checkPriority(double priority) {
        if (Double.isNaN(priority)) {
          throw new IllegalArgumentException("Priority is NaN");
        }
      }

      public Entry<T> insert(T value, double priority) {
        checkPriority(priority);

        Entry<T> result = new Entry<>(value, priority);
        min = mergeLists(min, result);
        size++;

        return result;
      }
      
      public Entry<T> deleteMin() {
        if (isEmpty()) return null;
        
        Entry<T> minElem = min;
        this.size--;
        
        if (min.next == min) min = null;
        else {
          min.prev.next = min.next;
          min.next.prev = min.prev;
          min = min.next;
        }
        
        if (minElem.child != null) {
          Entry<?> curr = minElem.child;
          do {
            curr.parent = null;
            curr = curr.next;
          } while (curr != minElem.child);
        }

        min = mergeLists(min, minElem.child);
        
        if (min == null) return minElem;
        
        List<Entry<T>> treeTable = new ArrayList<>();
        List<Entry<T>> toVisit = new ArrayList<>();
        
        for (Entry<T> curr = min; toVisit.isEmpty() || toVisit.get(0) != curr; curr = curr.next) {
          toVisit.add(curr);
        }

        for (Entry<T> curr: toVisit) {
          while (true) {
            while (curr.degree >= treeTable.size()) {
              treeTable.add(null);
            }
            
            if (treeTable.get(curr.degree) == null) {
              treeTable.set(curr.degree, curr);
              break;
            }
            
            Entry<T> other = treeTable.get(curr.degree);
            treeTable.set(curr.degree, null);

            Entry<T> min = (other.priority < curr.priority) ? other : curr;
            Entry<T> max = (other.priority < curr.priority) ? curr : other;
            
            max.next.prev = max.prev;
            max.prev.next = max.next;
            
            max.next = max.prev = max;
            min.child = mergeLists(min.child, max);
            max.parent = min;
            max.marked = false;
            min.degree++;
            curr = min;
          }
          
          if (curr.priority <= min.priority) min = curr;
        }
        return minElem;
      }
      
      public static <T> FibonacciHeap<T> merge(FibonacciHeap<T> one, FibonacciHeap<T> two) {
        FibonacciHeap<T> result = new FibonacciHeap<>();
        result.min = mergeLists(one.min, two.min);
        result.size = one.size + two.size;

        one.size = two.size = 0;
        one.min = null;
        two.min = null;

        return result;
      }

      private static <T> Entry<T> mergeLists(Entry<T> one, Entry<T> two) {
        if (one == null && two == null) return null;
        else if (one != null && two == null) return one;
        else if (one == null && two != null) return two; 

        else {
          Entry<T> oneNext = one.next;
          one.next = two.next;
          one.next.prev = one;
          two.next = oneNext;
          two.next.prev = two;
          return one.priority < two.priority ? one : two;
        }
      }

      public void decreaseKey(Entry<T> entry, double priority) {
        checkPriority(priority);
        if (priority > entry.priority) {
          throw new IllegalArgumentException("New priority is greater than old priority");
        }

        entry.priority = priority;
        if (entry.parent != null && entry.priority <= entry.parent.priority) {
          cutNode(entry);
          // cascadingCut(entry.parent);
        }

        if (entry.priority <= min.priority) min = entry;
      }

      private void cutNode(Entry<T> entry) {
        entry.marked = false;
        if (entry.parent == null) return;

        if (entry.next != entry) {
          entry.next.prev = entry.prev;
          entry.prev.next = entry.next;
        }

        if (entry.parent.child == entry) {
          if (entry.next != entry) {
            entry.parent.child = entry.next;
          } else {
            entry.parent.child = null;
          }
        }

        entry.parent.degree--;
        entry.prev = entry.next = entry;
        min = mergeLists(min, entry);

        if (entry.parent.marked) cutNode(entry.parent);
        else entry.parent.marked = true;

        entry.parent = null;
      }

      public boolean contains(Entry<T> entry) {
        return entry.prev != null && entry.next != null;
      }
    }
  }

  /**
   * Input Reader class for faster IO
   */
  static class InputReader {
    public BufferedReader reader;
    public StringTokenizer tokenizer;
  
    public InputReader(InputStream stream) {
      reader = new BufferedReader(new InputStreamReader(stream), 32768);
      tokenizer = null;
    }
  
    public String next() {
      while (tokenizer == null || !tokenizer.hasMoreTokens()) {
        try {
            tokenizer = new StringTokenizer(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
      return tokenizer.nextToken();
    }
  
    public int nextInteger() {
      return Integer.parseInt(next());
    }
  
    public long nextLong() {
      return Long.parseLong(next());
    }

    public double nextDouble() {
      return Double.parseDouble(next());
    }
  }
}