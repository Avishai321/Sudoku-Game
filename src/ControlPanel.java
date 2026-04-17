import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalTime;

public class ControlPanel extends JPanel {
    private int moves = 0;
    private final JLabel movesLabel = new JLabel("Moves: 0");

    private final JButton resetBoardButton = new JButton("Reset Board");
    private final JButton autoSolveButton;

    private Runnable autoSolveCallable;
    private Runnable resetBoardCallable;

    public ControlPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));

        setLayout(new GridLayout(1, 2));

        movesLabel.setHorizontalAlignment(JLabel.CENTER);
        add(movesLabel);

        autoSolveButton = getAutoSolveButton();
        add(autoSolveButton);

        resetBoardButton.setFocusable(false);
        resetBoardButton.addActionListener(e -> {
            if (resetBoardCallable != null) resetBoardCallable.run();
        });
        add(resetBoardButton);

        //TODO fix functionality and use SwingUtilities.invokeLater() when changing ui
        JLabel timeLabel = new JLabel("00:00");
        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        LocalTime startTime = LocalTime.now();

        Timer timer = new Timer(1000, e -> {
            Duration duration = Duration.between(startTime, LocalTime.now());
            long totalSeconds = duration.getSeconds();

            long mins = totalSeconds / 60;
            long secs = totalSeconds % 60;

            timeLabel.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs));
        });
        timer.start();
        add(timeLabel);
    }

    private JButton getAutoSolveButton() {
        JButton autoSolveButton = new JButton("Auto Solve");
        autoSolveButton.setFocusable(false);
        autoSolveButton.addActionListener(e -> {
            autoSolveButton.setEnabled(false);
            resetBoardButton.setEnabled(false);

            if (autoSolveCallable != null) autoSolveCallable.run();
        });

        return autoSolveButton;
    }

    public void enableButtons() {
        this.autoSolveButton.setEnabled(true);
        this.resetBoardButton.setEnabled(true);
    }

    public void setAutoSolveCallable(Runnable autoSolveCallable) {
        this.autoSolveCallable = autoSolveCallable;
    }

    public void setResetBoardCallable(Runnable resetBoardCallable) {
        this.resetBoardCallable = resetBoardCallable;
    }

    private void updateLabel() {
        movesLabel.setText("Moves " + moves);
    }

    public void increaseMoves() {
        moves++;
        updateLabel();
    }

    public void resetMoves() {
        moves = 0;
        updateLabel();
    }
}
