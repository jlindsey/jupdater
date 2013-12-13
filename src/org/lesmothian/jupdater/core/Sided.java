package org.lesmothian.jupdater.core;

public interface Sided {
  public static enum Side { 
    LOCAL, REMOTE;
  }

  public void setLocal();
  public void setRemote();
  
  public boolean isLocal();
  public boolean isRemote();
}
