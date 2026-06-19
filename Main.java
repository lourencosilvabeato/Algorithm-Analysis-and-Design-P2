/* AUTHORS
    Tomás Sousa 68302
    Lourenço Beato 68461
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws IOException {

        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

        int tests = Integer.parseInt(bf.readLine());

        for (int i = 0; i < tests; i++) {
            String[] line1 = bf.readLine().split(" ");
            int rows = Integer.parseInt(line1[0]);
            int cols = Integer.parseInt(line1[1]);

            String[] line2 = bf.readLine().split(" ");
            int corridorCols = Integer.parseInt(line2[0]);
            int corridorLeft = Integer.parseInt(line2[1]);

            int beams = Integer.parseInt(bf.readLine());

            MagicBeams mb = new MagicBeams(rows, cols, corridorCols, corridorLeft, beams);

            for (int j = 0; j < beams; j++) {
                String[] tokens = bf.readLine().split(" ");
                int beamRow = Integer.parseInt(tokens[0]);
                int beamCol = Integer.parseInt(tokens[1]);
                int beamLength = Integer.parseInt(tokens[2]);
                char beamDirection = tokens[3].charAt(0);

                mb.addBeam(beamRow, beamCol, beamLength, beamDirection);
            }

            Queue<Integer> result = mb.getResult();

            if (result == null) {
                System.out.println("Disaster");
            } else if (result.isEmpty()) {
                System.out.println("False alarm");
            } else {
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                while (!result.isEmpty()) {
                    if (!first)
                        sb.append(" ");
                    sb.append(result.remove());
                    first = false;
                }
                System.out.println(sb);
            }

        }

    }
}


