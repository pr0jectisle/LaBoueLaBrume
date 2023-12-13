class Canvas {
  String shape;
  int xpad;
  int ypad;
  color col;
  color bc;
  int stroke;
  float maxDistance;

  Canvas(String shape, int xpad, int ypad, color col, color bc, int stroke) {
    this.shape = shape;
    this.xpad = xpad;
    this.ypad = ypad;
    this.col = col;
    this.bc = bc;
    this.stroke = stroke;
    if (shape == "square") {
      this.maxDistance = sqrt(pow(width/2 - xpad, 2) + pow(height/2 - ypad, 2));
    } else if (shape == "circle") {
      this.maxDistance = max(sqrt(pow(xpad - width/2, 2)),sqrt(pow(ypad-height/2,2)));
    }
  }

  void bounce(Agent a) {
    if (a.collisionCenterDir) { //Angle towards center

      if (shape!="circle") { //Bounce on square

        if (a.pos.x <= 0 + xpad || a.pos.x>=width - xpad || a.pos.y<=0 + ypad || a.pos.y>= height - ypad) {
          a.angle = atan2(a.pos.y - height/2, a.pos.x - width/2) + PI;
          a.bounced = true;
        }
      } else { //Bounce on circle

        if ( !insideEllipse(a) ) { //Out of bounds
          a.angle = atan2(a.pos.y - height/2, a.pos.x - width/2) + PI;
          a.bounced = true;
        }
      }
    } else { //Agent bounces on wall
      if (shape!="circle") {
        if (a.pos.x<=0 + xpad || a.pos.x>= width - xpad) {//Vertical wall
          a.angle = PI - a.angle;
          a.bounced = true;
        } else if (a.pos.y<= 0  + ypad|| a.pos.y>= height - ypad) {//Horizontal wall
          a.angle = -a.angle;
          a.bounced = true;
        }
      } else { //Bounce off sphere
        if ( !insideEllipse(a) ) { //Out of bounds

          PVector agentToCenter = PVector.sub(a.pos, new PVector(width/2 - xpad, height/2 - ypad));
          float angleToCenter = atan2(agentToCenter.y, agentToCenter.x);
          float tangentAngle = angleToCenter + PI / 2;
          float angleDiff = a.angle - tangentAngle;
          a.angle = a.angle - 2 * angleDiff;
          float newPositionDistance = maxDistance - a.size;
          a.pos = new PVector(width/2 + newPositionDistance * cos(angleToCenter), height/2 + newPositionDistance * sin(angleToCenter));
          a.bounced = true;
        }
      }
    }
  }
  boolean insideEllipse(Agent a){
     float t1 = pow((a.pos.x - width/2) / (width/2 - xpad), 2);
     float t2 = pow((a.pos.y - height/2) / (height/2 -ypad), 2);
     return t1 + t2 <=1;
  }
  void display(){
    if(shape == "square"){
      rectMode(CENTER);
      fill(bc);
      stroke(col);
      strokeWeight(stroke);
      rect(width/2,height/2,width - (2*xpad), height - (2*ypad));
    } else if(shape == "circle"){
      fill(bc);
      stroke(col);
      strokeWeight(stroke);
      ellipse(width/2,height/2, width - (2*xpad), height - (2*ypad));
    }
  }
}