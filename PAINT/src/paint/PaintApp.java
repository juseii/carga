package paint;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PaintApp extends JFrame {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private int prevX, prevY;
    private Color currentColor = Color.BLACK;
    private int currentThickness = 10;

    private Canvas canvas;
    private JFrame projectorFrame;

    public PaintApp() {
        setupFrame();
        setupCanvas();
        setupButtonPanel();
        setupProjectorButton();
    }

    private void setupFrame() {
        setTitle("PAINT");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void setupCanvas() {
        canvas = new Canvas();
        canvas.setBackground(Color.WHITE);
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drawOnCanvas(canvas.getGraphics(), e.getX(), e.getY());
            }
        });

        add(canvas, BorderLayout.CENTER);
    }

    private void setupButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(173, 216, 230));

        JButton colorButton = createStyledButton("Seleccionar Color");
        colorButton.addActionListener(e -> chooseColor());

        JButton thicknessButton = createStyledButton("Grosor");
        thicknessButton.addActionListener(e -> chooseThickness());

        buttonPanel.add(colorButton);
        buttonPanel.add(thicknessButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupProjectorButton() {
        JButton projectorButton = createStyledButton("Proyector");
        projectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProjector();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(173, 216, 230));
        buttonPanel.add(projectorButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        styleButton(button);
        return button;
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(221, 160, 221)); 
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        ButtonModel model = button.getModel();
        model.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (model.isPressed() || model.isSelected()) {
                    button.setBackground(new Color(173, 216, 230)); 
                } else {
                    button.setBackground(new Color(221, 160, 221)); 
                }
            }
        });
    }

    private void drawOnCanvas(Graphics g, int x, int y) {
        g.setColor(currentColor);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(currentThickness));
        g2d.drawLine(prevX, prevY, x, y);
        prevX = x;
        prevY = y;
    }

    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Seleccionar Color", currentColor);
        if (newColor != null) {
            currentColor = newColor;
        }
    }

    private void chooseThickness() {
        try {
            String input = JOptionPane.showInputDialog(this, "Ingrese el grosor del lápiz (1-20):", currentThickness);
            int thickness = Integer.parseInt(input);
            if (thickness >= 1 && thickness <= 20) {
                currentThickness = thickness;
            } else {
                showErrorMessage("El grosor debe estar en el rango de 1 a 20.");
            }
        } catch (NumberFormatException e) {
            showErrorMessage("Ingrese un número válido para el grosor.");
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void openProjector() {
        if (projectorFrame == null) {
            projectorFrame = new JFrame("Proyector");
            projectorFrame.setSize(WIDTH, HEIGHT);
            projectorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            projectorFrame.setLayout(new BorderLayout());

            Canvas projectorCanvas = new Canvas();
            projectorCanvas.setBackground(Color.WHITE);
            projectorFrame.add(projectorCanvas, BorderLayout.CENTER);

            // Sincronizar los eventos de dibujo en la pantalla principal con la del proyector
            canvas.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    projectorCanvas.draw(prevX, prevY, e.getX(), e.getY());
                    prevX = e.getX();
                    prevY = e.getY();
                }
            });
        }

        projectorFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
            PaintApp paintApp = new PaintApp();
            paintApp.setVisible(true);
        });
    }

    private class Canvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Aquí podrías realizar dibujos adicionales si es necesario
        }

        public void draw(int x1, int y1, int x2, int y2) {
            Graphics2D g2d = (Graphics2D) getGraphics();
            g2d.setColor(currentColor);
            g2d.setStroke(new BasicStroke(currentThickness));
            g2d.drawLine(x1, y1, x2, y2);
        }
    }
}