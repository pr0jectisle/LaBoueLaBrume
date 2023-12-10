class Pheromone { //Trail of agents

  PVector pos;
  float strength;
  float decaySpeed;
  float acc;
  String colorChange;
  float diff;
  color rgbA;
  color rgbP;
  color contour;
  float size;
  boolean tokill;
  //Create pheromone
  Pheromone(PVector pos, float pheroDecay, float acc, color rgbA, color rgbP, color contour, String colorChange, float diff, float size) {
    this.pos = pos;
    this.decaySpeed = pheroDecay;
    this.acc = acc;
    this.rgbA = rgbA;
    this.rgbP = rgbP;
    this.contour = contour;
    this.colorChange = colorChange;
    this.diff = diff;
    this.size = size;
    this.strength = 1;
    this.tokill = false;
  }
  void decay() { //Decay pheromone strength
    this.strength += - decaySpeed;
  }
  void show() { //Display

    //Get fade of objects given colours & strength
    color trailColor = lerpColor(rgbA, rgbP, (1-strength));

    if (this.colorChange == "distance") {
      trailColor = lerpColor(rgbA, rgbP, (this.diff));
    }
    if (tokill) {
      trailColor = contour;
    }
    //stroke(255);
    fill(trailColor);
    ellipse(pos.x, pos.y, size, size);
  }
}
