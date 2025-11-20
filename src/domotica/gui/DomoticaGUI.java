package domotica.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DomoticaGUI extends JFrame {
    private JTextArea console;
    private JPanel housePanel;
    private JLabel countdownLabel;
    private Map<Integer, JPanel> lights;
    private Map<Integer, JPanel> fans;
    private Map<Integer, JPanel> thermostats;

    // --- NUEVAS VARIABLES PARA COMPILADOR ---
    private JTextArea sourceCodeEditor;
    private JTextArea astRealDisplay;      // Para el AST real
    private JTextArea objectCodeDisplay;   // Para el código objeto (Arduino)
    private JButton compileButton;
    private JButton loadButton;
    private JButton saveButton;
    private JLabel statusLabel;
    

    public DomoticaGUI() {
        initializeGUI();
    }

    private void initializeGUI() {
    setTitle("Compilador Domotico - Sistema con COCO/R");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());
    
    // --- PANEL SUPERIOR: ESTADO DEL COMPILADOR ---
    JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    statusLabel = new JLabel("Compilador Domotico Listo - Cargue un archivo .dom");
    statusPanel.add(statusLabel);
    add(statusPanel, BorderLayout.NORTH);

    // --- PANEL CENTRAL DIVIDIDO EN 3 ---
    JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    mainSplitPane.setDividerLocation(400);

    // --- PANEL IZQUIERDO: CODIGO FUENTE Y AST ---
    JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    leftSplitPane.setDividerLocation(300);

    // Panel de codigo fuente
    JPanel sourcePanel = new JPanel(new BorderLayout());
    sourcePanel.setBorder(BorderFactory.createTitledBorder(" 1. CODIGO FUENTE (.dom) "));
    
    sourceCodeEditor = new JTextArea(10, 35);
    sourceCodeEditor.setFont(new Font("Consolas", Font.PLAIN, 12));
    sourceCodeEditor.setTabSize(2);
    
    JScrollPane sourceScrollPane = new JScrollPane(sourceCodeEditor);
    sourcePanel.add(sourceScrollPane, BorderLayout.CENTER);
    
    // Botones para codigo fuente
    JPanel sourceButtonsPanel = new JPanel();
    loadButton = new JButton("Cargar Archivo .dom");
    saveButton = new JButton("Guardar Archivo");
    compileButton = new JButton("Compilar");
    
    sourceButtonsPanel.add(loadButton);
    sourceButtonsPanel.add(saveButton);
    sourceButtonsPanel.add(compileButton);
    sourcePanel.add(sourceButtonsPanel, BorderLayout.SOUTH);

    leftSplitPane.setTopComponent(sourcePanel);

    // Panel de AST REAL
    JPanel astPanel = new JPanel(new BorderLayout());
    astPanel.setBorder(BorderFactory.createTitledBorder(" 2. Codigo intermedio "));
    
    astRealDisplay = new JTextArea(10, 35);
    astRealDisplay.setFont(new Font("Consolas", Font.PLAIN, 11));
    astRealDisplay.setEditable(false);
    astRealDisplay.setBackground(new Color(245, 245, 245));
    
    JScrollPane astScrollPane = new JScrollPane(astRealDisplay);
    astPanel.add(astScrollPane, BorderLayout.CENTER);

    leftSplitPane.setBottomComponent(astPanel);
    mainSplitPane.setLeftComponent(leftSplitPane);

    // --- PANEL DERECHO: CODIGO OBJETO Y EJECUCION ---
    JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    rightSplitPane.setDividerLocation(300);

    // Panel de codigo objeto (Arduino)
    JPanel objectCodePanel = new JPanel(new BorderLayout());
    objectCodePanel.setBorder(BorderFactory.createTitledBorder(" 3. CODIGO OBJETO (Arduino) "));
    
    objectCodeDisplay = new JTextArea(10, 35);
    objectCodeDisplay.setFont(new Font("Consolas", Font.PLAIN, 11));
    objectCodeDisplay.setEditable(false);
    objectCodeDisplay.setBackground(new Color(245, 245, 245));
    
    JScrollPane objectCodeScrollPane = new JScrollPane(objectCodeDisplay);
    objectCodePanel.add(objectCodeScrollPane, BorderLayout.CENTER);

    rightSplitPane.setTopComponent(objectCodePanel);

    // Panel de ejecucion (casa inteligente y consola)
    JPanel executionPanel = new JPanel(new BorderLayout());
    executionPanel.setBorder(BorderFactory.createTitledBorder(" 4. EJECUCION Y SIMULACION "));

    // Casa inteligente
    housePanel = new JPanel();
    housePanel.setLayout(new GridLayout(2, 3));
    housePanel.setBackground(new Color(230, 230, 250));
    
    lights = new HashMap<>();
    fans = new HashMap<>();
    thermostats = new HashMap<>();

    // Crear representaciones de dispositivos
    for (int i = 1; i <= 3; i++) {
        JPanel lightPanel = createLightPanel(i);
        lights.put(i, lightPanel);
        housePanel.add(lightPanel);

        if (i <= 2) {
            JPanel fanPanel = createFanPanel(i);
            fans.put(i, fanPanel);
            housePanel.add(fanPanel);
        }
    }

    JPanel thermostatPanel = createThermostatPanel(3);
    thermostats.put(3, thermostatPanel);
    housePanel.add(thermostatPanel);

    executionPanel.add(housePanel, BorderLayout.CENTER);

    // Consola de ejecucion
    JPanel consolePanel = new JPanel(new BorderLayout());
    consolePanel.setBorder(BorderFactory.createTitledBorder(" Consola de Ejecucion "));

    console = new JTextArea(8, 35);
    console.setEditable(false);
    console.setBackground(Color.BLACK);
    console.setForeground(Color.GREEN);
    console.setFont(new Font("Consolas", Font.PLAIN, 12));

    JScrollPane consoleScrollPane = new JScrollPane(console);
    consolePanel.add(consoleScrollPane, BorderLayout.CENTER);

    // Cuenta regresiva
    countdownLabel = new JLabel("", SwingConstants.CENTER);
    countdownLabel.setFont(new Font("Arial", Font.BOLD, 16));
    countdownLabel.setForeground(Color.RED);
    consolePanel.add(countdownLabel, BorderLayout.NORTH);

    executionPanel.add(consolePanel, BorderLayout.SOUTH);
    rightSplitPane.setBottomComponent(executionPanel);
    mainSplitPane.setRightComponent(rightSplitPane);

    add(mainSplitPane, BorderLayout.CENTER);

    // --- CONFIGURAR ACCIONES ---
    compileButton.addActionListener(e -> compilarYEjecutar());
    loadButton.addActionListener(e -> cargarArchivoDom());
    saveButton.addActionListener(e -> guardarArchivoDom());

    pack();
    setLocationRelativeTo(null);
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setVisible(true);

    // Cargar ejemplo por defecto
    cargarEjemploPorDefecto();
    log("=== COMPILADOR DOMOTICO INICIALIZADO ===");
    log("Sistema listo para compilar archivos .dom");
}

    // --- MÉTODOS PARA EL COMPILADOR ---

    private void compilarYEjecutar() {
        String codigoFuente = sourceCodeEditor.getText();
        if (codigoFuente.trim().isEmpty()) {
            mostrarError("El editor de código fuente está vacío");
            return;
        }

        statusLabel.setText("Compilando...");
        log("=== INICIANDO PROCESO DE COMPILACIÓN ===");
        log("Fase 1: Análisis léxico y sintáctico...");
        
        // Limpiar AST anterior
        astRealDisplay.setText("");
        objectCodeDisplay.setText("");

        // Notificar al Main para que compile
        if (onCompileRequest != null) {
            onCompileRequest.accept(codigoFuente);
        }
    }

    private void cargarArchivoDom() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Cargar archivo domótico");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos Domóticos (*.dom)", "dom"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Scanner scanner = new Scanner(fileChooser.getSelectedFile())) {
                StringBuilder content = new StringBuilder();
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine()).append("\n");
                }
                sourceCodeEditor.setText(content.toString());
                statusLabel.setText("Archivo cargado: " + fileChooser.getSelectedFile().getName());
                log("Archivo .dom cargado: " + fileChooser.getSelectedFile().getName());
            } catch (IOException ex) {
                mostrarError("Error al cargar archivo: " + ex.getMessage());
            }
        }
    }

    private void guardarArchivoDom() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar archivo domótico");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos Domóticos (*.dom)", "dom"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".dom")) {
                file = new File(file.getAbsolutePath() + ".dom");
            }
            
            try (PrintWriter writer = new PrintWriter(file)) {
                writer.write(sourceCodeEditor.getText());
                statusLabel.setText("Archivo guardado: " + file.getName());
                log("Archivo .dom guardado: " + file.getName());
            } catch (IOException ex) {
                mostrarError("Error al guardar: " + ex.getMessage());
            }
        }
    }

    private void cargarEjemploPorDefecto() {
        String ejemplo = "// RUTINA DOMÓTICA DE DEMOSTRACIÓN\n" +
                        "// Compilador con COCO/R - Trabajo Práctico\n\n" +
                        "// Encender luces con diferentes colores\n" +
                        "ON LIGHT 1\n" +
                        "WAIT 2 SECONDS\n" +
                        "COLOR LIGHT 1 TO #FF0000\n" +
                        "ON LIGHT 2\n" +
                        "WAIT 2 SECONDS\n" +
                        "COLOR LIGHT 2 TO #00FF00\n" +
                        "ON LIGHT 3\n" +
                        "WAIT 2 SECONDS\n" +
                        "COLOR LIGHT 3 TO #0000FF\n\n" +
                        "// Activar ventiladores\n" +
                        "LOG \"Activando sistema de ventilación...\"\n" +
                        "ON FAN 1\n" +
                        "SET FAN 1 SPEED 3\n" +
                        "WAIT 3 SECONDS\n" +
                        "ON FAN 2\n" +
                        "SET FAN 2 SPEED 2\n" +
                        "WAIT 3 SECONDS\n\n" +
                        "// Finalizar rutina\n" +
                        "LOG \"Apagando todos los dispositivos...\"\n" +
                        "OFF FAN 1\n" +
                        "OFF FAN 2\n" +
                        "OFF LIGHT 1\n" +
                        "OFF LIGHT 2\n" +
                        "OFF LIGHT 3\n\n" +
                        "LOG \"Rutina domótica ejecutada exitosamente\"";
        
        sourceCodeEditor.setText(ejemplo);
    }

    
  

    // --- INTERFAZ PARA COMUNICACIÓN CON EL MAIN ---
    private java.util.function.Consumer<String> onCompileRequest;
    
    public void setOnCompileRequest(java.util.function.Consumer<String> handler) {
        this.onCompileRequest = handler;
    }

        // --- METODOS PARA ACTUALIZAR LOS NUEVOS PANELES ---
    public void mostrarASTReal(String astRepresentacion) {
        SwingUtilities.invokeLater(() -> {
            astRealDisplay.setText(astRepresentacion);
        });
    }

    public void mostrarCodigoObjeto(String codigoObjeto) {
        SwingUtilities.invokeLater(() -> {
            objectCodeDisplay.setText(codigoObjeto);
        });
    }

    // Cambiar mostrarError a publico
    public void mostrarError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Error: " + mensaje);
            log("ERROR: " + mensaje);
            JOptionPane.showMessageDialog(this, mensaje, "Error de Compilacion", JOptionPane.ERROR_MESSAGE);
        });
    }

    // --- MÉTODOS PARA ACTUALIZAR EL ESTADO DEL COMPILADOR ---
   

    public void compilacionExitosa(int nodosAST) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Compilación exitosa - " + nodosAST + " nodos AST generados");
            log("✓ Compilación completada - AST con " + nodosAST + " nodos");
        });
    }

    public void compilacionFallida(int errores) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Compilación fallida - " + errores + " errores");
            log("✗ Compilación fallida - " + errores + " errores encontrados");
        });
    }
 private JPanel createThermostatPanel(int id) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setBackground(Color.WHITE); // El fondo exterior siempre blanco

        JLabel label = new JLabel(" Termostato ", SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        // --- CAMBIO: Panel interno (display) igual que el de las luces ---
        JPanel displayPanel = new JPanel(new GridBagLayout()); // GridBag para centrar el texto
        displayPanel.setPreferredSize(new Dimension(60, 60));
        displayPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        displayPanel.setBackground(Color.WHITE); // Inicialmente blanco/apagado
        
        JLabel tempLabel = new JLabel("--", SwingConstants.CENTER);
        tempLabel.setFont(new Font("Arial", Font.BOLD, 20));
        tempLabel.setForeground(Color.BLACK);
        
        displayPanel.add(tempLabel);
        panel.add(displayPanel, BorderLayout.CENTER);
        // ---------------------------------------------------------------

        JLabel status = new JLabel("APAGADO", SwingConstants.CENTER);
        status.setForeground(Color.RED);
        panel.add(status, BorderLayout.SOUTH);

        // Guardamos referencias (incluyendo el nuevo displayPanel)
        panel.putClientProperty("displayPanel", displayPanel); 
        panel.putClientProperty("tempLabel", tempLabel);
        panel.putClientProperty("status", status);

        return panel;
    }

 private JPanel createLightPanel(int id) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(" Luz " + id, SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        JPanel bulb = new JPanel();
        bulb.setPreferredSize(new Dimension(60, 60));
        bulb.setBackground(Color.GRAY); // Apagado inicialmente
        bulb.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        panel.add(bulb, BorderLayout.CENTER);

        JLabel status = new JLabel("APAGADA", SwingConstants.CENTER);
        status.setForeground(Color.RED);
        panel.add(status, BorderLayout.SOUTH);

        // Guardar referencia al bulbo y estado
        panel.putClientProperty("bulb", bulb);
        panel.putClientProperty("status", status);

        return panel;
    }

    private JPanel createFanPanel(int id) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel(" Ventilador " + id, SwingConstants.CENTER);
        panel.add(label, BorderLayout.NORTH);

        // Contenedor para centrar el ventilador - CON FONDO BLANCO
        JPanel fanContainer = new JPanel(new GridBagLayout());
        fanContainer.setPreferredSize(new Dimension(80, 80));
        fanContainer.setBackground(Color.WHITE); // Fondo blanco para mejor contraste
        fanContainer.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        
        // Crear el componente de ventilador animado con imagen
        AnimatedFanLabel fanIcon = new AnimatedFanLabel();
        fanContainer.add(fanIcon);
        
        panel.add(fanContainer, BorderLayout.CENTER);

        JLabel status = new JLabel("APAGADO", SwingConstants.CENTER);
        status.setForeground(Color.RED);
        panel.add(status, BorderLayout.SOUTH);

        // Guardamos la referencia al icono animado
        panel.putClientProperty("fanIcon", fanIcon); 
        panel.putClientProperty("status", status);

        return panel;
    }
    
    // Clase interna que usa imagen PNG como recurso
    private class AnimatedFanLabel extends JPanel implements ActionListener {
        private Timer timer;
        private int rotationAngle = 0;
        private boolean isSpinning = false;
        private BufferedImage fanImage;
        private boolean imageLoaded = false;
        
        public AnimatedFanLabel() {
            setOpaque(false); // CRITICO: hacer transparente el panel
            setPreferredSize(new Dimension(60, 60));
            
            // Cargar la imagen del ventilador COMO RECURSO
            try {
                // Intentar cargar como recurso desde el classpath
                fanImage = ImageIO.read(getClass().getResource("/domotica/img/fan.png"));
                imageLoaded = true;
                System.out.println("Imagen del ventilador cargada correctamente como recurso");
            } catch (IOException | IllegalArgumentException e) {
                System.err.println("Error cargando imagen como recurso: " + e.getMessage());
                // Segundo intento: cargar desde sistema de archivos
                try {
                    fanImage = ImageIO.read(new java.io.File("src/domotica/img/fan.png"));
                    imageLoaded = true;
                    System.out.println("Imagen del ventilador cargada desde sistema de archivos");
                } catch (IOException ex) {
                    System.err.println("Error cargando imagen desde archivo: " + ex.getMessage());
                    createFallbackImage();
                }
            }
            
            this.timer = new Timer(50, this); // 50ms = 20 FPS
        }

        private void createFallbackImage() {
            // Crear una imagen de fallback más visible
            fanImage = new BufferedImage(60, 60, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = fanImage.createGraphics();
            
            // Aplicar antialiasing para el fallback
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fondo transparente
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.fillRect(0, 0, 60, 60);
            
            // Dibujar un ventilador simple y visible
            g2d.setColor(Color.BLUE);
            g2d.fillOval(5, 5, 50, 50); // Circulo exterior
            
            g2d.setColor(Color.WHITE);
            // Aspas del ventilador
            g2d.fillRect(27, 10, 6, 40); // Aspa vertical
            g2d.fillRect(10, 27, 40, 6); // Aspa horizontal
            
            // Centro del ventilador
            g2d.setColor(Color.RED);
            g2d.fillOval(25, 25, 10, 10);
            
            g2d.dispose();
            imageLoaded = false;
        }

        public void setSpinning(boolean spinning) {
            if (this.isSpinning == spinning) return;
            
            this.isSpinning = spinning;
            if (spinning) {
                if (!timer.isRunning()) {
                    timer.start();
                }
            } else {
                if (timer.isRunning()) {
                    timer.stop();
                }
                SwingUtilities.invokeLater(() -> {
                    rotationAngle = 0; 
                    repaint();
                });
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            rotationAngle = (rotationAngle + 20) % 360; 
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (fanImage == null) return;
            
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Aplicar antialiasing para mejor calidad
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            
            // Calcular transformacion para rotar la imagen alrededor de su centro
            AffineTransform transform = new AffineTransform();
            transform.translate(centerX, centerY);
            transform.rotate(Math.toRadians(rotationAngle));
            
            // Escalar la imagen si es necesario para que quepa en el panel
            double scale = Math.min((double)getWidth() / fanImage.getWidth(), 
                                   (double)getHeight() / fanImage.getHeight());
            transform.scale(scale, scale);
            
            transform.translate(-fanImage.getWidth() / 2, -fanImage.getHeight() / 2);
            
            g2d.drawImage(fanImage, transform, this);
            g2d.dispose();
        }
    }
    public void updateThermostatColor(int id, int temperature) {
    JPanel panel = thermostats.get(id);
    if (panel == null) return;

    if (temperature > 24) {
        panel.setBackground(Color.RED);
    } else {
        panel.setBackground(Color.CYAN);
    }

    panel.repaint();
}

    // Métodos de control (ON/OFF)
    
    public void turnOnFan(int fanId) {
        SwingUtilities.invokeLater(() -> {
            JPanel fanPanel = fans.get(fanId);
            if (fanPanel != null) {
                AnimatedFanLabel fanIcon = (AnimatedFanLabel) fanPanel.getClientProperty("fanIcon");
                JLabel status = (JLabel) fanPanel.getClientProperty("status");

                if (fanIcon != null) {
                    fanIcon.setSpinning(true); // Iniciar la animacion
                }
                if (status != null) {
                    status.setText("ENCENDIDO");
                    status.setForeground(Color.BLUE);
                }
            }
        });
    }

    public void turnOffFan(int fanId) {
        SwingUtilities.invokeLater(() -> {
            JPanel fanPanel = fans.get(fanId);
            if (fanPanel != null) {
                AnimatedFanLabel fanIcon = (AnimatedFanLabel) fanPanel.getClientProperty("fanIcon");
                JLabel status = (JLabel) fanPanel.getClientProperty("status");

                if (fanIcon != null) {
                    fanIcon.setSpinning(false); // Detener la animacion
                }
                if (status != null) {
                    status.setText("APAGADO");
                    status.setForeground(Color.RED);
                }
            }
        });
    }

    public void turnOnLight(int lightId) {
        SwingUtilities.invokeLater(() -> {
            JPanel lightPanel = lights.get(lightId);
            if (lightPanel != null) {
                JPanel bulb = (JPanel) lightPanel.getClientProperty("bulb");
                JLabel status = (JLabel) lightPanel.getClientProperty("status");
                
                bulb.setBackground(Color.YELLOW);
                status.setText("ENCENDIDA");
                status.setForeground(Color.GREEN);
                
                new Thread(() -> {
                    try {
                        for (int i = 0; i < 3; i++) {
                            bulb.setBackground(Color.ORANGE);
                            Thread.sleep(100);
                            bulb.setBackground(Color.YELLOW);
                            Thread.sleep(100);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); 
                    }
                }).start();
            }
        });
    }
    
    public void turnOffLight(int lightId) {
        SwingUtilities.invokeLater(() -> {
            JPanel lightPanel = lights.get(lightId);
            if (lightPanel != null) {
                JPanel bulb = (JPanel) lightPanel.getClientProperty("bulb");
                JLabel status = (JLabel) lightPanel.getClientProperty("status");
                
                bulb.setBackground(Color.GRAY);
                status.setText("APAGADA");
                status.setForeground(Color.RED);
            }
        });
    }

    // Métodos de control para Termostatos
    public void turnOnThermostat(int id) {
        SwingUtilities.invokeLater(() -> {
            JPanel tPanel = thermostats.get(id);
            if (tPanel != null) {
                JLabel status = (JLabel) tPanel.getClientProperty("status");
                if (status != null) {
                    status.setText("ENCENDIDO");
                    status.setForeground(Color.BLUE);
                }
            }
        });
    }

    public void turnOffThermostat(int id) {
        SwingUtilities.invokeLater(() -> {
            JPanel tPanel = thermostats.get(id);
            if (tPanel != null) {
                // Recuperamos componentes
                JPanel displayPanel = (JPanel) tPanel.getClientProperty("displayPanel");
                JLabel status = (JLabel) tPanel.getClientProperty("status");
                JLabel tempLabel = (JLabel) tPanel.getClientProperty("tempLabel");
                
                if (status != null) {
                    status.setText("APAGADO");
                    status.setForeground(Color.RED);
                }
                
                // Restaurar estado visual a "apagado" (Blanco)
                if (tempLabel != null) {
                    tempLabel.setText("--");
                    tempLabel.setForeground(Color.BLACK);
                }
                
                if (displayPanel != null) {
                    displayPanel.setBackground(Color.WHITE); // Cuadro blanco al apagar
                }
                
                tPanel.repaint();
            }
        });
    }
    
    public void setThermostatTemperature(int id, int temperature) {
        SwingUtilities.invokeLater(() -> {
            JPanel tPanel = thermostats.get(id);
            if (tPanel != null) {
                JPanel displayPanel = (JPanel) tPanel.getClientProperty("displayPanel");
                JLabel tempLabel = (JLabel) tPanel.getClientProperty("tempLabel");
                JLabel status = (JLabel) tPanel.getClientProperty("status");
                if (tempLabel != null) {
                    tempLabel.setText(String.valueOf(temperature));
                }
                if (displayPanel != null) {
                    if (temperature > 24) {
                        displayPanel.setBackground(Color.RED);
                        if (tempLabel != null) tempLabel.setForeground(Color.WHITE);
                    } else {
                        displayPanel.setBackground(Color.CYAN);
                        if (tempLabel != null) tempLabel.setForeground(Color.BLACK);
                    }
                }
                
                // 2. Logica de Colores solicitada
                if (displayPanel != null) {
                    if (temperature > 24) {
                        displayPanel.setBackground(Color.RED);
                        if (tempLabel != null) tempLabel.setForeground(Color.WHITE);
                    } else {
                        displayPanel.setBackground(Color.CYAN);
                        if (tempLabel != null) tempLabel.setForeground(Color.BLACK);
                    }
                }
                if (status != null) {
                    status.setText("ENCENDIDO");
                    status.setForeground(Color.BLUE);
                }
                tPanel.repaint();
            }
        });
    }

    // Método de conveniencia para que los comandos AST actualicen el número
    public void updateThermostatDisplay(int id, String text) {
        SwingUtilities.invokeLater(() -> {
            JPanel tPanel = thermostats.get(id);
            if (tPanel != null) {
                JLabel tempLabel = (JLabel) tPanel.getClientProperty("tempLabel");
                if (tempLabel != null) {
                    tempLabel.setText(text);
                }
            }
        });
    }

    
 
    public void setLightColor(int lightId, String color) {
       SwingUtilities.invokeLater(() -> {
           JPanel lightPanel = lights.get(lightId);
           if (lightPanel != null) {
               JPanel bulb = (JPanel) lightPanel.getClientProperty("bulb");
               
               try {
                   Color awtColor = Color.decode(color);
                   bulb.setBackground(awtColor);
                   
                   new Thread(() -> {
                       try {
                           for (int i = 0; i < 5; i++) {
                               bulb.setBackground(brighter(awtColor, i * 0.1f));
                               Thread.sleep(50);
                               bulb.setBackground(awtColor); 
                               Thread.sleep(50);
                           }
                       } catch (InterruptedException e) {
                           Thread.currentThread().interrupt(); 
                       }
                   }).start();
                   
               } catch (Exception e) {
                   bulb.setBackground(Color.PINK);
               }
           }
       });
     }


    private Color brighter(Color color, float factor) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        
        r = Math.min(255, (int)(r + (255 - r) * factor));
        g = Math.min(255, (int)(g + (255 - g) * factor));
        b = Math.min(255, (int)(b + (255 - b) * factor));
        
        return new Color(r, g, b);
    }
    
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            console.append("[" + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")) + "] " + message + "\n");
            console.setCaretPosition(console.getDocument().getLength());
        });
    }
    
    public void updateCountdown(String text) {
        SwingUtilities.invokeLater(() -> {
            countdownLabel.setText(text);
        });
    }
    
    public void resetAll() {
        SwingUtilities.invokeLater(() -> {
            for (int i = 1; i <= 3; i++) {
                turnOffLight(i);
            }
            
            for (int i = 1; i <= 2; i++) {
                turnOffFan(i);
            }
            
            console.setText("");
            countdownLabel.setText("");
            
            log("Sistema reiniciado. Listo para nueva ejecución.");
        });
    }
}