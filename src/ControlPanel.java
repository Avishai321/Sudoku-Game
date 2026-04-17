import javax.swing.*;
import java.awt.*;
import java.time.Duration;
import java.time.LocalTime;

public class ControlPanel extends JPanel {
    private int moves = 0;
    private final JLabel movesLabel = new JLabel("Moves: 0");
    private final JButton autoSolveButton = getAutoSolveButton();
    private final JButton resetBoardButton = getResetBoardButton();
    private final JLabel timeLabel = new JLabel("00:00");

    private Runnable autoSolveCallable;
    private Runnable resetBoardCallable;

    public ControlPanel(int width, int height) {
        setPreferredSize(new Dimension(width, height));

        setLayout(new GridLayout(1, 4));

        movesLabel.setHorizontalAlignment(JLabel.CENTER);
        add(movesLabel);

        add(autoSolveButton);
        add(resetBoardButton);

        timeLabel.setHorizontalAlignment(JLabel.CENTER);
        add(timeLabel);

        initTimer();
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

    public JButton getResetBoardButton() {
        JButton resetBoardButton = new JButton("Reset Board");
        resetBoardButton.setFocusable(false);

        resetBoardButton.addActionListener(e -> {
            if (resetBoardCallable != null) resetBoardCallable.run();
        });

        return resetBoardButton;
    }

    public void initTimer() {
        LocalTime startTime = LocalTime.now();

        new Timer(1000, e -> {
            Duration duration = Duration.between(startTime, LocalTime.now());
            int totalSeconds = (int) duration.getSeconds();

            int mins = totalSeconds / 60;
            int secs = totalSeconds % 60;

            timeLabel.setText(String.format("%02d:%02d", mins, secs));
        }).start();
    }

    public void enableButtons() {
        this.autoSolveButton.setEnabled(true);
        this.resetBoardButton.setEnabled(true);
    }

    private void updateMovesLabel() {
        movesLabel.setText("Moves " + moves);
    }

    public void increaseMoves() {
        moves++;
        updateMovesLabel();
    }

    public void resetMoves() {
        moves = 0;
        updateMovesLabel();
    }

    public void setAutoSolveCallable(Runnable autoSolveCallable) {
        this.autoSolveCallable = autoSolveCallable;
    }
    public void setResetBoardCallable(Runnable resetBoardCallable) {
        this.resetBoardCallable = resetBoardCallable;
    }
}
