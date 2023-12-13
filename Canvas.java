class Canvas {
  String shape;
  int pad;
  color col;
  color bc;
  int stroke;
  float maxDistance;

  Canvas(String shape, int pad, color col, color bc, int stroke) {
    this.shape = shape;
    this.pad = pad;
    this.col = col;
    this.bc = bc;
    this.stroke = stroke;
    if (shape == "square") {
      this.maxDistance = sqrt(pow(width/2 - pad, 2) + pow(height/2 - pad, 2));
    } else if (shape == "circle") {
      this.maxDistance = sqrt(pow(pad - width/2, 2));
    }
  }

  void bounce(Agent a) {
    if (a.collisionCenterDir) { //Angle towards center

      if (shape!="circle") { //Bounce on square

        if (a.pos.x <= 0 + pad || a.pos.x>=width - pad || a.pos.y<=0 + pad || a.pos.y>= height - pad) {
          a.angle = atan2(a.pos.y - height/2, a.pos.x - width/2) + PI;
          a.bounced = true;
        }
      } else { //Bounce on circle (w radius == width)

        if ( sqrt(pow((a.pos.x - width/2), 2) + pow((a.pos.y - height/2), 2)) > (width/2) - pad) { //Out of bounds
          a.angle = atan2(a.pos.y - height/2, a.pos.x - width/2) + PI;
          a.bounced = true;
        }
      }
    } else { //Agent bounces on wall
      if (shape!="circle") {
        if (a.pos.x<=0 + pad || a.pos.x>= width - pad) {//Vertical wall
          a.angle = PI - a.angle;
          a.bounced = true;
        } else if (a.pos.y<= 0  + pad|| a.pos.y>= height - pad) {//Horizontal wall
          a.angle = -a.angle;
          a.bounced = true;
        }
      } else { //Bounce off sphere
        if (sqrt(pow(a.pos.x - width/2, 2) + pow(a.pos.y - height/2, 2)) >= maxDistance ) { //Out of bounds

          PVector agentToCenter = PVector.sub(a.pos, new PVector(width/2 - pad, height/2 - pad));
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
  
  void display(){
    if(shape == "square"){
      rectMode(CENTER);
      fill(bc);
      stroke(col);
      strokeWeight(stroke);
      rect(width/2,height/2,width - (2*pad), height - (2*pad));
    } else if(shape == "circle"){
      fill(bc);
      stroke(col);
      strokeWeight(stroke);
      ellipse(width/2,height/2, width - (2*pad), height - (2*pad));
    }
  }
}