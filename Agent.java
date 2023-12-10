class Agent {
  // CUSTOMIZABLE BELOW:
  float tScale = 15; // "Curvature" of spiral
  float a = 500; // Size of spiral
  // END OF CUSTOMIZABLE

  Canvas canvas;
  String mode;

  boolean randomSpawn;
  float num;
  float total;

  boolean collisionCenterDir;
  boolean spawnCenterDir;
  boolean correctAngle;

  float speed;
  float acc;
  float size;

  PVector pos;
  float angle;

  color contour;
  color[] palette;
  String colorChange;

  color ac;
  color pc;
  int cIndex;
  float diff = 1;
  boolean bounced;

  //ALIGMENT
  boolean vertical;
  boolean horizontal;
  String angles;
  float scalor;

  Agent(Canvas canvas, String mode, boolean randomSpawn, float num, float total, boolean collisionCenterDir, boolean spawnCenterDir, boolean correctAngle, float speed, float acc, float size, String spawn, String detail, int radius, color[] palette, color contour, String colorChange, boolean vertical, boolean horizontal, String angles, float scalor) {
    this.canvas = canvas;
    this.mode = mode;

    this.randomSpawn = randomSpawn;
    this.num = num;
    this.total = total;

    this.spawnCenterDir = spawnCenterDir;
    this.collisionCenterDir = collisionCenterDir;
    this.correctAngle = correctAngle;

    this.speed = speed;
    this.acc = acc;
    this.size = size;

    this.palette = palette;
    this.contour = contour;
    this.colorChange = colorChange;

    this.cIndex = 1;
    this.pc = palette[cIndex-1];
    this.ac = palette[cIndex];

    this.vertical = vertical;
    this.horizontal = horizontal;
    this.angles = angles;
    this.scalor = scalor;

    if (mode == "entropy") {
      if (randomSpawn) {
        this.angle = random(TAU);
      } else {
        this.angle = (num * TAU)/total;
      }

      if (spawn == "center") { // Spawn in center (customizable)

        this.pos = new PVector(width/2, height/2);
      } else if (spawn == "corners") {

        float[] angless = {PI/4, 3*PI/4, 5*PI/4, 7*PI/4};
        boolean[] isInCorner = {false, false, false, false};

        int i = int(random(4));
        if (!randomSpawn) {
          i = int(num)%4;
        } /*else {
         num = (num - num%4)/4;
         total = total/4;
         }*/

        float spawnAngle = angless[i]; //BOT RIGHT
        isInCorner[i] = true;
        float x = width/2 + canvas.maxDistance * cos(spawnAngle);
        float y = height/2 + canvas.maxDistance * sin(spawnAngle);
        this.pos = new PVector(x, y);

        if (correctAngle) { // Correct angle to point towards canvas

          float[][] correctAngles;
          if (canvas.shape == "square") {
            correctAngles = new float[][]{{PI, 3*PI/2}, {3*PI/2, 2*PI}, {0, PI/2}, {PI/2, PI}};
          } else {
            correctAngles = new float[][]{{3*PI/4, 7*PI/4}, {-3*PI/4, PI/4}, {7*PI/4, 11*PI/4}, {PI/4, 5*PI/4}};
          }

          if (isInCorner[0]) { // Bot right: angle to top left
            if (randomSpawn) {
              this.angle = random(correctAngles[0][0], correctAngles[0][1]);
            } else {
              this.angle = correctAngles[0][0] + (num/2*total)*(180/PI);
            }
          } else if (isInCorner[1]) { // Bot left: angle to top right
            if (randomSpawn) {
              this.angle = random(correctAngles[1][0], correctAngles[1][1]);
            } else {
              this.angle = correctAngles[1][0] + (num/2*total)*(180/PI);
            }
          } else if (isInCorner[2]) { // Top left: angle to bot right
            if (randomSpawn) {
              this.angle = random(correctAngles[2][0], correctAngles[2][1]);
            } else {
              this.angle = correctAngles[2][0] + (num/2*total)*(180/PI);
            }
          } else if (isInCorner[3]) { // Top right: angle to bot left
            if (randomSpawn) {
              this.angle = random(correctAngles[3][0], correctAngles[3][1]);
            } else {
              this.angle = correctAngles[3][0] + (num/2*total)*(180/PI);
            }
          }
        }
      } else if (spawn == "edges") { // Spawn on edges
        // Define 4 edge centers
        PVector [] edges = {
          new PVector(0 + canvas.pad, height/2),
          new PVector(width/2, 0+canvas.pad),
          new PVector(width-canvas.pad, height/2),
          new PVector(width/2, height - canvas.pad)
        };
        int i = int(random(4));
        if (!randomSpawn) {
          i = int(num)%4;
        }
        this.pos = edges[i];

        if (correctAngle) { // Correct angle to point towards canvas
          if (this.pos.equals(edges[0])) { // Left edge: angle to the right
            this.angle = random(3*PI/2, 5*PI/2);
            if (randomSpawn) {
              this.angle = random(3*PI/2, 5*PI/2);
            } else {
              this.angle = 3*PI/2 + (num/2*total)*(180/PI);
            }
          } else if (this.pos.equals(edges[1])) { // Top edge: angle to the bot
            this.angle = random(0, PI);
            if (randomSpawn) {
              this.angle = random(0, PI);
            } else {
              this.angle = 0 + (num/2*total)*(180/PI);
            }
          } else if (this.pos.equals(edges[2])) { // Right edge: angle to the left
            this.angle = random(PI/2, 3*PI/2);
            if (randomSpawn) {
              this.angle = random(PI/2, 3*PI/2);
            } else {
              this.angle = PI/2 + (num/2*total)*(180/PI);
            }
          } else if (this.pos.equals(edges[3])) { // Bot edge: angle to the top
            this.angle = random(PI, 2*PI);
            if (randomSpawn) {
              this.angle = random(PI, 2*PI);
            } else {
              this.angle = PI + (num/2*total)*(180/PI);
            }
          }
        }
      }

      if (spawn == "random") { // Random position
        this.pos = new PVector(random(pad, width-pad), random(pad, height-pad));
      } else if (spawn == "spiral") { // Spiral position
        // Equation for spiral: x(t) = a * t * cos(t), y(t) = a * t * sin(t)
        float t = random(1) * tScale;
        //if(!randomSpawn){ //Gradual spawn doesn't work this way?
        //t = num/total * tScale;
        //}
        float offsetX = a * t * cos(t);
        float offsetY = a * t * sin(t);
        float x = width/2 ;
        float y = height/2 ;

        if (random(1)>0.5) {
          x += offsetX;
          y += offsetY;
        } else {
          x -= offsetX;
          y -= offsetY;
        }
        this.pos = new PVector(x, y);
      } else {
        float xfactor = cos(angle);
        float yfactor = sin(angle);

        if (detail == "on") {
          xfactor *= radius;
          yfactor *= radius;
        } else if (detail == "in") {
          xfactor *= random(1) * radius;
          yfactor *= random(1) * radius;
        }
        this.pos.add(new PVector(xfactor, yfactor));
      }

      if (spawnCenterDir) { // Change angle to point to the center
        this.angle = atan2(pos.y - height/2, pos.x - width/2) + PI;
      }
    } else if (mode == "alignment") {
      //Alignment spawn : spawn on a line
      int[] vertBounds = {pad, height-pad};
      int[] horiBounds = {pad, width-pad};

      if (vertical && horizontal) {
        if (random(1)>0.5) {
          vertical = false;
        } else {
          horizontal = false;
        }
      }
      float[] possibleAngles = {0, PI};
      if (vertical) {
        if (randomSpawn) {
          this.pos = new PVector(width/2, random(vertBounds[0], vertBounds[1]));
        } else {
          this.pos = new PVector(width/2, vertBounds[0] + (num/total)*(vertBounds[1] - vertBounds[0]));
        }
      }
      if (horizontal) {
        if (randomSpawn) {
          this.pos = new PVector(random(horiBounds[0], horiBounds[1]), height/2);
        } else {
          this.pos = new PVector(horiBounds[0] + (num/total) * (horiBounds[1]-horiBounds[0]), height/2);
        }
        possibleAngles = new float[]{PI/2, 3*PI/2};
      }
      boolean direction = random(1)>0.5;
      if (!randomSpawn) {
        direction = num%2 ==0;
      }
      boolean direction2 = random(1)>0.5;
      if (!randomSpawn) {
        if (direction) {
          direction2 = num%4==0;
        } else {
          direction2 = (num-1)%4==0;
        }
      }
      if (direction) {
        this.angle = possibleAngles[0];
      } else {
        this.angle = possibleAngles[1];
      }
      float dist = sqrt(pow(pos.x - width/2, 2) + pow(pos.y - height/2, 2));

      if (angles == "sin") {
        if (direction2) {
          this.angle -= (sin(scalor*dist/width)) * TAU;
        } else {
          this.angle += (sin(scalor*dist/width)) * TAU;
        }
      } else if (angles == "cos") {
        if (direction2) {
          this.angle -= (cos(scalor*dist/width)) * TAU;
        } else {
          this.angle += (cos(scalor*dist/width)) * TAU;
        }
      } else if (angles == "log") {
        if (direction2) {
          this.angle += (log(dist))/(scalor);
        } else {
          this.angle-= (log(dist))/(scalor);
        }
      } else if (angles == "exp") {
        if (direction2) {
          //this.angle += pow(dist/scalor, 2)/(canvas.maxDistance);
          this.angle += pow(dist, 2)/(pow(scalor,5)*canvas.maxDistance);

        } else {
          //this.angle -= pow(dist/scalor, 2)/(canvas.maxDistance);
          this.angle -= pow(dist, 2)/(pow(scalor,5)*canvas.maxDistance);
        }
      } else {
        this.angle += random(1) * PI;
      }
    }
  }

  // Update
  void update() {
    // Update position
    PVector dir = new PVector(cos(angle), sin(angle));
    this.pos.add(dir.mult(speed));
    this.speed += this.acc; // Apply acceleration
    float distance = sqrt(pow(pos.x - width/2, 2) + pow(pos.y - height/2, 2));

    if (distance <= canvas.maxDistance+size) {
      canvas.bounce(this);
    } else {
      distance = canvas.maxDistance-size;
    }


    if (colorChange == "bounce" && this.bounced) {//COLOR CHANGE ON BOUNCE
      colorChange();
      this.bounced = false;
    } else if (colorChange == "distance") {
      float fi = map(distance, 0, canvas.maxDistance, 0, palette.length-1); //float index
      int ip = floor(fi);
      this.diff = fi - ip;
      int ia = ip+1;
      //println("phero index : " + ip);
      //println("agent index : " + ia);
      if (ip >= palette.length) {
        ip= palette.length-1;
      }
      if (ia >= palette.length) {
        ia = 0;
      }
      this.pc = palette[ia];
      this.ac = palette[ip];
    }
  }

  void colorChange() {
    this.pc = this.ac;
    this.cIndex ++;
    if (cIndex == palette.length) {
      cIndex = 0;
    }
    this.ac = palette[cIndex];
  }

  // Display
  void show() {
    fill(this.contour);
    noStroke();
    rect(pos.x, pos.y, this.size, this.size);
  }
}
