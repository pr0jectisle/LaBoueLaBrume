// CUSTOMIZABLE BELOW: (CHECK AGENT CLASS FOR ADVANCED COLLISION & SPAWN MECHANICS)

// AGENT: agents that move on the canvas

// Number of agents
int numAgents = 2000;
// Agent size
float agentSize = 5;
// Agent speed :
float speed = 3;
// Agent acceleration
float acc = 0.00;

//Colision direction : whether agents points towards center after collision with wall
boolean collisionCenterDir = true;
//Center direction : whether spawn direction is aimed at center
boolean spawnCenterDir = false;
//Correct angle : whether to correct spawn angle to be directed within the grid (for "corners" and "edges" spawns)
boolean correctAngle = true;


// PHEROMONES: trail left behind by agents

// Phero threshold : min value for a pheromone to exist. (CUT TRAIL OFF)
float pheroThreshold = 0.2;
// Phero decay speed : speed at which pheromones (trails) will fade (SHORTEN TRAIL)
float pheroDecay = 0.25;


// SPAWN: spawn parameters for agents

// spawnAtInit : whether agents will spawn at initialization
boolean spawnAtInit = false;
//randomSpawn : whether agents are spawned at random spots on the spawn point or gradual spots
boolean randomSpawn = false;
/* spawn: specify the spawning point of agents:
 - center: agents will spawn at center of the screen
 - corners: agents will spawn at corners of the screen
 - edges: agents will spawn at edges of the screen
 - random: agents will spawn at random places
 - spiral: agents will spawn on a spiral (see agent class CUSTOMIZABLE to customize spiral) */
String spawn = "corners";
/* detail: "on" agents spawn on the edge of the circle defined by radius in the center
        "in" agents spawn in the circle defined by radius (random pos in circle)
        "off" agents spawn in the center of the environment */
String detail = "off";
// Radius: radius of circle spawn
int radius = 100;



//CANVAS : define canvas in which agents move & bounce
 
//shape : shape of canvas : "square" or "circle"
String shape = "circle";
//pad : padding from the edge of the window to the edge of the canvas
int pad = 100;

// DISPLAY

// heads: indicate whether head of the trail is displayed on top (true) or below (false) the tail of the trail.
boolean heads = false;

// COLORS

//background colour
color bc = color(0, 0, 0);    
//palette : palette of colours the agents will have
color[] palette = {color(249, 0, 99), color(229, 78, 208), color(159, 69, 176), color(68, 0, 139), color(0, 7, 111)}; //Galaxy //color(249,0,99),color(255,228,242)
/*colorChange : how agents choose colours whithin palette
  - bounce : agents will change colors on bounce with canvas
  - distance : agents will change colors based on distance from center
  - else : agents will not change colors
  */
String colorChange = "bounce";
//contour : color of front & end of trail
color contour = color(0,0,0);

// CLICK

/* Click type: what program does when clicking (left-click / right-click)
 - true: spawn new gen / delete old gen
 - false: attract agents / push agents away from point of click
 */
boolean clickType = true;

// RECORDING: recording parameters for saving frames

boolean recording = false;     // Whether to record simulation
int fr = 60;                   // Frame rate to record at
int intro = 1;                 // Length of intro (seconds) (== background screen without agents)
int outro = 1;                 // Length of outro (seconds) (== background screen without agents)
int totalLength = 15;          // Length of recording
String folderAddress = "frames/build/"; // Address of folder where frames are saved
String fileName = "frames-";   // Name of PNG file
int digits = 4;                // Digits to add after the name

//MISALIGNMENT
String mode = "alignment";
boolean vertical = true;
boolean horizontal = false;
String angles = "sin";
float scalor =2.5;

// END OF CUSTOMIZABLE

Canvas canvas;
ArrayList<Agent> agents = new ArrayList<Agent>();
ArrayList<Pheromone> pheromones = new ArrayList<Pheromone>();

int age = 0;
int lowestAge = 0;

// Method spawn: method to spawn generation of agents given agent number, size, etc...
void spawn() {
  // Spawn new agents
  for (int i = 0; i < numAgents; i++) {
    agents.add(new Agent(canvas, mode, randomSpawn, i, numAgents, collisionCenterDir, spawnCenterDir, correctAngle, speed, acc, agentSize, spawn, detail, radius, palette, contour, colorChange, vertical, horizontal, angles, scalor));
  }
  age++; // Increase age
}

// Simulation setup
void setup() {
  canvas = new Canvas(shape, pad);
  size(800, 800);
  noStroke();
  rectMode(CENTER);
  if (spawnAtInit) {
    spawn();
  }
  println("Welcome");
  printRules();
  printType(clickType);
  if (recording) {
    frameRate(fr);
  }
}

// draw method: draws iteratively
void draw() {
  if (recording && frameCount == int(intro * fr)) { // Spawn setup for recording
    spawn = "corners";
    spawn();
  }
  if (frameCount%50 == 0) {
    println("Iter: " + frameCount);
  }
  // Background color
  background(bc);

  // Move agents, Display agents & add pheromones
  for (int i = 0; i < agents.size(); i++) {
    Agent a = agents.get(i);
    pheromones.add(new Pheromone(a.pos.copy(), pheroDecay, a.acc, a.ac, a.pc,a.contour,a.colorChange, a.diff, a.size));
    a.update();
    a.show();
  }

  // Display pheromones
  if (heads == true) { // Display "head" of agent on top of "tail"
    for (Pheromone p : pheromones) {
      p.show();
      p.decay();
    }
  }

  // Clean pheromones array
  for (int j = pheromones.size() - 1; j >= 0; j--) {
    Pheromone p = pheromones.get(j);
    if (heads == false) { // Display "tail" of agent on top of "head"
      p.show();
      p.decay();
    }

    if (p.tokill) {
      pheromones.remove(j);
    }
    //Remove pheromones under threshold
    if (p.strength <= pheroThreshold) {
      p.tokill = true;
    }
  }

  if (recording) {
    String a = folderAddress + fileName + nf(frameCount, digits) + ".png";
    saveFrame(a);
    if (frameCount > int(fr * (totalLength - outro))) { // Make outro: purge all agents
      agents = new ArrayList<Agent>();
    }
    if (frameCount > fr * totalLength) { // Exit after outro
      exit();
      println("Use this FFmpeg command to stitch frames together : ");
      String regex = "%0" + digits + "d";
      println("FFmpeg -framerate " + fr + " -i " + fileName + regex + ".png -c:v libx264 -pix_fmt yuv420p output.mp4");
    }
  }
}

void mouseClicked() {
  if (mouseButton == LEFT) { // Left click
    if (clickType) { // Spawn
      println("Spawning");
      int x = mouseX;
      int y = mouseY;
      // Check if mouse is in corners, edges, or center
      boolean tl = (x >= 0 && x <= width / 3) && (y >= 0 && y <= height / 3);
      boolean tr = (x >= 2 * width / 3 && x <= width) && (y >= 0 && y <= height / 3);
      boolean bl = (x >= 0 && x <= width / 3) && (y >= 2 * height / 3 && y <= height);
      boolean br = (x >= 2 * width / 3 && x <= width) && (y >= 2 * height / 3 && y <= height);
      boolean corners = tl || tr || bl || br;
      // Stitch statements together
      boolean center = (x >= width / 3 && x <= 2 * width / 3) && (y >= width / 3 && y <= 2 * width / 3);
      if (corners) { // Set spawn accordingly
        spawn = "corners";
      } else if (center) {
        spawn = "center";
      } else {
        spawn = "edges";
      }
      spawn(); // Spawn
    } else { // Attract
      println("Attracting");
      for (Agent a : agents) { // Change agent angle to point at the point of click
        a.angle = atan2(a.pos.y - mouseY, a.pos.x - mouseX) + PI;
      }
    }
  } else { // Right click
    if (clickType) { //Purge
      agents = new ArrayList<Agent>();
    } else { // Push away
      println("Repulsing");
      for (Agent a : agents) { // Change agent angle to point away from the point of click
        a.angle = atan2(a.pos.y - mouseY, a.pos.x - mouseX);
      }
    }
  }
}

void keyPressed() {
  if (keyCode == ENTER) { // Change click type
    clickType = !clickType;
    printType(clickType);
  } else if (keyCode == 32) { // Turn around
    println("Turning around");
    for (Agent a : agents) {
      a.angle += PI;
    }
    spawn = "random";
  } else if (key == 's') { // Spiral spawn
    spawn = "spiral";
    spawn();
  } else if (key == 'e') { // Edges spawn
    spawn = "edges";
    spawn();
  } else if (key == 'c') { // Corner spawn
    spawn = "corners";
    spawn();
  } else if (key == 'o') { // Center spawn
    spawn = "center";
    spawn();
  }
}

// Print info
void printType(boolean clickType) {
  print("Current click type: ");
  if (clickType) {
    println("SPAWN/DESPAWN");
  } else {
    println("ATTRACT/REPULSE");
  }
}

// Print info
void printRules() {
  println("Mode SPAWN/DESPAWN:");
  println("Left click: spawn " + numAgents + " agents");
  println("Right click: delete agents");
  println("");
  println("Mode ATTRACT/REPULSE");
  println("Left click: agents will change direction towards the point of click");
  println("Right click: agents will change direction away from the point of click");
  println("");
  println("Press ENTER to change click type");
  println("Press S to spawn in spiral configuration");
  println("Press E to spawn in edges");
  println("Press C to spawn in corners");
  println("Press O to spawn in center");
  println("Press SPACEBAR turn around all agents");

  println("");
}
