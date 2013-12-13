package org.lesmothian.jupdater.ui;

import javax.swing.JWindow;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import java.awt.BorderLayout;

@SuppressWarnings("serial")
public class ProgressWindow extends JWindow {
  private JLabel label;
  private JProgressBar progressBar;

  public ProgressWindow() {
    super();

    setLayout(new BorderLayout());

    label = new JLabel("Initializing...");
    progressBar = new JProgressBar();
    getContentPane().add(label, BorderLayout.CENTER);
    getContentPane().add(progressBar, BorderLayout.PAGE_END);

    pack();
  }
}
