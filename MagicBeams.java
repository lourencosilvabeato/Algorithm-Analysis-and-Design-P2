/* AUTHORS
    Tomás Sousa 68302
    Lourenço Beato 68461
 */

import java.util.Queue;
import java.util.PriorityQueue;
import java.util.LinkedList;
import java.util.List;

record Beam(int row, int col, char dir, int id) {
}

public class MagicBeams {

    private static final char NORTH = 'N';
    private static final char EAST = 'E';
    private static final char SOUTH = 'S';
    private static final char WEST = 'W';

    private int[][] map;
    private int cols;
    private int rows;
    private int corridorCols;
    private int corridorLeft;
    private int numBeams;

    private int idx;

    // List that stores all beams that must be released
    private List<Integer> relevantBeams;

    private char[] directions;
    private List<Integer>[] inEdges;
    private List<Integer>[] outEdges;

    @SuppressWarnings("unchecked")
    public MagicBeams(int rows, int cols, int corridorCols, int corridorLeft, int beams) {

        this.map = new int[rows][cols];
        this.rows = rows;
        this.cols = cols;
        this.corridorCols = corridorCols;
        this.corridorLeft = corridorLeft;
        this.numBeams = beams;

        this.idx = 1;
        this.relevantBeams = new LinkedList<>();

        // Beams+1 so that position in list/array matches idx
        this.directions = new char[beams+1];
        this.outEdges = new List[beams+1];
        this.inEdges = new List[beams+1];

        for (int i = 1; i < numBeams+1; i++) {
            outEdges[i] = new LinkedList<>();
            inEdges[i] = new LinkedList<>();

        }

    }

    /* Adds a new beam to the map and registers its direction (found by index)
     */
    public void addBeam(int row, int col, int length, char dir) {

        directions[idx] = dir;

        switch (dir) {
            case NORTH -> {
                for (int i = row; i > row - length; i--)
                    map[i][col] = idx;
            }
            case SOUTH -> {
                for (int i = row; i < row + length; i++)
                    map[i][col] = idx;
            }
            case EAST -> {
                for (int j = col; j < col + length; j++)
                    map[row][j] = idx;
            }
            case WEST -> {
                for (int j = col; j > col - length; j--)
                    map[row][j] = idx;
            }
        }
        idx++;
    }

    /* Builds only the necessary graph for the corridors to be emptied
       Each beam that is found, has its dependencies built
     */
    private void buildGraph() {

        boolean[] found = new boolean[numBeams+1];

        for (int i = 0; i < rows; i++) {
            for (int j = corridorLeft; j < corridorLeft + corridorCols; j++) {

                int currentBeam = map[i][j];
                if (currentBeam != 0 && !found[currentBeam]) {

                    found[currentBeam] = true;
                    relevantBeams.add(currentBeam);
                    buildGraphDependencies(i, j, currentBeam, found);
                }
            }
        }
    }

    /* Connects nodes to each other, uses currentBeam direction to find all beams that are in the way of the current beam.
     All beams that are found are added to the queue and also processed
     */
    private void buildGraphDependencies(int row, int col, int currentBeam, boolean[] found) {

        Queue<Beam> queue = new LinkedList<>();
        queue.add(new Beam(row, col, directions[currentBeam], currentBeam));

        while (!queue.isEmpty()) {
            Beam cur = queue.poll();
            int curRow = cur.row();
            int curCol = cur.col();
            char dir = cur.dir();
            currentBeam = cur.id();

            switch (dir) {
                case NORTH -> {
                    for (int k = curRow - 1; k >= 0; k--) {
                        int nextBeam = map[k][curCol];
                        processBeam(nextBeam, currentBeam, found, queue, k, curCol);
                    }
                }
                case SOUTH -> {
                    for (int k = curRow + 1; k < rows; k++) {
                        int nextBeam = map[k][curCol];
                        processBeam(nextBeam, currentBeam, found, queue, k, curCol);
                    }
                }
                case EAST -> {
                    for (int k = curCol + 1; k < cols; k++) {
                        int nextBeam = map[curRow][k];
                        processBeam(nextBeam, currentBeam, found, queue, curRow, k);
                    }
                }
                case WEST -> {
                    for (int k = curCol - 1; k >= 0; k--) {
                        int nextBeam = map[curRow][k];
                        processBeam(nextBeam, currentBeam, found, queue, curRow, k);
                    }
                }
            }
        }
    }

        // Connects currentBeam to nextBeam
        private void processBeam(int nextBeam, int currentBeam, boolean[] found, Queue<Beam> queue, int i, int j) {

            List<Integer> list = outEdges[currentBeam];

            // IMPORTANT: In a previous submission (number 7 and 8), we attempted to use list.getLast() instead of list.get(list.size()-1),
            // which would have complexity O(1), instead of O(n). Mooshak did not recognize the function,
            // and so we tried to use an ArrayList with get(), which resulted in a Time Exceeded Exception. Therefore, we decided
            // to keep the LinkedList and list.get(list.size()-1) in order to pass the test, but functionally, it should be list.getLast().

            if (nextBeam != currentBeam && nextBeam != 0 && (list.isEmpty() || list.get(list.size()-1) != nextBeam)) {
                outEdges[currentBeam].add(nextBeam);
                inEdges[nextBeam].add(currentBeam);

                if (!found[nextBeam]) {
                    found[nextBeam] = true;
                    relevantBeams.add(nextBeam);
                    queue.add(new Beam(i, j, directions[nextBeam], nextBeam));
                }
            }
        }


    /* Gets a queue with the ordered beams,
       whenever there are several beams that can be freed at the same time, the beam with the lowest identifier is freed first.
       @return null: if there is a cycle (beams cannot be released)
       @return empty queue: if there are no beams in the important columns
       @return queue with ordered beams: otherwise
     */
    public Queue<Integer> getResult() {
        buildGraph();

        Queue<Integer> result = new LinkedList<>();

        if(relevantBeams.isEmpty())
            return result;

        Queue<Integer> current = new PriorityQueue<>();

        int[] outDegree = new int[numBeams+1];
        for (int beam : relevantBeams) {
            outDegree[beam] = outEdges[beam].size();
            if (outEdges[beam].isEmpty()) {
                current.add(beam);
            }
        }

        while(!current.isEmpty()) {
            int min = current.poll();
            result.add(min);

            for(int father : inEdges[min]) {
                outDegree[father]--;
                if(outDegree[father] == 0)
                   current.add(father);
            }
        }

        if(result.size() < relevantBeams.size())
            return null;

        return result;
    }
}


