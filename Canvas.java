class Canvas {
  PVector center;
  String shape;
  int w;
  int h;
  color col;
  color bc;
  int stroke;
  float maxDistance;

  Canvas(PVector center, String shape, int w, int h, color col, color bc, int stroke) {
    this.center = center;
    this.shape = shape;
    this.w = w;
    this.h = h;
    
    this.col = col;
    this.bc = bc;
    this.stroke = stroke;
    if (shape == "square") {
      this.maxDistance = sqrt(pow(w, 2) + pow(h, 2));
    } else if (shape == "circle") {
      this.maxDistance = max(sqrt(pow(w, 2)),sqrt(pow(h,2)));
    }
  }

  void bounce(Agent a) {
    if (a.collisionCenterDir) { //Angle towards center

      if (shape!="circle") { //Bounce on square

        if (a.pos.x <= center.x - w || a.pos.x>= center.x + w || a.pos.y<= center.y - h || a.pos.y>= center.y + h) {
          a.angle = atan2(a.pos.y - center.y, a.pos.x - center.x) + PI;
          a.bounced = true;
        }
      } else { //Bounce on circle

        if ( !insideEllipse(a) ) { //Out of bounds
          a.angle = atan2(a.pos.y - center.y, a.pos.x - center.x) + PI;
          a.bounced = true;
        }
      }
    } else { //Agent bounces on wall
      if (shape!="circle") {
        if (a.pos.x<= center.x + w || a.pos.x>= center.x - w) {//Vertical wall
          a.angle = PI - a.angle;
          a.bounced = true;
        } else if (a.pos.y<= center.y + h|| a.pos.y>= center.y - h) {//Horizontal wall
          a.angle = -a.angle;
          a.bounced = true;
        }
      } else { //Bounce off sphere
        if ( !insideEllipse(a) ) { //Out of bounds

          PVector agentToCenter = PVector.sub(a.pos, new PVector(center.x, center.y));
          float angleToCenter = atan2(agentToCenter.y, agentToCenter.x);
          float tangentAngle = angleToCenter + PI / 2;
          float angleDiff = a.angle - tangentAngle;
          a.angle = a.angle - 2 * angleDiff;
          float newPositionDistance = maxDistance - a.size;
          a.pos = new PVector(center.x + newPositionDistance * cos(angleToCenter), center.y + newPositionDistance * sin(angleToCenter));
          a.bounced = true;
        }
      }
    }
  }
  boolean insideEllipse(Agent a){
     float t1 = pow((a.pos.x - center.x) / (w/2), 2);
     float t2 = pow((a.pos.y - center.y) / (h/2), 2);
     return t1 + t2 <=1;
  }
  void display(){
    if(shape == "square"){
      rectMode(CENTER);
      fill(bc);
      stroke(col);
      strokeWeight(stroke);
      rect(center.x,center.y,w, h);
    } else if(shape == "circle"){
      fill(bc);
      stroke(col);
      strokeWeight(stroke);
      ellipse(center.x,center.y, w,h);
    }
  }
}