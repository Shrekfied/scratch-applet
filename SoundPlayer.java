import java.util.Vector;

class SoundPlayer implements Runnable {
  static Vector activeSounds = new Vector();
  
  static Thread sndThread;
  
  static synchronized Object startSound(Object paramObject, Sprite paramSprite, LContext paramLContext) {
    if (!(paramObject instanceof ScratchSound)) {
      Logo.error("argument of startSound must be a ScratchSound", paramLContext);
      return new Object[0];
    } 
    Object[] arrayOfObject = activeSounds.toArray();
    for (byte b = 0; b < arrayOfObject.length; b++) {
      PlayingSound playingSound1 = (PlayingSound)arrayOfObject[b];
      if (playingSound1.snd == (ScratchSound)paramObject && playingSound1.sprite == paramSprite) {
        playingSound1.closeLine();
        activeSounds.remove(playingSound1);
      } 
    } 
    PlayingSound playingSound = new PlayingSound((ScratchSound)paramObject, paramSprite);
    playingSound.startPlaying(paramLContext);
    activeSounds.add(playingSound);
    return playingSound;
  }
  
  static synchronized boolean isSoundPlaying(Object paramObject) {
    if (!(paramObject instanceof PlayingSound))
      return false; 
    return ((PlayingSound)paramObject).isPlaying();
  }
  
  static synchronized void stopSound(Object paramObject) {
    if (!(paramObject instanceof PlayingSound))
      return; 
    ((PlayingSound)paramObject).closeLine();
    activeSounds.remove(paramObject);
  }
  
  static synchronized void stopSoundsForApplet(LContext paramLContext) {
    PlayerPrims.stopMIDINotes();
    Vector vector = new Vector();
    for (PlayingSound playingSound : activeSounds) {
      if (playingSound.owner == paramLContext) {
        playingSound.closeLine();
        continue;
      } 
      vector.addElement(playingSound);
    } 
    activeSounds = vector;
  }
  
  static synchronized void updateActiveSounds() {
    Vector vector = new Vector();
    for (PlayingSound playingSound : activeSounds) {
      if (playingSound.isPlaying()) {
        playingSound.writeSomeSamples();
        vector.addElement(playingSound);
        continue;
      } 
      playingSound.closeLine();
    } 
    activeSounds = vector;
  }
  
  static synchronized void startPlayer() {
    sndThread = new Thread(new SoundPlayer(), "SoundPlayer");
    sndThread.setPriority(10);
    sndThread.start();
  }
  
  public void run() {
    Thread thread = Thread.currentThread();
    while (sndThread == thread) {
      updateActiveSounds();
      try {
        Thread.sleep(10L);
      } catch (InterruptedException interruptedException) {}
    } 
  }
}
