import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class ModelLoader {
    private File file;
    private Vec3[] vertices;
    private Vec3[] vertexNormals;
    private int[][] faceElements;

    public ModelLoader(File file) {
        this.file = file;
        try {
            Scanner scanner = new Scanner(file);
            ArrayList<Vec3> vertices = new ArrayList<>();
            ArrayList<Vec3> vertexNormals = new ArrayList<>();
            ArrayList<int[]> faceElements = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("v ")) {
                    String[] values = line.substring(line.indexOf(" ") + 1).split(" ");
                    vertices.add(new Vec3(Double.parseDouble(values[0]), Double.parseDouble(values[1]), Double.parseDouble(values[2])));
                } 
            }
            scanner.close();

            this.vertices = new Vec3[vertices.size()];
            for (int i = 0; i < this.vertices.length; i++) {
                this.vertices[i] = vertices.get(i);
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

    public Vec3[] getVertices() {
        return this.vertices;
    }
}
