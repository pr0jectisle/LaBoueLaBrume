import ddf.minim.spi.*;
import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;
// CUSTOMIZABLE BELOW: (CHECK AGENT CLASS FOR ADVANCED COLLISION & SPAWN MECHANICS)

// AGENT: agents that move on the canvas

// Number of agents
int numAgents = 1;
// Agent size
float agentSize = 1;
// Agent speed :
float ogSpeed = 1;
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
float ogPheroDecay = 0.25;
float pheroDecay = ogPheroDecay;


// SPAWN: spawn parameters for agents

// spawnAtInit : whether agents will spawn at initialization
boolean spawnAtInit = true;
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
int w = 100;
int h = 100;
color col = color(255, 255, 255);
int stroke = 4;
PVector center;

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
color contour = color(0, 0, 0);

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
int totalLength = 30;          // Length of recording
String folderAddress = "frames/build/"; // Address of folder where frames are saved
String fileName = "frames-";   // Name of PNG file
int digits = 4;                // Digits to add after the name

//MISALIGNMENT
String mode = "alignment";
boolean vertical = true;
boolean horizontal = false;
String angles = "sin";
float scalor =2.5;

//LABOUELABRUME : SOUND SYNC
boolean sync = true;
boolean txtFile = false;
String source = "Carnaval";
String audio = source + ".mp3";
String txt = source + ".txt";

boolean ampSpeed = true;
// END OF CUSTOMIZABLE

Canvas canvas;
ArrayList<Canvas> tempCanvases = new ArrayList<Canvas>();
ArrayList<Canvas> canvases = new ArrayList<Canvas>();
ArrayList<Agent> agents = new ArrayList<Agent>();
ArrayList<Pheromone> pheromones = new ArrayList<Pheromone>();
ArrayList<Agent[][]> agentsSync = new ArrayList<Agent[][]>();

int out = (totalLength-outro)*fr;
int top = (totalLength) * fr;

int bands = 8;
Sound sound;
boolean[] st;
boolean[] bandSpawn;
float speedFac;
boolean display = true;

//Method choreography
boolean choreography(int band) {
  if (band == 0) {
    tempCanvases = new ArrayList<Canvas>();
    mode = "alignment";
    spawn = "corners";
    vertical = true;
    horizontal = true;
    angles = "cos";
    scalor = 5.25;
    collisionCenterDir = true;
    spawnCenterDir = false;
    correctAngle = false;
    palette = new color[]{color(242, 29, 129), color(190, 148, 91), color(82, 132, 60), color(31, 63, 43), color(233, 237, 96)};
    colorChange = "distance";
    shape = "circle";
    w = 200;
    h = 200;
    ogSpeed = 1;
    agentSize = 2;
    radius = 0;
    detail = "on";
    center = new PVector(width/4, height/4);
    canvas = new Canvas(center, shape, w, h, col, bc, stroke);
    tempCanvases.add(canvas);
    canvases.add(canvas);
    center = new PVector(3*width/4, height/4);
    canvas = new Canvas(center, shape, w, h, col, bc, stroke);
    tempCanvases.add(canvas);
    canvases.add(canvas);
    center = new PVector(width/4, 3*height/4);
    canvas = new Canvas(center, shape, w, h, col, bc, stroke);
    tempCanvases.add(canvas);
    canvases.add(canvas);
    center = new PVector(3*width/4, 3*height/4);
    canvas = new Canvas(center, shape, w, h, col, bc, stroke);
    tempCanvases.add(canvas);
    canvases.add(canvas);
    numAgents = 1500;
    return true;
  } else if (band ==1) {
    tempCanvases = new ArrayList<Canvas>();
    mode = "alignment";
    spawn = "center";
    vertical = false;
    horizontal = true;
    angles = "log";
    scalor = 1.25;
    collisionCenterDir = true;
    spawnCenterDir = false;
    correctAngle = false;
    palette = new color[]{color(155, 255, 255), color(100, 100, 100)};
    colorChange = "distance";
    shape = "circle";
    w = 800;
    h = 200;
    ogSpeed = 1;
    agentSize = 2;
    radius = 0;
    detail = "on";
    center = new PVector(width/2, height/2);
    canvas = new Canvas(center, shape, w, h, col, bc, stroke);
    tempCanvases.add(canvas);
    canvases.add(canvas);
    numAgents = 3000;
    return true;
  } else if (band == 2) {
    mode = "entropy";
    spawn = "center";
    radius = 250;
    palette = new color[]{  color(0, 0, 0), color(65, 65, 65), color(150, 150, 150)};
    collisionCenterDir = true;
    spawnCenterDir = false;
    correctAngle = true;
    ogSpeed = 5;
    agentSize = 1;
    colorChange = "distance";
    shape = "circle";
    canvas = new Canvas(center, shape, w, h, col, bc, stroke);
    return false;
  } else {
    return false;
  }
}

// Method spawn: method to spawn generation of agents given agent number, size, etc...
void spawn(int band) {
  // Spawn new agents
  for (int i = 0; i < numAgents; i++) {
    if (!sync) {
      for (int j=0; j<tempCanvases.size(); j++) {
        agents.add(new Agent(tempCanvases.get(j), mode, randomSpawn, i, numAgents, collisionCenterDir, spawnCenterDir, correctAngle, ogSpeed, acc, agentSize, spawn, detail, radius, palette, contour, colorChange, vertical, horizontal, angles, scalor, ampSpeed));
      }
    } else {
      for (int j=0; j<tempCanvases.size(); j++) {
        agentsSync.get(band)[i][j] = new Agent(tempCanvases.get(j), mode, randomSpawn, i, numAgents, collisionCenterDir, spawnCenterDir, correctAngle, ogSpeed, acc, agentSize, spawn, detail, radius, palette, contour, colorChange, vertical, horizontal, angles, scalor, ampSpeed);
      }
    }
  }
}

// Simulation setup
void setup() {
  if (recording && sync && !txtFile) {
    sync = false;
  }
  col = bc;
  size(800, 800);
  //center = new PVector(width/3,height/3);
  //canvas = new Canvas(center,shape, w, h, col, bc, stroke);
  if (spawnAtInit && !sync) {
    if (choreography(0)) {
      spawn(-1);
    }
    if (choreography(1)) {
      spawn(-1);
    }
  }
  println("Welcome");
  printRules();
  if (sync) {
    Minim minim = new Minim(this);
    AudioPlayer player = minim.loadFile(audio);
    String input = audio;
    if (txtFile) {
      input = txt;
    }
    sound = new Sound(txtFile, input, bands, minim, player);
    if (txtFile) {
      bands = sound.bands;
      this.fr = sound.fr;
      out = sound.ampArray.length-1 - (outro*fr);
      top = sound.ampArray.length-1;
    }
    for (int i=0; i<bands; i++) {
      if (choreography(i)) {
        agentsSync.add(new Agent[numAgents][tempCanvases.size()]);
      }
    }
    st = new boolean[agentsSync.size()];

    for (int i=0; i<st.length; i++) {
      st[i] = false;
    }
  }
}

// draw method: draws iteratively
void draw() {
  if (recording && !sync && frameCount == int(intro * fr)) { // Spawn setup for recording
    choreography(0);
    spawn(0);
  }
  if (frameCount%50 == 0) {
    println("Iter: " + frameCount);
  }
  // Background color
  background(bc);
  for (Canvas c : canvases) {
    c.display();
  }

  if (sync) {
    sound.update(frameCount);
    if (!txtFile) {
      speedFac = sound.player.mix.level();
    } else {
      speedFac = sound.amp;
    }
    if (txtFile) {
      speedFac = pow(speedFac, 2) * 100000 * 2.5;
      if (sound.pitch != 0) {
        speedFac = max(speedFac, 0.1);
      }
    } else {
      speedFac = pow(speedFac, 3) * 3000;
    }

    if (speedFac >= 1) {
      print("|");
      for (int i=0; i<sound.activated.length; i++) {
        if (sound.activated[i]) {
          print("1");
        } else {
          print("0");
        }
      }
      print("| ");
      print(sound.pitch + " Hz, ");
      print(sound.onset);
      print(" " + speedFac);
      println("");
    }

    for (int i=0; i<st.length; i++) {
      if (!st[i]) {
        if (sound.started) {
          choreography(i);
          spawn(i);
          st[i] = true;
        }
      }
    }
  }


  // Move agents, Display agents & add pheromones


  if (!sync) { //Normal agent arraylist
    for (int i = agents.size()-1; i>= 0; i--) {
      Agent a = agents.get(i);
      pheromones.add(new Pheromone(a.pos.copy(), pheroDecay, a.acc, a.ac, a.pc, a.contour, a.colorChange, a.diff, a.size));
      a.update();
      a.show();
    }
  } else { //Sync agent arraylist
    for (int i=0; i<st.length; i++) {
      if (st[i]) {

        for (int j=agentsSync.get(i).length-1; j>=0; j--) { //Num agents
          for (int k=agentsSync.get(i)[0].length-1; k>=0; k--) {

            Agent a = agentsSync.get(i)[j][k];
            if (a.ampSpeed) {
              a.speed = a.ogSpeed * speedFac;
              pheroDecay = max(ogPheroDecay, ogPheroDecay * speedFac);
            }

            if (display) {
              pheromones.add(new Pheromone(a.pos.copy(), pheroDecay, a.acc, a.ac, a.pc, a.contour, a.colorChange, a.diff, a.size));
              a.update();
              a.show();
            }
          }
        }
      }
    }
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

    //if (frameCount < out || (frameCount > out && frameCount < top)) {
    //println("BELOW/AFTER OUT");
    int fc = frameCount;
    String a = folderAddress + fileName + nf(fc, digits) + ".png";
    saveFrame(a);
    if (frameCount == out) {
      if (!sync) {
        agents = new ArrayList<Agent>();
      } else {
        this.display = false;
      }
    } else if (frameCount == top) {
      println("ENDING : " + frameCount);
      exit();
      println("Use this FFmpeg command to stitch frames together : ");
      String regex = "%0" + digits + "d";
      //ffmpeg -framerate 60 -i frames-%04d.png -i audio.mp3 -c:v libx264 -pix_fmt yuv420p output.mp4
      String aud = "";
      if (txtFile) {
        aud = "-i " + audio + " ";
      }
      println("FFmpeg -framerate " + fr + " -i " + fileName + regex + ".png " + aud +"-c:v libx264 -pix_fmt yuv420p output.mp4");
    }
  }
}
void mouseClicked() {
  if (!recording && !sync) {
    if (mouseButton != RIGHT) { //Left click
      if (clickType) {//spawn

        println("Spawning");
        int x = mouseX;
        int y = mouseY;
        //Check if mouse is in corners, edges or center
        boolean tl = (x >= 0 && x<= width/3) && (y>= 0 && y<= height/3);
        boolean tr = (x >= 2*width/3 && x<= width) && (y>= 0 && y<= height/3);
        boolean bl = (x >= 0 && x<= width/3) && (y>= 2*height/3 && y<= height);
        boolean br = (x >= 2*width/3 && x<= width) && (y>= 2*height/3 && y<= height);
        boolean corners = tl || tr || bl || br;
        //Stitch statements together
        boolean center = (x>= width/3 && x <= 2*width/3) && (y>= width/3 && y<= 2*width/3);
        if (corners) { //Set spawn accordingly
          spawn = "corners";
        } else if (center) {
          spawn = "center";
        } else {
          spawn = "edges";
        }
        spawn(-1);   //Spawn
      } else { //Attract
        println("Attracting");
        for (Agent a : agents) { //Change agent angle to point at point of click
          a.angle = atan2(a.pos.y - mouseY, a.pos.x - mouseX) + PI;
        }
      }
    } else { //Right click
      if (clickType) {
        agents = new ArrayList<Agent>();
      } else {//Push away
        println("Repulsing");
        for (Agent a : agents) { //Change agent angle to point away from point of click
          a.angle = atan2(a.pos.y - mouseY, a.pos.x - mouseX);
        }
      }
    }
  } else {
    println("Software is recording / in sync, please do not disturb");
  }
}

void keyPressed() {
  if (!recording &&!sync) {
    if (keyCode == ENTER) { //Change click type
      clickType = !clickType;
      printType(clickType);
    } else if (keyCode == 32) {//Turn around
      println("turning around");
      for (Agent a : agents) {
        a.angle += PI;
      }
      spawn = "random";
    } else if (keyCode == BACKSPACE) { //Purge all agents
      agents = new ArrayList<Agent>();
    } else if (key == 's') { //Spiral spawn
      spawn = "spiral";
      spawn(-1);
    } else if (key=='e') { //Edges spawn
      spawn = "edges";
      spawn(-1);
    } else if (key=='c') { //Corner spawn
      spawn = "corners";
      spawn(-1);
    } else if (key=='o') { //Center spawn
      spawn = "center";
      spawn(-1);
    } else if (key =='p') {
      spawn="center";
      spawn(-1);
      spawn = "edges" ;
      spawn(-1);
      spawn = "corners";
    }
  } else {
    println("Software is recording / in sync, please do not disturb");
  }
}

//Print info
void printType(boolean clickType) {
  print("Current click type : ");
  if (clickType) {
    println("SPAWN/DESPAWN");
  } else {
    println("ATTRACT/REPULSE");
  }
}

// Print info
void printRules() {
  if (!recording) {
    if (!sync) {
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
    } else {
      if(!txtFile){
        println("Software is reacting live to audio : " + audio);
        println("Sit back & enjoy");
      } else {
        println("Software is reacting live to txt file : " + txt);
      }
    }
  } else {
    print("Software is recording animation ");
    if (sync) {
      if (!txtFile) {
        print( "synced with audio : " + audio);
      } else {
        print("based on text file : " + txt);
      }
    }
    println(" into folder : " + folderAddress);
    println("Please wait for animation to finish. " + top + " frames to record");
  }
}
