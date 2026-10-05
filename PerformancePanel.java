package networktraffic;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PerformancePanel extends JPanel {

    private final List<Integer> bufferData;
    private final List<Integer> transmittedData;
    private final List<Integer> droppedData;

    private int bufferCapacity = 10;

    public PerformancePanel() {

        bufferData = new ArrayList<>();
        transmittedData = new ArrayList<>();
        droppedData = new ArrayList<>();

        setBackground(Color.WHITE);
    }

    public void setBufferCapacity(int capacity) {
        this.bufferCapacity = capacity;
    }

    public void addData(
            int bufferSize,
            int transmitted,
            int dropped) {

        bufferData.add(bufferSize);
        transmittedData.add(transmitted);
        droppedData.add(dropped);

        repaint();
    }

    public void clearData() {

        bufferData.clear();
        transmittedData.clear();
        droppedData.clear();

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D graphics =
                (Graphics2D) g;

        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int width = getWidth();
        int height = getHeight();

        // =========================================
        // TITLE
        // =========================================

        graphics.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        18
                )
        );

        graphics.drawString(
                "Live Network Performance",
                30,
                30
        );

        // =========================================
        // AXIS
        // =========================================

        int left = 60;
        int top = 60;
        int right = width - 30;
        int bottom = height - 50;

        graphics.drawLine(
                left,
                bottom,
                right,
                bottom
        );

        graphics.drawLine(
                left,
                top,
                left,
                bottom
        );

        // =========================================
        // GRID
        // =========================================

        graphics.setColor(
                new Color(220, 220, 220)
        );

        int graphHeight =
                bottom - top;

        for (int i = 0; i <= 5; i++) {

            int y =
                    bottom
                            - (graphHeight * i / 5);

            graphics.drawLine(
                    left,
                    y,
                    right,
                    y
            );
        }

        // Restore black
        graphics.setColor(Color.BLACK);

        // =========================================
        // DRAW BUFFER GRAPH
        // =========================================

        drawLineGraph(
                graphics,
                bufferData,
                Color.BLUE,
                left,
                top,
                right,
                bottom,
                bufferCapacity
        );

        // =========================================
        // DRAW TRANSMITTED GRAPH
        // =========================================

        drawLineGraph(
                graphics,
                transmittedData,
                Color.GREEN,
                left,
                top,
                right,
                bottom,
                getMaximumValue()
        );

        // =========================================
        // DRAW DROPPED GRAPH
        // =========================================

        drawLineGraph(
                graphics,
                droppedData,
                Color.RED,
                left,
                top,
                right,
                bottom,
                getMaximumValue()
        );

        // =========================================
        // LEGEND
        // =========================================

        int legendY = height - 20;

        graphics.setColor(Color.BLUE);

        graphics.fillRect(
                80,
                legendY - 10,
                12,
                12
        );

        graphics.setColor(Color.BLACK);

        graphics.drawString(
                "Buffer",
                100,
                legendY
        );

        graphics.setColor(Color.GREEN);

        graphics.fillRect(
                180,
                legendY - 10,
                12,
                12
        );

        graphics.setColor(Color.BLACK);

        graphics.drawString(
                "Transmitted",
                200,
                legendY
        );

        graphics.setColor(Color.RED);

        graphics.fillRect(
                320,
                legendY - 10,
                12,
                12
        );

        graphics.setColor(Color.BLACK);

        graphics.drawString(
                "Dropped",
                340,
                legendY
        );
    }

    private void drawLineGraph(
            Graphics2D graphics,
            List<Integer> data,
            Color color,
            int left,
            int top,
            int right,
            int bottom,
            int maximum) {

        if (data.size() < 2) {
            return;
        }

        if (maximum <= 0) {
            maximum = 1;
        }

        graphics.setColor(color);

        double xStep =
                (double) (right - left)
                        / (data.size() - 1);

        int previousX = left;

        int previousY =
                bottom
                        - (data.get(0)
                        * (bottom - top)
                        / maximum);

        for (int i = 1;
             i < data.size();
             i++) {

            int x =
                    left
                            + (int)
                            (i * xStep);

            int y =
                    bottom
                            - (data.get(i)
                            * (bottom - top)
                            / maximum);

            graphics.drawLine(
                    previousX,
                    previousY,
                    x,
                    y
            );

            previousX = x;
            previousY = y;
        }
    }

    private int getMaximumValue() {

        int maximum = 1;

        for (Integer value :
                transmittedData) {

            maximum =
                    Math.max(
                            maximum,
                            value
                    );
        }

        for (Integer value :
                droppedData) {

            maximum =
                    Math.max(
                            maximum,
                            value
                    );
        }

        maximum =
                Math.max(
                        maximum,
                        bufferCapacity
                );

        return maximum;
    }
}
