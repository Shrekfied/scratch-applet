import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

class PlayingSound {
  LContext owner;
  
  ScratchSound snd;
  
  Sprite sprite;
  
  SourceDataLine line;
  
  int i;
  
  PlayingSound(ScratchSound paramScratchSound, Sprite paramSprite) {
    this.snd = paramScratchSound;
    this.sprite = paramSprite;
  }
  
  void startPlaying(LContext paramLContext) {
    this.owner = paramLContext;
    this.i = 0;
    if (this.line == null)
      openLine(); 
    writeSomeSamples();
  }
  
  boolean isPlaying() {
    if (this.line == null)
      return false; 
    return (this.line.getFramePosition() < this.snd.samples.length / 2);
  }
  
  void writeSomeSamples() {
    if (this.line == null)
      return; 
    int i = Math.min(this.line.available(), this.snd.samples.length - this.i);
    if (i > 0) {
      if (!this.line.isRunning())
        this.line.start(); 
      int j = this.line.write(this.snd.samples, this.i, i);
      this.i += j;
    } 
  }
  
  void openLine() {
    try {
      AudioFormat audioFormat = new AudioFormat(this.snd.rate, 16, 1, true, true);
      this.line = (SourceDataLine)AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, audioFormat));
      this.line.open(audioFormat);
    } catch (LineUnavailableException lineUnavailableException) {
      this.line = null;
      return;
    } 
    this.line.start();
  }
  
  void closeLine() {
    if (this.line != null) {
      this.line.close();
      this.line = null;
    } 
  }
}
