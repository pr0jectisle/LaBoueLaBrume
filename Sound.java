import ddf.minim.spi.*;
import ddf.minim.signals.*;
import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.ugens.*;
import ddf.minim.effects.*;

class Sound {

  Minim minim;
  AudioPlayer player;
  FFT fft;

  float smoothingFactor = 0.5;
  float thresholdFactor = 1.2;
  float avgDecay = 0.9999;

  int bands;
  float spectrum [];
  float smoothed [];
  float sum [];
  float avgs[];
  float difSum[];
  float stdDev[];
  ArrayList<float[]> vals = new ArrayList<float[]>();

  boolean[][] act;
  float[] pitchArray;
  boolean[] onsetArray;
  float[] ampArray;
  int fr;

  boolean activated [];
  float pitch;
  boolean onset;
  float amp;
  float currentMax;
  float activation [];
  float curMax[];
  int bandActive = -1;

  boolean started = false;
  float prevAmp = 0.02;
  boolean txtFile;

  Sound(boolean txtFile, String file, int bands, Minim minim, AudioPlayer player) {
    this.bands = bands;
    this.txtFile = txtFile;
    if (txtFile) {
      readfile(file);
      println("Reading .txt file");
    } else {
      println("Reading .mp3 file");
    }
    spectrum = new float[bands];
    smoothed = new float[bands];
    sum = new float[bands];
    avgs = new float[bands];
    difSum = new float[bands];
    stdDev = new float[bands];
    activated = new boolean[bands];
    activation  = new float[bands];
    curMax = new float[bands];


    this.minim = minim;
    this.player = player;
    println("BS : " + player.bufferSize());
    println("SR : " + player.sampleRate());
    fft = new FFT(player.bufferSize(), player.sampleRate());
    //Set num of bands
    fft.linAverages(bands);
    //init arrays
    for (int i=0; i<bands; i++) {
      sum[i] = 0;
      difSum[i] = 0;
      activated[i] = false;
      activation[i] = 0;
    }
    if (!txtFile) {
      player.play();
    }
  }
  
  void readfile(String filePath) {
    String[] lines = loadStrings(filePath);

    int fr = int(lines[0]);
    this.fr = fr;
    int nb = int(lines[1]);
    this.bands = nb;
    int numLines = lines.length-2;
    boolean[][] act = new boolean[numLines][nb];
    float[] pitchArr = new float[numLines];
    boolean[] onsetArr = new boolean[numLines];
    float[] ampArr = new float[numLines];
    
    this.currentMax = 0;
    for (int i = 2; i < lines.length; i++) {
      String[] elements = splitTokens(lines[i]);
     
      for (int j = 0; j < elements.length; j++) {
        if (j<nb) {
          act[i - 2][j] = parseBoolean(elements[j]);
        } else if (j == nb) {
          pitchArr[i-2] = parseFloat(elements[j]);
        } else if (j == nb+1) {
          onsetArr[i-2] = parseBoolean(elements[j]);
        } else if (j == nb+2) {
          float a = parseFloat(elements[j]);
          if(a > this.currentMax){
            this.currentMax = a;
          }
          ampArr[i-2] = a;
        }
      }
      
    }
    this.act = act;
    this.pitchArray = pitchArr;
    this.onsetArray = onsetArr;
    this.ampArray = ampArr;
  }
  void update(int fc) {
    ;
    if (!txtFile) {
      if ((!started) && (player.mix.level() > 3*prevAmp)) {
        started = true;
        println("----------------------------STARTED " + frameCount + "----------------------------");
      }
      //Reset activated
      for (int i=0; i<bands; i++) {
        activated[i] = false;
      }

      // Analyze the audio and update the FFT
      fft.forward(player.mix);

      for (int i=0; i<fft.avgSize(); i++) {
        spectrum[i] = fft.getAvg(i);
        smoothed[i] += (abs(spectrum[i]) - smoothed[i]) * smoothingFactor;
        sum[i] += smoothed[i];
        if (vals.size() > 0) {
          avgs[i] = sum[i] / vals.size();
          difSum[i] += (pow(avgs[i] - smoothed[i], 2));
          stdDev[i] = difSum[i] / vals.size();

          if (smoothed[i] > avgs[i] + thresholdFactor * stdDev[i]) {
            activated[i] = true;
            float act =  smoothed[i] - (avgs[i] + thresholdFactor * stdDev[i]);
            if (act > curMax[i]) {
              curMax[i] = act;
            }
            activation[i] = map(act, 0, curMax[i], 0, 1);
          }
        }
        //Decay avgs
        //sum[i] *= avgDecay;
        difSum[i] *= avgDecay;
      }

      if (started) {
        for (int i=0; i<bands; i++) {
          if (activated[i]) {
            //println(i + ", " + activation[i]);
            this.bandActive = i;
            break;
          }
        }
      }


      //Record data
      vals.add(spectrum);
    } else {
      
      this.activated = act[fc];
      this.pitch = pitchArray[fc];
      this.onset = onsetArray[fc];
      this.amp = ampArray[fc];
      if ((!started) && pitch != 0) { //pitch != 0
        started = true;
        println("----------------------------STARTED " + frameCount + "----------------------------");  
      }
    }
  }
}