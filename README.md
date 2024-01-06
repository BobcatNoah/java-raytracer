# java-raytracer
A work in progress java raytracer based off the Ray Tracing in One Weekend guide.
https://raytracing.github.io/books/RayTracingInOneWeekend.html

Can be run with on Macos
```
javac -cp ".:obj-0.4.0.jar" *.java
java -cp ".:obj-0.4.0.jar" Main > image.ppm
```
I'm putting a pause on multithreading due to issues. I'll continue working on mulithreading later.
For now, just don't use multithreading.

If you're on windows, make sure to run this in CMD, otherwise the ppm file won't be viewable.
