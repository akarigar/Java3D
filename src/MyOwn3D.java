import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class MyOwn3D {

  private static final float ROOM_SIZE = 3.0f;
  private static final float ROOM_HALF = ROOM_SIZE / 2;
  /**
   * This describes where the camera is located in the room.
   */
  private final Point3d EYE = new Point3d(-ROOM_HALF, 0, 0);

  public MyOwn3D() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setOpaque(false);
    panel.setPreferredSize(new Dimension(512, 512));
    panel.add("Center", createCanvas());

    JFrame frame = new JFrame("MyOwn3D");
    frame.add(panel);
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  private Canvas3D createCanvas() {
    Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
    final SimpleUniverse universe = new SimpleUniverse(canvas3D);
    universe.addBranchGraph(createSceneGraph());

    // Set the camera
    Transform3D viewPlatformTransform = new Transform3D();
    universe.getViewingPlatform().getViewPlatformTransform().getTransform(viewPlatformTransform);
    viewPlatformTransform.lookAt(EYE, new Point3d(0, -ROOM_HALF, 0), new Vector3d(0, 1, 0));
    viewPlatformTransform.invert();
    universe.getViewingPlatform().getViewPlatformTransform().setTransform(viewPlatformTransform);

    return canvas3D;
  }

  private BranchGroup createSceneGraph() {
    TransformGroup group = createAndPopulateRoom();
    group.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    group.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    // Allow rotating the scene with the mouse
    MouseRotate mouseRotate = new MouseRotate();
    mouseRotate.setTransformGroup(group);
    mouseRotate.setSchedulingBounds(new BoundingSphere());

    BranchGroup scene = new BranchGroup();
    scene.addChild(group);
    scene.addChild(mouseRotate);
    scene.compile();
    return scene;
  }

  private TransformGroup createAndPopulateRoom() {
    TransformGroup group = new TransformGroup();

    group.addChild(createRoom());
    group.addChild(createInclinedPlanes());
    group.addChild(createSpheres());
    group.addChild(createCubes());
    group.addChild(createColumn());
    group.addChild(createPoles());

    return group;
  }

  private Room createRoom() {
    Room room = new Room(ROOM_SIZE, ROOM_SIZE, ROOM_SIZE);
    PolygonAttributes attributes = new PolygonAttributes();
    attributes.setCullFace(PolygonAttributes.CULL_NONE);

    // Paint walls
    Appearance wallsAppearance = new Appearance();
    wallsAppearance.setColoringAttributes(
      new ColoringAttributes(new Color3f(1.0f, 0.0f, 0.0f), ColoringAttributes.NICEST)
    );
    wallsAppearance.setPolygonAttributes(attributes);
    room.setAppearance(Room.BACK, wallsAppearance);
    room.setAppearance(Room.FRONT, wallsAppearance);
    room.setAppearance(Room.LEFT, wallsAppearance);
    room.setAppearance(Room.RIGHT, wallsAppearance);

    // Paint floor
    Appearance floorAppearance = new Appearance();
    floorAppearance.setColoringAttributes(
      new ColoringAttributes(new Color3f(0.22f, 0.37f, 0.06f), ColoringAttributes.NICEST)
    );
    floorAppearance.setPolygonAttributes(attributes);
    room.setAppearance(Room.FLOOR, floorAppearance);

    // Paint ceiling
    TransparencyAttributes transparencyAttributes = new TransparencyAttributes(TransparencyAttributes.NICEST, 1.0f);
    Appearance ceilingAppearance = new Appearance();
    ceilingAppearance.setColoringAttributes(
      new ColoringAttributes(new Color3f(1.0f, 1.0f, 1.0f), ColoringAttributes.NICEST)
    );
    ceilingAppearance.setPolygonAttributes(attributes);
    ceilingAppearance.setTransparencyAttributes(transparencyAttributes);
    room.setAppearance(Room.CEILING, ceilingAppearance);

    return room;
  }

  private TransformGroup createInclinedPlanes() {
    TransformGroup root = new TransformGroup();
    float width = 1.0f;
    float height = 1.0f;
    float depth = 0.5f;
    Appearance appearance;
    Transform3D location;
    TransformGroup transformGroup;
    PolygonAttributes attributes = new PolygonAttributes();
    attributes.setCullFace(PolygonAttributes.CULL_NONE);

    // Create a small inclined plane at the back left of the room
    appearance = new Appearance();
    appearance.setColoringAttributes(new ColoringAttributes(new Color3f(0.0f, 1.0f, 0.0f), ColoringAttributes.NICEST));
    appearance.setPolygonAttributes(attributes);
    location = new Transform3D();
    location.setTranslation(new Vector3f(width / 2 - ROOM_HALF, height / 2 - ROOM_HALF, depth / 2 - ROOM_HALF));
    transformGroup = new TransformGroup(location);
    transformGroup.addChild(new InclinedPlane(width, height, depth, appearance));
    root.addChild(transformGroup);

    // Create a bigger inclined plane at the back right of the room
    width = ROOM_SIZE - width;
    depth = 0.75f;
    appearance = new Appearance();
    appearance.setColoringAttributes(new ColoringAttributes(new Color3f(0.0f, 1.0f, 1.0f), ColoringAttributes.NICEST));
    appearance.setPolygonAttributes(attributes);
    location = new Transform3D();
    location.setTranslation(new Vector3f(ROOM_HALF - width / 2, height / 2 - ROOM_HALF, depth / 2 - ROOM_HALF + 0.5f));
    transformGroup = new TransformGroup(location);
    transformGroup.addChild(new InclinedPlane(width, height, depth, appearance));
    root.addChild(transformGroup);

    // Create a box at the back right of the room (behind the inclined plane above)
    width /= 2;
    height /= 2;
    depth = 0.25f;
    location = new Transform3D();
    location.setTranslation(new Vector3f(ROOM_HALF - width, height - ROOM_HALF, depth - ROOM_HALF));
    transformGroup = new TransformGroup(location);
    transformGroup.addChild(new Box(width, height, depth, appearance));
    root.addChild(transformGroup);

    // Create a steep incline at the front right of the room
    depth = 0.5f;
    width = ROOM_HALF - depth / 2;
    height = 2.0f;
    appearance = new Appearance();
    appearance.setColoringAttributes(new ColoringAttributes(new Color3f(0.0f, 0.0f, 1.0f), ColoringAttributes.NICEST));
    appearance.setPolygonAttributes(attributes);
    Transform3D rotation = new Transform3D();
    rotation.rotY(Math.PI);
    TransformGroup rotationTransformGroup = new TransformGroup(rotation);
    location = new Transform3D();
    location.setTranslation(new Vector3f(ROOM_HALF - width / 2, height / 2 - ROOM_HALF, ROOM_HALF - depth / 2));
    transformGroup = new TransformGroup(location);
    transformGroup.addChild(rotationTransformGroup);
    rotationTransformGroup.addChild(new InclinedPlane(width, height, depth, appearance));
    root.addChild(transformGroup);

    return root;
  }

  private TransformGroup createSpheres() {
    TransformGroup root = new TransformGroup();
    float radius = 0.15f;
    Appearance appearance;
    Transform3D location;
    TransformGroup transformGroup;

    // Create a small ball that will be on top of the big inclined plane at the back right of the room
    appearance = new Appearance();
    appearance.setColoringAttributes(new ColoringAttributes(new Color3f(1.0f, 1.0f, 1.0f), ColoringAttributes.NICEST));
    location = new Transform3D();
    location.setTranslation(new Vector3f(ROOM_HALF - 1.0f, 1.0f - ROOM_HALF + radius, radius - ROOM_HALF));
    transformGroup = new TransformGroup(location);
    transformGroup.addChild(new Sphere(radius, appearance));
    root.addChild(transformGroup);

    // Create a larger sphere that will be near the center of the room
    radius *= 2;
    appearance = new Appearance();
    appearance.setColoringAttributes(new ColoringAttributes(new Color3f(1.0f, 1.0f, 0.0f), ColoringAttributes.NICEST));
    location = new Transform3D();
    location.setTranslation(new Vector3f(radius * 2 - ROOM_HALF, radius - ROOM_HALF, 0.0f));
    transformGroup = new TransformGroup(location);
    transformGroup.addChild(new Sphere(radius, appearance));
    root.addChild(transformGroup);

    return root;
  }

  private TransformGroup createColumn() {
    TransformGroup group = new TransformGroup();

    Appearance appearance = new Appearance();
    appearance.setColoringAttributes(new ColoringAttributes(new Color3f(0.3f, 0.3f, 0.3f), ColoringAttributes.NICEST));
    group.addChild(new Box(0.25f, ROOM_SIZE / 2, 0.25f, appearance));

    return group;
  }

  private TransformGroup createCubes() {
    TransformGroup root = new TransformGroup();
    float dim = 0.15f;
    Appearance appearance;
    Transform3D location;
    TransformGroup transformGroup;

    // Create a small cube that will be on top of the big inclined plane & its pole, at the back right of the room
    appearance = new Appearance();
    appearance.setColoringAttributes(new ColoringAttributes(new Color3f(0.0f, 0.0f, 0.0f), ColoringAttributes.NICEST));
    location = new Transform3D();
    location.setTranslation(new Vector3f(ROOM_HALF - dim, ROOM_HALF - dim, dim - ROOM_HALF));
    transformGroup = new TransformGroup(location);
    transformGroup.addChild(new Box(dim, dim, dim, appearance));
    root.addChild(transformGroup);

    // Create a larger cube that will sit at the front-center of the room
    dim = 0.25f;
    appearance = new Appearance();
    appearance.setColoringAttributes(new ColoringAttributes(new Color3f(1.0f, 0.0f, 1.0f), ColoringAttributes.NICEST));
    location = new Transform3D();
    location.setTranslation(new Vector3f(0.0f, dim - ROOM_HALF, ROOM_HALF - dim));
    transformGroup = new TransformGroup(location);
    transformGroup.addChild(new Box(dim, dim, dim, appearance));
    root.addChild(transformGroup);

    return root;
  }

  private TransformGroup createPoles() {
    TransformGroup root = new TransformGroup();
    float radius = 0.1f;
    Transform3D location;
    TransformGroup transformGroup;

    Appearance appearance = new Appearance();
    appearance.setMaterial(
      new Material(
        new Color3f(0.57f, 0.4f, 0.0f),
        new Color3f(0.22f, 0.15f, 0.0f),
        new Color3f(0.22f, 0.15f, 0.0f),
        new Color3f(0.71f, 0.70f, 0.56f),
        0.16f
      ));

    // Create a pole on top of the cube at the front of the room.
    location = new Transform3D();
    location.setTranslation(new Vector3f(0.0f, 0.25f, ROOM_HALF - 0.25f));
    transformGroup = new TransformGroup(location);
    transformGroup.addChild(new Cylinder(radius, ROOM_SIZE - 0.5f, appearance));
    root.addChild(transformGroup);

    // Create a pole on top of the cube at the back right of the room.
    float height = ROOM_SIZE - 1.3f;
    location = new Transform3D();
    location.setTranslation(new Vector3f(ROOM_HALF - 0.15f, height / 2 - ROOM_HALF + 1.0f, 0.15f - ROOM_HALF));
    transformGroup = new TransformGroup(location);
    transformGroup.addChild(new Cylinder(radius, height, appearance));
    root.addChild(transformGroup);

    return root;
  }

  public static void main(String[] args) {
    System.setProperty("sun.awt.noerasebackground", "true");
    new MyOwn3D();
  }
}
