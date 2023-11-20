public class Color {
    public static void write_color(Vec3 pixel_color) {
        System.out.printf("%i %i %i\n", (int)(255 * pixel_color.x()), (int)(255 * pixel_color.y()), (int)(255 * pixel_color.z()));
    }

}
